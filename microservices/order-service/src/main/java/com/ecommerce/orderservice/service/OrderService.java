package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.ProductServiceClient;
import com.ecommerce.orderservice.client.UserServiceClient;
import com.ecommerce.orderservice.client.dto.ProductResponseDTO;
import com.ecommerce.orderservice.client.dto.UpdateStockDTO;
import com.ecommerce.orderservice.client.dto.UserResponseDTO;
import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderResponseDTO;
import com.ecommerce.orderservice.dto.UpdateOrderStatusDTO;
import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import com.ecommerce.orderservice.repository.OrderItemRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service para gerenciamento de pedidos
 * Implementa regras de negócio para CRUD de pedidos e comunicação inter-serviços
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;

    // ============= OPERAÇÕES CRUD =============

    /**
     * Criar novo pedido
     */
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderDTO createOrderDTO) {
        log.info("Criando pedido para usuário ID: {}", createOrderDTO.getUserId());

        try {
            // 1. Validar usuário
            validateUser(createOrderDTO.getUserId());

            // 2. Validar produtos e calcular valores
            List<OrderItem> validatedItems = validateAndCreateOrderItems(createOrderDTO.getItems());

            // 3. Gerar número do pedido
            String orderNumber = generateOrderNumber();

            // 4. Calcular total
            BigDecimal totalAmount = validatedItems.stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 5. Criar pedido
            Order order = Order.builder()
                    .userId(createOrderDTO.getUserId())
                    .orderNumber(orderNumber)
                    .status(Order.OrderStatus.PENDING)
                    .totalAmount(totalAmount)
                    .shippingAddress(createOrderDTO.getShippingAddress())
                    .paymentMethod(createOrderDTO.getPaymentMethod())
                    .paymentStatus(Order.PaymentStatus.PENDING)
                    .notes(createOrderDTO.getNotes())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // 6. Adicionar itens ao pedido
            validatedItems.forEach(order::addItem);

            // 7. Salvar pedido
            Order savedOrder = orderRepository.save(order);

            // 8. Reduzir estoque dos produtos
            reserveProductStock(validatedItems);

            log.info("Pedido criado com sucesso - ID: {}, Número: {}", savedOrder.getId(), savedOrder.getOrderNumber());

            return convertToResponseDTO(savedOrder);

        } catch (Exception e) {
            log.error("Erro ao criar pedido: {}", e.getMessage());
            throw new RuntimeException("Falha ao criar pedido: " + e.getMessage());
        }
    }

    /**
     * Buscar pedido por ID
     */
    public OrderResponseDTO getOrderById(Long id) {
        log.info("Buscando pedido por ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado - ID: " + id));
        return convertToResponseDTO(order);
    }

    /**
     * Buscar pedido por número
     */
    public OrderResponseDTO getOrderByNumber(String orderNumber) {
        log.info("Buscando pedido por número: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado - Número: " + orderNumber));
        return convertToResponseDTO(order);
    }

    /**
     * Listar todos os pedidos
     */
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Listando todos os pedidos");
        return orderRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos por usuário
     */
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        log.info("Listando pedidos do usuário ID: {}", userId);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos por usuário com paginação
     */
    public Page<OrderResponseDTO> getOrdersByUserWithPagination(Long userId, Pageable pageable) {
        log.info("Listando pedidos do usuário ID: {} - Página: {}", userId, pageable.getPageNumber());
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::convertToResponseDTO);
    }

    /**
     * Listar pedidos por status
     */
    public List<OrderResponseDTO> getOrdersByStatus(String status) {
        log.info("Listando pedidos com status: {}", status);
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // ============= OPERAÇÕES DE STATUS =============

    /**
     * Atualizar status do pedido
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, UpdateOrderStatusDTO updateStatusDTO) {
        log.info("Atualizando status do pedido ID: {} para {}", id, updateStatusDTO.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado - ID: " + id));

        Order.OrderStatus newStatus = Order.OrderStatus.valueOf(updateStatusDTO.getStatus().toUpperCase());
        
        try {
            switch (newStatus) {
                case CONFIRMED:
                    order.confirm();
                    break;
                case PROCESSING:
                    order.startProcessing();
                    break;
                case SHIPPED:
                    order.ship();
                    break;
                case DELIVERED:
                    order.deliver();
                    break;
                case CANCELLED:
                    order.cancel(updateStatusDTO.getReason());
                    // Devolver produtos ao estoque
                    returnProductsToStock(order);
                    break;
                default:
                    throw new IllegalArgumentException("Status inválido: " + newStatus);
            }

            if (updateStatusDTO.getNotes() != null && !updateStatusDTO.getNotes().trim().isEmpty()) {
                order.setNotes(updateStatusDTO.getNotes());
            }

            Order updatedOrder = orderRepository.save(order);
            log.info("Status do pedido atualizado com sucesso - ID: {}, Novo status: {}", 
                    updatedOrder.getId(), updatedOrder.getStatus());

            return convertToResponseDTO(updatedOrder);

        } catch (Exception e) {
            log.error("Erro ao atualizar status do pedido ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Falha ao atualizar status: " + e.getMessage());
        }
    }

    /**
     * Confirmar pedido
     */
    @Transactional
    public OrderResponseDTO confirmOrder(Long id) {
        log.info("Confirmando pedido ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado - ID: " + id));

        order.confirm();
        Order confirmedOrder = orderRepository.save(order);

        log.info("Pedido confirmado com sucesso - ID: {}", confirmedOrder.getId());
        return convertToResponseDTO(confirmedOrder);
    }

    /**
     * Cancelar pedido
     */
    @Transactional
    public OrderResponseDTO cancelOrder(Long id, String reason) {
        log.info("Cancelando pedido ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado - ID: " + id));

        if (!order.canBeCancelled()) {
            throw new RuntimeException("Pedido não pode ser cancelado no status atual: " + order.getStatus());
        }

        order.cancel(reason);
        
        // Devolver produtos ao estoque
        returnProductsToStock(order);

        Order cancelledOrder = orderRepository.save(order);

        log.info("Pedido cancelado com sucesso - ID: {}", cancelledOrder.getId());
        return convertToResponseDTO(cancelledOrder);
    }

    // ============= OPERAÇÕES DE PAGAMENTO =============

    /**
     * Atualizar status do pagamento
     */
    @Transactional
    public OrderResponseDTO updatePaymentStatus(Long id, String paymentStatus) {
        log.info("Atualizando status de pagamento do pedido ID: {} para {}", id, paymentStatus);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado - ID: " + id));

        Order.PaymentStatus newPaymentStatus = Order.PaymentStatus.valueOf(paymentStatus.toUpperCase());
        order.updatePaymentStatus(newPaymentStatus);

        Order updatedOrder = orderRepository.save(order);

        log.info("Status de pagamento atualizado - ID: {}, Novo status: {}", 
                updatedOrder.getId(), updatedOrder.getPaymentStatus());

        return convertToResponseDTO(updatedOrder);
    }

    // ============= MÉTODOS AUXILIARES =============

    /**
     * Validar se usuário existe e está ativo
     */
    private void validateUser(Long userId) {
        try {
            log.debug("Validando usuário ID: {}", userId);
            
            var response = userServiceClient.getUserById(userId);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Usuário não encontrado - ID: " + userId);
            }

            UserResponseDTO user = response.getBody();
            if (!user.getActive()) {
                throw new RuntimeException("Usuário está inativo - ID: " + userId);
            }

            log.debug("Usuário válido: {} - {}", user.getId(), user.getName());

        } catch (FeignException e) {
            log.error("Erro ao validar usuário ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Falha na comunicação com User Service");
        }
    }

    /**
     * Validar produtos e criar itens do pedido
     */
    private List<OrderItem> validateAndCreateOrderItems(List<CreateOrderDTO.CreateOrderItemDTO> itemDTOs) {
        log.debug("Validando {} itens do pedido", itemDTOs.size());

        return itemDTOs.stream().map(itemDTO -> {
            try {
                // Buscar dados do produto
                var response = productServiceClient.getProductById(itemDTO.getProductId());
                
                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new RuntimeException("Produto não encontrado - ID: " + itemDTO.getProductId());
                }

                ProductResponseDTO product = response.getBody();
                
                // Validar se produto está ativo e disponível
                if (!product.getActive()) {
                    throw new RuntimeException("Produto inativo - ID: " + itemDTO.getProductId());
                }

                if (!product.getAvailable()) {
                    throw new RuntimeException("Produto indisponível - ID: " + itemDTO.getProductId());
                }

                // Verificar estoque
                var stockResponse = productServiceClient.checkStockAvailability(
                        itemDTO.getProductId(), itemDTO.getQuantity());
                
                if (!stockResponse.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Falha ao verificar estoque do produto - ID: " + itemDTO.getProductId());
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> stockCheck = (Map<String, Object>) stockResponse.getBody();
                Boolean available = (Boolean) stockCheck.get("available");
                
                if (!available) {
                    throw new RuntimeException("Estoque insuficiente para produto: " + product.getName() + 
                            " (Solicitado: " + itemDTO.getQuantity() + ", Disponível: " + product.getStockQuantity() + ")");
                }

                // Criar item do pedido
                OrderItem orderItem = OrderItem.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .productSku(product.getSku())
                        .unitPrice(product.getPrice())
                        .quantity(itemDTO.getQuantity())
                        .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())))
                        .productCategory(product.getCategory())
                        .productBrand(product.getBrand())
                        .productImageUrl(product.getImageUrl())
                        .notes(itemDTO.getNotes())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                log.debug("Item validado: {} - Quantidade: {}, Preço: {}", 
                        product.getName(), itemDTO.getQuantity(), orderItem.getTotalPrice());

                return orderItem;

            } catch (FeignException e) {
                log.error("Erro ao validar produto ID {}: {}", itemDTO.getProductId(), e.getMessage());
                throw new RuntimeException("Falha na comunicação com Product Service");
            }
        }).toList();
    }

    /**
     * Reservar estoque dos produtos
     */
    private void reserveProductStock(List<OrderItem> items) {
        log.debug("Reservando estoque para {} itens", items.size());

        for (OrderItem item : items) {
            try {
                UpdateStockDTO stockUpdate = UpdateStockDTO.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .operation(UpdateStockDTO.StockOperation.REDUCE)
                        .build();

                var response = productServiceClient.updateStock(stockUpdate);
                
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Falha ao reservar estoque para produto ID: " + item.getProductId());
                }

                log.debug("Estoque reservado para produto ID: {} - Quantidade: {}", 
                        item.getProductId(), item.getQuantity());

            } catch (FeignException e) {
                log.error("Erro ao reservar estoque do produto ID {}: {}", item.getProductId(), e.getMessage());
                throw new RuntimeException("Falha ao reservar estoque - Product Service indisponível");
            }
        }
    }

    /**
     * Devolver produtos ao estoque (em caso de cancelamento)
     */
    private void returnProductsToStock(Order order) {
        log.debug("Devolvendo produtos ao estoque para pedido ID: {}", order.getId());

        for (OrderItem item : order.getItems()) {
            try {
                UpdateStockDTO stockUpdate = UpdateStockDTO.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .operation(UpdateStockDTO.StockOperation.INCREASE)
                        .build();

                var response = productServiceClient.updateStock(stockUpdate);
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.debug("Estoque devolvido para produto ID: {} - Quantidade: {}", 
                            item.getProductId(), item.getQuantity());
                } else {
                    log.warn("Falha ao devolver estoque para produto ID: {}", item.getProductId());
                }

            } catch (FeignException e) {
                log.error("Erro ao devolver estoque do produto ID {}: {}", item.getProductId(), e.getMessage());
                // Não falhar a operação de cancelamento por falha na devolução de estoque
            }
        }
    }

    /**
     * Gerar número único do pedido
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String orderNumber = "ORD" + timestamp + uuid;
        
        // Garantir que o número é único
        while (orderRepository.existsByOrderNumber(orderNumber)) {
            uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            orderNumber = "ORD" + timestamp + uuid;
        }
        
        return orderNumber;
    }

    // Continua na próxima parte...

    // ============= OPERAÇÕES DE CONSULTA =============

    /**
     * Listar pedidos pendentes
     */
    public List<OrderResponseDTO> getPendingOrders() {
        log.info("Listando pedidos pendentes");
        return orderRepository.findPendingOrders().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos confirmados
     */
    public List<OrderResponseDTO> getConfirmedOrders() {
        log.info("Listando pedidos confirmados");
        return orderRepository.findConfirmedOrders().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos em processamento
     */
    public List<OrderResponseDTO> getProcessingOrders() {
        log.info("Listando pedidos em processamento");
        return orderRepository.findProcessingOrders().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos enviados
     */
    public List<OrderResponseDTO> getShippedOrders() {
        log.info("Listando pedidos enviados");
        return orderRepository.findShippedOrders().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos entregues
     */
    public List<OrderResponseDTO> getDeliveredOrders() {
        log.info("Listando pedidos entregues");
        return orderRepository.findDeliveredOrders().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos cancelados
     */
    public List<OrderResponseDTO> getCancelledOrders() {
        log.info("Listando pedidos cancelados");
        return orderRepository.findCancelledOrders().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar pedidos de hoje
     */
    public List<OrderResponseDTO> getTodaysOrders() {
        log.info("Listando pedidos de hoje");
        return orderRepository.findTodaysOrders().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Buscar pedidos por período
     */
    public List<OrderResponseDTO> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Buscando pedidos por período: {} a {}", startDate, endDate);
        return orderRepository.findOrdersByDateRange(startDate, endDate).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Buscar pedidos com entrega atrasada
     */
    public List<OrderResponseDTO> getOverdueDeliveries() {
        log.info("Listando pedidos com entrega atrasada");
        return orderRepository.findOverdueDeliveries().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // ============= OPERAÇÕES ESTATÍSTICAS =============

    /**
     * Estatísticas de pedidos
     */
    public Map<String, Object> getOrderStatistics() {
        log.info("Gerando estatísticas de pedidos");

        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByStatus(Order.OrderStatus.CONFIRMED);
        long processingOrders = orderRepository.countByStatus(Order.OrderStatus.PROCESSING);
        long shippedOrders = orderRepository.countByStatus(Order.OrderStatus.SHIPPED);
        long deliveredOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);

        BigDecimal todaysSales = orderRepository.getTodaysTotalSales();
        if (todaysSales == null) todaysSales = BigDecimal.ZERO;

        BigDecimal averageOrderValue = orderRepository.getAverageOrderValue();
        if (averageOrderValue == null) averageOrderValue = BigDecimal.ZERO;

        return Map.of(
                "total_orders", totalOrders,
                "pending_orders", pendingOrders,
                "confirmed_orders", confirmedOrders,
                "processing_orders", processingOrders,
                "shipped_orders", shippedOrders,
                "delivered_orders", deliveredOrders,
                "cancelled_orders", cancelledOrders,
                "todays_total_sales", todaysSales,
                "average_order_value", averageOrderValue,
                "todays_orders_count", orderRepository.countTodaysOrders()
        );
    }

    /**
     * Vendas por período
     */
    public BigDecimal getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculando vendas por período: {} a {}", startDate, endDate);
        BigDecimal sales = orderRepository.getTotalSalesByDateRange(startDate, endDate);
        return sales != null ? sales : BigDecimal.ZERO;
    }

    // ============= MÉTODO DE CONVERSÃO =============

    /**
     * Converter entidade para DTO de resposta
     */
    private OrderResponseDTO convertToResponseDTO(Order order) {
        List<OrderResponseDTO.OrderItemResponseDTO> itemDTOs = order.getItems().stream()
                .map(this::convertItemToResponseDTO)
                .toList();

        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().getCode())
                .statusDescription(order.getStatus().getDescription())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus().getCode())
                .paymentStatusDescription(order.getPaymentStatus().getDescription())
                .notes(order.getNotes())
                .estimatedDelivery(order.getEstimatedDelivery())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .cancellationReason(order.getCancellationReason())
                .items(itemDTOs)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .totalItems(order.getTotalItems())
                .canBeCancelled(order.canBeCancelled())
                .isFinalized(order.isFinalized())
                .build();
    }

    /**
     * Converter item para DTO de resposta
     */
    private OrderResponseDTO.OrderItemResponseDTO convertItemToResponseDTO(OrderItem item) {
        return OrderResponseDTO.OrderItemResponseDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .productCategory(item.getProductCategory())
                .productBrand(item.getProductBrand())
                .productImageUrl(item.getProductImageUrl())
                .notes(item.getNotes())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
