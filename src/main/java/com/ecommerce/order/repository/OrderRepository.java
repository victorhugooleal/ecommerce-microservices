package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import com.ecommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Order
 * Este repositório será movido para o microsserviço de pedidos
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Busca pedidos por usuário
     */
    List<Order> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Busca pedidos por usuário e status
     */
    List<Order> findByUserAndStatus(User user, Order.OrderStatus status);

    /**
     * Busca pedidos por status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Busca pedidos por período
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Busca pedidos por usuário e período
     */
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<Order> findByUserAndCreatedAtBetween(@Param("user") User user, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);

    /**
     * Busca último pedido do usuário
     */
    Optional<Order> findFirstByUserOrderByCreatedAtDesc(User user);

    /**
     * Conta pedidos por status
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Busca pedidos que podem ser cancelados
     */
    @Query("SELECT o FROM Order o WHERE o.status NOT IN ('DELIVERED', 'CANCELLED')")
    List<Order> findCancellableOrders();

    /**
     * Busca pedidos pendentes há mais de X horas
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :cutoffTime")
    List<Order> findPendingOrdersOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Busca pedidos para processamento (confirmados)
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'CONFIRMED' ORDER BY o.createdAt ASC")
    List<Order> findOrdersForProcessing();
}
