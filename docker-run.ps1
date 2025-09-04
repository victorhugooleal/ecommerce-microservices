# PowerShell script para build e execução do Docker
param(
    [switch]$Build,
    [switch]$Run,
    [switch]$Stop,
    [switch]$Logs,
    [switch]$Clean
)

function Write-ColorMessage {
    param($Message, $Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

function Write-Info {
    param($Message)
    Write-ColorMessage "[INFO] $Message" "Cyan"
}

function Write-Success {
    param($Message)
    Write-ColorMessage "[SUCCESS] $Message" "Green"
}

function Write-Error {
    param($Message)
    Write-ColorMessage "[ERROR] $Message" "Red"
}

# Header
Write-ColorMessage "🚀 E-commerce Monolith - Docker Manager" "Yellow"
Write-ColorMessage "=======================================" "Yellow"

# Verificar se Docker está instalado
if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker não está instalado!"
    exit 1
}

# Verificar se Docker Compose está instalado
if (!(Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    Write-Error "Docker Compose não está instalado!"
    exit 1
}

# Se nenhum parâmetro foi passado, executar build e run
if (-not ($Build -or $Run -or $Stop -or $Logs -or $Clean)) {
    $Build = $true
    $Run = $true
}

if ($Stop) {
    Write-Info "Parando containers..."
    docker-compose down
    Write-Success "Containers parados!"
    exit 0
}

if ($Clean) {
    Write-Info "Limpando containers e imagens..."
    docker-compose down --rmi all --volumes --remove-orphans
    Write-Success "Limpeza concluída!"
    exit 0
}

if ($Logs) {
    Write-Info "Mostrando logs..."
    docker-compose logs -f
    exit 0
}

if ($Build) {
    Write-Info "Parando containers existentes..."
    docker-compose down

    Write-Info "Construindo a imagem Docker..."
    if (docker-compose build) {
        Write-Success "Imagem construída com sucesso!"
    } else {
        Write-Error "Falha ao construir a imagem!"
        exit 1
    }
}

if ($Run) {
    Write-Info "Iniciando a aplicação..."
    if (docker-compose up -d) {
        Write-Success "Aplicação iniciada com sucesso!"
        Write-Host ""
        Write-ColorMessage "🌐 Aplicação disponível em:" "Yellow"
        Write-Host "   API: http://localhost:8080"
        Write-Host "   Swagger: http://localhost:8080/swagger-ui.html"
        Write-Host "   Health: http://localhost:8080/actuator/health"
        Write-Host ""
        Write-ColorMessage "👥 Usuários de teste:" "Yellow"
        Write-Host "   Admin: admin@ecommerce.com / admin123"
        Write-Host "   User:  joao@email.com / user123"
        Write-Host ""
        Write-ColorMessage "📋 Comandos úteis:" "Yellow"
        Write-Host "   Ver logs: .\docker-run.ps1 -Logs"
        Write-Host "   Parar: .\docker-run.ps1 -Stop"
        Write-Host "   Rebuild: .\docker-run.ps1 -Build -Run"
        Write-Host "   Limpar: .\docker-run.ps1 -Clean"
    } else {
        Write-Error "Falha ao iniciar a aplicação!"
        exit 1
    }
}
