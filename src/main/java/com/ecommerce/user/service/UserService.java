package com.ecommerce.user.service;

import com.ecommerce.user.dto.CreateUserDTO;
import com.ecommerce.user.dto.UserResponseDTO;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço de usuários
 * Este serviço será movido para o microsserviço de usuários
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário
     */
    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        User user = new User();
        user.setName(createUserDTO.getName());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser);
    }

    /**
     * Busca usuário por ID
     */
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponseDTO::new);
    }

    /**
     * Busca usuário por email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Lista todos os usuários ativos
     */
    public List<UserResponseDTO> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza dados do usuário
     */
    public UserResponseDTO updateUser(Long id, CreateUserDTO updateUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verifica se o email já está em uso por outro usuário
        if (!user.getEmail().equals(updateUserDTO.getEmail()) &&
            userRepository.existsByEmail(updateUserDTO.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        user.setName(updateUserDTO.getName());
        user.setEmail(updateUserDTO.getEmail());
        
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return new UserResponseDTO(updatedUser);
    }

    /**
     * Desativa usuário (soft delete)
     */
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Implementação do UserDetailsService para Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    /**
     * Verifica se usuário é admin
     */
    public boolean isAdmin(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == User.Role.ADMIN)
                .orElse(false);
    }
}
