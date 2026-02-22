package edu.mis.lecturitas.model

/**
 * Modelo para representar una medalla/logro en el sistema de gamificación
 */
data class Achievement(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "", // Emoji o nombre del icono de Material
    val requirement: Int = 0, // Cantidad necesaria para desbloquear
    val type: AchievementType = AchievementType.BOOKS,
    val unlocked: Boolean = false,
    val unlockedDate: Long? = null,
    val pointsReward: Int = 0 // Puntos bonus al desbloquear
) {
    companion object {
        fun getDefaultAchievements(): List<Achievement> {
            return listOf(
                Achievement(
                    id = "primera_lectura",
                    name = "Primera Lectura",
                    description = "Completaste tu primer libro",
                    icon = "🏆",
                    requirement = 1,
                    type = AchievementType.BOOKS,
                    pointsReward = 10
                ),
                Achievement(
                    id = "lector_curioso",
                    name = "Lector Curioso",
                    description = "Leíste 5 libros diferentes",
                    icon = "📚",
                    requirement = 5,
                    type = AchievementType.BOOKS,
                    pointsReward = 25
                ),
                Achievement(
                    id = "lector_dedicado",
                    name = "Lector Dedicado",
                    description = "¡Leíste 10 libros!",
                    icon = "🌟",
                    requirement = 10,
                    type = AchievementType.BOOKS,
                    pointsReward = 50
                ),
                Achievement(
                    id = "super_lector",
                    name = "Súper Lector",
                    description = "¡Increíble! Leíste 50 libros",
                    icon = "💫",
                    requirement = 50,
                    type = AchievementType.BOOKS,
                    pointsReward = 200
                ),
                Achievement(
                    id = "fan_audiolibros",
                    name = "Fan de Audiolibros",
                    description = "Escuchaste 10 audiolibros",
                    icon = "🎧",
                    requirement = 10,
                    type = AchievementType.AUDIOBOOKS,
                    pointsReward = 50
                ),
                Achievement(
                    id = "maestro_juegos",
                    name = "Maestro de Juegos",
                    description = "Completaste todos los juegos",
                    icon = "🎮",
                    requirement = 3, // Actualizar según cantidad de juegos
                    type = AchievementType.GAMES,
                    pointsReward = 75
                ),
                Achievement(
                    id = "racha_fuego",
                    name = "Racha de Fuego",
                    description = "7 días consecutivos leyendo",
                    icon = "🔥",
                    requirement = 7,
                    type = AchievementType.STREAK,
                    pointsReward = 100
                ),
                Achievement(
                    id = "racha_imparable",
                    name = "Racha Imparable",
                    description = "30 días consecutivos leyendo",
                    icon = "⚡",
                    requirement = 30,
                    type = AchievementType.STREAK,
                    pointsReward = 300
                )
            )
        }
    }
}

enum class AchievementType {
    BOOKS,        // Medallas relacionadas con lectura de libros
    AUDIOBOOKS,   // Medallas relacionadas con audiolibros
    GAMES,        // Medallas relacionadas con juegos
    STREAK,       // Medallas relacionadas con rachas
    POINTS        // Medallas relacionadas con puntos totales
}
