package com.ecommerce.userservice.config;

import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Data Loader para criar usuários de teste
 * Executa na inicialização da aplicação
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadTestUsers();
    }

    /**
     * Carrega usuários de teste se o banco estiver vazio
     */
    private void loadTestUsers() {
        if (userRepository.count() > 0) {
            log.info("Usuários já existem no banco de dados. Pulando criação de dados de teste.");
            return;
        }

        log.info("Criando usuários de teste...");

        try {
            // Usuário Admin
            User admin = User.builder()
                    .name("Administrador do Sistema")
                    .email("admin@ecommerce.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);
            log.info("✅ Usuário admin criado: {}", admin.getEmail());

            // Usuário Cliente 1
            User customer1 = User.builder()
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .role(User.Role.USER)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(customer1);
            log.info("✅ Usuário cliente criado: {}", customer1.getEmail());

            // Usuário Cliente 2
            User customer2 = User.builder()
                    .name("Maria Santos")
                    .email("maria.santos@email.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .role(User.Role.USER)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(customer2);
            log.info("✅ Usuário cliente criado: {}", customer2.getEmail());

            // Usuário Cliente 3 (desativado para teste)
            User customer3 = User.builder()
                    .name("Pedro Oliveira")
                    .email("pedro.oliveira@email.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .role(User.Role.USER)
                    .active(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(customer3);
            log.info("✅ Usuário cliente desativado criado: {}", customer3.getEmail());

            log.info("🎯 Usuários de teste criados com sucesso!");
            log.info("📝 Credenciais de teste:");
            log.info("   Admin: admin@ecommerce.com / admin123");
            log.info("   Cliente: joao.silva@email.com / cliente123");
            log.info("   Cliente: maria.santos@email.com / cliente123");
            log.info("   Cliente (desativado): pedro.oliveira@email.com / cliente123");

        } catch (Exception e) {
            log.error("❌ Erro ao criar usuários de teste: {}", e.getMessage());
        }
    }
}
