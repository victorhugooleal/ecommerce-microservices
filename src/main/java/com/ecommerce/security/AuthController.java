package com.ecommerce.security;

import com.ecommerce.user.dto.CreateUserDTO;
import com.ecommerce.user.dto.LoginDTO;
import com.ecommerce.user.dto.UserResponseDTO;
import com.ecommerce.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticação
 * Esta API será movida para o microsserviço de autenticação/usuários
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API para autenticação e registro de usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * DTO para resposta de login
     */
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private UserResponseDTO user;

        public JwtResponse(String accessToken, UserResponseDTO user) {
            this.token = accessToken;
            this.user = user;
        }

        // Getters e Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public UserResponseDTO getUser() { return user; }
        public void setUser(UserResponseDTO user) { this.user = user; }
    }

    @Operation(summary = "Login de usuário", description = "Autentica usuário e retorna token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserResponseDTO userDetails = userService.getUserByEmail(loginRequest.getEmail())
                .map(UserResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            return ResponseEntity.ok(new JwtResponse(jwt, userDetails));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Erro: Credenciais inválidas!");
        }
    }

    @Operation(summary = "Registro de usuário", description = "Registra novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Email já está em uso")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserDTO signUpRequest) {
        try {
            UserResponseDTO user = userService.createUser(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Verificar token", description = "Verifica se o token JWT é válido")
    @ApiResponse(responseCode = "200", description = "Token válido")
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken() {
        return ResponseEntity.ok("Token válido");
    }
}
