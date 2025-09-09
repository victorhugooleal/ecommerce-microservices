# 🐳 E-commerce Microservices - Guia Docker

Este documento explica como executar toda a arquitetura de microsserviços usando Docker Compose.

## 📋 Pré-requisitos

### Software Necessário
- **Docker Desktop** ou **Docker Engine** + **Docker Compose**
- **Git** (para clonar o repositório)
- **8GB RAM** livre (recomendado)

### Verificar Instalação
```bash
docker --version
docker-compose --version
```

## 🏗️ Arquitetura no Docker

### Containers que serão criados:
- **3 Databases MySQL** (portas 3306, 3307, 3308)
- **6 Microservices Spring Boot**
  - Config Server (8888)
  - Eureka Server (8761)  
  - API Gateway (8080)
  - User Service (8081)
  - Product Service (8082)
  - Order Service (8083)

### Rede Docker
- Todos os containers estão na rede `microservices-network`
- Comunicação interna entre containers via nomes de serviço

## 🚀 Executando a Aplicação

### 1️⃣ Executar todos os serviços
```bash
# Na raiz do projeto
docker-compose up -d
```

### 2️⃣ Verificar status dos containers
```bash
docker-compose ps
```

### 3️⃣ Ver logs em tempo real
```bash
# Todos os serviços
docker-compose logs -f

# Serviço específico
docker-compose logs -f config-server
docker-compose logs -f eureka-server
docker-compose logs -f api-gateway
```

### 4️⃣ Ordem de inicialização
Os serviços iniciam automaticamente na ordem correta devido às dependências configuradas:

1. **Databases** (MySQL containers)
2. **Config Server** (configurações centralizadas)
3. **Eureka Server** (service discovery)
4. **Business Services** (User, Product, Order)
5. **API Gateway** (ponto único de entrada)

## 🔍 Health Checks e Monitoramento

### URLs de Acesso (após inicialização completa):

#### Serviços de Infraestrutura
- **Config Server**: http://localhost:8888/actuator/health
- **Eureka Server**: http://localhost:8761
- **API Gateway**: http://localhost:8080/actuator/health

#### Serviços de Negócio
- **User Service**: http://localhost:8081/actuator/health
- **Product Service**: http://localhost:8082/actuator/health  
- **Order Service**: http://localhost:8083/actuator/health

### Dashboard Eureka
- **URL**: http://localhost:8761
- Ver todos os serviços registrados

## 🧪 Testando a Aplicação

### 1️⃣ Teste via API Gateway (Recomendado)
```bash
# Health check geral
curl http://localhost:8080/actuator/health

# Info do Gateway
curl http://localhost:8080/gateway/info

# Listar produtos via Gateway
curl http://localhost:8080/api/products

# Registrar usuário via Gateway
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2️⃣ Teste direto nos serviços
```bash
# User Service
curl http://localhost:8081/api/users

# Product Service  
curl http://localhost:8082/api/products

# Order Service
curl http://localhost:8083/api/orders
```

### 3️⃣ Testar Circuit Breaker
```bash
# Testar fallback quando serviços estão indisponíveis
curl http://localhost:8080/fallback/user-service
curl http://localhost:8080/fallback/product-service
curl http://localhost:8080/fallback/order-service
```

## 🛠️ Comandos Úteis

### Gerenciamento de Containers
```bash
# Parar todos os serviços
docker-compose down

# Parar e remover volumes (CUIDADO: remove dados)
docker-compose down -v

# Rebuild e restart
docker-compose up -d --build

# Restart serviço específico
docker-compose restart user-service

# Escalar serviço (criar múltiplas instâncias)
docker-compose up -d --scale user-service=2
```

### Debugging
```bash
# Entrar no container
docker exec -it config-server sh
docker exec -it mysql-user-db mysql -u root -p

# Ver recursos utilizados
docker stats

# Inspecionar container
docker inspect config-server
```

### Limpeza
```bash
# Remover containers parados
docker container prune

# Remover imagens não utilizadas
docker image prune

# Limpeza completa
docker system prune -a
```

## 📊 Monitoramento

### Actuator Endpoints
Todos os serviços expõem endpoints de monitoramento:

- `/actuator/health` - Status da aplicação
- `/actuator/info` - Informações da aplicação  
- `/actuator/metrics` - Métricas da aplicação
- `/actuator/env` - Variáveis de ambiente

### Logs Estruturados
```bash
# Ver logs com timestamp
docker-compose logs -t config-server

# Filtrar logs por nível
docker-compose logs | grep ERROR
docker-compose logs | grep WARN
```

## ❗ Troubleshooting

### Problemas Comuns

#### 1. Container não inicia
```bash
# Ver erro específico
docker-compose logs service-name

# Verificar recursos
docker stats
```

#### 2. Database connection error
```bash
# Verificar se MySQL está rodando
docker-compose ps mysql-user

# Testar conexão
docker exec -it mysql-user-db mysql -u userapp -p
```

#### 3. Service discovery falha
```bash
# Verificar Eureka
curl http://localhost:8761/eureka/apps

# Ver logs do Eureka
docker-compose logs eureka-server
```

#### 4. Config Server inacessível
```bash
# Testar configuração
curl http://configuser:configpass@localhost:8888/user-service/docker

# Ver logs
docker-compose logs config-server
```

### Limpeza Completa (Reset)
```bash
# Parar tudo
docker-compose down -v

# Remover imagens criadas
docker rmi $(docker images "ecommerce-monolith*" -q)

# Rebuild completo
docker-compose up -d --build
```

## 🎯 Próximos Passos

1. **Testar todos os endpoints**
2. **Verificar comunicação entre serviços**
3. **Monitorar performance via Actuator**
4. **Implementar testes de integração**
5. **Adicionar observabilidade (Zipkin, Prometheus)**

---

## 📝 Notas Importantes

- ⏱️ **Tempo de inicialização**: ~3-5 minutos para todos os serviços
- 🔄 **Dependencies**: Respeitam ordem de inicialização automática
- 🐳 **Profiles**: Usam profile `docker` automaticamente
- 🔒 **Segurança**: Senhas em variáveis de ambiente
- 🌐 **Network**: Isolamento via rede Docker dedicada

**Sucesso!** 🎉 Sua arquitetura de microsserviços está rodando no Docker!
