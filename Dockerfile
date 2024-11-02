# Utiliza a imagem Maven para compilar o projeto
FROM maven:3.8.6-openjdk-17 AS builder

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o código fonte para o container
COPY . .

# Executa o Maven para compilar o projeto
RUN mvn clean package -DskipTests

# Usa uma imagem mais leve para executar o JAR compilado
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho para a aplicação
WORKDIR /app

# Copia o JAR do estágio de build para o estágio de runtime
COPY --from=builder /app/target/totem.jar /app/totem.jar

# Porta em que a aplicação irá rodar
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "/app/totem.jar"]
