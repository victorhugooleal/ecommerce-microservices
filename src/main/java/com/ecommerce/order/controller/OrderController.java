package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CreateOrderDTO;
import com.ecommerce.order.dto.OrderResponseDTO;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciamento de pedidos
 * Esta API será movida para o microsserviço de pedidos
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "API para gerenciamento de pedidos")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido para o usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Produto indisponível ou estoque insuficiente")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody CreateOrderDTO createOrderDTO,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            OrderResponseDTO createdOrder = orderService.createOrder(userEmail, createOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.getOrderById(#id).orElse(null)?.userId == authentication.principal.id")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(order))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar pedidos do usuário", description = "Retorna todos os pedidos do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso")
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(Authentication authentication) {
        String userEmail = authentication.getName();
        List<OrderResponseDTO> orders = orderService.getUserOrders(userEmail);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Listar todos os pedidos", description = "Retorna todos os pedidos (admin)")
    @ApiResponse(responseCode = "200", description = "Lista de todos os pedidos retornada com sucesso")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Buscar pedidos por status", description = "Retorna pedidos com status específico (admin)")
    @ApiResponse(responseCode = "200", description = "Pedidos com o status retornados com sucesso")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(
            @Parameter(description = "Status do pedido") @PathVariable Order.OrderStatus status) {
        List<OrderResponseDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Confirmar pedido", description = "Confirma um pedido pendente (admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido confirmado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser confirmado")
    })
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> confirmOrder(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        try {
            OrderResponseDTO confirmedOrder = orderService.confirmOrder(id);
            return ResponseEntity.ok(confirmedOrder);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Processar pedido", description = "Coloca pedido em processamento (admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido em processamento"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser processado")
    })
    @PatchMapping("/{id}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> processOrder(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        try {
            OrderResponseDTO processedOrder = orderService.processOrder(id);
            return ResponseEntity.ok(processedOrder);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Enviar pedido", description = "Marca pedido como enviado (admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido enviado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser enviado")
    })
    @PatchMapping("/{id}/ship")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> shipOrder(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        try {
            OrderResponseDTO shippedOrder = orderService.shipOrder(id);
            return ResponseEntity.ok(shippedOrder);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Entregar pedido", description = "Marca pedido como entregue (admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido entregue"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser entregue")
    })
    @PatchMapping("/{id}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> deliverOrder(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        try {
            OrderResponseDTO deliveredOrder = orderService.deliverOrder(id);
            return ResponseEntity.ok(deliveredOrder);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado")
    })
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @orderService.getOrderById(#id).orElse(null)?.userId == authentication.principal.id")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(description = "Motivo do cancelamento") @RequestParam(required = false) String reason) {
        try {
            OrderResponseDTO cancelledOrder = orderService.cancelOrder(id, reason);
            return ResponseEntity.ok(cancelledOrder);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Atualizar status de pagamento", description = "Atualiza status de pagamento do pedido (admin)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status de pagamento atualizado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PatchMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updatePaymentStatus(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(description = "Status de pagamento") @RequestParam Order.PaymentStatus paymentStatus) {
        try {
            OrderResponseDTO updatedOrder = orderService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Pedidos para processamento", description = "Retorna pedidos confirmados aguardando processamento (admin)")
    @ApiResponse(responseCode = "200", description = "Pedidos para processamento retornados")
    @GetMapping("/processing-queue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersForProcessing() {
        List<OrderResponseDTO> orders = orderService.getOrdersForProcessing();
        return ResponseEntity.ok(orders);
    }
}
