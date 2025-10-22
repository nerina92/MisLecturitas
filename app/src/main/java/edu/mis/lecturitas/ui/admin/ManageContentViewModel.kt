package edu.mis.lecturitas.ui.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.mis.lecturitas.model.AudioLibro
import edu.mis.lecturitas.model.Libro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

data class ManageContentUiState(
    val libros: List<Libro> = emptyList(),
    val audioLibros: List<AudioLibro> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ManageContentViewModel : ViewModel(), KoinComponent {
    
    private val _uiState = MutableStateFlow(ManageContentUiState())
    val uiState: StateFlow<ManageContentUiState> = _uiState.asStateFlow()
    
    private val database = FirebaseDatabase.getInstance()
    
    fun loadContent(nivel: Int, contentType: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        when (contentType) {
            "libro" -> loadLibros(nivel)
            "audiolibro" -> loadAudioLibros(nivel)
        }
    }
    
    private fun loadLibros(nivel: Int) {
        val librosRef = database.reference.child("libros")
        val query = librosRef.orderByChild("nivel").equalTo(nivel.toDouble())
        
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val libros = mutableListOf<Libro>()
                for (snapshot in dataSnapshot.children) {
                    val libro: Libro? = snapshot.getValue(Libro::class.java)
                    if (libro != null) {
                        libros.add(libro)
                    }
                }
                _uiState.value = _uiState.value.copy(
                    libros = libros,
                    isLoading = false
                )
            }
            
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ManageContentViewModel", "Error loading libros", databaseError.toException())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar libros: ${databaseError.message}"
                )
            }
        })
    }
    
    private fun loadAudioLibros(nivel: Int) {
        val audioLibrosRef = database.reference.child("audiolibros")
        val query = audioLibrosRef.orderByChild("nivel").equalTo(nivel.toDouble())
        
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val audioLibros = mutableListOf<AudioLibro>()
                for (snapshot in dataSnapshot.children) {
                    val audioLibro: AudioLibro? = snapshot.getValue(AudioLibro::class.java)
                    if (audioLibro != null) {
                        audioLibros.add(audioLibro)
                    }
                }
                _uiState.value = _uiState.value.copy(
                    audioLibros = audioLibros,
                    isLoading = false
                )
            }
            
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ManageContentViewModel", "Error loading audiolibros", databaseError.toException())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar audiolibros: ${databaseError.message}"
                )
            }
        })
    }
    
    fun deleteContent(content: Any) {
        viewModelScope.launch {
            try {
                when (content) {
                    is Libro -> deleteLibro(content)
                    is AudioLibro -> deleteAudioLibro(content)
                }
            } catch (e: Exception) {
                Log.e("ManageContentViewModel", "Error deleting content", e)
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun deleteLibro(libro: Libro) {
        val librosRef = database.reference.child("libros")
        val query = librosRef.orderByChild("idLibro").equalTo(libro.idLibro.toDouble())
        
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
                // Recargar la lista
                loadLibros(libro.nivel)
            }
            
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ManageContentViewModel", "Error deleting libro", databaseError.toException())
            }
        })
    }
    
    private suspend fun deleteAudioLibro(audioLibro: AudioLibro) {
        val audioLibrosRef = database.reference.child("audiolibros")
        val query = audioLibrosRef.orderByChild("idAudioLibro").equalTo(audioLibro.idAudioLibro.toDouble())
        
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
                // Recargar la lista
                loadAudioLibros(audioLibro.nivel)
            }
            
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ManageContentViewModel", "Error deleting audiolibro", databaseError.toException())
            }
        })
    }
}
