package com.ecommerce.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server - Gerenciamento Centralizado de Configurações
 * 
 * Responsável por centralizar todas as configurações dos microsserviços,
 * permitindo mudanças dinâmicas sem necessidade de restart dos serviços.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
