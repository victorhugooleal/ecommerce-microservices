package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositório para a entidade Product
 * Este repositório será movido para o microsserviço de produtos
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca produtos ativos
     */
    List<Product> findByIsActiveTrue();

    /**
     * Busca produtos por categoria
     */
    List<Product> findByCategoryAndIsActiveTrue(String category);

    /**
     * Busca produtos por nome (contendo)
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);

    /**
     * Busca produtos por faixa de preço
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceBetweenAndIsActiveTrue(@Param("minPrice") BigDecimal minPrice, 
                                                   @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Busca produtos com estoque disponível
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stockQuantity > 0")
    List<Product> findAvailableProducts();

    /**
     * Busca produtos com estoque baixo
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.stockQuantity <= :threshold")
    List<Product> findProductsWithLowStock(@Param("threshold") Integer threshold);

    /**
     * Busca todas as categorias distintas
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true")
    List<String> findDistinctCategories();

    /**
     * Verifica se produto existe e está ativo
     */
    boolean existsByIdAndIsActiveTrue(Long id);
}
