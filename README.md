# Tuticuenta

Aplicación web demostrativa para una cuenta de ahorros colombiana orientada a niños y niñas de 7 a 14 años. El proyecto incluye:

- **Backend Java ligero**: Servidor HTTP incluido en el JDK con autenticación firmada y almacenamiento en memoria para registrar familias y consultar el resumen de la cuenta en pesos colombianos.
- **Frontend Angular**: Interfaz lúdica con módulos de registro, inicio de sesión y tablero de progreso adaptados a la cultura colombiana.

## Requisitos

- Java 17 (incluye `javac`)
- Node.js 18+ y npm (para compilar Angular)

## Ejecución

1. **Backend**
   - Linux / macOS
     ```bash
     cd backend
     ./run.sh
     ```
   - Windows (PowerShell o CMD)
     ```powershell
     cd backend
     .\run.cmd
     ```
   - Nota: si venías de una versión anterior basada en Spring Boot y aún tienes carpetas como `backend/src/main/java/com/bankids`, bórralas o vuelve a clonar el repositorio. Los scripts de ejecución solo compilan las clases bajo `com/tuticuenta`.

2. **Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

El frontend está configurado para comunicarse con `http://localhost:8080`.

## Credenciales de prueba

- **Correo**: `tutora@tuticuenta.com`
- **Contraseña**: `tutisegura`

Con estas credenciales se carga una cuenta de ejemplo con metas y movimientos expresados en pesos colombianos.
