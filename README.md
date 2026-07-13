# Matricula

## Requisitos

- JDK 21
- Docker
- Driver JDBC de PostgreSQL: `lib/postgresql-42.7.4.jar` (ya incluido en el repo)

## 1. Levantar la base de datos

```bash
docker compose up -d
```

Esto crea el contenedor `postgres_container` con la base `matricula` en el puerto `5432` (usuario/clave: `admin`/`admin`, ver `docker-compose.yaml`).

## 2. Compilar

```bash
javac -cp "lib/postgresql-42.7.4.jar" -d out/production/Matricula $(find src -name "*.java")
```

## 3. Ejecutar

Windows:

```bash
java -cp "out/production/Matricula;lib/postgresql-42.7.4.jar" Main
```

## IntelliJ IDEA

El módulo (`Matricula.iml`) ya referencia `lib/postgresql-42.7.4.jar` como librería.
