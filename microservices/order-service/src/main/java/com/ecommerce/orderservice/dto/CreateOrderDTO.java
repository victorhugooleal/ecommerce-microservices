package com.ecommerce.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para criação de novos pedidos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {

    @NotNull(message = "ID do usuário é obrigatório")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "Lista de itens é obrigatória")
    @NotEmpty(message = "Pedido deve conter pelo menos um item")
    @Valid
    @JsonProperty("items")
    private List<CreateOrderItemDTO> items;

    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Size(max = 500, message = "Endereço deve ter no máximo 500 caracteres")
    @JsonProperty("shipping_address")
    private String shippingAddress;

    @Size(max = 50, message = "Método de pagamento deve ter no máximo 50 caracteres")
    @JsonProperty("payment_method")
    private String paymentMethod;

    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    @JsonProperty("notes")
    private String notes;

    /**
     * DTO para itens do pedido na criação
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderItemDTO {

        @NotNull(message = "ID do produto é obrigatório")
        @JsonProperty("product_id")
        private Long productId;

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        @JsonProperty("quantity")
        private Integer quantity;

        @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
        @JsonProperty("notes")
        private String notes;
    }
}
