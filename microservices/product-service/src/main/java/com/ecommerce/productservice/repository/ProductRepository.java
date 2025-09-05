package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Product
 * Contém queries customizadas para busca de produtos
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ============= QUERIES BÁSICAS =============

    /**
     * Buscar produto por SKU
     */
    Optional<Product> findBySku(String sku);

    /**
     * Verificar se SKU existe
     */
    boolean existsBySku(String sku);

    /**
     * Buscar produtos ativos
     */
    List<Product> findByActiveTrue();

    /**
     * Buscar produtos inativos
     */
    List<Product> findByActiveFalse();

    /**
     * Buscar produtos em destaque
     */
    List<Product> findByFeaturedTrueAndActiveTrue();

    // ============= QUERIES POR CATEGORIA =============

    /**
     * Buscar produtos por categoria
     */
    List<Product> findByCategoryIgnoreCaseAndActiveTrue(String category);

    /**
     * Buscar produtos por categoria com paginação
     */
    Page<Product> findByCategoryIgnoreCaseAndActiveTrue(String category, Pageable pageable);

    /**
     * Buscar todas as categorias ativas
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true AND p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctActiveCategories();

    // ============= QUERIES POR MARCA =============

    /**
     * Buscar produtos por marca
     */
    List<Product> findByBrandIgnoreCaseAndActiveTrue(String brand);

    /**
     * Buscar todas as marcas ativas
     */
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true AND p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findDistinctActiveBrands();

    // ============= QUERIES POR PREÇO =============

    /**
     * Buscar produtos por faixa de preço
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price")
    List<Product> findByPriceRangeAndActiveTrue(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    /**
     * Buscar produtos por preço máximo
     */
    List<Product> findByPriceLessThanEqualAndActiveTrueOrderByPriceAsc(BigDecimal maxPrice);

    /**
     * Buscar produtos por preço mínimo
     */
    List<Product> findByPriceGreaterThanEqualAndActiveTrueOrderByPriceAsc(BigDecimal minPrice);

    // ============= QUERIES POR ESTOQUE =============

    /**
     * Buscar produtos disponíveis (com estoque > 0)
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity > 0")
    List<Product> findAvailableProducts();

    /**
     * Buscar produtos com estoque baixo
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.minStockLevel IS NOT NULL AND p.stockQuantity <= p.minStockLevel")
    List<Product> findLowStockProducts();

    /**
     * Buscar produtos sem estoque
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();

    /**
     * Buscar produtos por quantidade mínima em estoque
     */
    List<Product> findByStockQuantityGreaterThanEqualAndActiveTrueOrderByStockQuantityDesc(Integer minStock);

    // ============= QUERIES DE BUSCA TEXTUAL =============

    /**
     * Buscar produtos por nome (like)
     */
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    /**
     * Buscar produtos por descrição (like)
     */
    List<Product> findByDescriptionContainingIgnoreCaseAndActiveTrue(String description);

    /**
     * Busca textual completa (nome, descrição, categoria, marca)
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(UPPER(p.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(p.description) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(p.category) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(p.brand) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchProducts(@Param("searchTerm") String searchTerm);

    /**
     * Busca textual com paginação
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(UPPER(p.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(p.description) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(p.category) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(p.brand) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    // ============= QUERIES ESTATÍSTICAS =============

    /**
     * Contar produtos ativos
     */
    long countByActiveTrue();

    /**
     * Contar produtos por categoria
     */
    long countByCategoryIgnoreCaseAndActiveTrue(String category);

    /**
     * Contar produtos em destaque
     */
    long countByFeaturedTrueAndActiveTrue();

    /**
     * Valor total do estoque
     */
    @Query("SELECT SUM(p.price * p.stockQuantity) FROM Product p WHERE p.active = true")
    BigDecimal getTotalStockValue();

    // ============= QUERIES PARA RELATÓRIOS =============

    /**
     * Produtos mais caros
     */
    List<Product> findTop10ByActiveTrueOrderByPriceDesc();

    /**
     * Produtos mais baratos  
     */
    List<Product> findTop10ByActiveTrueOrderByPriceAsc();

    /**
     * Produtos adicionados recentemente
     */
    List<Product> findTop10ByActiveTrueOrderByCreatedAtDesc();
}
