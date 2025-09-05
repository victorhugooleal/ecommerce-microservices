package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade User - Microsserviço
 * Responsável pelo acesso aos dados de usuários
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe usuário com email
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por role
     */
    List<User> findByRole(User.Role role);

    /**
     * Busca usuários por nome (LIKE)
     */
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameContaining(String name);

    /**
     * Conta usuários por role
     */
    long countByRole(User.Role role);

    /**
     * Busca usuários ativos (implementação futura para soft delete)
     */
    @Query("SELECT u FROM User u WHERE u.createdAt IS NOT NULL ORDER BY u.createdAt DESC")
    List<User> findAllOrderByCreatedAtDesc();
}
