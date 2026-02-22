# Plan de Implementación: Sistema de Gamificación

## Fecha de Inicio: 2026-02-22
## Desarrollador: Claude (NeriBot) + Nerina Uliana

---

## 🎯 Objetivo

Implementar un sistema completo de gamificación para aumentar el engagement de los niños con la lectura, incluyendo puntos, medallas, avatares personalizables y rachas de lectura.

---

## 📋 Features a Implementar

### 1. Sistema de Puntos
- **10 puntos** por libro leído completo
- **5 puntos** por audiolibro escuchado completo
- **3 puntos** por juego completado
- **Bonus diario**: +5 puntos por primera actividad del día

### 2. Medallas (Achievements)
- 🏆 **Primera Lectura**: Leer el primer libro
- 📚 **Lector Curioso**: Leer 5 libros
- 🌟 **Lector Dedicado**: Leer 10 libros
- 💫 **Súper Lector**: Leer 50 libros
- 🎮 **Maestro de Juegos**: Completar todos los juegos disponibles
- 🎧 **Fan de Audiolibros**: Escuchar 10 audiolibros
- 🔥 **Racha de Fuego**: 7 días consecutivos leyendo
- ⚡ **Racha Imparable**: 30 días consecutivos leyendo

### 3. Avatares Personalizables
- Avatar base seleccionable (6 opciones de personajes)
- Items desbloqueables con puntos:
  - Sombreros (10 opciones)
  - Accesorios (8 opciones)
  - Fondos (6 opciones)
  - Marcos (5 opciones)

### 4. Sistema de Rachas
- Contador de días consecutivos leyendo
- Notificación si está por perder la racha
- Visualización de racha actual y récord personal

### 5. Perfil de Usuario Gamificado
- Visualización de puntos totales
- Galería de medallas obtenidas
- Avatar personalizado
- Estadísticas:
  - Libros leídos
  - Audiolibros escuchados
  - Juegos completados
  - Racha actual
  - Récord de racha

---

## 🏗️ Arquitectura de Implementación

### Nuevos Archivos a Crear

```
app/src/main/java/edu/mis/lecturitas/
├── model/
│   ├── Achievement.kt          ✨ NUEVO
│   ├── UserProgress.kt         ✨ NUEVO
│   ├── AvatarItem.kt          ✨ NUEVO
│   └── ReadingStreak.kt       ✨ NUEVO
│
├── repository/
│   └── GamificationRepository.kt  ✨ NUEVO
│
├── ui/gamification/
│   ├── ProfileActivity.kt      ✨ NUEVO
│   ├── ProfileViewModel.kt     ✨ NUEVO
│   ├── AchievementsActivity.kt ✨ NUEVO
│   ├── AvatarCustomizerActivity.kt ✨ NUEVO
│   └── components/
│       ├── AchievementCard.kt  ✨ NUEVO
│       ├── PointsDisplay.kt    ✨ NUEVO
│       ├── StreakCounter.kt    ✨ NUEVO
│       └── AvatarView.kt       ✨ NUEVO
│
└── utils/
    ├── AchievementManager.kt   ✨ NUEVO
    └── StreakCalculator.kt     ✨ NUEVO
```

### Archivos a Modificar

```
app/src/main/java/edu/mis/lecturitas/
├── model/
│   └── Usuario.kt              📝 MODIFICAR (agregar referencia a UserProgress)
│
├── ui/main/
│   ├── MainActivity.kt         📝 MODIFICAR (agregar botón de perfil)
│   └── MainViewModel.kt        📝 MODIFICAR (tracking de actividades)
│
├── ui/playread/
│   └── PlayReadViewModel.kt    📝 MODIFICAR (otorgar puntos al terminar)
│
├── ui/audiolibros/
│   └── AudioViewModel.kt       📝 MODIFICAR (otorgar puntos al terminar)
│
└── ui/juegos/
    └── JuegosViewModel.kt      📝 MODIFICAR (otorgar puntos al completar)
```

---

## 🗄️ Estructura de Firebase

### Nueva estructura en Firebase Realtime Database:

```json
{
  "usuarios": {
    "user_id": {
      "user": "nombre_usuario",
      "name": "Nombre",
      "progress": {
        "totalPoints": 120,
        "level": 3,
        "booksRead": 12,
        "audiobooksListened": 5,
        "gamesCompleted": 8,
        "currentStreak": 7,
        "longestStreak": 15,
        "lastActivityDate": "2026-02-22"
      },
      "achievements": {
        "primera_lectura": {
          "unlocked": true,
          "unlockedDate": "2024-06-10"
        },
        "lector_curioso": {
          "unlocked": true,
          "unlockedDate": "2024-07-15"
        }
      },
      "avatar": {
        "baseCharacter": "character_1",
        "hat": "hat_3",
        "accessory": "glasses_1",
        "background": "bg_space",
        "frame": "frame_gold"
      }
    }
  }
}
```

---

## 📱 UI/UX Design

### 1. Pantalla Principal (MainActivity)
- Agregar ícono de perfil en AppBar (esquina superior derecha)
- Mostrar puntos actuales en chip pequeño
- Badge de notificación si hay medallas nuevas

### 2. Pantalla de Perfil (ProfileActivity)
```
┌─────────────────────────────┐
│  ← Perfil            🔧      │
├─────────────────────────────┤
│     [Avatar Grande]         │
│     Nombre del Niño         │
│                             │
│  ⭐ 120 puntos  |  Nivel 3  │
├─────────────────────────────┤
│  📚 Libros: 12              │
│  🎧 Audios: 5               │
│  🎮 Juegos: 8               │
│  🔥 Racha: 7 días           │
├─────────────────────────────┤
│  🏆 Medallas (4/8)          │
│  [🏆] [🌟] [⚡] [🔒]       │
│  [🔒] [🔒] [🔒] [🔒]       │
├─────────────────────────────┤
│  [Personalizar Avatar]      │
│  [Ver Todas las Medallas]   │
└─────────────────────────────┘
```

### 3. Pantalla de Medallas (AchievementsActivity)
- Grid de todas las medallas
- Mostradas en color si están desbloqueadas
- Grises/bloqueadas si no
- Al hacer tap: modal con descripción y requisito

### 4. Personalizador de Avatar (AvatarCustomizerActivity)
- Preview del avatar en tiempo real
- Categorías: Personaje, Sombreros, Accesorios, Fondos, Marcos
- Precio en puntos para desbloquear items
- Botón "Guardar" para aplicar cambios

---

## 🎨 Assets Necesarios

### Imágenes de Avatares
- 6 personajes base (niño, niña, robot, animal, etc.)
- 10 sombreros diferentes
- 8 accesorios (gafas, moños, etc.)
- 6 fondos temáticos
- 5 marcos decorativos

### Iconos de Medallas
- 8 iconos de medallas (pueden ser Material Icons o custom)

**Nota:** Para la primera versión, usaremos Material Icons y emojis. Después se pueden crear assets custom.

---

## 📝 Modelos de Datos (Código)

### Achievement.kt
```kotlin
data class Achievement(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val requirement: Int = 0,
    val type: AchievementType = AchievementType.BOOKS,
    val unlocked: Boolean = false,
    val unlockedDate: Long? = null
)

enum class AchievementType {
    BOOKS,
    AUDIOBOOKS,
    GAMES,
    STREAK,
    POINTS
}
```

### UserProgress.kt
```kotlin
data class UserProgress(
    val userId: String = "",
    val totalPoints: Int = 0,
    val level: Int = 1,
    val booksRead: Int = 0,
    val audiobooksListened: Int = 0,
    val gamesCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: String = "",
    val achievements: Map<String, Achievement> = emptyMap(),
    val avatar: AvatarCustomization = AvatarCustomization()
)
```

### AvatarCustomization.kt
```kotlin
data class AvatarCustomization(
    val baseCharacter: String = "character_1",
    val hat: String? = null,
    val accessory: String? = null,
    val background: String = "bg_default",
    val frame: String = "frame_default"
)
```

### AvatarItem.kt
```kotlin
data class AvatarItem(
    val id: String = "",
    val name: String = "",
    val category: AvatarCategory = AvatarCategory.HAT,
    val resourceId: Int = 0,
    val pointsCost: Int = 0,
    val unlocked: Boolean = false
)

enum class AvatarCategory {
    CHARACTER,
    HAT,
    ACCESSORY,
    BACKGROUND,
    FRAME
}
```

---

## ⚙️ Lógica de Negocio

### Sistema de Niveles
- **Nivel 1:** 0-50 puntos
- **Nivel 2:** 51-150 puntos
- **Nivel 3:** 151-300 puntos
- **Nivel 4:** 301-500 puntos
- **Nivel 5:** 501-1000 puntos
- **Nivel 6+:** +500 puntos por nivel

### Cálculo de Rachas
```kotlin
fun updateStreak(lastDate: String, todayDate: String): StreakResult {
    val daysDifference = calculateDaysDifference(lastDate, todayDate)

    return when {
        daysDifference == 0 -> StreakResult.SAME_DAY
        daysDifference == 1 -> StreakResult.CONTINUE_STREAK
        else -> StreakResult.BREAK_STREAK
    }
}
```

### Notificaciones de Racha
- Si no hay actividad en 20 horas → "¡No pierdas tu racha de X días! 🔥"
- Al completar actividad → "+5 puntos de bonus diario"

---

## 🧪 Testing

### Test Cases
1. Otorgar puntos al completar libro
2. Desbloquear medalla al alcanzar requisito
3. Calcular racha correctamente
4. Incrementar nivel al alcanzar puntos
5. Comprar item de avatar con puntos
6. Persistir datos en Firebase

---

## 📅 Timeline Estimado

### Día 1 (Hoy)
- ✅ Diseñar plan completo
- ⏳ Crear modelos de datos
- ⏳ Crear estructura de Firebase

### Día 2
- Implementar GamificationRepository
- Crear ProfileViewModel
- Implementar tracking de puntos

### Día 3
- Crear UI de ProfileActivity
- Implementar AchievementManager
- Testing de lógica de puntos

### Día 4
- Crear AvatarCustomizerActivity
- Implementar sistema de compra de items
- Integrar con pantalla principal

### Día 5
- Implementar sistema de rachas
- Agregar notificaciones
- Testing end-to-end

### Día 6
- Pulir UI/UX
- Agregar animaciones
- Testing en dispositivo real

### Día 7
- Bug fixes
- Documentación
- Preparar para deploy

---

## 🚀 Deployment

1. Testing completo en emulador
2. Testing en 2-3 dispositivos reales
3. Beta testing con 10 usuarios del jardín
4. Recopilar feedback
5. Ajustes finales
6. Deploy a Play Store (versión 2.2)

---

## 📊 Métricas de Éxito

### KPIs a Medir
- **Retention:** % de usuarios que vuelven al día siguiente
- **Engagement:** Promedio de libros leídos por usuario (antes vs después)
- **Streak Duration:** Promedio de días de racha
- **Achievement Completion:** % de medallas desbloqueadas
- **Time in App:** Tiempo promedio por sesión

### Objetivo
- Aumentar retention de 20% a 50%
- Aumentar libros leídos por usuario de 3 a 10+
- 70% de usuarios con racha de 3+ días
- Tiempo en app +30%

---

## 💡 Ideas para Futuras Versiones (v3.0)

- Tabla de clasificación entre amigos
- Desafíos semanales
- Eventos especiales (Día del Libro, Navidad)
- Mascotas virtuales que crecen con la lectura
- Modo competitivo entre hermanos
- Compartir logros en redes sociales

---

## 🔧 Próximos Pasos Inmediatos

1. ✅ Crear este plan de implementación
2. ⏳ Crear modelos de datos (Achievement, UserProgress, etc.)
3. ⏳ Configurar estructura de Firebase
4. ⏳ Implementar GamificationRepository
5. ⏳ Crear ProfileActivity con UI básica

---

**¿Lista para empezar a codear? 🚀**
