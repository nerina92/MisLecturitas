package edu.mis.lecturitas.ui.gamification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.mis.lecturitas.model.AvatarCustomization
import edu.mis.lecturitas.model.AvatarItem
import edu.mis.lecturitas.model.AvatarCategory
import edu.mis.lecturitas.model.UserProgress
import edu.mis.lecturitas.repository.GamificationRepository
import edu.mis.lecturitas.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el personalizador de avatar
 */
class AvatarCustomizerViewModel(
    private val gamificationRepository: GamificationRepository = GamificationRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AvatarCustomizerUiState>(AvatarCustomizerUiState.Loading)
    val uiState: StateFlow<AvatarCustomizerUiState> = _uiState.asStateFlow()

    private val _currentAvatar = MutableStateFlow(AvatarCustomization())
    val currentAvatar: StateFlow<AvatarCustomization> = _currentAvatar.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AvatarCategory.CHARACTER)
    val selectedCategory: StateFlow<AvatarCategory> = _selectedCategory.asStateFlow()

    private val _availableItems = MutableStateFlow<List<AvatarItem>>(emptyList())
    val availableItems: StateFlow<List<AvatarItem>> = _availableItems.asStateFlow()

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private var currentProgress: UserProgress? = null
    private var hasUnsavedChanges = false

    companion object {
        private const val TAG = "AvatarCustomizerVM"
    }

    init {
        loadAvatarData()
    }

    /**
     * Carga los datos del avatar y progreso del usuario
     */
    fun loadAvatarData() {
        viewModelScope.launch {
            try {
                _uiState.value = AvatarCustomizerUiState.Loading

                val currentUser = userRepository.currentUser.value
                if (currentUser == null) {
                    _uiState.value = AvatarCustomizerUiState.Error("Usuario no encontrado")
                    return@launch
                }

                gamificationRepository.getUserProgress(currentUser.idUser.toString())
                    .collect { progress ->
                        if (progress != null) {
                            currentProgress = progress
                            _currentAvatar.value = progress.avatar
                            _userPoints.value = progress.totalPoints
                            updateAvailableItems(progress)
                            _uiState.value = AvatarCustomizerUiState.Success
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading avatar data", e)
                _uiState.value = AvatarCustomizerUiState.Error("Error al cargar avatar: ${e.message}")
            }
        }
    }

    /**
     * Actualiza la lista de items disponibles según la categoría seleccionada
     */
    private fun updateAvailableItems(progress: UserProgress) {
        val allItems = AvatarItem.getAllItems()
        val categoryItems = allItems.filter { it.category == _selectedCategory.value }

        // Marcar como desbloqueados los que tienen costo 0 o que ya fueron comprados
        // TODO: Persistir items comprados en Firebase
        val itemsWithUnlockStatus = categoryItems.map { item ->
            item.copy(unlocked = item.pointsCost == 0 || progress.totalPoints >= item.pointsCost)
        }

        _availableItems.value = itemsWithUnlockStatus
    }

    /**
     * Cambia la categoría seleccionada
     */
    fun selectCategory(category: AvatarCategory) {
        _selectedCategory.value = category
        currentProgress?.let { updateAvailableItems(it) }
    }

    /**
     * Selecciona un item para el avatar (previsualización)
     */
    fun selectItem(item: AvatarItem) {
        val updatedAvatar = when (item.category) {
            AvatarCategory.CHARACTER -> _currentAvatar.value.copy(baseCharacter = item.id)
            AvatarCategory.HAT -> _currentAvatar.value.copy(hat = item.id)
            AvatarCategory.ACCESSORY -> _currentAvatar.value.copy(accessory = item.id)
            AvatarCategory.BACKGROUND -> _currentAvatar.value.copy(background = item.id)
            AvatarCategory.FRAME -> _currentAvatar.value.copy(frame = item.id)
        }

        _currentAvatar.value = updatedAvatar
        hasUnsavedChanges = true
        Log.d(TAG, "Item selected: ${item.name} (${item.category})")
    }

    /**
     * Compra un item bloqueado
     */
    fun purchaseItem(item: AvatarItem) {
        viewModelScope.launch {
            try {
                val progress = currentProgress ?: return@launch
                val currentUser = userRepository.currentUser.value ?: return@launch

                if (item.unlocked || item.pointsCost == 0) {
                    // Ya está desbloqueado, solo seleccionarlo
                    selectItem(item)
                    return@launch
                }

                _uiState.value = AvatarCustomizerUiState.Purchasing

                val result = gamificationRepository.purchaseAvatarItem(
                    userId = currentUser.idUser.toString(),
                    itemId = item.id,
                    cost = item.pointsCost,
                    currentProgress = progress
                )

                result.onSuccess { purchaseResult ->
                    if (purchaseResult.success) {
                        _userPoints.value = purchaseResult.newPoints
                        selectItem(item)
                        _uiState.value = AvatarCustomizerUiState.PurchaseSuccess(purchaseResult.message)

                        // Volver a estado Success después de 2 segundos
                        kotlinx.coroutines.delay(2000)
                        _uiState.value = AvatarCustomizerUiState.Success
                    } else {
                        _uiState.value = AvatarCustomizerUiState.PurchaseError(purchaseResult.message)
                        kotlinx.coroutines.delay(2000)
                        _uiState.value = AvatarCustomizerUiState.Success
                    }
                }.onFailure { error ->
                    _uiState.value = AvatarCustomizerUiState.PurchaseError("Error al comprar: ${error.message}")
                    kotlinx.coroutines.delay(2000)
                    _uiState.value = AvatarCustomizerUiState.Success
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error purchasing item", e)
                _uiState.value = AvatarCustomizerUiState.PurchaseError("Error al comprar item")
                kotlinx.coroutines.delay(2000)
                _uiState.value = AvatarCustomizerUiState.Success
            }
        }
    }

    /**
     * Guarda los cambios del avatar
     */
    fun saveAvatar() {
        viewModelScope.launch {
            try {
                if (!hasUnsavedChanges) {
                    _uiState.value = AvatarCustomizerUiState.SaveSuccess
                    return@launch
                }

                val currentUser = userRepository.currentUser.value ?: return@launch
                _uiState.value = AvatarCustomizerUiState.Saving

                val result = gamificationRepository.updateAvatar(
                    userId = currentUser.idUser.toString(),
                    avatar = _currentAvatar.value
                )

                result.onSuccess {
                    hasUnsavedChanges = false
                    _uiState.value = AvatarCustomizerUiState.SaveSuccess
                    Log.d(TAG, "Avatar saved successfully")
                }.onFailure { error ->
                    _uiState.value = AvatarCustomizerUiState.Error("Error al guardar: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving avatar", e)
                _uiState.value = AvatarCustomizerUiState.Error("Error al guardar avatar")
            }
        }
    }

    /**
     * Cancela los cambios y vuelve al avatar guardado
     */
    fun cancelChanges() {
        currentProgress?.let {
            _currentAvatar.value = it.avatar
            hasUnsavedChanges = false
        }
    }

    /**
     * Verifica si hay cambios sin guardar
     */
    fun hasUnsavedChanges(): Boolean = hasUnsavedChanges
}

/**
 * Estados de la UI del personalizador de avatar
 */
sealed class AvatarCustomizerUiState {
    object Loading : AvatarCustomizerUiState()
    object Success : AvatarCustomizerUiState()
    object Saving : AvatarCustomizerUiState()
    object SaveSuccess : AvatarCustomizerUiState()
    object Purchasing : AvatarCustomizerUiState()
    data class PurchaseSuccess(val message: String) : AvatarCustomizerUiState()
    data class PurchaseError(val message: String) : AvatarCustomizerUiState()
    data class Error(val message: String) : AvatarCustomizerUiState()
}
