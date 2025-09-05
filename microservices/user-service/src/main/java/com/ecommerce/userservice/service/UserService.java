package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.CreateUserDTO;
import com.ecommerce.userservice.dto.LoginDTO;
import com.ecommerce.userservice.dto.LoginResponseDTO;
import com.ecommerce.userservice.dto.UserResponseDTO;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service para gerenciamento de usuários
 * Implementa regras de negócio para CRUD de usuários e autenticação
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * Implementação do UserDetailsService para autenticação
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole())
                .build();
    }

    /**
     * Criar novo usuário
     */
    @Transactional
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        log.info("Criando usuário com email: {}", createUserDTO.getEmail());

        // Verificar se email já existe
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new RuntimeException("Email já está em uso: " + createUserDTO.getEmail());
        }

        // Criar novo usuário
        User user = User.builder()
                .name(createUserDTO.getName())
                .email(createUserDTO.getEmail())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .role(createUserDTO.getRole())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("Usuário criado com sucesso - ID: {}", savedUser.getId());

        return convertToResponseDTO(savedUser);
    }

    /**
     * Buscar usuário por ID
     */
    public UserResponseDTO getUserById(Long id) {
        log.info("Buscando usuário por ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado - ID: " + id));
        return convertToResponseDTO(user);
    }

    /**
     * Buscar usuário por email
     */
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Buscando usuário por email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado - Email: " + email));
        return convertToResponseDTO(user);
    }

    /**
     * Listar todos os usuários
     */
    public List<UserResponseDTO> getAllUsers() {
        log.info("Listando todos os usuários");
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Listar usuários ativos
     */
    public List<UserResponseDTO> getActiveUsers() {
        log.info("Listando usuários ativos");
        return userRepository.findByActiveTrue().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    /**
     * Atualizar usuário
     */
    @Transactional
    public UserResponseDTO updateUser(Long id, CreateUserDTO updateUserDTO) {
        log.info("Atualizando usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado - ID: " + id));

        // Verificar se novo email já está em uso (se diferente do atual)
        if (!user.getEmail().equals(updateUserDTO.getEmail()) && 
            userRepository.existsByEmail(updateUserDTO.getEmail())) {
            throw new RuntimeException("Email já está em uso: " + updateUserDTO.getEmail());
        }

        // Atualizar dados
        user.setName(updateUserDTO.getName());
        user.setEmail(updateUserDTO.getEmail());
        user.setRole(updateUserDTO.getRole());
        user.setUpdatedAt(LocalDateTime.now());

        // Atualizar senha apenas se fornecida
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("Usuário atualizado com sucesso - ID: {}", updatedUser.getId());

        return convertToResponseDTO(updatedUser);
    }

    /**
     * Desativar usuário (soft delete)
     */
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Desativando usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado - ID: " + id));

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Usuário desativado com sucesso - ID: {}", id);
    }

    /**
     * Reativar usuário
     */
    @Transactional
    public UserResponseDTO reactivateUser(Long id) {
        log.info("Reativando usuário ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado - ID: " + id));

        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        User reactivatedUser = userRepository.save(user);

        log.info("Usuário reativado com sucesso - ID: {}", id);
        return convertToResponseDTO(reactivatedUser);
    }

    /**
     * Deletar usuário permanentemente
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deletando usuário permanentemente - ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado - ID: " + id);
        }

        userRepository.deleteById(id);
        log.info("Usuário deletado permanentemente - ID: {}", id);
    }

    /**
     * Autenticação de usuário
     */
    public LoginResponseDTO authenticateUser(LoginDTO loginDTO) {
        log.info("Tentativa de autenticação para email: {}", loginDTO.getEmail());

        try {
            // Autenticar credenciais
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Buscar dados completos do usuário
            User user = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!user.getActive()) {
                throw new RuntimeException("Usuário está desativado");
            }

            // Gerar token JWT
            String jwt = jwtUtils.generateJwtToken(user.getEmail(), user.getRole(), user.getId());

            log.info("Autenticação realizada com sucesso para usuário ID: {}", user.getId());

            return LoginResponseDTO.builder()
                    .token(jwt)
                    .tokenType("Bearer")
                    .expiresIn(86400000L) // 24 horas em millisegundos
                    .user(convertToResponseDTO(user))
                    .build();

        } catch (AuthenticationException e) {
            log.error("Falha na autenticação para email: {}", loginDTO.getEmail());
            throw new RuntimeException("Credenciais inválidas");
        }
    }

    /**
     * Verificar se usuário existe por email
     */
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Converter entidade para DTO de resposta
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
