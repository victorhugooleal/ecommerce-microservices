package com.ecommerce.productservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para resposta de produtos nas APIs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    @JsonProperty("category")
    private String category;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("weight")
    private BigDecimal weight;

    @JsonProperty("dimensions")
    private String dimensions;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("featured")
    private Boolean featured;

    @JsonProperty("min_stock_level")
    private Integer minStockLevel;

    @JsonProperty("available")
    private Boolean available;

    @JsonProperty("low_stock")
    private Boolean lowStock;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Campos calculados para facilitar uso no frontend
    
    @JsonProperty("display_price")
    public String getDisplayPrice() {
        return price != null ? "R$ " + price.toString() : "R$ 0,00";
    }

    @JsonProperty("stock_status")
    public String getStockStatus() {
        if (!active) return "INATIVO";
        if (stockQuantity == 0) return "SEM_ESTOQUE";
        if (lowStock) return "ESTOQUE_BAIXO";
        return "DISPONIVEL";
    }

    @JsonProperty("category_display")
    public String getCategoryDisplay() {
        return category != null ? category.toUpperCase() : "SEM_CATEGORIA";
    }
}
