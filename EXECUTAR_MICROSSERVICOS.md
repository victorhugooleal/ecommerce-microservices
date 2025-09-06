# 🚀 E-commerce Microservices - Guia de Execução

Este documento explica como executar a arquitetura completa de microsserviços do e-commerce.

## 📋 Pré-requisitos

### Software Necessário
- **Java 17+** (Eclipse Temurin recomendado)
- **Maven 3.8+**
- **MySQL 8.0+** 
- **Docker** (opcional, para containers)
- **IDE** (IntelliJ IDEA, Eclipse ou VS Code)

### Bancos de Dados
Configure 3 instâncias MySQL nas portas:
```bash
# User Database
mysql -u root -p -P 3306 -e "CREATE DATABASE IF NOT EXISTS user_db;"

# Product Database  
mysql -u root -p -P 3307 -e "CREATE DATABASE IF NOT EXISTS product_db;"

# Order Database
mysql -u root -p -P 3308 -e "CREATE DATABASE IF NOT EXISTS order_db;"
```

## 🏗️ Arquitetura dos Serviços

### Serviços de Infraestrutura
1. **Config Server** (porta 8888) - Configurações centralizadas
2. **Eureka Server** (porta 8761) - Service Discovery
3. **API Gateway** (porta 8080) - Ponto único de entrada

### Serviços de Negócio
4. **User Service** (porta 8081) - Gestão de usuários e autenticação
5. **Product Service** (porta 8082) - Catálogo e inventário de produtos  
6. **Order Service** (porta 8083) - Gestão de pedidos e comunicação entre serviços

## 🚀 Ordem de Execução

> **IMPORTANTE**: Execute os serviços na ordem exata abaixo para evitar problemas de dependência!

### 1️⃣ Config Server (PRIMEIRO)
```bash
cd microservices/config-server
mvn spring-boot:run
```
- **URL**: http://localhost:8888
- **Health**: http://localhost:8888/actuator/health
- **Credenciais**: configuser / configpass

### 2️⃣ Eureka Server (SEGUNDO)
```bash
cd microservices/eureka-server
mvn spring-boot:run
```
- **URL**: http://localhost:8761
- **Web UI**: http://localhost:8761
- **API**: http://localhost:8761/eureka/apps

### 3️⃣ Serviços de Negócio (TERCEIRO - pode ser em paralelo)

#### User Service
```bash
cd microservices/user-service
mvn spring-boot:run
```
- **API**: http://localhost:8081/api/v1/users
- **Auth**: http://localhost:8081/api/v1/auth
- **Health**: http://localhost:8081/actuator/health

#### Product Service
```bash
cd microservices/product-service  
mvn spring-boot:run
```
- **API**: http://localhost:8082/api/v1/products
- **Health**: http://localhost:8082/actuator/health

#### Order Service
```bash
cd microservices/order-service
mvn spring-boot:run
```
- **API**: http://localhost:8083/api/v1/orders
- **Health**: http://localhost:8083/actuator/health

### 4️⃣ API Gateway (ÚLTIMO)
```bash
cd microservices/api-gateway
mvn spring-boot:run
```
- **URL**: http://localhost:8080
- **Health**: http://localhost:8080/gateway/health
- **Info**: http://localhost:8080/gateway/info
- **Welcome**: http://localhost:8080/gateway/welcome

## 🔍 Verificação do Sistema

### 1. Verifique o Eureka Dashboard
Acesse http://localhost:8761 e confirme que todos os serviços estão registrados:
- `API-GATEWAY`
- `USER-SERVICE`
- `PRODUCT-SERVICE` 
- `ORDER-SERVICE`

### 2. Teste o API Gateway
```bash
# Health check
curl http://localhost:8080/gateway/health

# Informações do Gateway
curl http://localhost:8080/gateway/info

# Boas-vindas
curl http://localhost:8080/gateway/welcome
```

### 3. Teste os Microsserviços via Gateway

#### User Service via Gateway
```bash
# Listar usuários
curl http://localhost:8080/api/v1/users

# Health check
curl http://localhost:8080/api/v1/users/health
```

#### Product Service via Gateway
```bash
# Listar produtos
curl http://localhost:8080/api/v1/products

# Health check  
curl http://localhost:8080/api/v1/products/health
```

#### Order Service via Gateway
```bash
# Listar pedidos
curl http://localhost:8080/api/v1/orders

# Health check
curl http://localhost:8080/api/v1/orders/health
```

## 📊 Monitoramento e Observabilidade

### Actuator Endpoints (todos os serviços)
- `/actuator/health` - Status de saúde
- `/actuator/info` - Informações da aplicação
- `/actuator/metrics` - Métricas de performance
- `/actuator/env` - Variáveis de ambiente

### Config Server Endpoints
- `/{service-name}/{profile}` - Configurações de um serviço
- `/user-service/dev` - Configurações do User Service
- `/product-service/dev` - Configurações do Product Service
- `/order-service/dev` - Configurações do Order Service

### Circuit Breakers
O API Gateway possui circuit breakers para todos os serviços:
- `http://localhost:8080/actuator/health` - Status dos circuit breakers
- Fallback URLs:
  - `/fallback/user-service`
  - `/fallback/product-service`
  - `/fallback/order-service`

## 🔧 Configurações Importantes

### Credenciais do Config Server
- **Usuário**: `configuser`
- **Senha**: `configpass`

### Bancos de Dados
- **User DB**: `localhost:3306/user_db`
- **Product DB**: `localhost:3307/product_db` 
- **Order DB**: `localhost:3308/order_db`
- **Usuário**: `root` / **Senha**: `admin123`

### Profiles Ativos
Todos os serviços estão configurados com profile `dev`.

## 🐳 Execução com Docker (Opcional)

```bash
# Na raiz do projeto
docker-compose up -d
```

Isso iniciará todos os serviços e bancos de dados automaticamente.

## 🆘 Troubleshooting

### Problema: Serviço não registra no Eureka
- Verifique se o Eureka Server está rodando
- Confirme as configurações de `eureka.client.service-url.defaultZone`
- Aguarde até 30 segundos para registro aparecer

### Problema: Config Server não responde
- Verifique as credenciais de autenticação
- Confirme se a porta 8888 está livre
- Verifique os logs para erros de configuração

### Problema: Gateway retorna 503
- Confirme que todos os serviços estão registrados no Eureka
- Verifique se os circuit breakers não estão abertos
- Teste os serviços diretamente (sem gateway)

### Problema: Erro de conexão com banco
- Verifique se o MySQL está rodando nas portas corretas
- Confirme as credenciais nos arquivos de configuração
- Verifique se os bancos de dados foram criados

## 📈 Próximos Passos

1. **Implementar autenticação JWT** completa
2. **Adicionar distributed tracing** (Sleuth/Zipkin)
3. **Implementar métricas** (Micrometer/Prometheus)
4. **Adicionar logs centralizados** (ELK Stack)
5. **Configurar pipelines CI/CD**
6. **Implementar testes de integração**

## 🎯 Endpoints Principais

| Serviço | Direto | Via Gateway | Descrição |
|---------|--------|-------------|-----------|
| User Service | :8081 | :8080/api/v1/users | Usuários e Auth |
| Product Service | :8082 | :8080/api/v1/products | Produtos |
| Order Service | :8083 | :8080/api/v1/orders | Pedidos |
| Eureka | :8761 | :8080/eureka/web | Service Discovery |
| Config Server | :8888 | - | Configurações |

---

🎉 **Parabéns!** Sua arquitetura de microsserviços está funcionando!
