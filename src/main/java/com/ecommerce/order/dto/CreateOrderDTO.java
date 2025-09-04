package com.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para criação de pedido
 */
public class CreateOrderDTO {

    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Size(max = 500, message = "Endereço pode ter no máximo 500 caracteres")
    private String shippingAddress;

    private String paymentMethod;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    @Valid
    private List<OrderItemDTO> items;

    // Construtores
    public CreateOrderDTO() {}

    public CreateOrderDTO(String shippingAddress, String paymentMethod, List<OrderItemDTO> items) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }

    // Getters e Setters
    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
