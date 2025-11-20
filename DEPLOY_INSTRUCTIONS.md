# 📚 Instrucciones para Deploy de Documentación

## ✅ GitHub Actions configurado

Se ha configurado un workflow de GitHub Actions para desplegar automáticamente la documentación a GitHub Pages.

## 🚀 Pasos para activar el deploy:

### 1. Hacer commit y push de los cambios

```bash
cd /Users/des-nuliana/Documents/MisLecturitas

# Ver cambios
git status

# Agregar todos los archivos nuevos
git add .github/workflows/deploy-docs.yml
git add docs-site/

# Hacer commit
git commit -m "Add Docusaurus documentation and GitHub Actions deployment"

# Push al repositorio
git push origin Juegos
```

### 2. Habilitar GitHub Pages en el repositorio

1. Ve a tu repositorio en GitHub: https://github.com/nerina92/MisLecturitas
2. Haz clic en **Settings** (Configuración)
3. En el menú lateral, haz clic en **Pages**
4. En **Source** (Fuente), selecciona:
   - Source: **GitHub Actions**
5. Guarda los cambios

### 3. Ejecutar el workflow

El workflow se ejecutará automáticamente cuando:
- Hagas push a los branches `Juegos` o `main`
- Modifiques archivos en `docs-site/`

También puedes ejecutarlo manualmente:
1. Ve a la pestaña **Actions** en GitHub
2. Selecciona el workflow "Deploy Documentation to GitHub Pages"
3. Haz clic en **Run workflow**
4. Selecciona el branch y haz clic en **Run workflow**

### 4. Ver tu documentación

Una vez que el workflow termine (tarda ~2-3 minutos):

**URL de tu documentación:**
```
https://nerina92.github.io/MisLecturitas/
```

## 📊 Monitorear el deploy

- Ve a la pestaña **Actions** en tu repositorio
- Verás el workflow ejecutándose
- Puede tardar unos minutos la primera vez

## 🔧 Troubleshooting

### Si el workflow falla:

1. **Verifica los permisos**: En Settings → Actions → General → Workflow permissions, asegúrate de que esté seleccionado "Read and write permissions"

2. **Verifica GitHub Pages**: En Settings → Pages, asegúrate de que Source esté en "GitHub Actions"

3. **Revisa los logs**: En la pestaña Actions, haz clic en el workflow fallido para ver los detalles

### Si la página no se ve correctamente:

El archivo `docusaurus.config.js` ya está configurado con:
```javascript
url: 'https://nerina92.github.io',
baseUrl: '/MisLecturitas/',
```

## 📝 Archivos configurados

- `.github/workflows/deploy-docs.yml` - Workflow de GitHub Actions
- `docs-site/docusaurus.config.js` - Configuración de Docusaurus
- `docs-site/docs/` - Documentación en Markdown
- `docs-site/blog/` - Posts de blog

## 🎯 Próximos pasos después del deploy

1. Visita https://nerina92.github.io/MisLecturitas/
2. Verifica que todo se vea correctamente
3. Comparte el link de la documentación

## 💡 Actualizar la documentación

Para actualizar la documentación en el futuro:

1. Edita los archivos en `docs-site/docs/` o `docs-site/blog/`
2. Haz commit y push
3. GitHub Actions automáticamente hará el deploy

```bash
# Editar archivos...
git add docs-site/
git commit -m "Update documentation"
git push
```

¡Eso es todo! 🎉
