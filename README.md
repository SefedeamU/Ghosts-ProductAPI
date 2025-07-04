Product Microservice - Microservicio de Gestión de Productos para E-commerce
![alt text](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![alt text](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![alt text](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![alt text](https://img.shields.io/badge/Liquibase-2962FF?style=for-the-badge&logo=liquibase&logoColor=white)
![alt text](https://img.shields.io/badge/Testcontainers-262261?style=for-the-badge&logo=testcontainers&logoColor=white)
![alt text](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![alt text](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

🎯 Descripción y Objetivo
Este proyecto forma parte de una serie de microservicios orientados a construir una aplicación completa de e-commerce. Previamente, se desarrolló y publicó el microservicio de autenticación; ahora es el turno del microservicio encargado de la gestión de productos. Este servicio permite el registro, consulta y administración de productos, así como la gestión de categorías y subcategorías a las que pueden pertenecer. Además, incorpora un sistema de auditoría para registrar acciones sensibles realizadas por administradores, validación de datos y manejo centralizado de excepciones para una gestión robusta de errores.

Todo el proyecto está diseñado bajo el paradigma de la reactividad usando Spring WebFlux, permitiendo procesar múltiples peticiones en paralelo y garantizando alta escalabilidad y rendimiento.

El microservicio incluye una suite de pruebas de integración que cubre todos los aspectos críticos del sistema, utilizando TestContainers y Docker para garantizar entornos de prueba aislados y reproducibles.

📊 Visualización de la Arquitectura
(Sugerencia: Puedes añadir aquí diagramas para una mejor comprensión visual)

Diagrama de Arquitectura del Microservicio
[Aquí puedes insertar un diagrama que muestre la interacción entre WebFlux, R2DBC y la base de datos]

Diagrama Entidad-Relación
[Aquí puedes insertar un diagrama visual del modelo de datos]

🛠️ Principales Dependencias
A continuación, se destacan las principales dependencias utilizadas de la caja de herramientas que tiene Spring:

Spring Boot: Framework principal para el desarrollo de aplicaciones Java modernas, facilita la configuración y despliegue de microservicios.

Spring WebFlux: Permite construir APIs reactivas y no bloqueantes, ideales para aplicaciones que requieren alta concurrencia y escalabilidad.

Spring Data R2DBC: Integración reactiva con bases de datos relacionales, permitiendo operaciones asíncronas sobre PostgreSQL.

Liquibase: Herramienta de versionado y migración de esquemas de base de datos, asegurando consistencia y trazabilidad en los cambios.

TestContainers: Permite ejecutar pruebas de integración utilizando contenedores Docker efímeros, garantizando entornos limpios y realistas para cada ejecución de test.

JUnit 5: Framework de pruebas unitarias y de integración.

Lombok: Reduce el código boilerplate en las entidades y servicios, facilitando la mantenibilidad.

Spring Boot Actuator: Provee endpoints para monitoreo y gestión del microservicio en producción.

SpringDoc OpenAPI: Genera documentación interactiva de la API REST automáticamente.

🧬 Modelo de Datos y Relaciones
Entidades
Las entidades principales del microservicio son:

Product: Representa un producto del catálogo.

Category: Categoría a la que pertenece un producto.

SubCategory: Subcategoría asociada a una categoría.

ProductPrice: Gestiona tanto el precio vigente de un producto como el historial de todos los precios previos, marcando claramente aquellos que han quedado obsoletos.

ProductAttribute: Atributos personalizados de un producto.

ProductAudit: Registro de auditoría de acciones sensibles.

Relaciones
Un Product pertenece a una Category y a una SubCategory.

Un Product puede tener múltiples ProductPrice, ProductImage y ProductAttribute.

ProductAudit registra acciones sobre productos, categorías y subcategorías, asociando cada registro con los identificadores correspondientes.

🏛️ Centralización de la Lógica en el Controlador de Productos
En lugar de fragmentar la lógica en múltiples controladores para cada entidad relacionada, se decidió centralizar la lógica de gestión de productos en un solo controlador. Esto permite abstraer la complejidad interna y ofrecer una API más sencilla y coherente para los consumidores. Así, las operaciones sobre productos, junto con sus precios, imágenes y atributos, se gestionan desde un único punto de entrada, facilitando la integración y reduciendo la complejidad para los desarrolladores que consumen la API.

⚙️ Variables de Entorno (.env)
El archivo .env permite definir de manera sencilla y centralizada las variables de entorno necesarias para la configuración del microservicio y su base de datos. Las variables principales son:

SPRING_API_PORT: Puerto en el que se expone la API del microservicio.

DB_HOST: Host de la base de datos (Si planeas despliegar la API y la base de datos en una misma red de docker recuerda que esta variable coincida con el nombre del servicio de la base de datos en docker-compose.yml).

DB_PORT: Puerto de la base de datos.

DB_NAME: Nombre de la base de datos.

DB_USER: Usuario de la base de datos.

DB_PASS: Contraseña de la base de datos.

Utilidad:
Al definir estas variables, puedes cambiar fácilmente la configuración del entorno (desarrollo, pruebas, producción) sin modificar el código fuente. El microservicio y la base de datos se configuran automáticamente leyendo estos valores, facilitando el despliegue y la portabilidad.

📦 Compilación y Empaquetado
Requisitos Previos
Java 21 instalado.

Maven instalado.

Docker y Docker Compose instalados (para pruebas y despliegue).

Pasos
Compila y Empaqueta el proyecto:

Generated sh
mvn clean package
Use code with caution.
Sh
Esto generará un archivo JAR en la carpeta target/.

✅ Ejecución de Pruebas
Requisitos
Docker debe estar corriendo (para TestContainers).

Las variables de entorno de test están configuradas (por defecto, el proyecto ya está preparado para esto).

Ejecución
Las pruebas de integración deben ejecutarse en un orden específico para garantizar la consistencia de los datos y la correcta inicialización de los contenedores. Para ello, utiliza la suite de test RunAllIntegrationTests:

Generated sh
mvn test -Dtest=RunAllIntegrationTests
Use code with caution.
Sh
O asegúrate de ejecutar los tests a partir del archivo RunAllIntegrationTests.java, que agrupa y ordena todas las pruebas de integración relevantes.

🐳 Dockerización del Proyecto
Requisitos Previos
Docker y Docker Compose instalados.

Archivo .env correctamente configurado con las variables necesarias.

Pasos
Compila y empaqueta el proyecto (ver sección anterior).

Construye la imagen Docker del microservicio:

Generated sh
docker-compose build
Use code with caution.
Sh
Levanta los servicios (microservicio y base de datos):

Generated sh
docker-compose up
Use code with caution.
Sh
Esto iniciará tanto la base de datos PostgreSQL como el microservicio, usando las variables definidas en .env.

📝 Resumen
Este microservicio es una pieza clave para la gestión de productos en una arquitectura de e-commerce basada en microservicios. Está preparado para escalar, es fácil de configurar y mantener, y cuenta con una suite de pruebas robusta y automatizada. Su diseño reactivo y su integración con herramientas modernas del ecosistema Java lo hacen ideal para proyectos profesionales y de alto rendimiento.
