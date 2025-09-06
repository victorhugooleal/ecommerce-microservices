package com.ecommerce.orderservice.config;

import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DataLoader para carregar dados de teste de pedidos
 * 
 * Cria pedidos de exemplo para demonstrar o funcionamento do sistema
 * Inclui pedidos com diferentes status e situações
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "test"}) // Só executa em ambientes de desenvolvimento e teste
public class OrderDataLoader implements CommandLineRunner {

    private final OrderService orderService;

    @Override
    public void run(String... args) throws Exception {
        if (shouldLoadData()) {
            log.info("🔄 Carregando dados de teste de pedidos...");
            loadOrderData();
            log.info("✅ Dados de teste de pedidos carregados com sucesso!");
        }
    }

    /**
     * Verifica se deve carregar os dados (se não existem pedidos)
     */
    private boolean shouldLoadData() {
        try {
            return orderService.findAll().isEmpty();
        } catch (Exception e) {
            log.warn("Erro ao verificar pedidos existentes: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Carrega dados de teste de pedidos
     */
    private void loadOrderData() {
        try {
            // Pedidos do usuário 1 (admin@example.com)
            createUserOrders();

            // Pedidos do usuário 2 (user@example.com)  
            createUser2Orders();

            // Pedidos do usuário 3 (manager@example.com)
            createUser3Orders();

            log.info("📦 Total de pedidos de teste criados: {}", orderService.findAll().size());
            
        } catch (Exception e) {
            log.error("❌ Erro ao carregar dados de pedidos: {}", e.getMessage(), e);
        }
    }

    /**
     * Cria pedidos para o usuário 1 (Admin)
     */
    private void createUserOrders() {
        // Pedido 1: Pedido grande com múltiplos produtos
        CreateOrderDTO order1 = CreateOrderDTO.builder()
                .userId(1L)
                .shippingAddress("Rua das Flores, 123 - Centro - São Paulo/SP - CEP: 01234-567")
                .paymentMethod("CREDIT_CARD")
                .notes("Pedido de inauguração da loja")
                .items(List.of(
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(1L)
                                .quantity(2)
                                .notes("Cor: Preto")
                                .build(),
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(2L)
                                .quantity(1)
                                .notes("iPhone 14 Pro Max")
                                .build(),
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(3L)
                                .quantity(3)
                                .notes("Camiseta básica")
                                .build()
                ))
                .build();

        // Pedido 2: Pedido médio com produtos eletrônicos
        CreateOrderDTO order2 = CreateOrderDTO.builder()
                .userId(1L)
                .shippingAddress("Av. Paulista, 1000 - Bela Vista - São Paulo/SP - CEP: 01310-100")
                .paymentMethod("PIX")
                .notes("Entrega rápida solicitada")
                .items(List.of(
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(4L)
                                .quantity(1)
                                .notes("Dell Inspiron 15")
                                .build(),
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(5L)
                                .quantity(2)
                                .notes("Livro técnico")
                                .build()
                ))
                .build();

        try {
            orderService.createOrder(order1);
            log.info("✅ Pedido 1 do usuário 1 criado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao criar pedido 1: {}", e.getMessage());
        }

        try {
            orderService.createOrder(order2);
            log.info("✅ Pedido 2 do usuário 1 criado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao criar pedido 2: {}", e.getMessage());
        }
    }

    /**
     * Cria pedidos para o usuário 2
     */
    private void createUser2Orders() {
        // Pedido 3: Pedido de roupas
        CreateOrderDTO order3 = CreateOrderDTO.builder()
                .userId(2L)
                .shippingAddress("Rua Augusta, 500 - Consolação - São Paulo/SP - CEP: 01305-000")
                .paymentMethod("DEBIT_CARD")
                .notes("Presente de aniversário")
                .items(List.of(
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(3L)
                                .quantity(5)
                                .notes("Tamanhos variados")
                                .build(),
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(6L)
                                .quantity(2)
                                .notes("Calça jeans")
                                .build()
                ))
                .build();

        // Pedido 4: Pedido de livros
        CreateOrderDTO order4 = CreateOrderDTO.builder()
                .userId(2L)
                .shippingAddress("Rua Augusta, 500 - Consolação - São Paulo/SP - CEP: 01305-000")
                .paymentMethod("BOLETO")
                .notes("Material de estudo")
                .items(List.of(
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(5L)
                                .quantity(3)
                                .notes("Coleção completa")
                                .build(),
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(7L)
                                .quantity(1)
                                .notes("Tênis de corrida")
                                .build()
                ))
                .build();

        try {
            orderService.createOrder(order3);
            log.info("✅ Pedido 3 do usuário 2 criado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao criar pedido 3: {}", e.getMessage());
        }

        try {
            orderService.createOrder(order4);
            log.info("✅ Pedido 4 do usuário 2 criado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao criar pedido 4: {}", e.getMessage());
        }
    }

    /**
     * Cria pedidos para o usuário 3
     */
    private void createUser3Orders() {
        // Pedido 5: Pedido de casa e decoração
        CreateOrderDTO order5 = CreateOrderDTO.builder()
                .userId(3L)
                .shippingAddress("Rua Oscar Freire, 200 - Jardins - São Paulo/SP - CEP: 01426-000")
                .paymentMethod("CREDIT_CARD")
                .notes("Decoração do escritório")
                .items(List.of(
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(8L)
                                .quantity(1)
                                .notes("Sofá 3 lugares")
                                .build(),
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(9L)
                                .quantity(2)
                                .notes("Panela antiaderente")
                                .build()
                ))
                .build();

        // Pedido 6: Pedido de eletrônicos
        CreateOrderDTO order6 = CreateOrderDTO.builder()
                .userId(3L)
                .shippingAddress("Rua Oscar Freire, 200 - Jardins - São Paulo/SP - CEP: 01426-000")
                .paymentMethod("PIX")
                .notes("Setup gamer")
                .items(List.of(
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(10L)
                                .quantity(1)
                                .notes("Monitor 4K")
                                .build(),
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(1L)
                                .quantity(1)
                                .notes("Notebook Dell")
                                .build()
                ))
                .build();

        // Pedido 7: Pedido pequeno
        CreateOrderDTO order7 = CreateOrderDTO.builder()
                .userId(3L)
                .shippingAddress("Rua Oscar Freire, 200 - Jardins - São Paulo/SP - CEP: 01426-000")
                .paymentMethod("CREDIT_CARD")
                .notes("Compra rápida")
                .items(List.of(
                        CreateOrderDTO.OrderItemDTO.builder()
                                .productId(11L)
                                .quantity(1)
                                .notes("Relógio smartwatch")
                                .build()
                ))
                .build();

        try {
            orderService.createOrder(order5);
            log.info("✅ Pedido 5 do usuário 3 criado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao criar pedido 5: {}", e.getMessage());
        }

        try {
            orderService.createOrder(order6);
            log.info("✅ Pedido 6 do usuário 3 criado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao criar pedido 6: {}", e.getMessage());
        }

        try {
            orderService.createOrder(order7);
            log.info("✅ Pedido 7 do usuário 3 criado com sucesso");
        } catch (Exception e) {
            log.warn("⚠️ Erro ao criar pedido 7: {}", e.getMessage());
        }
    }
}
