package edu.mis.lecturitas.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Utilidad para calcular y manejar rachas de lectura diaria
 */
object StreakCalculator {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Actualiza la racha basándose en la última fecha de actividad
     * @param lastActivityDate última fecha en que el usuario realizó una actividad (formato: yyyy-MM-dd)
     * @param currentStreak racha actual del usuario
     * @return nueva racha actualizada
     */
    fun updateStreak(lastActivityDate: String, currentStreak: Int): StreakUpdate {
        if (lastActivityDate.isEmpty()) {
            // Primera actividad del usuario
            return StreakUpdate(
                newStreak = 1,
                shouldAwardBonus = true,
                streakStatus = StreakStatus.STARTED
            )
        }

        val today = LocalDate.now()
        val lastDate = try {
            LocalDate.parse(lastActivityDate, dateFormatter)
        } catch (e: Exception) {
            // Si hay error parseando la fecha, reiniciar racha
            return StreakUpdate(
                newStreak = 1,
                shouldAwardBonus = true,
                streakStatus = StreakStatus.STARTED
            )
        }

        val daysDifference = ChronoUnit.DAYS.between(lastDate, today).toInt()

        return when {
            daysDifference == 0 -> {
                // Misma fecha, no incrementar racha
                StreakUpdate(
                    newStreak = currentStreak,
                    shouldAwardBonus = false,
                    streakStatus = StreakStatus.SAME_DAY
                )
            }
            daysDifference == 1 -> {
                // Día consecutivo, incrementar racha
                StreakUpdate(
                    newStreak = currentStreak + 1,
                    shouldAwardBonus = true,
                    streakStatus = StreakStatus.CONTINUED
                )
            }
            else -> {
                // Se rompió la racha, reiniciar
                StreakUpdate(
                    newStreak = 1,
                    shouldAwardBonus = true,
                    streakStatus = StreakStatus.BROKEN
                )
            }
        }
    }

    /**
     * Obtiene la fecha actual en formato yyyy-MM-dd
     */
    fun getCurrentDate(): String {
        return LocalDate.now().format(dateFormatter)
    }

    /**
     * Verifica si el usuario está en riesgo de perder su racha
     * (si su última actividad fue hace más de 20 horas)
     */
    fun isStreakAtRisk(lastActivityDate: String): Boolean {
        if (lastActivityDate.isEmpty()) return false

        val today = LocalDate.now()
        val lastDate = try {
            LocalDate.parse(lastActivityDate, dateFormatter)
        } catch (e: Exception) {
            return false
        }

        val daysDifference = ChronoUnit.DAYS.between(lastDate, today).toInt()
        return daysDifference == 0 // Hoy todavía, pero se acerca el límite
    }

    /**
     * Calcula el emoji de la racha basado en la cantidad de días
     */
    fun getStreakEmoji(streakDays: Int): String {
        return when {
            streakDays == 0 -> "💤"
            streakDays < 3 -> "🔥"
            streakDays < 7 -> "🔥🔥"
            streakDays < 30 -> "🔥🔥🔥"
            else -> "⚡⚡⚡"
        }
    }

    /**
     * Obtiene un mensaje de motivación basado en la racha
     */
    fun getStreakMessage(streakDays: Int): String {
        return when {
            streakDays == 0 -> "¡Empezá tu racha hoy!"
            streakDays == 1 -> "¡Primera racha! Seguí así"
            streakDays < 7 -> "¡Llevás $streakDays días! 🔥"
            streakDays < 30 -> "¡Increíble racha de $streakDays días! 🌟"
            else -> "¡Imparable! $streakDays días consecutivos ⚡"
        }
    }
}

/**
 * Resultado de actualizar la racha
 */
data class StreakUpdate(
    val newStreak: Int,
    val shouldAwardBonus: Boolean, // Si se debe otorgar el bonus de 5 puntos
    val streakStatus: StreakStatus
)

enum class StreakStatus {
    STARTED,     // Primera vez o reinicio después de romper racha
    CONTINUED,   // Racha continúa (día consecutivo)
    SAME_DAY,    // Misma fecha que la última actividad
    BROKEN       // Racha se rompió (más de 1 día sin actividad)
}
