package com.ecommerce.user.repository;

import com.ecommerce.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade User
 * Este repositório será movido para o microsserviço de usuários
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se email já existe
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos
     */
    List<User> findByIsActiveTrue();

    /**
     * Busca usuários por role
     */
    List<User> findByRole(User.Role role);

    /**
     * Busca usuários ativos por role
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role = :role")
    List<User> findActiveUsersByRole(User.Role role);
}
