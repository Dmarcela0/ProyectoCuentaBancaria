# Tuticuenta

Aplicación web demostrativa para una cuenta de ahorros orientada a niños y niñas de 7 a 14 años. El proyecto incluye:

- **Backend Java (Spring Boot)**: API con autenticación JWT, almacenamiento en memoria y endpoints para gestionar sesiones y consultar el resumen de la cuenta.
- **Frontend Angular**: Interfaz lúdica con módulos de registro, inicio de sesión y tablero de progreso.

## Requisitos

- Java 17 y Maven
- Node.js 18+ y npm (para compilar Angular)

## Ejecución

1. **Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

El frontend está configurado para comunicarse con `http://localhost:8080`.

## Credenciales de prueba

- **Correo**: `demo@tuticuenta.com`
- **Contraseña**: `demo123`

Con estas credenciales se carga una cuenta de ejemplo con metas y movimientos.
