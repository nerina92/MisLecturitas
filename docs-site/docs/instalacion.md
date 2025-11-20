---
sidebar_position: 5
---

# Instalación y Configuración

Esta guía te ayudará a configurar el entorno de desarrollo para trabajar en **Mis Lecturitas**.

## Requisitos previos

### Software necesario

1. **Android Studio**
   - Versión: Hedgehog (2023.1.1) o superior
   - [Descargar Android Studio](https://developer.android.com/studio)

2. **JDK 11**
   - Incluido con Android Studio
   - O descarga desde [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)

3. **Git**
   - [Descargar Git](https://git-scm.com/downloads)

4. **Cuenta de Firebase**
   - [Firebase Console](https://console.firebase.google.com/)

## Clonar el repositorio

```bash
git clone https://github.com/nerina92/MisLecturitas.git
cd MisLecturitas
```

## Configuración de Firebase

### 1. Crear proyecto en Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Haz clic en "Agregar proyecto"
3. Sigue los pasos del asistente

### 2. Agregar aplicación Android

1. En la consola de Firebase, haz clic en el ícono de Android
2. Ingresa el ID del paquete: `edu.mis.lecturitas`
3. Descarga el archivo `google-services.json`
4. Coloca el archivo en `app/google-services.json`

### 3. Habilitar servicios

#### Authentication
1. Ve a **Authentication** → **Sign-in method**
2. Habilita **Email/Password**

#### Realtime Database
1. Ve a **Realtime Database**
2. Haz clic en "Crear base de datos"
3. Selecciona una ubicación
4. Inicia en "modo de prueba" (luego configuraremos las reglas)

#### Storage
1. Ve a **Storage**
2. Haz clic en "Comenzar"
3. Acepta las reglas predeterminadas

## Configuración del proyecto

### 1. Abrir en Android Studio

1. Abre Android Studio
2. Selecciona **File** → **Open**
3. Navega a la carpeta del proyecto
4. Haz clic en **OK**

### 2. Sincronizar Gradle

Cuando Android Studio abra el proyecto:
1. Espera a que se indexen los archivos
2. Haz clic en **Sync Now** en el banner superior
3. Espera a que se descarguen todas las dependencias

### 3. Configurar emulador o dispositivo

#### Opción A: Usar emulador
1. Ve a **Tools** → **Device Manager**
2. Haz clic en **Create Device**
3. Selecciona un dispositivo (recomendado: Pixel 6)
4. Descarga una imagen del sistema (API 33 o superior)
5. Finaliza la creación

#### Opción B: Usar dispositivo físico
1. Habilita las opciones de desarrollador en tu dispositivo
2. Activa la depuración USB
3. Conecta el dispositivo mediante USB
4. Acepta la autorización en el dispositivo

## Estructura de archivos clave

```
MisLecturitas/
├── app/
│   ├── google-services.json       # Configuración de Firebase (no en Git)
│   ├── build.gradle.kts            # Dependencias del módulo app
│   └── src/
│       └── main/
│           └── java/edu/mis/lecturitas/
│
├── build.gradle.kts                # Configuración del proyecto
├── gradle/                         # Wrapper de Gradle
├── local.properties                # Configuración local (no en Git)
└── settings.gradle.kts             # Configuración de módulos
```

## Ejecutar la aplicación

### Desde Android Studio

1. Selecciona el dispositivo/emulador en la barra superior
2. Haz clic en el botón **Run** (▶️) o presiona `Shift + F10`
3. Espera a que se compile e instale la aplicación

### Desde línea de comandos

```bash
# Compilar debug APK
./gradlew assembleDebug

# Instalar en dispositivo conectado
./gradlew installDebug

# Ejecutar app
adb shell am start -n edu.mis.lecturitas/.MainActivity
```

## Problemas comunes

### Error: "google-services.json not found"

**Solución**: Descarga el archivo desde Firebase Console y colócalo en `app/google-services.json`

### Error: "SDK not found"

**Solución**: 
1. Abre `File` → `Project Structure`
2. Ve a `SDK Location`
3. Configura la ruta correcta del Android SDK

### Error de compilación de Gradle

**Solución**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### La app se cierra al iniciar

**Solución**: 
1. Verifica que Firebase esté configurado correctamente
2. Revisa los logs en Logcat
3. Asegúrate de que las reglas de Firebase permitan lectura

## Configurar reglas de Firebase

Una vez que tengas datos en Firebase, aplica estas reglas de seguridad:

```json
{
  "rules": {
    "libros": {
      ".read": true,
      ".write": "auth != null",
      ".indexOn": ["nivel", "idLibro"]
    },
    "audiolibros": {
      ".read": true,
      ".write": "auth != null",
      ".indexOn": ["nivel", "idAudioLibro"]
    },
    "usuarios": {
      ".read": true,
      ".write": false,
      ".indexOn": ["user"]
    }
  }
}
```

## Próximos pasos

¡Listo! Ahora puedes:

- 📖 Leer la [Arquitectura](./arquitectura.md) del proyecto
- 🔐 Revisar la implementación de [Firebase Auth](./firebase-auth.md)
- 💻 Explorar el código y hacer cambios
- 🧪 Ejecutar pruebas con `./gradlew test`

## Recursos adicionales

- [Documentación de Android](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase para Android](https://firebase.google.com/docs/android/setup)
