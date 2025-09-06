package com.ecommerce.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server - Servidor de Configuração Centralizada
 * 
 * Responsável por:
 * - Centralizar configurações de todos os microsserviços
 * - Fornecer configurações baseadas em profiles (dev, test, prod)
 * - Permitir atualização de configurações sem restart dos serviços
 * - Integração com Git para versionamento das configurações
 * 
 * Porta: 8888 (padrão do Spring Cloud Config)
 * 
 * Endpoints importantes:
 * - /{application}/{profile} - Configurações de uma aplicação
 * - /{application}/{profile}/{label} - Configurações de uma versão específica
 * - /actuator/health - Health check
 * - /actuator/info - Informações do serviço
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
