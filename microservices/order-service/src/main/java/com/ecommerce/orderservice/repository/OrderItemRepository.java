package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para OrderItem
 * Contém queries para análise de itens de pedidos
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // ============= QUERIES BÁSICAS =============

    /**
     * Buscar itens por pedido
     */
    List<OrderItem> findByOrderIdOrderByCreatedAtAsc(Long orderId);

    /**
     * Buscar itens por produto
     */
    List<OrderItem> findByProductIdOrderByCreatedAtDesc(Long productId);

    /**
     * Buscar itens por produto e período
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.productId = :productId AND oi.createdAt BETWEEN :startDate AND :endDate ORDER BY oi.createdAt DESC")
    List<OrderItem> findByProductIdAndDateRange(@Param("productId") Long productId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // ============= QUERIES ESTATÍSTICAS =============

    /**
     * Contar itens por produto
     */
    long countByProductId(Long productId);

    /**
     * Soma de quantidades vendidas por produto
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    Long getTotalQuantitySoldByProduct(@Param("productId") Long productId);

    /**
     * Soma de vendas por produto
     */
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.productId = :productId")
    BigDecimal getTotalSalesByProduct(@Param("productId") Long productId);

    /**
     * Produtos mais vendidos (por quantidade)
     */
    @Query("SELECT oi.productId, oi.productName, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productId, oi.productName " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingProductsByQuantity();

    /**
     * Produtos que mais geraram receita
     */
    @Query("SELECT oi.productId, oi.productName, SUM(oi.totalPrice) as totalRevenue " +
           "FROM OrderItem oi " +
           "GROUP BY oi.productId, oi.productName " +
           "ORDER BY totalRevenue DESC")
    List<Object[]> findTopSellingProductsByRevenue();

    /**
     * Produtos vendidos por categoria
     */
    @Query("SELECT oi.productCategory, COUNT(oi), SUM(oi.quantity), SUM(oi.totalPrice) " +
           "FROM OrderItem oi " +
           "WHERE oi.productCategory IS NOT NULL " +
           "GROUP BY oi.productCategory " +
           "ORDER BY SUM(oi.totalPrice) DESC")
    List<Object[]> getSalesByCategory();

    /**
     * Produtos vendidos por marca
     */
    @Query("SELECT oi.productBrand, COUNT(oi), SUM(oi.quantity), SUM(oi.totalPrice) " +
           "FROM OrderItem oi " +
           "WHERE oi.productBrand IS NOT NULL " +
           "GROUP BY oi.productBrand " +
           "ORDER BY SUM(oi.totalPrice) DESC")
    List<Object[]> getSalesByBrand();

    // ============= QUERIES POR PERÍODO =============

    /**
     * Itens vendidos hoje
     */
    @Query("SELECT oi FROM OrderItem oi WHERE DATE(oi.createdAt) = CURRENT_DATE ORDER BY oi.createdAt DESC")
    List<OrderItem> findTodaysItems();

    /**
     * Itens vendidos no período
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.createdAt BETWEEN :startDate AND :endDate ORDER BY oi.createdAt DESC")
    List<OrderItem> findItemsByDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Total de receita no período
     */
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // ============= QUERIES PARA RELATÓRIOS =============

    /**
     * Itens mais caros vendidos
     */
    List<OrderItem> findTop10ByOrderByTotalPriceDesc();

    /**
     * Últimos itens vendidos
     */
    List<OrderItem> findTop20ByOrderByCreatedAtDesc();

    /**
     * Buscar itens por faixa de preço
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.totalPrice BETWEEN :minPrice AND :maxPrice ORDER BY oi.totalPrice DESC")
    List<OrderItem> findItemsByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice);
}
