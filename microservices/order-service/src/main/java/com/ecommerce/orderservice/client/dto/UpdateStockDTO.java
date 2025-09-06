package com.ecommerce.orderservice.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de estoque via Product Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockDTO {

    @JsonProperty("product_id")
    private Long productId;

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
