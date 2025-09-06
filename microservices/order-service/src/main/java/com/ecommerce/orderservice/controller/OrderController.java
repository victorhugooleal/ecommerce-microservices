package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.dto.CreateOrderDTO;
import com.ecommerce.orderservice.dto.OrderResponseDTO;
import com.ecommerce.orderservice.dto.UpdateOrderStatusDTO;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller para operações de pedidos
 * 
 * Endpoints disponíveis:
 * - CRUD completo de pedidos
 * - Gerenciamento de status
 * - Consultas por filtros
 * - Relatórios e estatísticas
 * - Operações administrativas
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs para gerenciamento de pedidos")
public class OrderController {

    private final OrderService orderService;

    // ============= OPERAÇÕES BÁSICAS =============

    /**
     * Criar novo pedido
     */
    @PostMapping
    @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido com validação de usuário, produtos e estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário ou produto não encontrado"),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente")
    })
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody CreateOrderDTO createOrderDTO) {
        
        log.info("Criando pedido para usuário: {}", createOrderDTO.getUserId());
        OrderResponseDTO order = orderService.createOrder(createOrderDTO);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    /**
     * Buscar pedido por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Long id) {
        
        log.info("Buscando pedido ID: {}", id);
        OrderResponseDTO order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Buscar pedido por número
     */
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Buscar pedido por número", description = "Retorna um pedido específico pelo número")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<OrderResponseDTO> getOrderByNumber(
            @PathVariable String orderNumber) {
        
        log.info("Buscando pedido número: {}", orderNumber);
        OrderResponseDTO order = orderService.findByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    /**
     * Listar todos os pedidos
     */
    @GetMapping
    @Operation(summary = "Listar todos os pedidos", description = "Retorna lista de todos os pedidos")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        
        log.info("Listando todos os pedidos");
        List<OrderResponseDTO> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }

    /**
     * Listar pedidos por usuário
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar pedidos por usuário", description = "Retorna todos os pedidos de um usuário específico")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos do usuário")
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(
            @PathVariable Long userId) {
        
        log.info("Listando pedidos do usuário: {}", userId);
        List<OrderResponseDTO> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Deletar pedido
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pedido", description = "Remove um pedido do sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("Deletando pedido ID: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ============= GERENCIAMENTO DE STATUS =============

    /**
     * Atualizar status do pedido
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Transição de status não permitida")
    })
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusDTO updateStatusDTO) {
        
        log.info("Atualizando status do pedido {}: {}", id, updateStatusDTO.getStatus());
        OrderResponseDTO order = orderService.updateOrderStatus(id, updateStatusDTO);
        return ResponseEntity.ok(order);
    }

    /**
     * Confirmar pedido
     */
    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirmar pedido", description = "Confirma um pedido pendente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido confirmado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Pedido não pode ser confirmado")
    })
    public ResponseEntity<OrderResponseDTO> confirmOrder(@PathVariable Long id) {
        log.info("Confirmando pedido ID: {}", id);
        OrderResponseDTO order = orderService.confirmOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Processar pedido
     */
    @PutMapping("/{id}/process")
    @Operation(summary = "Processar pedido", description = "Coloca o pedido em processamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido em processamento"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Pedido não pode ser processado")
    })
    public ResponseEntity<OrderResponseDTO> processOrder(@PathVariable Long id) {
        log.info("Processando pedido ID: {}", id);
        OrderResponseDTO order = orderService.processOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Enviar pedido
     */
    @PutMapping("/{id}/ship")
    @Operation(summary = "Enviar pedido", description = "Marca o pedido como enviado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido enviado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Pedido não pode ser enviado")
    })
    public ResponseEntity<OrderResponseDTO> shipOrder(@PathVariable Long id) {
        log.info("Enviando pedido ID: {}", id);
        OrderResponseDTO order = orderService.shipOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Entregar pedido
     */
    @PutMapping("/{id}/deliver")
    @Operation(summary = "Entregar pedido", description = "Marca o pedido como entregue")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido entregue"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Pedido não pode ser entregue")
    })
    public ResponseEntity<OrderResponseDTO> deliverOrder(@PathVariable Long id) {
        log.info("Entregando pedido ID: {}", id);
        OrderResponseDTO order = orderService.deliverOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancelar pedido
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "422", description = "Pedido não pode ser cancelado")
    })
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        
        log.info("Cancelando pedido ID: {} com motivo: {}", id, reason);
        OrderResponseDTO order = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(order);
    }

    // ============= CONSULTAS POR STATUS =============

    /**
     * Listar pedidos pendentes
     */
    @GetMapping("/status/pending")
    @Operation(summary = "Listar pedidos pendentes", description = "Retorna todos os pedidos pendentes")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos pendentes")
    public ResponseEntity<List<OrderResponseDTO>> getPendingOrders() {
        List<OrderResponseDTO> orders = orderService.getPendingOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Listar pedidos confirmados
     */
    @GetMapping("/status/confirmed")
    @Operation(summary = "Listar pedidos confirmados", description = "Retorna todos os pedidos confirmados")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos confirmados")
    public ResponseEntity<List<OrderResponseDTO>> getConfirmedOrders() {
        List<OrderResponseDTO> orders = orderService.getConfirmedOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Listar pedidos em processamento
     */
    @GetMapping("/status/processing")
    @Operation(summary = "Listar pedidos em processamento", description = "Retorna todos os pedidos em processamento")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos em processamento")
    public ResponseEntity<List<OrderResponseDTO>> getProcessingOrders() {
        List<OrderResponseDTO> orders = orderService.getProcessingOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Listar pedidos enviados
     */
    @GetMapping("/status/shipped")
    @Operation(summary = "Listar pedidos enviados", description = "Retorna todos os pedidos enviados")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos enviados")
    public ResponseEntity<List<OrderResponseDTO>> getShippedOrders() {
        List<OrderResponseDTO> orders = orderService.getShippedOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Listar pedidos entregues
     */
    @GetMapping("/status/delivered")
    @Operation(summary = "Listar pedidos entregues", description = "Retorna todos os pedidos entregues")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos entregues")
    public ResponseEntity<List<OrderResponseDTO>> getDeliveredOrders() {
        List<OrderResponseDTO> orders = orderService.getDeliveredOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Listar pedidos cancelados
     */
    @GetMapping("/status/cancelled")
    @Operation(summary = "Listar pedidos cancelados", description = "Retorna todos os pedidos cancelados")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos cancelados")
    public ResponseEntity<List<OrderResponseDTO>> getCancelledOrders() {
        List<OrderResponseDTO> orders = orderService.getCancelledOrders();
        return ResponseEntity.ok(orders);
    }

    // ============= CONSULTAS POR DATA =============

    /**
     * Listar pedidos de hoje
     */
    @GetMapping("/today")
    @Operation(summary = "Listar pedidos de hoje", description = "Retorna todos os pedidos criados hoje")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos de hoje")
    public ResponseEntity<List<OrderResponseDTO>> getTodaysOrders() {
        List<OrderResponseDTO> orders = orderService.getTodaysOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Buscar pedidos por período
     */
    @GetMapping("/date-range")
    @Operation(summary = "Buscar pedidos por período", description = "Retorna pedidos criados em um período específico")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos no período")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<OrderResponseDTO> orders = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(orders);
    }

    /**
     * Listar pedidos com entrega atrasada
     */
    @GetMapping("/overdue-deliveries")
    @Operation(summary = "Listar entregas atrasadas", description = "Retorna pedidos com entrega atrasada")
    @ApiResponse(responseCode = "200", description = "Lista de entregas atrasadas")
    public ResponseEntity<List<OrderResponseDTO>> getOverdueDeliveries() {
        List<OrderResponseDTO> orders = orderService.getOverdueDeliveries();
        return ResponseEntity.ok(orders);
    }

    // ============= RELATÓRIOS E ESTATÍSTICAS =============

    /**
     * Estatísticas gerais de pedidos
     */
    @GetMapping("/statistics")
    @Operation(summary = "Estatísticas de pedidos", description = "Retorna estatísticas gerais dos pedidos")
    @ApiResponse(responseCode = "200", description = "Estatísticas de pedidos")
    public ResponseEntity<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> statistics = orderService.getOrderStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Vendas por período
     */
    @GetMapping("/sales/date-range")
    @Operation(summary = "Vendas por período", description = "Retorna o total de vendas em um período")
    @ApiResponse(responseCode = "200", description = "Total de vendas no período")
    public ResponseEntity<Map<String, Object>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        BigDecimal totalSales = orderService.getSalesByDateRange(startDate, endDate);
        
        return ResponseEntity.ok(Map.of(
                "start_date", startDate,
                "end_date", endDate,
                "total_sales", totalSales
        ));
    }

    // ============= ENDPOINT DE SAÚDE =============

    /**
     * Health check do serviço
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se o serviço está funcionando")
    @ApiResponse(responseCode = "200", description = "Serviço funcionando")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "service", "order-service",
                "status", "UP",
                "timestamp", LocalDateTime.now()
        ));
    }
}
