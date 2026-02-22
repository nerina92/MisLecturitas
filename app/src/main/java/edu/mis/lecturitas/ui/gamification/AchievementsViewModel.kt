package edu.mis.lecturitas.ui.gamification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.mis.lecturitas.model.Achievement
import edu.mis.lecturitas.model.AchievementType
import edu.mis.lecturitas.model.UserProgress
import edu.mis.lecturitas.repository.GamificationRepository
import edu.mis.lecturitas.repository.UserRepository
import edu.mis.lecturitas.utils.AchievementManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de logros/medallas
 */
class AchievementsViewModel(
    private val gamificationRepository: GamificationRepository = GamificationRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AchievementsUiState>(AchievementsUiState.Loading)
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow<AchievementFilter>(AchievementFilter.ALL)
    val selectedFilter: StateFlow<AchievementFilter> = _selectedFilter.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementWithProgress>>(emptyList())
    val achievements: StateFlow<List<AchievementWithProgress>> = _achievements.asStateFlow()

    private var currentProgress: UserProgress? = null

    companion object {
        private const val TAG = "AchievementsViewModel"
    }

    init {
        loadAchievements()
    }

    /**
     * Carga todos los logros con su progreso
     */
    fun loadAchievements() {
        viewModelScope.launch {
            try {
                _uiState.value = AchievementsUiState.Loading

                val currentUser = userRepository.currentUser.value
                if (currentUser == null) {
                    _uiState.value = AchievementsUiState.Error("Usuario no encontrado")
                    return@launch
                }

                gamificationRepository.getUserProgress(currentUser.idUser.toString())
                    .collect { progress ->
                        if (progress != null) {
                            currentProgress = progress
                            updateAchievementsList(progress)

                            val unlockedCount = progress.unlockedAchievementsCount()
                            val totalCount = Achievement.getDefaultAchievements().size

                            _uiState.value = AchievementsUiState.Success(
                                unlockedCount = unlockedCount,
                                totalCount = totalCount,
                                completionPercentage = (unlockedCount.toFloat() / totalCount.toFloat() * 100).toInt()
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading achievements", e)
                _uiState.value = AchievementsUiState.Error("Error al cargar logros: ${e.message}")
            }
        }
    }

    /**
     * Actualiza la lista de logros con filtro aplicado
     */
    private fun updateAchievementsList(progress: UserProgress) {
        val allAchievements = Achievement.getDefaultAchievements()

        val filteredAchievements = when (_selectedFilter.value) {
            AchievementFilter.ALL -> allAchievements
            AchievementFilter.UNLOCKED -> allAchievements.filter {
                progress.isAchievementUnlocked(it.id)
            }
            AchievementFilter.LOCKED -> allAchievements.filter {
                !progress.isAchievementUnlocked(it.id)
            }
            AchievementFilter.BOOKS -> allAchievements.filter {
                it.type == AchievementType.BOOKS
            }
            AchievementFilter.AUDIOBOOKS -> allAchievements.filter {
                it.type == AchievementType.AUDIOBOOKS
            }
            AchievementFilter.GAMES -> allAchievements.filter {
                it.type == AchievementType.GAMES
            }
            AchievementFilter.STREAK -> allAchievements.filter {
                it.type == AchievementType.STREAK
            }
        }

        val achievementsWithProgress = filteredAchievements.map { achievement ->
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
     * Cambia el filtro de logros
     */
    fun setFilter(filter: AchievementFilter) {
        _selectedFilter.value = filter
        currentProgress?.let { updateAchievementsList(it) }
    }

    /**
     * Muestra detalles de un logro
     */
    fun onAchievementClick(achievement: Achievement) {
        // Podría mostrar un dialog con más detalles
        Log.d(TAG, "Achievement clicked: ${achievement.name}")
    }

    /**
     * Refresca la lista de logros
     */
    fun refresh() {
        loadAchievements()
    }
}

/**
 * Estados de la UI de logros
 */
sealed class AchievementsUiState {
    object Loading : AchievementsUiState()
    data class Success(
        val unlockedCount: Int,
        val totalCount: Int,
        val completionPercentage: Int
    ) : AchievementsUiState()
    data class Error(val message: String) : AchievementsUiState()
}

/**
 * Filtros para la lista de logros
 */
enum class AchievementFilter {
    ALL,        // Todos los logros
    UNLOCKED,   // Solo desbloqueados
    LOCKED,     // Solo bloqueados
    BOOKS,      // Solo de libros
    AUDIOBOOKS, // Solo de audiolibros
    GAMES,      // Solo de juegos
    STREAK      // Solo de rachas
}
