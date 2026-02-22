# 🔒 Configuración de Seguridad de Firebase

## ⚠️ IMPORTANTE: Debes aplicar estas reglas en Firebase Console

### Paso 1: Regenerar API Keys (URGENTE)

1. Ve a: https://console.firebase.google.com
2. Selecciona tu proyecto: **mis-lecturitas-7b62a**
3. Click en ⚙️ (Settings) > Project Settings
4. En la sección "Your apps", click en la app Android
5. **Elimina la app actual** (esto invalida las keys expuestas)
6. **Agrega una nueva app Android:**
   - Package name: `edu.mis.lecturitas`
   - Descarga el nuevo `google-services.json`
7. **Copia el archivo** a `app/google-services.json` en tu proyecto LOCAL
8. **NO lo commitees a git** (ya está en .gitignore)

---

### Paso 2: Aplicar Reglas de Realtime Database

1. Ve a: https://console.firebase.google.com
2. Selecciona tu proyecto: **mis-lecturitas-7b62a**
3. En el menú lateral: **Realtime Database**
4. Click en la pestaña **"Rules"**
5. **Borra todo** el contenido actual
6. **Copia y pega** el contenido del archivo `database.rules.json`
7. Click en **"Publish"**

**Verificación:**
- Antes: Cualquiera puede leer/escribir
- Después: Solo usuarios autenticados pueden leer, solo admins pueden escribir

---

### Paso 3: Aplicar Reglas de Storage

1. Ve a: https://console.firebase.google.com
2. Selecciona tu proyecto: **mis-lecturitas-7b62a**
3. En el menú lateral: **Storage**
4. Click en la pestaña **"Rules"**
5. **Borra todo** el contenido actual
6. **Copia y pega** el contenido del archivo `storage.rules`
7. Click en **"Publish"**

**Verificación:**
- Antes: Acceso público a todos los archivos
- Después: Solo usuarios autenticados pueden leer, solo admins pueden subir

---

### Paso 4: Habilitar Firebase Authentication (si no está habilitado)

1. Ve a: https://console.firebase.google.com
2. Selecciona tu proyecto: **mis-lecturitas-7b62a**
3. En el menú lateral: **Authentication**
4. Click en **"Get started"** (si no está habilitado)
5. Habilita **"Email/Password"** como método de inicio de sesión
6. Opcional: Habilita **"Anonymous"** para usuarios invitados

---

## 🔐 Explicación de las Reglas

### Realtime Database Rules

```json
"usuarios": {
  "$userId": {
    ".read": "auth != null && auth.uid == $userId",
    ".write": "auth != null && auth.uid == $userId"
  }
}
```
- ✅ Solo el usuario puede leer/escribir sus propios datos
- ✅ Nadie más puede ver datos de otros usuarios
- ✅ El campo `tipo` (admin) no puede ser modificado por usuarios

```json
"libros": {
  ".read": "auth != null",
  ".write": "auth != null && root.child('usuarios').child(auth.uid).child('tipo').val() == 1"
}
```
- ✅ Todos los usuarios autenticados pueden leer libros
- ✅ Solo admins (tipo == 1) pueden crear/modificar/eliminar libros
- ✅ No se puede hackear el sistema para ser admin desde el APK

---

## ✅ Checklist de Seguridad

Marca cada item cuando lo completes:

- [ ] Regenerar API Keys en Firebase Console
- [ ] Descargar nuevo google-services.json
- [ ] Copiarlo a app/google-services.json (LOCAL, no commitear)
- [ ] Aplicar database.rules.json en Realtime Database
- [ ] Aplicar storage.rules en Storage
- [ ] Verificar que Authentication esté habilitado
- [ ] Probar login en la app
- [ ] Verificar que usuarios normales NO puedan acceder a admin
- [ ] Verificar que admins SÍ puedan subir contenido

---

## 🧪 Testing de Seguridad

### Test 1: Usuario no autenticado
```bash
# Desde consola de Firebase, intentar leer sin auth
# Resultado esperado: "Permission denied"
```

### Test 2: Usuario normal intenta ser admin
```bash
# Modificar APK para cambiar tipo a 1
# Intentar subir contenido
# Resultado esperado: "Permission denied"
```

### Test 3: Admin puede subir contenido
```bash
# Login como admin (tipo == 1)
# Subir un libro
# Resultado esperado: Éxito
```

---

## ⏰ Tiempo Estimado

- **Paso 1 (API Keys):** 5 minutos
- **Paso 2 (Database Rules):** 2 minutos
- **Paso 3 (Storage Rules):** 2 minutos
- **Paso 4 (Authentication):** 1 minuto
- **Testing:** 5 minutos

**Total: ~15 minutos**

---

## 🆘 Problemas Comunes

### "Permission denied" al hacer login
- **Causa:** Authentication no está habilitado
- **Solución:** Habilitar Email/Password en Authentication

### "Error: Invalid API key"
- **Causa:** Usaste el google-services.json viejo
- **Solución:** Descargar el nuevo y reemplazarlo

### Los usuarios normales pueden subir contenido
- **Causa:** Las reglas no se aplicaron correctamente
- **Solución:** Verificar que dice "tipo == 1" en las reglas

---

## 📞 Soporte

Si tenés problemas aplicando estas reglas, avisame y te ayudo paso a paso.
