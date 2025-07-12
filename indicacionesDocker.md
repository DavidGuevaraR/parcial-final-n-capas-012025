# 🚀 Cómo levantar este backend con Docker

Este proyecto está desarrollado con **Spring Boot + Maven** y requiere **Java 21**. Puedes levantarlo fácilmente usando Docker y Docker Compose.

---

## 📁 Requisitos

Antes de comenzar, asegúrate de tener instalado:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- Git (opcional)

---

## ⚙️ Estructura esperada

Tu proyecto debe contener los siguientes archivos en la raíz:

```
.
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── src/
└── README.md
```

---

## 🐳 Dockerfile (Java 21)

```dockerfile
# Etapa 1: Build con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con Java 21
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 🧬 docker-compose.yml

```yaml
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: parcialdb
    ports:
      - "5432:5432"
    networks:
      - backend-net

  springboot-app:
    build: .
    container_name: springboot-backend
    ports:
      - "8080:8080"
    depends_on:
      - db
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/parcialdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    networks:
      - backend-net

networks:
  backend-net:
```

> ⚠️ *Puedes eliminar la línea `version:` si ves un warning sobre obsolescencia.*

---

## ✅ Pasos para levantar el backend

1. **Abre la terminal en la raíz del proyecto**
   ```bash
   cd parcial-final-n-capas-012025
   ```

2. **Construye la imagen y levanta el contenedor**
   ```bash
   docker compose up --build
   ```

3. **Accede al backend**
   - En tu navegador: [http://localhost:8080](http://localhost:8080)

4. **Para detener el backend**
   ```bash
   docker compose down
   ```

---

## 🛠️ Solución de problemas

- **Error `release version 21 not supported`**  
  → Asegúrate de usar las imágenes de Docker con `temurin-21` como en el `Dockerfile`.

- **Error: `Cannot connect to the Docker daemon`**  
  → Verifica que Docker esté en ejecución:
    ```bash
    sudo systemctl start docker
    ```

- **Permisos en Linux**  
  Agrega tu usuario al grupo Docker (opcional):
  ```bash
  sudo usermod -aG docker $USER
  newgrp docker
  ```
