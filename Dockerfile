# 1: Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copia apenas o pom.xml primeiro para baixar as dependências (otimiza o cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# 2: Run (Execucao)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o arquivo .jar
COPY --from=builder /app/target/*.jar app.jar

# Expoe a porta 8088
EXPOSE 8088

# Inicia a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]