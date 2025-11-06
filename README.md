# Tuticuenta

Aplicación web demostrativa para una cuenta de ahorros colombiana dirigida a niñas, niños y adolescentes entre 0 y 17 años, desarrollada en colaboración con Banco Caja Social. El proyecto incluye:

- **Backend Java ligero**: Servidor HTTP incluido en el JDK que se conecta a PostgreSQL para gestionar registros, credenciales y resúmenes de cuenta en pesos colombianos.
- **Frontend Angular**: Interfaz lúdica y vibrante con módulos de registro, inicio de sesión y tablero de progreso inspirados en el universo de Tuticuenta y Banco Caja Social.
- **Gestión interactiva**: Desde el tablero ahora es posible añadir nuevos movimientos y metas de ahorro sin salir de la aplicación.

## Requisitos

- Java 17 (incluye `javac`)
- Controlador JDBC de PostgreSQL (por ejemplo `postgresql-42.x.x.jar`). Colócalo en `backend/lib/` o define la variable de entorno `POSTGRES_JDBC_JAR` con la ruta al archivo.
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
   - Define la variable `DATABASE_URL` si necesitas apuntar a otra instancia. Por defecto se utiliza `jdbc:postgresql://db.xjelotkvsagcinxbkgkh.supabase.co:5432/postgres?user=postgres&password=Garzon103*`.
   - Los scripts añaden automáticamente el primer `.jar` encontrado en `backend/lib/` al classpath; alternativamente, exporta `POSTGRES_JDBC_JAR` con la ruta al driver.
   - El servidor queda expuesto en todas las interfaces de red (`0.0.0.0`), por lo que se puede consumir desde clientes externos siempre que el firewall lo permita.
   - Nota: si venías de una versión anterior basada en Spring Boot y aún tienes carpetas como `backend/src/main/java/com/bankids`, bórralas o vuelve a clonar el repositorio. Los scripts de ejecución solo compilan las clases bajo `com/tuticuenta`.

2. **Frontend**
   ```bash
   cd frontend
   npm install
   npm start
   ```

   El comando incluye un proxy de desarrollo para que las llamadas a `/api` se reenvíen al backend que se ejecute en `localhost:8080`.

El frontend está configurado para comunicarse con `http://localhost:8080`.

La primera ejecución del backend crea las tablas `tuticuenta_accounts`, `tuticuenta_transactions` y `tuticuenta_goals` si no existen, por lo que basta con iniciar el servidor para preparar la base de datos.

## Despliegue público

Si quieres compartir la aplicación bajo un dominio como `tuticuentaaprueba.com.co`, sigue la [guía de despliegue público](docs/DESPLIEGUE_PUBLICO.md) para configurar DNS, proxy inverso, HTTPS y la publicación del frontend.

## Credenciales de prueba

- **Correo**: `tutora@tuticuenta.com`
- **Contraseña**: `tutisegura`

Con estas credenciales se carga una cuenta de ejemplo con metas y movimientos expresados en pesos colombianos.
