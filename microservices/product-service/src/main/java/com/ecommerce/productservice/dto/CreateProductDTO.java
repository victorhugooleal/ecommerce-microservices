package com.ecommerce.productservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para criação e atualização de produtos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @JsonProperty("name")
    private String name;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Formato de preço inválido")
    @JsonProperty("price")
    private BigDecimal price;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @JsonProperty("stock_quantity")
    private Integer stockQuantity;

    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    @JsonProperty("category")
    private String category;

    @Size(max = 50, message = "Marca deve ter no máximo 50 caracteres")
    @JsonProperty("brand")
    private String brand;

    @Size(max = 20, message = "SKU deve ter no máximo 20 caracteres")
    @JsonProperty("sku")
    private String sku;

    @DecimalMin(value = "0.0", message = "Peso deve ser positivo")
    @DecimalMax(value = "999.99", message = "Peso deve ser menor que 1000kg")
    @JsonProperty("weight")
    private BigDecimal weight;

    @Size(max = 20, message = "Dimensões devem ter no máximo 20 caracteres")
    @JsonProperty("dimensions")
    private String dimensions;

    @Size(max = 255, message = "URL da imagem deve ter no máximo 255 caracteres")
    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("active")
    @Builder.Default
    private Boolean active = true;

    @JsonProperty("featured")
    @Builder.Default
    private Boolean featured = false;

    @Min(value = 0, message = "Nível mínimo de estoque deve ser positivo")
    @JsonProperty("min_stock_level")
    private Integer minStockLevel;
}
