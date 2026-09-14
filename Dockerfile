# 1: Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e gera o pacote (com logs de erro ativados '-e')
COPY src ./src
RUN mvn package -DskipTests -e

# 2: Run (Execução)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o arquivo .jar gerado na etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta 8088
EXPOSE 8088

# Inicia a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
