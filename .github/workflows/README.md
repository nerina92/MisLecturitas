# 🤖 GitHub Actions - Compilación Automática de APKs

Este proyecto usa GitHub Actions para compilar automáticamente los APKs de la aplicación.

## 📦 Workflows Configurados

### 1. **Build Debug APK** (`build-debug.yml`)

**Se ejecuta cuando:**
- Hacés push a la rama `feature/gamification`
- Hacés push a la rama `develop`
- Creás un Pull Request hacia `master` o `main`

**Genera:**
- APK Debug en Artifacts (disponible por 90 días)

**Cómo descargar:**
1. Andá a: https://github.com/nerina92/MisLecturitas/actions
2. Click en el workflow más reciente (verde ✓)
3. Scrolleá hasta "Artifacts"
4. Click en "app-debug" para descargar

---

### 2. **Build Release APK** (`build-release.yml`)

**Se ejecuta cuando:**
- Creás un tag de versión (ej: `v2.1.0`, `v2.1.0-beta`)

**Genera:**
- APK Release firmado
- Release automático en GitHub
- APK permanente descargable

**Cómo usar:**

```bash
# 1. Commitear todos los cambios
git add .
git commit -m "Ready for release"

# 2. Crear tag de versión
git tag v2.1.0-gamification

# 3. Push del tag
git push origin v2.1.0-gamification

# 4. Esperar 5-10 minutos
# 5. El APK aparecerá en:
#    https://github.com/nerina92/MisLecturitas/releases
```

---

## 🎯 Ejemplos de Uso

### Para Testing (Debug)

```bash
# Hacer cambios en el código
git add .
git commit -m "feat: new feature"
git push origin feature/gamification

# Esperar 5 minutos
# Descargar APK desde Actions > Artifacts
```

### Para Lanzamiento (Release)

```bash
# Versión estable
git tag v2.1.0
git push origin v2.1.0

# Versión beta
git tag v2.1.0-beta
git push origin v2.1.0-beta

# Alpha/RC/etc
git tag v2.1.0-alpha.1
git push origin v2.1.0-alpha.1
```

---

## 📱 Instalación del APK en Android

1. **Descargar** el APK desde GitHub
2. **En tu celular:**
   - Configuración > Seguridad > Habilitar "Orígenes desconocidos"
3. **Abrir** el archivo APK descargado
4. **Instalar**

---

## ⚙️ Configuración de los Workflows

### JDK Version
Los workflows usan **Java 17** (Temurin distribution)

### Gradle Cache
Los workflows cachean las dependencias de Gradle para builds más rápidos

### Retención de Artifacts
- **Debug APKs**: 90 días
- **Release APKs**: 365 días (también en Releases permanentemente)

---

## 🔧 Solución de Problemas

### Build Falla

1. Verificá los logs en la pestaña Actions
2. Buscá errores de compilación en la sección "Build Debug/Release APK"
3. Los reportes completos están en los artifacts "build-reports"

### No Aparece el APK

- **Debug**: Esperá 5-10 minutos después del push
- **Release**: Esperá 5-10 minutos después de crear el tag
- Verificá que el build haya sido exitoso (verde ✓)

### APK No Instala

- Verificá que tengas permisos para instalar apps de fuentes desconocidas
- Asegurate de que el APK esté completamente descargado
- Verificá espacio disponible en el dispositivo

---

## 📊 Estado del Build

Podés ver el estado actual del build en:
https://github.com/nerina92/MisLecturitas/actions

Badge de estado (agregar al README principal):
```markdown
![Build Status](https://github.com/nerina92/MisLecturitas/workflows/Build%20Debug%20APK/badge.svg)
```

---

## 🎉 Listo!

Ahora cada vez que hagas push o crees un tag, GitHub compila automáticamente tu app 🚀
