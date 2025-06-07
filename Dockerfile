# Usa una imagen de Java
FROM eclipse-temurin:21-jdk-alpine

# Directorio de trabajo
WORKDIR /app

# Copia el JAR generado
COPY target/product-microservice-*.jar app.jar

# Expone el puerto de la aplicación
EXPOSE 8001

# Usa las variables de entorno para Spring Boot
ENV SPRING_PROFILES_ACTIVE=default

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]