---
sidebar_position: 2
---

# Arquitectura del Proyecto

## Patrón MVVM

**Mis Lecturitas** utiliza el patrón arquitectónico **MVVM (Model-View-ViewModel)** recomendado por Google para aplicaciones Android modernas.

```
┌─────────────────┐
│      View       │  ← Jetpack Compose UI
│  (Composables)  │
└────────┬────────┘
         │ observa
         ↓
┌─────────────────┐
│   ViewModel     │  ← Lógica de negocio
│  (LiveData)     │
└────────┬────────┘
         │ usa
         ↓
┌─────────────────┐
│  Repository     │  ← Acceso a datos
│   (Firebase)    │
└─────────────────┘
```

## Estructura de carpetas

```
app/src/main/java/edu/mis/lecturitas/
├── data/
│   ├── models/          # Modelos de datos
│   │   ├── AudioLibro.kt
│   │   ├── Libro.kt
│   │   └── Usuario.kt
│   └── repository/      # Repositorios de datos
│       └── FirebaseRepository.kt
│
├── ui/
│   ├── screens/         # Pantallas principales
│   │   ├── admin/      # Pantallas de administración
│   │   ├── home/       # Pantalla principal
│   │   ├── login/      # Autenticación
│   │   └── player/     # Reproductor de contenido
│   │
│   ├── components/      # Componentes reutilizables
│   └── theme/          # Tema y estilos
│
├── viewmodel/          # ViewModels
│   ├── AdminViewModel.kt
│   ├── LoginViewModel.kt
│   └── ManageContentViewModel.kt
│
└── di/                 # Inyección de dependencias (Koin)
    └── AppModule.kt
```

## Componentes principales

### 1. ViewModels

Los ViewModels manejan la lógica de negocio y el estado de la UI:

- **LoginViewModel**: Autenticación de usuarios
- **AdminViewModel**: Publicación de contenido
- **ManageContentViewModel**: Gestión de libros y audiolibros
- **HomeViewModel**: Navegación y visualización de contenido

### 2. Repository Pattern

`FirebaseRepository` centraliza todas las operaciones con Firebase:
- Lectura/escritura en Realtime Database
- Subida de imágenes a Storage
- Gestión de autenticación

### 3. Jetpack Compose

Todas las pantallas están construidas con **Jetpack Compose**, el toolkit moderno de UI de Android:
- Declarativo
- Sin XML
- Reactividad automática

## Flujo de datos

### Lectura de datos

```
Firebase → Repository → ViewModel → LiveData → Composable
```

### Escritura de datos

```
User Action → Composable → ViewModel → Repository → Firebase
```

## Estado de la aplicación

El estado se maneja mediante:
- **LiveData**: Para observar cambios en los datos
- **MutableState**: Para estados locales en Composables
- **Remember**: Para mantener estado durante recomposiciones

## Inyección de dependencias

Se utiliza **Koin** para la inyección de dependencias:

```kotlin
val appModule = module {
    single { FirebaseRepository() }
    viewModel { LoginViewModel(get()) }
    viewModel { AdminViewModel(get()) }
    // ...
}
```

## Seguridad

- Autenticación con **Firebase Authentication**
- Reglas de seguridad en **Realtime Database**
- Validación de permisos en el cliente
- Tokens de sesión manejados por Firebase

## Navegación

La navegación entre pantallas se maneja con:
- **Jetpack Compose Navigation**
- Rutas definidas como constantes
- Paso de parámetros mediante SafeArgs
