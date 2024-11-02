# Usa a imagem base do JDK 17
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR gerado para o contêiner
COPY target/*.jar app.jar

# Define a variável de ambiente para especificar o perfil de produção
ENV JAVA_OPTS=""

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
