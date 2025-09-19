# Etapa 1: Build (compilación de en el proyecto)
FROM maven:3.9.0-eclipse-temurin-17 AS build

# Directorio de trabajo dentro del contenedor de build
WORKDIR /app

# Copiar en el proyecto al contenedor
COPY . .

# Construir en el proyecto y empaquetar los JARs (sin tests para acelerar)
RUN mvn clean install -DskipTests

# --- Etapas de imagen final para cada servicio ---

# api-gateway
FROM eclipse-temurin:17-jre AS api-gateway
WORKDIR /app
COPY --from=build /app/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]

# auth-service
FROM eclipse-temurin:17-jre AS auth-service
WORKDIR /app
COPY --from=build /app/auth-service/target/auth-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]

# eureka-server
FROM eclipse-temurin:17-jre AS eureka-server
WORKDIR /app
COPY --from=build /app/eureka-server/target/eureka-server-1.0.0.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]

# inventory-service
FROM eclipse-temurin:17-jre AS inventory-service
WORKDIR /app
COPY --from=build /app/inventory-service/target/inventory-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]

# order-service
FROM eclipse-temurin:17-jre AS order-service
WORKDIR /app
COPY --from=build /app/order-service/target/order-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]