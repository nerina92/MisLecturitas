package edu.mis.lecturitas.utils

import edu.mis.lecturitas.model.Achievement
import edu.mis.lecturitas.model.AchievementType
import edu.mis.lecturitas.model.UserProgress

/**
 * Gestor para verificar y desbloquear logros/medallas
 */
object AchievementManager {

    /**
     * Verifica si se desbloquearon nuevas medallas basándose en el progreso actual
     * @param progress progreso actual del usuario
     * @param allAchievements lista de todos los logros disponibles
     * @return lista de logros recién desbloqueados
     */
    fun checkForNewAchievements(
        progress: UserProgress,
        allAchievements: List<Achievement>
    ): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()

        for (achievement in allAchievements) {
            // Si ya está desbloqueado, saltar
            if (progress.isAchievementUnlocked(achievement.id)) {
                continue
            }

            // Verificar si se cumplió el requisito
            val requirementMet = when (achievement.type) {
                AchievementType.BOOKS -> progress.booksRead >= achievement.requirement
                AchievementType.AUDIOBOOKS -> progress.audiobooksListened >= achievement.requirement
                AchievementType.GAMES -> progress.gamesCompleted >= achievement.requirement
                AchievementType.STREAK -> progress.currentStreak >= achievement.requirement
                AchievementType.POINTS -> progress.totalPoints >= achievement.requirement
            }

            if (requirementMet) {
                newlyUnlocked.add(achievement.copy(
                    unlocked = true,
                    unlockedDate = System.currentTimeMillis()
                ))
            }
        }

        return newlyUnlocked
    }

    /**
     * Calcula los puntos que se deben otorgar por una actividad
     */
    fun calculatePointsForActivity(activityType: ActivityType): Int {
        return when (activityType) {
            ActivityType.BOOK_READ -> 10
            ActivityType.AUDIOBOOK_LISTENED -> 5
            ActivityType.GAME_COMPLETED -> 3
            ActivityType.DAILY_BONUS -> 5
        }
    }

    /**
     * Obtiene el progreso hacia un logro específico
     * @return porcentaje de progreso (0.0 a 1.0)
     */
    fun getAchievementProgress(achievement: Achievement, progress: UserProgress): Float {
        val currentValue = when (achievement.type) {
            AchievementType.BOOKS -> progress.booksRead
            AchievementType.AUDIOBOOKS -> progress.audiobooksListened
            AchievementType.GAMES -> progress.gamesCompleted
            AchievementType.STREAK -> progress.currentStreak
            AchievementType.POINTS -> progress.totalPoints
        }

        return if (achievement.requirement > 0) {
            (currentValue.toFloat() / achievement.requirement.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    /**
     * Obtiene el texto de progreso hacia un logro
     * Ej: "3 / 10 libros"
     */
    fun getAchievementProgressText(achievement: Achievement, progress: UserProgress): String {
        val currentValue = when (achievement.type) {
            AchievementType.BOOKS -> progress.booksRead
            AchievementType.AUDIOBOOKS -> progress.audiobooksListened
            AchievementType.GAMES -> progress.gamesCompleted
            AchievementType.STREAK -> progress.currentStreak
            AchievementType.POINTS -> progress.totalPoints
        }

        val unit = when (achievement.type) {
            AchievementType.BOOKS -> "libros"
            AchievementType.AUDIOBOOKS -> "audiolibros"
            AchievementType.GAMES -> "juegos"
            AchievementType.STREAK -> "días"
            AchievementType.POINTS -> "puntos"
        }

        return "$currentValue / ${achievement.requirement} $unit"
    }

    /**
     * Genera un mensaje de felicitación por desbloquear un logro
     */
    fun getUnlockMessage(achievement: Achievement): String {
        return "¡Felicitaciones! Desbloqueaste: ${achievement.icon} ${achievement.name}"
    }
}

enum class ActivityType {
    BOOK_READ,
    AUDIOBOOK_LISTENED,
    GAME_COMPLETED,
    DAILY_BONUS
}
