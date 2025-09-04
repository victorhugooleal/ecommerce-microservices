# Dockerfile multi-stage para aplicação Spring Boot
FROM openjdk:17-jdk-slim as builder

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage final - runtime
FROM openjdk:17-jre-slim

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Diretório de trabalho
WORKDIR /app

# Copiar o JAR da aplicação do stage de build
COPY --from=builder /app/target/*.jar app.jar

# Expor a porta da aplicação
EXPOSE 8080

# Configurar variáveis de ambiente
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
