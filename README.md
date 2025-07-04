# Microservicio Orquestador y API Gateway (NestJS)

![NestJS](https://img.shields.io/badge/NestJS-E0234E?style=for-the-badge&logo=nestjs&logoColor=white) ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white) ![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

Este proyecto es un **API Gateway y Orquestador de microservicios** desarrollado con **NestJS** y **TypeScript**. Actúa como el punto de entrada único para una arquitectura de e-commerce, gestionando la comunicación entre los diferentes servicios backend y aplicando una capa de seguridad centralizada.

Este servicio es una pieza clave dentro de una suite de microservicios que incluye:
*   [Microservicio de Productos (Java & Spring WebFlux)](https://github.com/tu-usuario/repo-productos)
*   [Microservicio de Autenticación (Python & FastAPI)](https://github.com/tu-usuario/repo-autenticacion)
*   _(Opcional: Enlace al microservicio de facturación)_

---

## 🎯 Rol y Responsabilidades del Servicio

El objetivo de este microservicio es doble:

1.  **Orquestador:** Centraliza y simplifica las operaciones que requieren la colaboración de múltiples servicios. Por ejemplo, al crear una factura, este servicio se comunica internamente con el microservicio de usuarios y el de productos para obtener la información necesaria.
2.  **API Gateway:** Actúa como una fachada única para el frontend u otros clientes, ocultando la complejidad de la arquitectura distribuida. Es responsable de:
    *   **Enrutamiento Inteligente:** Dirigir las peticiones entrantes al microservicio correcto.
    *   **Seguridad Centralizada:** Aplicar políticas de autenticación y autorización.
    *   **Manejo de Errores Unificado:** Capturar y transformar errores de los servicios internos en respuestas consistentes para el cliente.

---

## ✨ Características Principales

*   **Arquitectura Modular con NestJS:** El código está organizado en módulos de dominio (facturas, usuarios, productos) para una máxima mantenibilidad y escalabilidad.
*   **Seguridad Robusta con Guards:** Se utilizan **Guards de NestJS** para implementar una lógica de autorización centralizada, protegiendo rutas críticas basadas en roles de usuario (`user`/`admin`) y en la propiedad de los recursos.
*   **Contenerización con Docker:** La aplicación está completamente contenerizada, y se utilizan **redes de Docker** para gestionar una comunicación segura y eficiente entre los microservicios en un entorno aislado.
*   **Manejo de Errores Resiliente:** Diseñado para capturar fallos de los servicios subyacentes y devolver respuestas de error HTTP claras y estandarizadas, mejorando la robustez del sistema.
*   **Contratos de Datos (DTOs):** Uso de Data Transfer Objects (DTOs) con `class-validator` y `class-transformer` para garantizar la integridad y validación de los datos en todas las peticiones.
*   **Calidad de Código:** Configurado con ESLint y Prettier para asegurar un estilo de código consistente y limpio.

---

## 🛠️ Stack Tecnológico

*   **Framework:** NestJS
*   **Lenguaje:** TypeScript
*   **Entorno de Ejecución:** Node.js
*   **Contenerización:** Docker & Docker Compose
*   **Herramientas de Testing API:** Postman

---

## 🚀 Cómo Ejecutar el Proyecto

Para levantar este microservicio, es necesario tener los otros servicios de la arquitectura corriendo en la misma red de Docker.

### Requisitos Previos

*   [Docker](https://www.docker.com/get-started) y [Docker Compose](https://docs.docker.com/compose/install/) instalados.
*   Los microservicios de `usuarios` y `productos` deben estar ejecutándose.
*   Una red de Docker compartida creada. Si no existe, puedes crearla con:
    ```sh
    docker network create mi-red-ecommerce
    ```

### Pasos para el Despliegue

1.  **Clona el repositorio:**
    ```sh
    git clone https://github.com/tu-usuario/repo-orquestador.git
    cd repo-orquestador
    ```

2.  **Configura las variables de entorno:**
    Crea un archivo `.env` en la raíz del proyecto a partir del archivo `.env.example`. Este archivo debe contener las URLs de los otros microservicios.
    ```env
    # .env
    PORT=3000
    USERS_API_URL=http://servicio-usuarios:8001
    PRODUCTS_API_URL=http://servicio-productos:8002
    ```
    *Nota: Los nombres de host (`servicio-usuarios`, `servicio-productos`) deben coincidir con los nombres de los servicios definidos en los archivos `docker-compose.yml` de los otros proyectos.*

3.  **Construye y levanta el contenedor:**
    Asegúrate de que tu `docker-compose.yml` conecte este servicio a la red compartida.
    ```sh
    docker-compose up --build -d
    ```

4.  **¡Listo!**
    El API Gateway estará disponible en `http://localhost:3000`.

---

## 📄 Colección de Postman

Para facilitar las pruebas y la interacción con la API, se incluye una colección de Postman en el repositorio.

*   Puedes importar el archivo `[nombre-de-tu-coleccion].postman_collection.json` directamente en Postman para tener acceso a todos los endpoints preconfigurados.

---

## ✍️ Autor

*   **Sergio Delgado Amado** - [SefedeamU](https://github.com/SefedeamU)
