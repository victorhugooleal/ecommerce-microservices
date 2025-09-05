# 🏗️ Transformação de Monólito para Microsserviços
**Arquiteturas Avançadas de Software com Microsserviços e Spring Framework**

---

**👨‍💻 Aluno:** Victor Hugo Olea  
**📅 Data:** Setembro 2025  
**🎯 Objetivo:** Transformar aplicação monolítica em arquitetura de microsserviços Cloud Native

---

## 📋 Índice

- [📊 Visão Geral](#-visão-geral)
- [🏢 Estado Atual - Monólito](#-estado-atual---monólito)
- [🎯 Competência 1 - Implementar Arquiteturas de Microsserviços](#-competência-1---implementar-arquiteturas-de-microsserviços)
- [🏗️ Competência 2 - Modelar e Implementar Microsserviços](#-competência-2---modelar-e-implementar-microsserviços)
- [☁️ Competência 3 - Desenvolver Aplicações Cloud Native](#-competência-3---desenvolver-aplicações-cloud-native)
- [📦 Competência 4 - Processar Informações em Lote](#-competência-4---processar-informações-em-lote)
- [🚀 Conclusão](#-conclusão)
- [📚 Repositório](#-repositório)

---

## 📊 Visão Geral

Este projeto demonstra a transformação completa de uma aplicação monolítica de e-commerce em uma arquitetura de microsserviços robusta e escalável, seguindo as melhores práticas de desenvolvimento Cloud Native.

### 🎯 Objetivos Principais

1. **Separar** monólito em microsserviços independentes
2. **Implementar** comunicação entre serviços via REST
3. **Configurar** infraestrutura Spring Cloud
4. **Adicionar** segurança, logging e monitoramento
5. **Containerizar** com Docker e Docker Compose
6. **Implementar** mensageria assíncrona
7. **Criar** processamento em lote

---

## 🏢 Estado Atual - Monólito

### 📐 Arquitetura Atual

```
📦 ecommerce-monolith
├── 👥 User Domain (Autenticação/Autorização)
├── 📱 Product Domain (Catálogo de Produtos)  
├── 🛒 Order Domain (Gestão de Pedidos)
├── 🔐 Security (JWT + Spring Security)
└── 💾 H2 Database (Em memória)
```

### 🛠️ Stack Tecnológico

- **Framework:** Spring Boot 3.1.5
- **Java:** 17 (Eclipse Temurin)
- **Database:** H2 (em memória)
- **Security:** JWT + Spring Security
- **Documentation:** Swagger/OpenAPI
- **Build:** Maven
- **Container:** Docker + Docker Compose

### 📊 Métricas do Monólito

| Componente | Arquivos | Linhas de Código | Responsabilidades |
|------------|----------|------------------|-------------------|
| **User Service** | 8 arquivos | ~400 LOC | Autenticação, Autorização, CRUD usuários |
| **Product Service** | 8 arquivos | ~350 LOC | Catálogo, Estoque, CRUD produtos |
| **Order Service** | 10 arquivos | ~500 LOC | Pedidos, Workflow, Items |
| **Security** | 5 arquivos | ~300 LOC | JWT, Filtros, Configuração |
| **Config** | 3 arquivos | ~150 LOC | DataLoader, Configurações |

**Total:** ~34 arquivos, ~1.700 linhas de código

### 🔗 APIs Disponíveis

#### 🔐 Authentication
- `POST /api/auth/login` - Login de usuário
- `POST /api/auth/register` - Registro de usuário

#### 👥 Users  
- `GET /api/users` - Listar usuários (Admin)
- `GET /api/users/{id}` - Buscar usuário
- `PUT /api/users/{id}` - Atualizar usuário
- `DELETE /api/users/{id}` - Remover usuário (Admin)

#### 📱 Products
- `GET /api/products` - Listar produtos
- `GET /api/products/{id}` - Buscar produto
- `GET /api/products/search` - Buscar por nome
- `POST /api/products` - Criar produto (Admin)
- `PUT /api/products/{id}` - Atualizar produto (Admin)
- `DELETE /api/products/{id}` - Remover produto (Admin)

#### 🛒 Orders
- `GET /api/orders` - Listar pedidos (Admin)
- `GET /api/orders/my-orders` - Meus pedidos
- `GET /api/orders/{id}` - Buscar pedido
- `POST /api/orders` - Criar pedido
- `PUT /api/orders/{id}/confirm` - Confirmar pedido (Admin)
- `PUT /api/orders/{id}/process` - Processar pedido (Admin)
- `PUT /api/orders/{id}/ship` - Enviar pedido (Admin)
- `PUT /api/orders/{id}/deliver` - Entregar pedido (Admin)
- `PUT /api/orders/{id}/cancel` - Cancelar pedido

### 📦 Docker Configuration

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### ✅ Funcionalidades Testadas

**🧪 Testes realizados via Postman:**
- ✅ Autenticação JWT funcionando
- ✅ CRUD completo de usuários
- ✅ CRUD completo de produtos
- ✅ Workflow completo de pedidos
- ✅ Controle de estoque
- ✅ Autorização por roles (Admin/User)
- ✅ Documentação Swagger ativa

### 🗂️ Estrutura de Arquivos

```
ecommerce-monolith/
├── 📁 src/main/java/com/ecommerce/
│   ├── 📁 user/
│   │   ├── 📄 entity/User.java
│   │   ├── 📄 repository/UserRepository.java
│   │   ├── 📄 service/UserService.java
│   │   ├── 📄 controller/UserController.java
│   │   └── 📁 dto/
│   ├── 📁 product/
│   │   ├── 📄 entity/Product.java
│   │   ├── 📄 repository/ProductRepository.java
│   │   ├── 📄 service/ProductService.java
│   │   ├── 📄 controller/ProductController.java
│   │   └── 📁 dto/
│   ├── 📁 order/
│   │   ├── 📄 entity/Order.java
│   │   ├── 📄 entity/OrderItem.java
│   │   ├── 📄 repository/OrderRepository.java
│   │   ├── 📄 service/OrderService.java
│   │   ├── 📄 controller/OrderController.java
│   │   └── 📁 dto/
│   ├── 📁 security/
│   │   ├── 📄 SecurityConfig.java
│   │   ├── 📄 JwtUtils.java
│   │   └── 📄 JwtAuthenticationFilter.java
│   └── 📁 config/
│       ├── 📄 DataLoader.java
│       └── 📄 PasswordConfig.java
├── 📁 postman/
│   ├── 📄 E-commerce_Monolith_API.postman_collection.json
│   ├── 📄 E-commerce_Local.postman_environment.json
│   └── 📄 README.md
├── 📄 Dockerfile
├── 📄 docker-compose.yml
└── 📄 pom.xml
```

### 🎯 Pontos de Melhoria Identificados

1. **🔄 Acoplamento:** Todos os domínios em uma única aplicação
2. **💾 Database:** H2 não é adequado para produção
3. **📊 Monitoramento:** Falta observabilidade distribuída
4. **🔧 Configuração:** Configurações centralizadas no application.yml
5. **📡 Comunicação:** Não há descoberta de serviços
6. **🔒 Segurança:** JWT compartilhado entre todos os domínios
7. **📨 Mensageria:** Falta comunicação assíncrona
8. **🏗️ Escalabilidade:** Impossível escalar domínios independentemente

---

## 🎯 Competência 1 - Implementar Arquiteturas de Microsserviços

*🔄 Em desenvolvimento...*

### 🎯 Objetivos da Competência 1

- [ ] Separar projeto monolítico em microsserviços distintos
- [ ] Implementar comunicação REST entre microsserviços
- [ ] Configurar serviço centralizado de gerenciamento (Spring Cloud Config)
- [ ] Implementar descoberta de serviços (Eureka)

### 📋 Tarefas Planejadas

1. **🏗️ Separação em Microsserviços**
   - User Service (8081)
   - Product Service (8082)  
   - Order Service (8083)

2. **🌐 Spring Cloud Infrastructure**
   - Config Server (8888)
   - Eureka Discovery (8761)
   - API Gateway (8080)

3. **🔗 Comunicação REST**
   - OpenFeign clients
   - Circuit breakers
   - Load balancing

### 📊 Arquitetura Alvo

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Config Server │    │ Eureka Discovery│    │   API Gateway   │
│     :8888       │    │      :8761      │    │      :8080      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                ▲                       │
                                │                       │
        ┌───────────────────────┼───────────────────────┼───────────────────────┐
        │                       │                       ▼                       │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  User Service   │    │Product Service  │    │ Order Service   │    │     MySQL       │
│     :8081       │    │     :8082       │    │     :8083       │    │   Databases     │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 🏗️ Competência 2 - Modelar e Implementar Microsserviços

*🔄 Planejado...*

### 🎯 Objetivos da Competência 2

- [ ] Utilizar Spring Cloud para escalabilidade
- [ ] Migrar H2 para MySQL/PostgreSQL
- [ ] Documentar APIs com Swagger/OpenAPI
- [ ] Implementar logs distribuídos (ELK Stack)

---

## ☁️ Competência 3 - Desenvolver Aplicações Cloud Native

*🔄 Planejado...*

### 🎯 Objetivos da Competência 3

- [ ] Implementar Spring Security distribuído
- [ ] Configurar autenticação entre microsserviços
- [ ] Containerizar cada microsserviço
- [ ] Criar Docker Compose para orquestração

---

## 📦 Competência 4 - Processar Informações em Lote

*🔄 Planejado...*

### 🎯 Objetivos da Competência 4

- [ ] Implementar mensageria assíncrona (RabbitMQ)
- [ ] Criar consumer de mensagens
- [ ] Desenvolver processamento em lote
- [ ] Monitorar filas de mensagens

---

## 🚀 Conclusão

*🔄 Será preenchida ao final...*

---

## 📚 Repositório

**🔗 GitHub:** [victorhugooleal/ecommerce-microservices](https://github.com/victorhugooleal/ecommerce-microservices)

### 📊 Commits por Competência

- **📦 Initial Commit:** Monólito funcional
- **🎯 Competência 1:** Microsserviços + Spring Cloud
- **🏗️ Competência 2:** Databases + Documentação + Logs  
- **☁️ Competência 3:** Security + Docker
- **📦 Competência 4:** Mensageria + Batch Processing

---

*📄 Este documento será convertido em PDF para entrega final.*
