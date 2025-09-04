package com.ecommerce.product.service;

import com.ecommerce.product.dto.CreateProductDTO;
import com.ecommerce.product.dto.ProductResponseDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço de produtos
 * Este serviço será movido para o microsserviço de produtos
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Cria um novo produto
     */
    public ProductResponseDTO createProduct(CreateProductDTO createProductDTO) {
        Product product = new Product();
        product.setName(createProductDTO.getName());
        product.setDescription(createProductDTO.getDescription());
        product.setPrice(createProductDTO.getPrice());
        product.setStockQuantity(createProductDTO.getStockQuantity());
        product.setCategory(createProductDTO.getCategory());
        product.setImageUrl(createProductDTO.getImageUrl());

        Product savedProduct = productRepository.save(product);
        return new ProductResponseDTO(savedProduct);
    }

    /**
     * Busca produto por ID
     */
    @Transactional(readOnly = true)
    public Optional<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getIsActive)
                .map(ProductResponseDTO::new);
    }

    /**
     * Lista todos os produtos ativos
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllActiveProducts() {
        return productRepository.findByIsActiveTrue()
                .stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca produtos por categoria
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndIsActiveTrue(category)
                .stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca produtos por nome
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name)
                .stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca produtos por faixa de preço
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetweenAndIsActiveTrue(minPrice, maxPrice)
                .stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza produto
     */
    public ProductResponseDTO updateProduct(Long id, CreateProductDTO updateProductDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setName(updateProductDTO.getName());
        product.setDescription(updateProductDTO.getDescription());
        product.setPrice(updateProductDTO.getPrice());
        product.setStockQuantity(updateProductDTO.getStockQuantity());
        product.setCategory(updateProductDTO.getCategory());
        product.setImageUrl(updateProductDTO.getImageUrl());

        Product updatedProduct = productRepository.save(product);
        return new ProductResponseDTO(updatedProduct);
    }

    /**
     * Desativa produto (soft delete)
     */
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        product.setIsActive(false);
        productRepository.save(product);
    }

    /**
     * Atualiza estoque do produto
     */
    public ProductResponseDTO updateStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.setStockQuantity(newStock);
        Product updatedProduct = productRepository.save(product);
        return new ProductResponseDTO(updatedProduct);
    }

    /**
     * Reduz estoque do produto (usado no processamento de pedidos)
     */
    public void reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        product.reduceStock(quantity);
        productRepository.save(product);
    }

    /**
     * Verifica disponibilidade do produto
     */
    @Transactional(readOnly = true)
    public boolean isProductAvailable(Long productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getIsActive() && product.hasStock(quantity))
                .orElse(false);
    }

    /**
     * Lista todas as categorias
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findDistinctCategories();
    }

    /**
     * Lista produtos com estoque baixo
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductsWithLowStock(Integer threshold) {
        return productRepository.findProductsWithLowStock(threshold)
                .stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
    }
}
