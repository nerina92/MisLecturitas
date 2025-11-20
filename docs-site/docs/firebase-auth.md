---
sidebar_position: 4
---

# 🔐 Firebase Authentication

Esta guía explica cómo está implementada la autenticación en **Mis Lecturitas**.

## 📋 Cambios Realizados en el Código

### ✅ Archivos Modificados:

1. **`app/build.gradle.kts`**
   - ✅ Agregada dependencia `firebase-auth-ktx`

2. **`LoginViewModel.kt`**
   - ✅ Implementada autenticación con Firebase Auth
   - ✅ Mantiene compatibilidad con estructura de usuarios existente

3. **`AdminViewModel.kt`**
   - ✅ Verificación de autenticación antes de publicar contenido

4. **`ManageContentViewModel.kt`**
   - ✅ Verificación de autenticación antes de eliminar contenido

---

## 🚀 Pasos para Completar la Implementación

### Paso 1: Sincronizar Gradle

1. Abre Android Studio
2. Haz clic en **"Sync Now"** cuando aparezca el banner
3. Espera a que se descarguen las dependencias de Firebase Auth

---

### Paso 2: Crear Usuarios en Firebase Authentication

Antes de poder usar la app, **debes crear los usuarios en Firebase Authentication** que coincidan con los usuarios en tu Realtime Database.

#### 2.1. Acceder a Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto **"mis-lecturitas-7b62a"**
3. En el menú lateral, haz clic en **"Authentication"**
4. Si es la primera vez, haz clic en **"Comenzar"**

#### 2.2. Habilitar Email/Password Authentication

1. Ve a la pestaña **"Sign-in method"**
2. Haz clic en **"Email/Password"**
3. Activa el switch **"Enable"**
4. Guarda los cambios

#### 2.3. Crear Usuarios

Para cada usuario que tengas en tu Realtime Database, debes crear uno en Authentication:

1. Ve a la pestaña **"Users"**
2. Haz clic en **"Add user"**
3. Ingresa:
   - **Email**: 
     - Si el usuario en tu DB tiene un campo `mail` válido, usa ese
     - Si no, usa: `{username}@mislecturitas.com` (ejemplo: si el username es "admin", usa "admin@mislecturitas.com")
   - **Password**: La misma contraseña que tiene en la base de datos
4. Haz clic en **"Add user"**
5. Repite para cada usuario

**Ejemplo:**
- Si tienes un usuario en la DB:
  ```json
  {
    "user": "admin",
    "pasword": "Admin123",
    "mail": "admin@jardín.com",
    "tipo": 1
  }
  ```
- Crea en Authentication:
  - Email: `admin@jardín.com`
  - Password: `Admin123`

---

### Paso 3: Actualizar Reglas de Seguridad en Firebase

#### 3.1. Acceder a Realtime Database

1. En Firebase Console, ve a **"Realtime Database"**
2. Haz clic en la pestaña **"Rules"**

#### 3.2. Reemplazar las Reglas

Copia y pega las siguientes reglas (también están en el archivo `firebase-realtime-database-rules.json`):

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

#### 3.3. Publicar las Reglas

1. Haz clic en **"Publish"**
2. Confirma la acción

---

## 🔍 Explicación de las Nuevas Reglas

### `libros` y `audiolibros`:
- **`.read: true`**: Cualquiera puede leer (para que usuarios no autenticados vean el contenido)
- **`.write: "auth != null"`**: Solo usuarios autenticados pueden escribir
- **`.indexOn`**: Optimización de búsquedas por nivel e ID

### `usuarios`:
- **`.read: true`**: Lectura pública (necesario para el flujo de login)
- **`.write: false`**: Nadie puede escribir directamente (solo desde Firebase Console)
- **`.indexOn: ["user"]`**: Optimización de búsqueda por username

---

## ✅ Verificar que Funcione

### Prueba 1: Login de Administrador
1. Abre la app
2. Ingresa con un usuario que tenga `tipo: 1` (administrador)
3. Debe permitirte entrar a las pantallas de administración
4. Intenta publicar un libro o audiolibro
5. Debe funcionar correctamente

### Prueba 2: Seguridad
1. Intenta acceder a tu base de datos sin autenticación desde un script externo
2. Debe rechazar las escrituras con error de permisos

---

## 🆘 Solución de Problemas

### Problema: "Usuario o contraseña incorrectos"

**Causa**: El usuario no existe en Firebase Authentication o la contraseña no coincide

**Solución**:
1. Verifica que el usuario esté creado en Firebase Authentication
2. Asegúrate de que el email y password coincidan exactamente con los de tu base de datos

---

### Problema: "Debes iniciar sesión para publicar contenido"

**Causa**: El usuario no está autenticado con Firebase Auth

**Solución**:
1. Cierra sesión y vuelve a iniciar sesión
2. Verifica que el login se complete correctamente

---

### Problema: Error al leer usuarios

**Causa**: Las reglas de seguridad bloquean el acceso

**Solución**:
1. Verifica que hayas publicado las nuevas reglas en Firebase Console
2. Asegúrate de que el usuario esté autenticado antes de intentar leer usuarios

---

## 📊 Estructura Final

```
Firebase Authentication
├── admin@mislecturitas.com (tipo: 1)
├── docente@mislecturitas.com (tipo: 0)
└── ... otros usuarios

Realtime Database
├── /libros (read: público, write: autenticados)
├── /audiolibros (read: público, write: autenticados)
└── /usuarios (read: público*, write: nadie)
     * Necesario para el flujo de login
```

---

## 🎯 Próximos Pasos Opcionales

Si quieres mejorar aún más la seguridad:

1. **Agregar roles específicos**: Modificar las reglas para que solo usuarios con `tipo: 1` puedan escribir
2. **Implementar registro de usuarios**: Permitir que nuevos usuarios se registren desde la app
3. **Agregar verificación de email**: Requerir que los usuarios verifiquen su email
4. **Implementar recuperación de contraseña**: Permitir que los usuarios recuperen su contraseña

---

## 📞 Soporte

Si tienes problemas durante la implementación:

1. Revisa los logs en Android Studio (Logcat)
2. Busca mensajes con el tag "LoginViewModel", "AdminViewModel", o "Firebase"
3. Verifica que Firebase Authentication esté correctamente configurado

---

**¡Listo!** Tu aplicación ahora tiene autenticación segura con Firebase. 🎉

