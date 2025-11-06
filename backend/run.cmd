@echo off
setlocal enabledelayedexpansion

rem Determinar directorio del script sin barra final
set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
set "BUILD_DIR=%SCRIPT_DIR%\out"
set "SOURCES_LIST=%TEMP%\tuticuenta_sources_%RANDOM%%RANDOM%.txt"

rem Limpiar compilaciones anteriores
if exist "%BUILD_DIR%" rd /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"
if errorlevel 1 goto :fail

rem Listar las fuentes Java en un archivo temporal
echo Compilando fuentes Java...
>"%SOURCES_LIST%" (
  for /r "%SCRIPT_DIR%\src\main\java\com\tuticuenta" %%f in (*.java) do (
    set "file=%%f"
    set "file=!file:\=/!"
    echo ^"!file!^"
  )
)

javac --release 17 -d "%BUILD_DIR%" @"%SOURCES_LIST%"
if errorlevel 1 goto :fail

del "%SOURCES_LIST%" >nul 2>&1

set "CLASSPATH=%BUILD_DIR%;%SCRIPT_DIR%\lib\postgresql-42.7.3.jar"
echo CLASSPATH=%CLASSPATH%


java -cp "%CLASSPATH%" com.tuticuenta.server.AppServer
set "EXITCODE=%ERRORLEVEL%"
goto :cleanup

:fail
set "EXITCODE=%ERRORLEVEL%"
if exist "%SOURCES_LIST%" del "%SOURCES_LIST%" >nul 2>&1

:cleanup
if not defined EXITCODE set "EXITCODE=1"
endlocal & exit /b %EXITCODE%
