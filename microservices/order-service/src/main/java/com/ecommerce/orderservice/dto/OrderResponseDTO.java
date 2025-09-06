package com.ecommerce.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para resposta de pedidos nas APIs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("status_description")
    private String statusDescription;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("payment_status_description")
    private String paymentStatusDescription;

    @JsonProperty("notes")
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("shipped_at")
    private LocalDateTime shippedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("delivered_at")
    private LocalDateTime deliveredAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("cancelled_at")
    private LocalDateTime cancelledAt;

    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    @JsonProperty("items")
    private List<OrderItemResponseDTO> items;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    // Campos calculados para facilitar uso no frontend

    @JsonProperty("total_items")
    private Integer totalItems;

    @JsonProperty("can_be_cancelled")
    private Boolean canBeCancelled;

    @JsonProperty("is_finalized")
    private Boolean isFinalized;

    @JsonProperty("display_total")
    public String getDisplayTotal() {
        return totalAmount != null ? "R$ " + totalAmount.toString() : "R$ 0,00";
    }

    @JsonProperty("days_since_order")
    public Long getDaysSinceOrder() {
        if (createdAt != null) {
            return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
        }
        return null;
    }

    /**
     * DTO para itens do pedido na resposta
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponseDTO {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("product_id")
        private Long productId;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("product_sku")
        private String productSku;

        @JsonProperty("unit_price")
        private BigDecimal unitPrice;

        @JsonProperty("quantity")
        private Integer quantity;

        @JsonProperty("total_price")
        private BigDecimal totalPrice;

        @JsonProperty("product_category")
        private String productCategory;

        @JsonProperty("product_brand")
        private String productBrand;

        @JsonProperty("product_image_url")
        private String productImageUrl;

        @JsonProperty("notes")
        private String notes;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;

        @JsonProperty("display_unit_price")
        public String getDisplayUnitPrice() {
            return unitPrice != null ? "R$ " + unitPrice.toString() : "R$ 0,00";
        }

        @JsonProperty("display_total_price")
        public String getDisplayTotalPrice() {
            return totalPrice != null ? "R$ " + totalPrice.toString() : "R$ 0,00";
        }
    }
}
