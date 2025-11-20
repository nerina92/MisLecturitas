---
sidebar_position: 3
---

# Tecnologías Utilizadas

## Frontend (Android)

### Lenguaje de programación

#### Kotlin
- **Versión JVM**: 11
- **Kotlin Compiler**: 2.0.0
- Lenguaje oficial de Android, moderno y conciso
- Soporte completo para programación funcional
- Null-safety integrado

### UI Framework

#### Jetpack Compose
```kotlin
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.ui)
implementation(libs.androidx.material3)
```

- **Material Design 3**: Última versión del sistema de diseño de Google
- **Composables**: UI declarativa y reactiva
- **Icons Extended**: Conjunto completo de iconos Material
- **Runtime LiveData**: Integración con LiveData de Android

### Arquitectura

#### AndroidX Core
```kotlin
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.activity.compose)
```

- **Core KTX**: Extensiones de Kotlin para Android
- **Lifecycle**: Manejo del ciclo de vida
- **Activity Compose**: Integración de Compose con Activities

### Inyección de dependencias

#### Koin
```kotlin
implementation(libs.koin.android)
implementation(libs.koin.core)
```

- Framework ligero de inyección de dependencias
- DSL en Kotlin para definir módulos
- Fácil integración con Android y Compose

### Carga de imágenes

#### Coil
```kotlin
implementation(libs.coil.compose)
```

- Biblioteca moderna para carga de imágenes
- Optimizada para Kotlin y Compose
- Caché automático
- Soporte para URLs y recursos locales

## Backend (Firebase)

### Firebase Platform
```kotlin
implementation(platform(libs.firebase.bom))
```

El **Bill of Materials (BOM)** de Firebase garantiza versiones compatibles de todas las bibliotecas.

### Servicios de Firebase

#### 1. Firebase Authentication
```kotlin
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.firebaseui:firebase-ui-auth:7.2.0")
```

- Autenticación de usuarios con email/password
- Manejo seguro de tokens
- Gestión de sesiones

#### 2. Firebase Realtime Database
```kotlin
implementation("com.google.firebase:firebase-database-ktx")
```

- Base de datos NoSQL en tiempo real
- Sincronización automática
- Acceso offline
- Estructura JSON flexible

**Estructura de datos:**
```json
{
  "libros": {
    "idLibro": {
      "titulo": "...",
      "autor": "...",
      "nivel": 1,
      "imagen": "url"
    }
  },
  "audiolibros": { ... },
  "usuarios": { ... }
}
```

#### 3. Firebase Storage
- Almacenamiento de imágenes de portadas
- URLs de descarga automáticas
- Gestión de permisos

#### 4. Firebase Analytics
```kotlin
implementation("com.google.firebase:firebase-analytics")
```

- Seguimiento de eventos
- Análisis de uso de la aplicación
- Métricas de rendimiento

## Build & Deploy

### Gradle
```kotlin
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services") version "4.4.1"
    alias(libs.plugins.kotlinCompose)
}
```

- **Android Gradle Plugin**: 8.x
- **Google Services**: Integración de Firebase
- **Kotlin Compose Plugin**: Compilador optimizado

### Configuración de compilación

```kotlin
android {
    compileSdk = 35
    minSdk = 23
    targetSdk = 35
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
```

## Testing

### Frameworks de prueba
```kotlin
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.ui.test.junit4)
```

- **JUnit**: Pruebas unitarias
- **Espresso**: Pruebas de UI
- **Compose Test**: Testing de composables

## Documentación

### Docusaurus
- **Versión**: 3.9.2
- Generador de sitios estáticos
- Soporte para MDX
- Tema responsive

## Control de versiones

### Git
- Repositorio en GitHub
- GitHub Pages para documentación
- Branch `gh-pages` para deployment

## Resumen de versiones

| Tecnología | Versión |
|------------|---------|
| Kotlin | 2.0.0 |
| Android SDK | 35 |
| Jetpack Compose | BOM latest |
| Firebase | BOM latest |
| Koin | latest |
| Coil | latest |
| Docusaurus | 3.9.2 |
| Node.js | ≥20.0 |
