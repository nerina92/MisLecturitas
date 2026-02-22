package edu.mis.lecturitas.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.mis.lecturitas.model.Achievement
import edu.mis.lecturitas.model.AvatarCustomization
import edu.mis.lecturitas.model.UserProgress
import edu.mis.lecturitas.utils.ActivityType
import edu.mis.lecturitas.utils.AchievementManager
import edu.mis.lecturitas.utils.StreakCalculator
import edu.mis.lecturitas.utils.StreakStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositorio para manejar toda la lógica de gamificación
 * Interactúa con Firebase Realtime Database
 */
class GamificationRepository {

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.reference.child("usuarios")

    companion object {
        private const val TAG = "GamificationRepo"
        private const val PROGRESS_NODE = "progress"
        private const val ACHIEVEMENTS_NODE = "achievements"
        private const val AVATAR_NODE = "avatar"
    }

    /**
     * Obtiene el progreso de un usuario como Flow
     * Se actualiza automáticamente cuando cambia en Firebase
     */
    fun getUserProgress(userId: String): Flow<UserProgress?> = callbackFlow {
        val progressRef = usersRef.child(userId).child(PROGRESS_NODE)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val progress = try {
                    snapshot.getValue(UserProgress::class.java)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing user progress", e)
                    null
                }
                trySend(progress)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching user progress", error.toException())
                trySend(null)
            }
        }

        progressRef.addValueEventListener(listener)

        awaitClose {
            progressRef.removeEventListener(listener)
        }
    }

    /**
     * Inicializa el progreso de un usuario nuevo
     */
    suspend fun initializeUserProgress(userId: String): Result<UserProgress> {
        return try {
            val initialProgress = UserProgress(
                userId = userId,
                totalPoints = 0,
                level = 1,
                booksRead = 0,
                audiobooksListened = 0,
                gamesCompleted = 0,
                currentStreak = 0,
                longestStreak = 0,
                lastActivityDate = "",
                achievements = emptyMap(),
                avatar = AvatarCustomization()
            )

            usersRef.child(userId).child(PROGRESS_NODE).setValue(initialProgress).await()
            Log.d(TAG, "User progress initialized for user: $userId")
            Result.success(initialProgress)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing user progress", e)
            Result.failure(e)
        }
    }

    /**
     * Registra que un usuario completó una actividad y otorga puntos
     * Actualiza rachas y verifica logros desbloqueados
     */
    suspend fun recordActivity(
        userId: String,
        activityType: ActivityType,
        currentProgress: UserProgress
    ): Result<ActivityResult> {
        return try {
            val basePoints = AchievementManager.calculatePointsForActivity(activityType)

            // Actualizar racha
            val streakUpdate = StreakCalculator.updateStreak(
                currentProgress.lastActivityDate,
                currentProgress.currentStreak
            )

            // Calcular puntos totales (incluyendo bonus de racha si aplica)
            val bonusPoints = if (streakUpdate.shouldAwardBonus) 5 else 0
            val totalPointsEarned = basePoints + bonusPoints

            // Actualizar contadores según tipo de actividad
            val updatedProgress = currentProgress.copy(
                totalPoints = currentProgress.totalPoints + totalPointsEarned,
                booksRead = if (activityType == ActivityType.BOOK_READ)
                    currentProgress.booksRead + 1 else currentProgress.booksRead,
                audiobooksListened = if (activityType == ActivityType.AUDIOBOOK_LISTENED)
                    currentProgress.audiobooksListened + 1 else currentProgress.audiobooksListened,
                gamesCompleted = if (activityType == ActivityType.GAME_COMPLETED)
                    currentProgress.gamesCompleted + 1 else currentProgress.gamesCompleted,
                currentStreak = streakUpdate.newStreak,
                longestStreak = maxOf(currentProgress.longestStreak, streakUpdate.newStreak),
                lastActivityDate = StreakCalculator.getCurrentDate()
            )

            // Recalcular nivel
            val newLevel = updatedProgress.calculateLevel()
            val leveledUp = newLevel > currentProgress.level
            val finalProgress = updatedProgress.copy(level = newLevel)

            // Verificar logros desbloqueados
            val newAchievements = AchievementManager.checkForNewAchievements(
                finalProgress,
                Achievement.getDefaultAchievements()
            )

            // Actualizar logros desbloqueados en el progreso
            val updatedAchievements = finalProgress.achievements.toMutableMap()
            var bonusFromAchievements = 0
            newAchievements.forEach { achievement ->
                updatedAchievements[achievement.id] = true
                bonusFromAchievements += achievement.pointsReward
            }

            val finalProgressWithAchievements = finalProgress.copy(
                achievements = updatedAchievements,
                totalPoints = finalProgress.totalPoints + bonusFromAchievements
            )

            // Guardar en Firebase
            usersRef.child(userId).child(PROGRESS_NODE)
                .setValue(finalProgressWithAchievements).await()

            Log.d(TAG, "Activity recorded: $activityType, points earned: $totalPointsEarned, new streak: ${streakUpdate.newStreak}")

            Result.success(ActivityResult(
                pointsEarned = totalPointsEarned + bonusFromAchievements,
                newAchievements = newAchievements,
                leveledUp = leveledUp,
                newLevel = finalProgressWithAchievements.level,
                streakContinued = streakUpdate.streakStatus == StreakStatus.CONTINUED,
                streakBroken = streakUpdate.streakStatus == StreakStatus.BROKEN,
                newStreak = streakUpdate.newStreak
            ))
        } catch (e: Exception) {
            Log.e(TAG, "Error recording activity", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza el avatar del usuario
     */
    suspend fun updateAvatar(
        userId: String,
        avatar: AvatarCustomization
    ): Result<Unit> {
        return try {
            usersRef.child(userId).child(PROGRESS_NODE).child(AVATAR_NODE)
                .setValue(avatar).await()
            Log.d(TAG, "Avatar updated for user: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating avatar", e)
            Result.failure(e)
        }
    }

    /**
     * Compra un item de avatar con puntos
     */
    suspend fun purchaseAvatarItem(
        userId: String,
        itemId: String,
        cost: Int,
        currentProgress: UserProgress
    ): Result<PurchaseResult> {
        return try {
            // Verificar que tenga suficientes puntos
            if (currentProgress.totalPoints < cost) {
                return Result.success(PurchaseResult(
                    success = false,
                    message = "No tenés suficientes puntos",
                    newPoints = currentProgress.totalPoints
                ))
            }

            // Descontar puntos
            val newPoints = currentProgress.totalPoints - cost
            usersRef.child(userId).child(PROGRESS_NODE).child("totalPoints")
                .setValue(newPoints).await()

            // Nota: Los items desbloqueados se manejarán en el ViewModel/UI
            // Aquí solo actualizamos los puntos

            Log.d(TAG, "Item purchased: $itemId, cost: $cost, new points: $newPoints")

            Result.success(PurchaseResult(
                success = true,
                message = "¡Item desbloqueado!",
                newPoints = newPoints
            ))
        } catch (e: Exception) {
            Log.e(TAG, "Error purchasing item", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene el ranking de un usuario (top users por puntos)
     * Útil para futuras features de tablas de clasificación
     */
    suspend fun getUserRanking(userId: String): Result<Int> {
        return try {
            // TODO: Implementar cuando se agregue feature de ranking
            Result.success(1)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user ranking", e)
            Result.failure(e)
        }
    }
}

/**
 * Resultado de registrar una actividad
 */
data class ActivityResult(
    val pointsEarned: Int,
    val newAchievements: List<Achievement>,
    val leveledUp: Boolean,
    val newLevel: Int,
    val streakContinued: Boolean,
    val streakBroken: Boolean,
    val newStreak: Int
)

/**
 * Resultado de comprar un item
 */
data class PurchaseResult(
    val success: Boolean,
    val message: String,
    val newPoints: Int
)
