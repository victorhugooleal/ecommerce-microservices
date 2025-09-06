package com.ecommerce.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de status do pedido
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusDTO {

    @NotBlank(message = "Status é obrigatório")
    @JsonProperty("status")
    private String status;

    @Size(max = 500, message = "Motivo deve ter no máximo 500 caracteres")
    @JsonProperty("reason")
    private String reason;

    @Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    @JsonProperty("notes")
    private String notes;
}
