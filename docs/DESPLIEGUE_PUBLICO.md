# Despliegue público con dominio propio

Esta guía explica cómo publicar la aplicación bajo un dominio como `tuticuentaaprueba.com.co` para que cualquier persona pueda acceder al backend Java y al frontend Angular desde Internet.

## 1. Preparar la infraestructura

1. **Contrata un dominio** con un registrador (por ejemplo, Mi.com.co, GoDaddy, Namecheap, etc.).
2. **Provisiona un servidor** (VPS, instancia en la nube o máquina física) con acceso público a Internet y un sistema operativo compatible (Ubuntu 22.04 o similar).
3. **Abre los puertos 80 y 443** en el firewall del servidor y en cualquier capa de red intermedia para permitir tráfico HTTP y HTTPS. El puerto 8080 solo debe quedar accesible internamente porque lo utilizaremos detrás de un proxy.
4. (Opcional) **Configura un nombre host** para el servidor (por ejemplo, `app.tuticuentaaprueba.com.co`) si deseas separar ambientes.

## 2. Configurar DNS

1. Obtén la dirección IP pública del servidor.
2. En el panel del registrador crea un registro **A** (o **AAAA** si usas IPv6) apuntando el dominio o subdominio (`tuticuentaaprueba.com.co`, `app.tuticuentaaprueba.com.co`, etc.) a la IP del servidor.
3. Espera a que la propagación DNS surta efecto (puede tardar desde minutos hasta 24 h).

Puedes validar la propagación con:

```bash
dig tuticuentaaprueba.com.co +short
```

## 3. Desplegar el backend

1. Clona este repositorio en el servidor y entra a la carpeta `backend`.
2. Copia el driver de PostgreSQL en `backend/lib/` o define `POSTGRES_JDBC_JAR`.
3. Exporta las variables necesarias (por ejemplo `DATABASE_URL`) y crea un servicio para mantener el backend corriendo:

   ```bash
   cd /opt/tuticuenta/backend
   ./run.sh > /var/log/tuticuenta/backend.log 2>&1 &
   ```

   El servidor escucha en `0.0.0.0:8080`, por lo que seguirá listo para recibir solicitudes desde el proxy inverso.

4. Alternativamente, crea una unidad de systemd (`/etc/systemd/system/tuticuenta-backend.service`) que ejecute el script al arrancar el servidor.

## 4. Construir el frontend

1. Instala Node.js 18+ y npm.
2. Compila la aplicación Angular:

   ```bash
   cd /opt/tuticuenta/frontend
   npm ci
   npm run build
   ```

3. Copia el contenido de `dist/tuticuenta` al directorio donde el proxy servirá los archivos estáticos, por ejemplo `/var/www/tuticuenta`.

   ```bash
   sudo rm -rf /var/www/tuticuenta
   sudo cp -r dist/tuticuenta /var/www/tuticuenta
   ```

El frontend ya está configurado con `apiBaseUrl: '/api'`, por lo que mientras el proxy redirija `/api` al backend, no hace falta modificar el código.

## 5. Configurar proxy inverso (Nginx)

Instala Nginx y crea un archivo de configuración, por ejemplo `/etc/nginx/sites-available/tuticuenta`:

```nginx
server {
    listen 80;
    server_name tuticuentaaprueba.com.co;

    root /var/www/tuticuenta;
    index index.html;

    # Angular: devolver index.html para rutas cliente
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API Java
    location /api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

Activa el sitio y reinicia Nginx:

```bash
sudo ln -s /etc/nginx/sites-available/tuticuenta /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## 6. Habilitar HTTPS gratuito

1. Instala Certbot (`sudo apt install certbot python3-certbot-nginx`).
2. Ejecuta `sudo certbot --nginx -d tuticuentaaprueba.com.co` y sigue las instrucciones para emitir un certificado de Let's Encrypt.
3. Certbot actualizará automáticamente la configuración de Nginx para escuchar en 443 y redirigir HTTP a HTTPS. Verifica la renovación automática con `sudo systemctl status certbot.timer`.

## 7. Probar acceso externo

1. Visita `https://tuticuentaaprueba.com.co` desde un navegador externo y verifica que el frontend carga correctamente.
2. Inicia sesión con las credenciales de prueba y asegúrate de que las llamadas a `/api` responden.
3. Si necesitas probar el backend desde otro cliente (por ejemplo, Postman), usa la URL `https://tuticuentaaprueba.com.co/api/...` junto con el token Bearer.

## 8. Mantenimiento

- Automatiza el despliegue del frontend repitiendo `npm run build` y copiando los archivos tras cada actualización.
- Monitorea los logs del backend (`tail -f /var/log/tuticuenta/backend.log`).
- Actualiza certificados y paquetes del sistema con regularidad.
- Considera usar un proceso supervisor (systemd, pm2) para reiniciar servicios en caso de fallos.

Siguiendo estos pasos podrás compartir una URL amigable y segura para que otras personas accedan a la aplicación completa.
