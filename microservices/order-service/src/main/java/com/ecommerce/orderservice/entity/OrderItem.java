package com.ecommerce.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade OrderItem para itens do pedido
 * Representa cada produto dentro de um pedido
 */
@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Pedido é obrigatório")
    @ToString.Exclude
    private Order order;

    @Column(name = "product_id", nullable = false)
    @NotNull(message = "ID do produto é obrigatório")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(max = 100, message = "Nome do produto deve ter no máximo 100 caracteres")
    private String productName;

    @Column(name = "product_sku", length = 20)
    @Size(max = 20, message = "SKU deve ter no máximo 20 caracteres")
    private String productSku;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Preço unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço inválido")
    private BigDecimal unitPrice;

    @Column(nullable = false)
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantity;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Preço total é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço total deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço inválido")
    private BigDecimal totalPrice;

    @Column(name = "product_category", length = 50)
    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    private String productCategory;

    @Column(name = "product_brand", length = 50)
    @Size(max = 50, message = "Marca deve ter no máximo 50 caracteres")
    private String productBrand;

    @Column(name = "product_image_url", length = 255)
    @Size(max = 255, message = "URL da imagem deve ter no máximo 255 caracteres")
    private String productImageUrl;

    @Column(length = 500)
    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Métodos de negócio

    /**
     * Atualiza quantidade e recalcula preço total
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantity = newQuantity;
        calculateTotalPrice();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Atualiza preço unitário e recalcula preço total
     */
    public void updateUnitPrice(BigDecimal newUnitPrice) {
        if (newUnitPrice == null || newUnitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que zero");
        }
        this.unitPrice = newUnitPrice;
        calculateTotalPrice();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calcula o preço total (quantidade * preço unitário)
     */
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Verifica se o item está válido
     */
    public boolean isValid() {
        return productId != null && 
               productName != null && !productName.trim().isEmpty() &&
               unitPrice != null && unitPrice.compareTo(BigDecimal.ZERO) > 0 &&
               quantity != null && quantity > 0 &&
               totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Cria uma cópia do item (para edição)
     */
    public OrderItem copy() {
        return OrderItem.builder()
                .productId(this.productId)
                .productName(this.productName)
                .productSku(this.productSku)
                .unitPrice(this.unitPrice)
                .quantity(this.quantity)
                .totalPrice(this.totalPrice)
                .productCategory(this.productCategory)
                .productBrand(this.productBrand)
                .productImageUrl(this.productImageUrl)
                .notes(this.notes)
                .build();
    }

    // Hook para calcular preço total automaticamente antes de persistir
    @PrePersist
    @PreUpdate
    private void calculateTotalPriceBeforeSave() {
        calculateTotalPrice();
    }
}
