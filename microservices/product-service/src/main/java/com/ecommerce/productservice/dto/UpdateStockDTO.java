package com.ecommerce.productservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de estoque de produtos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockDTO {

    @NotNull(message = "ID do produto é obrigatório")
    @JsonProperty("product_id")
    private Long productId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("operation")
    @Builder.Default
    private StockOperation operation = StockOperation.REDUCE;

    /**
     * Enum para definir o tipo de operação no estoque
     */
    public enum StockOperation {
        REDUCE("REDUCE"),
        INCREASE("INCREASE"),
        SET("SET");

        private final String value;

        StockOperation(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
