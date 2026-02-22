package edu.mis.lecturitas.ui.gamification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.mis.lecturitas.model.Achievement
import edu.mis.lecturitas.model.UserProgress
import edu.mis.lecturitas.repository.GamificationRepository
import edu.mis.lecturitas.repository.UserRepository
import edu.mis.lecturitas.utils.AchievementManager
import edu.mis.lecturitas.utils.StreakCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de perfil del usuario
 */
class ProfileViewModel(
    private val gamificationRepository: GamificationRepository = GamificationRepository(),
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementWithProgress>>(emptyList())
    val achievements: StateFlow<List<AchievementWithProgress>> = _achievements.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        loadUserProfile()
    }

    /**
     * Carga el perfil completo del usuario
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState.Loading

                val currentUser = userRepository.currentUser.value
                if (currentUser == null) {
                    _uiState.value = ProfileUiState.Error("Usuario no encontrado")
                    return@launch
                }

                // Observar cambios en el progreso del usuario
                gamificationRepository.getUserProgress(currentUser.idUser.toString())
                    .collect { progress ->
                        if (progress == null) {
                            // Inicializar progreso si no existe
                            initializeProgress(currentUser.idUser.toString())
                        } else {
                            _userProgress.value = progress
                            updateAchievementsWithProgress(progress)
                            _uiState.value = ProfileUiState.Success(
                                userName = currentUser.name,
                                userProgress = progress,
                                streakMessage = StreakCalculator.getStreakMessage(progress.currentStreak),
                                streakEmoji = StreakCalculator.getStreakEmoji(progress.currentStreak)
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user profile", e)
                _uiState.value = ProfileUiState.Error("Error al cargar el perfil: ${e.message}")
            }
        }
    }

    /**
     * Inicializa el progreso de un usuario nuevo
     */
    private suspend fun initializeProgress(userId: String) {
        val result = gamificationRepository.initializeUserProgress(userId)
        result.onSuccess { progress ->
            _userProgress.value = progress
            updateAchievementsWithProgress(progress)
        }.onFailure { error ->
            Log.e(TAG, "Error initializing progress", error)
            _uiState.value = ProfileUiState.Error("Error al inicializar progreso")
        }
    }

    /**
     * Actualiza la lista de logros con el progreso actual
     */
    private fun updateAchievementsWithProgress(progress: UserProgress) {
        val allAchievements = Achievement.getDefaultAchievements()
        val achievementsWithProgress = allAchievements.map { achievement ->
            AchievementWithProgress(
                achievement = achievement.copy(
                    unlocked = progress.isAchievementUnlocked(achievement.id)
                ),
                progress = AchievementManager.getAchievementProgress(achievement, progress),
                progressText = AchievementManager.getAchievementProgressText(achievement, progress)
            )
        }
        _achievements.value = achievementsWithProgress
    }

    /**
     * Navega a la pantalla de personalización de avatar
     */
    fun onAvatarCustomizeClick() {
        // La navegación se maneja en la Activity
        Log.d(TAG, "Navigate to avatar customizer")
    }

    /**
     * Navega a la pantalla de todos los logros
     */
    fun onViewAllAchievementsClick() {
        // La navegación se maneja en la Activity
        Log.d(TAG, "Navigate to achievements screen")
    }

    /**
     * Refresca los datos del perfil
     */
    fun refresh() {
        loadUserProfile()
    }
}

/**
 * Estados de la UI del perfil
 */
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(
        val userName: String,
        val userProgress: UserProgress,
        val streakMessage: String,
        val streakEmoji: String
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

/**
 * Logro con información de progreso
 */
data class AchievementWithProgress(
    val achievement: Achievement,
    val progress: Float, // 0.0 a 1.0
    val progressText: String // "3 / 10 libros"
)
