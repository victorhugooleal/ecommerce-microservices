package com.ecommerce.productservice.config;

import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Loader para criar produtos de teste
 * Executa na inicialização da aplicação
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        loadTestProducts();
    }

    /**
     * Carrega produtos de teste se o banco estiver vazio
     */
    private void loadTestProducts() {
        if (productRepository.count() > 0) {
            log.info("Produtos já existem no banco de dados. Pulando criação de dados de teste.");
            return;
        }

        log.info("Criando produtos de teste...");

        try {
            createElectronicsProducts();
            createClothingProducts();
            createBookProducts();
            createSportsProducts();
            createHomeProducts();

            log.info("🎯 Produtos de teste criados com sucesso!");
            log.info("📊 Total de produtos criados: {}", productRepository.count());

        } catch (Exception e) {
            log.error("❌ Erro ao criar produtos de teste: {}", e.getMessage());
        }
    }

    private void createElectronicsProducts() {
        // Smartphone
        Product smartphone = Product.builder()
                .name("Smartphone Galaxy Pro")
                .description("Smartphone com tela de 6.5 polegadas, 128GB, câmera tripla")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(50)
                .category("Eletrônicos")
                .brand("Samsung")
                .sku("PHONE-001")
                .weight(new BigDecimal("0.18"))
                .dimensions("15.9x7.4x0.7cm")
                .imageUrl("https://example.com/images/smartphone.jpg")
                .active(true)
                .featured(true)
                .minStockLevel(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Notebook
        Product notebook = Product.builder()
                .name("Notebook Dell Inspiron")
                .description("Notebook Intel i7, 16GB RAM, SSD 512GB, Windows 11")
                .price(new BigDecimal("2899.99"))
                .stockQuantity(25)
                .category("Eletrônicos")
                .brand("Dell")
                .sku("LAPTOP-001")
                .weight(new BigDecimal("2.1"))
                .dimensions("35.8x24.2x1.8cm")
                .imageUrl("https://example.com/images/notebook.jpg")
                .active(true)
                .featured(true)
                .minStockLevel(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Fone Bluetooth
        Product headphones = Product.builder()
                .name("Fone Bluetooth Premium")
                .description("Fone de ouvido wireless com cancelamento de ruído")
                .price(new BigDecimal("299.99"))
                .stockQuantity(100)
                .category("Eletrônicos")
                .brand("Sony")
                .sku("HEADPHONE-001")
                .weight(new BigDecimal("0.25"))
                .dimensions("18x16x8cm")
                .imageUrl("https://example.com/images/headphones.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(20)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(smartphone);
        productRepository.save(notebook);
        productRepository.save(headphones);
        
        log.info("✅ Produtos de eletrônicos criados");
    }

    private void createClothingProducts() {
        // Camiseta
        Product tshirt = Product.builder()
                .name("Camiseta Básica Algodão")
                .description("Camiseta 100% algodão, disponível em várias cores")
                .price(new BigDecimal("39.90"))
                .stockQuantity(200)
                .category("Roupas")
                .brand("Nike")
                .sku("SHIRT-001")
                .weight(new BigDecimal("0.15"))
                .dimensions("M")
                .imageUrl("https://example.com/images/tshirt.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(50)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Jeans
        Product jeans = Product.builder()
                .name("Calça Jeans Skinny")
                .description("Calça jeans modelo skinny, cor azul escuro")
                .price(new BigDecimal("89.90"))
                .stockQuantity(80)
                .category("Roupas")
                .brand("Levi's")
                .sku("JEANS-001")
                .weight(new BigDecimal("0.6"))
                .dimensions("38")
                .imageUrl("https://example.com/images/jeans.jpg")
                .active(true)
                .featured(true)
                .minStockLevel(15)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Tênis
        Product sneakers = Product.builder()
                .name("Tênis Esportivo Air Max")
                .description("Tênis para corrida com tecnologia de amortecimento")
                .price(new BigDecimal("249.90"))
                .stockQuantity(60)
                .category("Calçados")
                .brand("Nike")
                .sku("SNEAKER-001")
                .weight(new BigDecimal("0.4"))
                .dimensions("42")
                .imageUrl("https://example.com/images/sneakers.jpg")
                .active(true)
                .featured(true)
                .minStockLevel(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(tshirt);
        productRepository.save(jeans);
        productRepository.save(sneakers);
        
        log.info("✅ Produtos de roupas e calçados criados");
    }

    private void createBookProducts() {
        // Livro Técnico
        Product techBook = Product.builder()
                .name("Clean Code: Código Limpo")
                .description("Livro sobre boas práticas de programação")
                .price(new BigDecimal("79.90"))
                .stockQuantity(30)
                .category("Livros")
                .brand("Alta Books")
                .sku("BOOK-001")
                .weight(new BigDecimal("0.5"))
                .dimensions("23x16x3cm")
                .imageUrl("https://example.com/images/cleancode.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Romance
        Product novel = Product.builder()
                .name("Dom Casmurro")
                .description("Clássico da literatura brasileira")
                .price(new BigDecimal("24.90"))
                .stockQuantity(150)
                .category("Livros")
                .brand("Editora Globo")
                .sku("BOOK-002")
                .weight(new BigDecimal("0.3"))
                .dimensions("21x14x2cm")
                .imageUrl("https://example.com/images/domcasmurro.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(20)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(techBook);
        productRepository.save(novel);
        
        log.info("✅ Livros criados");
    }

    private void createSportsProducts() {
        // Bicicleta
        Product bike = Product.builder()
                .name("Bicicleta Mountain Bike 21V")
                .description("Bicicleta para trilha com 21 marchas")
                .price(new BigDecimal("899.90"))
                .stockQuantity(15)
                .category("Esportes")
                .brand("Caloi")
                .sku("BIKE-001")
                .weight(new BigDecimal("15.5"))
                .dimensions("Aro 29")
                .imageUrl("https://example.com/images/bike.jpg")
                .active(true)
                .featured(true)
                .minStockLevel(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Bola de Futebol
        Product soccerBall = Product.builder()
                .name("Bola de Futebol Oficial")
                .description("Bola oficial para jogos profissionais")
                .price(new BigDecimal("89.90"))
                .stockQuantity(40)
                .category("Esportes")
                .brand("Adidas")
                .sku("BALL-001")
                .weight(new BigDecimal("0.43"))
                .dimensions("Tamanho 5")
                .imageUrl("https://example.com/images/soccerball.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(bike);
        productRepository.save(soccerBall);
        
        log.info("✅ Produtos esportivos criados");
    }

    private void createHomeProducts() {
        // Cafeteira
        Product coffeeMaker = Product.builder()
                .name("Cafeteira Elétrica Programável")
                .description("Cafeteira com timer e aquecedor automático")
                .price(new BigDecimal("159.90"))
                .stockQuantity(35)
                .category("Casa e Cozinha")
                .brand("Philips")
                .sku("COFFEE-001")
                .weight(new BigDecimal("1.8"))
                .dimensions("25x18x35cm")
                .imageUrl("https://example.com/images/coffeemaker.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(8)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Aspirador
        Product vacuum = Product.builder()
                .name("Aspirador de Pó Sem Fio")
                .description("Aspirador portátil com bateria de longa duração")
                .price(new BigDecimal("299.90"))
                .stockQuantity(20)
                .category("Casa e Cozinha")
                .brand("Electrolux")
                .sku("VACUUM-001")
                .weight(new BigDecimal("2.5"))
                .dimensions("110x25x20cm")
                .imageUrl("https://example.com/images/vacuum.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Produto com estoque baixo para teste
        Product lowStockItem = Product.builder()
                .name("Panela Antiaderente Premium")
                .description("Conjunto de panelas antiaderentes")
                .price(new BigDecimal("199.90"))
                .stockQuantity(2) // Estoque baixo
                .category("Casa e Cozinha")
                .brand("Tramontina")
                .sku("PAN-001")
                .weight(new BigDecimal("3.2"))
                .dimensions("Kit 5 peças")
                .imageUrl("https://example.com/images/pans.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(5) // Min 5, atual 2 = estoque baixo
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Produto sem estoque para teste
        Product outOfStock = Product.builder()
                .name("Micro-ondas Digital")
                .description("Micro-ondas com painel digital e 10 programas")
                .price(new BigDecimal("449.90"))
                .stockQuantity(0) // Sem estoque
                .category("Casa e Cozinha")
                .brand("LG")
                .sku("MICROWAVE-001")
                .weight(new BigDecimal("12.5"))
                .dimensions("51x38x30cm")
                .imageUrl("https://example.com/images/microwave.jpg")
                .active(true)
                .featured(false)
                .minStockLevel(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productRepository.save(coffeeMaker);
        productRepository.save(vacuum);
        productRepository.save(lowStockItem);
        productRepository.save(outOfStock);
        
        log.info("✅ Produtos para casa criados (incluindo testes de estoque)");
    }
}
