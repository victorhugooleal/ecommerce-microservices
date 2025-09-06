package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Order
 * Contém queries customizadas para busca de pedidos
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ============= QUERIES BÁSICAS =============

    /**
     * Buscar pedido por número
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Verificar se número do pedido existe
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Buscar pedidos por usuário
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Buscar pedidos por usuário com paginação
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // ============= QUERIES POR STATUS =============

    /**
     * Buscar pedidos por status
     */
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);

    /**
     * Buscar pedidos por status com paginação
     */
    Page<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status, Pageable pageable);

    /**
     * Buscar pedidos por usuário e status
     */
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Order.OrderStatus status);

    /**
     * Buscar pedidos pendentes
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<Order> findPendingOrders();

    /**
     * Buscar pedidos confirmados
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'CONFIRMED' ORDER BY o.createdAt ASC")
    List<Order> findConfirmedOrders();

    /**
     * Buscar pedidos em processamento
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PROCESSING' ORDER BY o.createdAt ASC")
    List<Order> findProcessingOrders();

    /**
     * Buscar pedidos enviados
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'SHIPPED' ORDER BY o.shippedAt DESC")
    List<Order> findShippedOrders();

    /**
     * Buscar pedidos entregues
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'DELIVERED' ORDER BY o.deliveredAt DESC")
    List<Order> findDeliveredOrders();

    /**
     * Buscar pedidos cancelados
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'CANCELLED' ORDER BY o.cancelledAt DESC")
    List<Order> findCancelledOrders();

    // ============= QUERIES POR STATUS DE PAGAMENTO =============

    /**
     * Buscar pedidos por status de pagamento
     */
    List<Order> findByPaymentStatusOrderByCreatedAtDesc(Order.PaymentStatus paymentStatus);

    /**
     * Buscar pedidos com pagamento pendente
     */
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PENDING' ORDER BY o.createdAt ASC")
    List<Order> findOrdersWithPendingPayment();

    /**
     * Buscar pedidos pagos
     */
    @Query("SELECT o FROM Order o WHERE o.paymentStatus = 'PAID' ORDER BY o.createdAt DESC")
    List<Order> findPaidOrders();

    // ============= QUERIES POR DATA =============

    /**
     * Buscar pedidos por período
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Buscar pedidos de hoje
     */
    @Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE ORDER BY o.createdAt DESC")
    List<Order> findTodaysOrders();

    /**
     * Buscar pedidos desta semana
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :weekStart ORDER BY o.createdAt DESC")
    List<Order> findThisWeekOrders(@Param("weekStart") LocalDateTime weekStart);

    /**
     * Buscar pedidos deste mês
     */
    @Query("SELECT o FROM Order o WHERE YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month ORDER BY o.createdAt DESC")
    List<Order> findOrdersByMonth(@Param("year") int year, @Param("month") int month);

    // ============= QUERIES POR VALOR =============

    /**
     * Buscar pedidos por faixa de valor
     */
    @Query("SELECT o FROM Order o WHERE o.totalAmount BETWEEN :minAmount AND :maxAmount ORDER BY o.totalAmount DESC")
    List<Order> findOrdersByAmountRange(@Param("minAmount") BigDecimal minAmount, 
                                       @Param("maxAmount") BigDecimal maxAmount);

    /**
     * Buscar pedidos acima de um valor
     */
    List<Order> findByTotalAmountGreaterThanEqualOrderByTotalAmountDesc(BigDecimal minAmount);

    /**
     * Buscar pedidos abaixo de um valor
     */
    List<Order> findByTotalAmountLessThanEqualOrderByTotalAmountAsc(BigDecimal maxAmount);

    // ============= QUERIES DE ENTREGAS =============

    /**
     * Buscar pedidos com entrega atrasada
     */
    @Query("SELECT o FROM Order o WHERE o.estimatedDelivery < CURRENT_TIMESTAMP AND o.status = 'SHIPPED'")
    List<Order> findOverdueDeliveries();

    /**
     * Buscar pedidos para entregar hoje
     */
    @Query("SELECT o FROM Order o WHERE DATE(o.estimatedDelivery) = CURRENT_DATE AND o.status = 'SHIPPED'")
    List<Order> findOrdersForDeliveryToday();

    /**
     * Buscar pedidos entregues no período
     */
    @Query("SELECT o FROM Order o WHERE o.deliveredAt BETWEEN :startDate AND :endDate ORDER BY o.deliveredAt DESC")
    List<Order> findDeliveredOrdersByDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    // ============= QUERIES ESTATÍSTICAS =============

    /**
     * Contar pedidos por status
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Contar pedidos por usuário
     */
    long countByUserId(Long userId);

    /**
     * Contar pedidos de hoje
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.createdAt) = CURRENT_DATE")
    long countTodaysOrders();

    /**
     * Soma total de vendas por período
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status IN ('DELIVERED', 'SHIPPED') AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalSalesByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Soma total de vendas de hoje
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status IN ('DELIVERED', 'SHIPPED') AND DATE(o.createdAt) = CURRENT_DATE")
    BigDecimal getTodaysTotalSales();

    /**
     * Valor médio dos pedidos
     */
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.status NOT IN ('CANCELLED')")
    BigDecimal getAverageOrderValue();

    // ============= QUERIES PARA RELATÓRIOS =============

    /**
     * Pedidos mais recentes
     */
    List<Order> findTop10ByOrderByCreatedAtDesc();

    /**
     * Maiores pedidos por valor
     */
    List<Order> findTop10ByStatusNotOrderByTotalAmountDesc(Order.OrderStatus excludeStatus);

    /**
     * Pedidos de um usuário específico no período
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findUserOrdersByDateRange(@Param("userId") Long userId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // ============= QUERIES PARA BUSCA TEXTUAL =============

    /**
     * Buscar pedidos por número (like)
     */
    List<Order> findByOrderNumberContainingIgnoreCaseOrderByCreatedAtDesc(String orderNumber);

    /**
     * Buscar pedidos por endereço de entrega
     */
    List<Order> findByShippingAddressContainingIgnoreCaseOrderByCreatedAtDesc(String address);

    /**
     * Buscar pedidos por observações
     */
    List<Order> findByNotesContainingIgnoreCaseOrderByCreatedAtDesc(String notes);
}
