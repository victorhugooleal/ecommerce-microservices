package com.ecommerce.order.service;

import com.ecommerce.order.dto.CreateOrderDTO;
import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponseDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço de pedidos
 * Este serviço será movido para o microsserviço de pedidos
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    /**
     * Cria um novo pedido
     */
    public OrderResponseDTO createOrder(String userEmail, CreateOrderDTO createOrderDTO) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Valida disponibilidade dos produtos
        validateProductAvailability(createOrderDTO.getItems());

        Order order = new Order(user, createOrderDTO.getShippingAddress());
        order.setPaymentMethod(createOrderDTO.getPaymentMethod());

        // Adiciona itens ao pedido
        for (OrderItemDTO itemDTO : createOrderDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemDTO.getProductId()));

            OrderItem orderItem = new OrderItem(product, itemDTO.getQuantity());
            order.addOrderItem(orderItem);
        }

        // Salva o pedido
        Order savedOrder = orderRepository.save(order);

        // Reduz estoque dos produtos
        for (OrderItem item : savedOrder.getOrderItems()) {
            productService.reduceStock(item.getProduct().getId(), item.getQuantity());
        }

        return new OrderResponseDTO(savedOrder);
    }

    /**
     * Busca pedido por ID
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponseDTO> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponseDTO::new);
    }

    /**
     * Busca pedidos do usuário
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os pedidos (admin)
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca pedidos por status
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Confirma pedido
     */
    public OrderResponseDTO confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.confirm();
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDTO(updatedOrder);
    }

    /**
     * Processa pedido
     */
    public OrderResponseDTO processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.process();
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDTO(updatedOrder);
    }

    /**
     * Envia pedido
     */
    public OrderResponseDTO shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.ship();
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDTO(updatedOrder);
    }

    /**
     * Entrega pedido
     */
    public OrderResponseDTO deliverOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.deliver();
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDTO(updatedOrder);
    }

    /**
     * Cancela pedido
     */
    public OrderResponseDTO cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (!order.canBeCancelled()) {
            throw new RuntimeException("Este pedido não pode ser cancelado");
        }

        // Restaura estoque dos produtos
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }

        order.cancel(reason);
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDTO(updatedOrder);
    }

    /**
     * Atualiza status de pagamento
     */
    public OrderResponseDTO updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setPaymentStatus(paymentStatus);
        Order updatedOrder = orderRepository.save(order);
        return new OrderResponseDTO(updatedOrder);
    }

    /**
     * Valida disponibilidade dos produtos
     */
    private void validateProductAvailability(List<OrderItemDTO> items) {
        for (OrderItemDTO item : items) {
            if (!productService.isProductAvailable(item.getProductId(), item.getQuantity())) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
                throw new RuntimeException("Produto indisponível ou com estoque insuficiente: " + product.getName());
            }
        }
    }

    /**
     * Busca pedidos pendentes há mais de X horas
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getPendingOrdersOlderThan(int hours) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        return orderRepository.findPendingOrdersOlderThan(cutoffTime)
                .stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca pedidos para processamento
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersForProcessing() {
        return orderRepository.findOrdersForProcessing()
                .stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }
}
