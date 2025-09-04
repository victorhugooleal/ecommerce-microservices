#!/bin/bash

echo "🚀 E-commerce Monolith - Docker Build & Run"
echo "=========================================="

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_message() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    print_error "Docker não está instalado!"
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose não está instalado!"
    exit 1
fi

print_message "Parando containers existentes..."
docker-compose down

print_message "Construindo a imagem Docker..."
if docker-compose build; then
    print_success "Imagem construída com sucesso!"
else
    print_error "Falha ao construir a imagem!"
    exit 1
fi

print_message "Iniciando a aplicação..."
if docker-compose up -d; then
    print_success "Aplicação iniciada com sucesso!"
    echo ""
    echo "🌐 Aplicação disponível em:"
    echo "   API: http://localhost:8080"
    echo "   Swagger: http://localhost:8080/swagger-ui.html"
    echo "   Health: http://localhost:8080/actuator/health"
    echo ""
    echo "👥 Usuários de teste:"
    echo "   Admin: admin@ecommerce.com / admin123"
    echo "   User:  joao@email.com / user123"
    echo ""
    echo "📋 Para ver logs: docker-compose logs -f"
    echo "🛑 Para parar: docker-compose down"
else
    print_error "Falha ao iniciar a aplicação!"
    exit 1
fi
