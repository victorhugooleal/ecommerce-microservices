# 🛒 E-commerce Monolith

Aplicação monolítica de e-commerce desenvolvida com Spring Boot que será transformada em uma arquitetura de microsserviços.

## 📋 Sobre o Projeto

Este projeto faz parte do trabalho de **Arquiteturas Avançadas de Software com Microsserviços e Spring Framework**. A aplicação implementa um sistema de e-commerce completo com:

- ✅ **Gestão de Usuários** (autenticação JWT, autorização)
- ✅ **Catálogo de Produtos** (CRUD, busca, categorias)
- ✅ **Sistema de Pedidos** (workflow completo, gestão de estoque)
- ✅ **Segurança** (Spring Security + JWT)
- ✅ **Documentação** (Swagger/OpenAPI)

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Security**
- **Spring Data JPA**
- **H2 Database** (será migrado para MySQL/PostgreSQL)
- **JWT** (autenticação)
- **Swagger/OpenAPI** (documentação)
- **Maven** (gerenciamento de dependências)

## 🎯 Objetivos do Projeto

Este monólito será transformado em microsserviços seguindo as 4 competências:

### 1. Implementar Arquiteturas de Microsserviços
- [x] Separação do monólito em microsserviços
- [x] Comunicação REST entre serviços
- [ ] Spring Cloud Config Server
- [ ] Service Discovery (Eureka)

### 2. Modelar e Implementar Microsserviços
- [x] Utilização do Spring Cloud
- [ ] Bancos MySQL/PostgreSQL separados
- [x] Documentação Swagger/OpenAPI
- [ ] Logs distribuídos (ELK Stack)

### 3. Desenvolver Aplicações Cloud Native
- [x] Spring Security implementado
- [x] Autenticação entre microsserviços
- [ ] Containerização com Docker
- [ ] Docker Compose

### 4. Processar Informações em Lote
- [ ] Mensageria assíncrona
- [ ] Consumo de mensagens
- [ ] Processamento em lote
- [ ] Monitoramento de filas

## 🔧 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.6+
- Git

### Executando Localmente

1. **Clone o repositório:**
```bash
git clone <url-do-repositorio>
cd ecommerce-monolith
```

2. **Compile o projeto:**
```bash
mvn clean compile
```

3. **Execute a aplicação:**
```bash
mvn spring-boot:run
```

### Executando com Docker

1. **Build da imagem:**
```bash
docker build -t ecommerce-monolith .
```

2. **Execute o container:**
```bash
docker run -p 8080:8080 ecommerce-monolith
```

## 📚 Documentação da API

Após executar a aplicação, acesse:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs
- **H2 Console:** http://localhost:8080/h2-console

## 🔐 Usuários de Teste

A aplicação vem com usuários pré-cadastrados:

| Email | Senha | Papel |
|-------|--------|-------|
| admin@ecommerce.com | admin123 | ADMIN |
| joao@email.com | user123 | USER |

## 📦 Estrutura do Projeto

```
src/main/java/com/ecommerce/
├── config/              # Configurações (Security, Swagger, DataLoader)
├── user/                # Domínio de Usuários
│   ├── entity/         # Entidades JPA
│   ├── dto/            # Data Transfer Objects
│   ├── repository/     # Repositórios Spring Data
│   ├── service/        # Regras de negócio
│   └── controller/     # Controllers REST
├── product/             # Domínio de Produtos
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   ├── service/
│   └── controller/
├── order/               # Domínio de Pedidos
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   ├── service/
│   └── controller/
└── security/            # Configurações de Segurança (JWT, Filters)
```

## 🔄 Próximas Etapas

1. **Separação em Microsserviços:**
   - User Service (porta 8081)
   - Product Service (porta 8082)
   - Order Service (porta 8083)

2. **Spring Cloud:**
   - Config Server (porta 8888)
   - Eureka Server (porta 8761)
   - API Gateway (porta 8080)

3. **Infraestrutura:**
   - Docker Compose
   - Bancos de dados separados
   - Mensageria (RabbitMQ/Kafka)
   - Monitoramento (ELK Stack/Prometheus)

## 📄 Licença

Este projeto é desenvolvido para fins educacionais.

## 👨‍💻 Autor

Projeto desenvolvido como parte do curso de Arquiteturas Avançadas de Software com Microsserviços e Spring Framework.
