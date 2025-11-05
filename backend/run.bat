@echo off
setlocal enabledelayedexpansion
REM === Configuración de directorios ===
set SCRIPT_DIR=%~dp0
set BUILD_DIR=%SCRIPT_DIR%out

REM === Eliminar y volver a crear carpeta de salida ===
if exist "%BUILD_DIR%" (
    echo Eliminando carpeta de salida anterior...
    rmdir /s /q "%BUILD_DIR%"
)
mkdir "%BUILD_DIR%"

REM === Buscar y compilar archivos .java ===
echo Compilando código fuente Java...
for /r "%SCRIPT_DIR%src\main\java" %%f in (*.java) do (
    echo %%f >> "%BUILD_DIR%\sources.txt"
)

javac --release 17 -d "%BUILD_DIR%" @"%BUILD_DIR%\sources.txt"
if %ERRORLEVEL% neq 0 (
    echo ❌ Error al compilar el código Java.
    exit /b 1
)

REM === Ejecutar clase principal ===
echo Iniciando la aplicación...
java -cp "%BUILD_DIR%" com.tuticuenta.server.AppServer

endlocal
pause
