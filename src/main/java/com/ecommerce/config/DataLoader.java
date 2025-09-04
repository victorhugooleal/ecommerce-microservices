package com.ecommerce.config;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Carrega dados de teste na aplicação
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadTestData();
    }

    private void loadTestData() {
        // Usuários de teste
        if (userRepository.count() == 0) {
            // Admin
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@ecommerce.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);

            // Usuário comum
            User user = new User();
            user.setName("João Silva");
            user.setEmail("joao@email.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(User.Role.USER);
            userRepository.save(user);

            System.out.println("✅ Usuários de teste criados:");
            System.out.println("   Admin: admin@ecommerce.com / admin123");
            System.out.println("   User:  joao@email.com / user123");
        }

        // Produtos de teste
        if (productRepository.count() == 0) {
            Product[] products = {
                new Product("Smartphone Samsung Galaxy S23", "Smartphone Android com 128GB", 
                           new BigDecimal("2499.99"), 50, "Eletrônicos"),
                new Product("Notebook Dell Inspiron", "Notebook i7 16GB RAM 512GB SSD", 
                           new BigDecimal("3499.99"), 20, "Eletrônicos"),
                new Product("Camiseta Nike Dri-FIT", "Camiseta esportiva masculina", 
                           new BigDecimal("89.99"), 100, "Roupas"),
                new Product("Tênis Adidas Ultraboost", "Tênis para corrida masculino", 
                           new BigDecimal("599.99"), 30, "Calçados"),
                new Product("Livro Java: Como Programar", "Livro de programação Java", 
                           new BigDecimal("129.99"), 25, "Livros"),
                new Product("Cafeteira Nespresso", "Máquina de café expresso", 
                           new BigDecimal("299.99"), 15, "Casa & Cozinha"),
                new Product("Monitor LG 24\"", "Monitor Full HD 24 polegadas", 
                           new BigDecimal("799.99"), 40, "Eletrônicos"),
                new Product("Fone JBL Tune 510BT", "Fone de ouvido Bluetooth", 
                           new BigDecimal("199.99"), 60, "Eletrônicos"),
                new Product("Mochila Nike Brasilia", "Mochila esportiva 20L", 
                           new BigDecimal("149.99"), 80, "Acessórios"),
                new Product("Relógio Apple Watch SE", "Smartwatch Apple Watch", 
                           new BigDecimal("1299.99"), 10, "Eletrônicos")
            };

            for (Product product : products) {
                productRepository.save(product);
            }

            System.out.println("✅ " + products.length + " produtos de teste criados");
        }
    }
}
