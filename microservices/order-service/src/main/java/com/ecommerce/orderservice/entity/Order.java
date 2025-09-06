package com.ecommerce.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Order para o Order Service
 * Gerencia pedidos com itens e estados
 */
@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "ID do usuário é obrigatório")
    private Long userId;

    @Column(name = "order_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Número do pedido é obrigatório")
    @Size(max = 20, message = "Número do pedido deve ter no máximo 20 caracteres")
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor total deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de valor inválido")
    private BigDecimal totalAmount;

    @Column(name = "shipping_address", nullable = false, length = 500)
    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Size(max = 500, message = "Endereço deve ter no máximo 500 caracteres")
    private String shippingAddress;

    @Column(name = "payment_method", length = 50)
    @Size(max = 50, message = "Método de pagamento deve ter no máximo 50 caracteres")
    private String paymentMethod;

    @Column(name = "payment_status", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(length = 1000)
    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    private String notes;

    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    @Size(max = 500, message = "Motivo do cancelamento deve ter no máximo 500 caracteres")
    private String cancellationReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Enums

    /**
     * Status do pedido
     */
    public enum OrderStatus {
        PENDING("PENDING", "Pendente"),
        CONFIRMED("CONFIRMED", "Confirmado"),
        PROCESSING("PROCESSING", "Processando"),
        SHIPPED("SHIPPED", "Enviado"),
        DELIVERED("DELIVERED", "Entregue"),
        CANCELLED("CANCELLED", "Cancelado"),
        RETURNED("RETURNED", "Devolvido");

        private final String code;
        private final String description;

        OrderStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * Status do pagamento
     */
    public enum PaymentStatus {
        PENDING("PENDING", "Pendente"),
        PROCESSING("PROCESSING", "Processando"),
        PAID("PAID", "Pago"),
        FAILED("FAILED", "Falhou"),
        REFUNDED("REFUNDED", "Reembolsado"),
        CANCELLED("CANCELLED", "Cancelado");

        private final String code;
        private final String description;

        PaymentStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    // Métodos de negócio

    /**
     * Adiciona item ao pedido
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    /**
     * Remove item do pedido
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }

    /**
     * Recalcula o valor total do pedido
     */
    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Confirma o pedido
     */
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Apenas pedidos pendentes podem ser confirmados");
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Inicia processamento do pedido
     */
    public void startProcessing() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Apenas pedidos confirmados podem ser processados");
        }
        this.status = OrderStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Marca pedido como enviado
     */
    public void ship() {
        if (status != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Apenas pedidos em processamento podem ser enviados");
        }
        this.status = OrderStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // Estimar entrega em 7 dias úteis
        if (estimatedDelivery == null) {
            this.estimatedDelivery = LocalDateTime.now().plusDays(7);
        }
    }

    /**
     * Marca pedido como entregue
     */
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Apenas pedidos enviados podem ser entregues");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancela o pedido
     */
    public void cancel(String reason) {
        if (status == OrderStatus.DELIVERED || status == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Pedidos enviados ou entregues não podem ser cancelados");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica se o pedido pode ser cancelado
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || 
               status == OrderStatus.CONFIRMED || 
               status == OrderStatus.PROCESSING;
    }

    /**
     * Verifica se o pedido está finalizado
     */
    public boolean isFinalized() {
        return status == OrderStatus.DELIVERED || 
               status == OrderStatus.CANCELLED || 
               status == OrderStatus.RETURNED;
    }

    /**
     * Calcula quantidade total de itens
     */
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    /**
     * Atualiza status do pagamento
     */
    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        // Se pagamento foi aprovado, confirmar pedido automaticamente
        if (newStatus == PaymentStatus.PAID && status == OrderStatus.PENDING) {
            confirm();
        }
    }
}
