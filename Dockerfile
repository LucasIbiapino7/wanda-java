# 1 etapa: builder     #
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copia todo o projeto (pom.xml, mvnw, src, etc.)
COPY . .

# Compila o jar
RUN ./mvnw -B -DskipTests clean package


# 2 etapa: runtime     #
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia somente o jar gerado na etapa builder
COPY --from=builder /app/target/wanda-web-0.0.1-SNAPSHOT.jar app.jar

# Porta interna da aplicação Spring
EXPOSE 8080

# Permite alterar perfil via variável sem reconstruir imagem
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}

# Comando de inicialização
ENTRYPOINT ["java","-jar","/app/app.jar"]
