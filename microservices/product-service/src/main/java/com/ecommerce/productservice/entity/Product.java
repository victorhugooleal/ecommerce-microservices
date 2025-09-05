package com.ecommerce.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Product para o Product Service
 * Migrada do monólito com adaptações para microsserviços
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;

    @Column(length = 500)
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço inválido")
    private BigDecimal price;

    @Column(nullable = false)
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Integer stockQuantity;

    @Column(length = 50)
    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    private String category;

    @Column(length = 50)
    @Size(max = 50, message = "Marca deve ter no máximo 50 caracteres")
    private String brand;

    @Column(length = 20)
    @Size(max = 20, message = "SKU deve ter no máximo 20 caracteres")
    private String sku;

    @Column(precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Peso deve ser positivo")
    @DecimalMax(value = "999.99", message = "Peso deve ser menor que 1000kg")
    private BigDecimal weight;

    @Column(length = 20)
    @Size(max = 20, message = "Dimensões devem ter no máximo 20 caracteres")
    private String dimensions;

    @Column(name = "image_url", length = 255)
    @Size(max = 255, message = "URL da imagem deve ter no máximo 255 caracteres")
    private String imageUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean featured = false;

    @Column(name = "min_stock_level")
    @Min(value = 0, message = "Nível mínimo de estoque deve ser positivo")
    private Integer minStockLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Métodos de negócio

    /**
     * Verifica se o produto está disponível para venda
     */
    public boolean isAvailable() {
        return active && stockQuantity > 0;
    }

    /**
     * Verifica se o produto está com estoque baixo
     */
    public boolean isLowStock() {
        return minStockLevel != null && stockQuantity <= minStockLevel;
    }

    /**
     * Reduz a quantidade em estoque
     */
    public void reduceStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        if (stockQuantity < quantity) {
            throw new IllegalStateException("Estoque insuficiente. Disponível: " + stockQuantity + ", Solicitado: " + quantity);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Aumenta a quantidade em estoque
     */
    public void increaseStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        this.stockQuantity += quantity;
    }

    /**
     * Calcula o valor total baseado na quantidade
     */
    public BigDecimal calculateTotal(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
