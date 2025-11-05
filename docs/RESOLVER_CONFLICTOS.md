# Guía rápida para resolver conflictos de fusión

Este repositorio mantiene ramas con historias distintas (por ejemplo, `main` y `codigo/create-bank-realistic-project-in-angular-and-java-data`).
Cuando ambas ramas modifican las mismas líneas de un archivo, Git detiene la fusión y
marca el archivo con delimitadores especiales para que puedas decidir qué contenido conservar.

## 1. Identificar los marcadores de conflicto

En el archivo verás bloques con este formato:

```text
<<<<<<< HEAD
contenido de la rama donde estás trabajando
=======
contenido de la rama que quieres fusionar
>>>>>>> nombre-de-la-otra-rama
```

Todo lo que esté entre `<<<<<<<` y `=======` pertenece a tu rama actual. El bloque entre `=======`
y `>>>>>>>` proviene de la otra rama. En el caso del `README.md`, verás algo así como
`# Tuticuenta` en un lado y `# Bankids Savings` en el otro.

## 2. Elegir y combinar el contenido correcto

Edita manualmente el archivo para quedarte con la versión que deba prevalecer o combina ambos
textos si es necesario. Elimina por completo las líneas con los marcadores (`<<<<<<<`, `=======`, `>>>>>>>`).

Por ejemplo, si quieres conservar el nuevo nombre del proyecto, el bloque final podría quedar como:

```markdown
# Tuticuenta
```

Si prefieres mencionar ambos nombres, puedes redactar algo como:

```markdown
# Tuticuenta
*(antes conocido como Bankids Savings)*
```

Lo importante es que al terminar no queden marcadores y que el documento tenga sentido.

## 3. Validar y marcar el conflicto como resuelto

Una vez que el archivo esté limpio:

```bash
git add README.md
```

Puedes repetir el paso `git status` para confirmar que ya no hay archivos en conflicto. Cuando
todos estén resueltos, completa la fusión con un commit:

```bash
git commit
```

## 4. Recomendaciones finales

- Si tenías la aplicación en ejecución, recompila o reiníciala para asegurarte de que todo siga funcionando.
- Comparte con tu equipo qué cambios tomaste para que todos actualicen sus ramas.
- Evita dejar archivos compilados (`target/`, `dist/`, etc.) en el repositorio; agrega estas rutas al `.gitignore` si todavía no están.

Seguir estos pasos te permitirá resolver conflictos similares en el futuro sin perder cambios.
