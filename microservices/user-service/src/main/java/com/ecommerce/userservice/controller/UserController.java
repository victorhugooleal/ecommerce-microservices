package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.CreateUserDTO;
import com.ecommerce.userservice.dto.LoginDTO;
import com.ecommerce.userservice.dto.LoginResponseDTO;
import com.ecommerce.userservice.dto.UserResponseDTO;
import com.ecommerce.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de usuários
 * Endpoints para CRUD de usuários e autenticação
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints para gerenciamento de usuários e autenticação")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    // ============= ENDPOINTS PÚBLICOS (SEM AUTENTICAÇÃO) =============

    /**
     * Registrar novo usuário
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", 
               description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já está em uso")
    })
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody CreateUserDTO createUserDTO) {
        
        try {
            log.info("Request para registrar usuário: {}", createUserDTO.getEmail());
            UserResponseDTO user = userService.createUser(createUserDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuário registrado com sucesso");
            response.put("user", user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao registrar usuário: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Login de usuário
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticação de usuário", 
               description = "Autentica usuário e retorna token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Usuário desativado")
    })
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        
        try {
            log.info("Request de login para: {}", loginDTO.getEmail());
            LoginResponseDTO loginResponse = userService.authenticateUser(loginDTO);
            
            return ResponseEntity.ok(loginResponse);
            
        } catch (Exception e) {
            log.error("Erro no login: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Verificar se email existe
     */
    @GetMapping("/check-email")
    @Operation(summary = "Verificar disponibilidade do email", 
               description = "Verifica se um email já está em uso")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(
            @RequestParam @Parameter(description = "Email a ser verificado") String email) {
        
        log.info("Verificando disponibilidade do email: {}", email);
        boolean exists = userService.userExistsByEmail(email);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        response.put("available", !exists);
        
        return ResponseEntity.ok(response);
    }

    // ============= ENDPOINTS PROTEGIDOS (REQUER AUTENTICAÇÃO) =============

    /**
     * Buscar usuário por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    @Operation(summary = "Buscar usuário por ID", 
               description = "Retorna dados do usuário pelo ID (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<?> getUserById(
            @PathVariable @Parameter(description = "ID do usuário") Long id) {
        
        try {
            log.info("Buscando usuário por ID: {}", id);
            UserResponseDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Listar todos os usuários (apenas admins)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os usuários", 
               description = "Retorna lista de todos os usuários (apenas admins)")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        
        log.info("Listando todos os usuários");
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Listar usuários ativos (apenas admins)
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuários ativos", 
               description = "Retorna lista de usuários ativos (apenas admins)")
    @ApiResponse(responseCode = "200", description = "Lista de usuários ativos retornada")
    public ResponseEntity<List<UserResponseDTO>> getActiveUsers() {
        
        log.info("Listando usuários ativos");
        List<UserResponseDTO> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Atualizar usuário
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    @Operation(summary = "Atualizar usuário", 
               description = "Atualiza dados do usuário (requer autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "409", description = "Email já está em uso")
    })
    public ResponseEntity<?> updateUser(
            @PathVariable @Parameter(description = "ID do usuário") Long id,
            @Valid @RequestBody CreateUserDTO updateUserDTO) {
        
        try {
            log.info("Atualizando usuário ID: {}", id);
            UserResponseDTO user = userService.updateUser(id, updateUserDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuário atualizado com sucesso");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Desativar usuário (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar usuário", 
               description = "Desativa usuário (soft delete) - apenas admins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Map<String, String>> deactivateUser(
            @PathVariable @Parameter(description = "ID do usuário") Long id) {
        
        try {
            log.info("Desativando usuário ID: {}", id);
            userService.deactivateUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário desativado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao desativar usuário ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Reativar usuário
     */
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reativar usuário", 
               description = "Reativa usuário desativado - apenas admins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário reativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<?> reactivateUser(
            @PathVariable @Parameter(description = "ID do usuário") Long id) {
        
        try {
            log.info("Reativando usuário ID: {}", id);
            UserResponseDTO user = userService.reactivateUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuário reativado com sucesso");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao reativar usuário ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Deletar usuário permanentemente
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar usuário permanentemente", 
               description = "Remove usuário permanentemente do sistema - apenas admins")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable @Parameter(description = "ID do usuário") Long id) {
        
        try {
            log.info("Deletando usuário permanentemente - ID: {}", id);
            userService.deleteUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuário deletado com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erro ao deletar usuário ID {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ============= ENDPOINT DE SAÚDE =============

    /**
     * Health check do serviço
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", 
               description = "Verifica se o serviço está funcionando")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "User Service");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}
