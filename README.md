# Product Microservice - Microservicio de Gestión de Productos para E-commerce

## Descripción y Objetivo

Este proyecto forma parte de una serie de microservicios orientados a construir una aplicación completa de e-commerce. Previamente, se desarrolló y publicó el microservicio de autenticación; ahora es el turno del microservicio encargado de la **gestión de productos**. Este servicio permite el registro, consulta y administración de productos, así como la gestión de categorías y subcategorías a las que pueden pertenecer. Además, incorpora un sistema de **auditoría** para registrar acciones sensibles realizadas por administradores, validación de datos y manejo centralizado de excepciones para una gestión robusta de errores.

Todo el proyecto está diseñado bajo el paradigma de la **reactividad** usando **Spring WebFlux**, permitiendo procesar múltiples peticiones en paralelo y garantizando alta escalabilidad y rendimiento.

El microservicio incluye una **suite de pruebas de integración** que cubre todos los aspectos críticos del sistema, utilizando **TestContainers** y Docker para garantizar entornos de prueba aislados y reproducibles. 

---

## Principales Dependencias

A continuación, se destacan las principales dependencias utilizadas de la caja de herramientas que tiene Spring:

- **Spring Boot**: Framework principal para el desarrollo de aplicaciones Java modernas, facilita la configuración y despliegue de microservicios.
- **Spring WebFlux**: Permite construir APIs reactivas y no bloqueantes, ideales para aplicaciones que requieren alta concurrencia y escalabilidad.
- **Spring Data R2DBC**: Integración reactiva con bases de datos relacionales, permitiendo operaciones asíncronas sobre PostgreSQL.
- **Liquibase**: Herramienta de versionado y migración de esquemas de base de datos, asegurando consistencia y trazabilidad en los cambios.
- **TestContainers**: Permite ejecutar pruebas de integración utilizando contenedores Docker efímeros, garantizando entornos limpios y realistas para cada ejecución de test.
- **JUnit 5**: Framework de pruebas unitarias y de integración.
- **Lombok**: Reduce el código boilerplate en las entidades y servicios, facilitando la mantenibilidad.
- **Spring Boot Actuator**: Provee endpoints para monitoreo y gestión del microservicio en producción.
- **SpringDoc OpenAPI**: Genera documentación interactiva de la API REST automáticamente.

---

## Modelo de Datos y Relaciones

### Entidades

Las entidades principales del microservicio son:

- **Product**: Representa un producto del catálogo.
- **Category**: Categoría a la que pertenece un producto.
- **SubCategory**: Subcategoría asociada a una categoría.
- **ProductPrice**: Gestiona tanto el precio vigente de un producto como el historial de todos los precios previos, marcando claramente aquellos que han quedado obsoletos.
- **ProductAttribute**: Atributos personalizados de un producto.
- **ProductAudit**: Registro de auditoría de acciones sensibles.

### Relaciones

- Un **Product** pertenece a una **Category** y a una **SubCategory**.
- Un **Product** puede tener múltiples **ProductPrice**, **ProductImage** y **ProductAttribute**.
- **ProductAudit** registra acciones sobre productos, categorías y subcategorías, asociando cada registro con los identificadores correspondientes.

---

## Centralización de la Lógica en el Controlador de Productos

En lugar de fragmentar la lógica en múltiples controladores para cada entidad relacionada, se decidió **centralizar la lógica de gestión de productos en un solo controlador**. Esto permite abstraer la complejidad interna y ofrecer una API más sencilla y coherente para los consumidores. Así, las operaciones sobre productos, junto con sus precios, imágenes y atributos, se gestionan desde un único punto de entrada, facilitando la integración y reduciendo la complejidad para los desarrolladores que consumen la API.

---

## Variables de Entorno (.env)

El archivo `.env` permite definir de manera sencilla y centralizada las variables de entorno necesarias para la configuración del microservicio y su base de datos. Las variables principales son:

- `SPRING_API_PORT`: Puerto en el que se expone la API del microservicio.
- `DB_HOST`: Host de la base de datos (Si planeas despliegar la API y la base de datos en una misma red de docker recuerda que esta variable coincida con el nombre del servicio de la base de datos en docker-compose.yml.).
- `DB_PORT`: Puerto de la base de datos.
- `DB_NAME`: Nombre de la base de datos.
- `DB_USER`: Usuario de la base de datos.
- `DB_PASS`: Contraseña de la base de datos.

**Utilidad:**
Al definir estas variables, puedes cambiar fácilmente la configuración del entorno (desarrollo, pruebas, producción) sin modificar el código fuente. El microservicio y la base de datos se configuran automáticamente leyendo estos valores, facilitando el despliegue y la portabilidad.

---

## Compilación y Empaquetado

### Requisitos Previos

- Java 21 instalado.
- Maven instalado.
- Docker y Docker Compose instalados (para pruebas y despliegue).

### Pasos

1. Compila y Empaqueta el proyecto:
   ```sh
   mvn clean package
   ```
   Esto generará un archivo JAR en la carpeta `target/`.

---

## Ejecución de Pruebas

### Requisitos

- Docker debe estar corriendo (para TestContainers).
- Las variables de entorno de test están configuradas (por defecto, el proyecto ya está preparado para esto).

### Ejecución

Las pruebas de integración deben ejecutarse en un orden específico para garantizar la consistencia de los datos y la correcta inicialización de los contenedores. Para ello, utiliza la suite de test `RunAllIntegrationTests`:

```sh
mvn test -Dtest=RunAllIntegrationTests
```

O asegúrate de ejecutar los tests a partir del archivo `RunAllIntegrationTests.java`, que agrupa y ordena todas las pruebas de integración relevantes.

---

## Dockerización del Proyecto

### Requisitos Previos

- Docker y Docker Compose instalados.
- Archivo `.env` correctamente configurado con las variables necesarias.

### Pasos

1. Compila y empaqueta el proyecto (ver sección anterior).
2. Construye la imagen Docker del microservicio:
   ```sh
   docker-compose build
   ```
3. Levanta los servicios (microservicio y base de datos):
   ```sh
   docker-compose up
   ```
   Esto iniciará tanto la base de datos PostgreSQL como el microservicio, usando las variables definidas en `.env`.

---

## Resumen

Este microservicio es una pieza clave para la gestión de productos en una arquitectura de e-commerce basada en microservicios. Está preparado para escalar, es fácil de configurar y mantener, y cuenta con una suite de pruebas robusta y automatizada. Su diseño reactivo y su integración con herramientas modernas del ecosistema Java lo hacen ideal para proyectos profesionales y de alto rendimiento.

---