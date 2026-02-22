package edu.mis.lecturitas.model

/**
 * Modelo para representar el progreso completo de un usuario en el sistema de gamificación
 */
data class UserProgress(
    val userId: String = "",
    val totalPoints: Int = 0,
    val level: Int = 1,
    val booksRead: Int = 0,
    val audiobooksListened: Int = 0,
    val gamesCompleted: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: String = "", // Formato: "yyyy-MM-dd"
    val achievements: Map<String, Boolean> = emptyMap(), // achievementId -> unlocked
    val avatar: AvatarCustomization = AvatarCustomization()
) {
    /**
     * Calcula el nivel basado en los puntos totales
     */
    fun calculateLevel(): Int {
        return when {
            totalPoints < 50 -> 1
            totalPoints < 150 -> 2
            totalPoints < 300 -> 3
            totalPoints < 500 -> 4
            totalPoints < 1000 -> 5
            else -> 5 + ((totalPoints - 1000) / 500)
        }
    }

    /**
     * Calcula cuántos puntos faltan para el siguiente nivel
     */
    fun pointsToNextLevel(): Int {
        val nextLevelThreshold = when (level) {
            1 -> 50
            2 -> 150
            3 -> 300
            4 -> 500
            5 -> 1000
            else -> 1000 + ((level - 5) * 500)
        }
        return nextLevelThreshold - totalPoints
    }

    /**
     * Retorna el progreso hacia el siguiente nivel (0.0 a 1.0)
     */
    fun levelProgress(): Float {
        val currentLevelThreshold = when (level) {
            1 -> 0
            2 -> 50
            3 -> 150
            4 -> 300
            5 -> 500
            else -> 1000 + ((level - 6) * 500)
        }

        val nextLevelThreshold = when (level) {
            1 -> 50
            2 -> 150
            3 -> 300
            4 -> 500
            5 -> 1000
            else -> 1000 + ((level - 5) * 500)
        }

        val pointsInCurrentLevel = totalPoints - currentLevelThreshold
        val pointsNeededForLevel = nextLevelThreshold - currentLevelThreshold

        return if (pointsNeededForLevel > 0) {
            (pointsInCurrentLevel.toFloat() / pointsNeededForLevel.toFloat()).coerceIn(0f, 1f)
        } else {
            1f
        }
    }

    /**
     * Verifica si una medalla está desbloqueada
     */
    fun isAchievementUnlocked(achievementId: String): Boolean {
        return achievements[achievementId] == true
    }

    /**
     * Cuenta cuántas medallas están desbloqueadas
     */
    fun unlockedAchievementsCount(): Int {
        return achievements.count { it.value }
    }
}
