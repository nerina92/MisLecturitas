package edu.mis.lecturitas.ui.admin

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import edu.mis.lecturitas.model.AudioLibro
import edu.mis.lecturitas.model.Libro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.util.*

enum class ContentType {
    LIBRO, AUDIOLIBRO
}

data class AdminUiState(
    val contentType: ContentType = ContentType.LIBRO,
    val titulo: String = "",
    val autor: String = "",
    val urlPdf: String = "",
    val descripcion: String = "",
    val nivel: Int = 3,
    val pdfUri: Uri? = null,
    val videoUri: Uri? = null,
    val imageUri: Uri? = null,
    val pdfFileName: String = "",
    val videoFileName: String = "",
    val isUploading: Boolean = false,
    val isValid: Boolean = false
) {
    fun validate(): Boolean {
        val tituloValid = titulo.isNotBlank()
        val autorValid = autor.isNotBlank()
        val nivelValid = nivel in 3..5
        val imageValid = imageUri != null
        val libroValid = contentType == ContentType.LIBRO && (urlPdf.isNotBlank() || pdfUri != null)
        val audiolibroValid = contentType == ContentType.AUDIOLIBRO && (descripcion.isNotBlank() && videoUri != null)
        val contentValid = libroValid || audiolibroValid
        
        val isValid = tituloValid && autorValid && nivelValid && imageValid && contentValid
        
        Log.d("AdminViewModel", "Validación: titulo=$tituloValid, autor=$autorValid, nivel=$nivelValid, image=$imageValid, content=$contentValid, isValid=$isValid")
        Log.d("AdminViewModel", "Detalles: titulo='$titulo', autor='$autor', nivel=$nivel, imageUri=$imageUri, contentType=$contentType")
        Log.d("AdminViewModel", "Libro: urlPdf='$urlPdf', pdfUri=$pdfUri")
        Log.d("AdminViewModel", "Audiolibro: descripcion='$descripcion', videoUri=$videoUri")
        
        return isValid
    }
}

class AdminViewModel : ViewModel(), KoinComponent {
    
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()
    
    private val _showMessage = MutableStateFlow("")
    val showMessage: StateFlow<String> = _showMessage.asStateFlow()
    
    private val _isPublished = MutableStateFlow(false)
    val isPublished: StateFlow<Boolean> = _isPublished.asStateFlow()
    
    private val database = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance("gs://mis-lecturitas-7b62a.appspot.com")
    
    init {
        // Validar estado inicial
        validateCurrentState()
    }
    
    fun setContentType(contentType: ContentType) {
        _uiState.value = _uiState.value.copy(contentType = contentType)
        validateCurrentState()
    }
    
    fun setTitulo(titulo: String) {
        _uiState.value = _uiState.value.copy(titulo = titulo)
        validateCurrentState()
    }
    
    fun setAutor(autor: String) {
        _uiState.value = _uiState.value.copy(autor = autor)
        validateCurrentState()
    }
    
    fun setUrlPdf(urlPdf: String) {
        _uiState.value = _uiState.value.copy(urlPdf = urlPdf)
        validateCurrentState()
    }
    
    fun setDescripcion(descripcion: String) {
        _uiState.value = _uiState.value.copy(descripcion = descripcion)
        validateCurrentState()
    }
    
    fun setNivel(nivel: Int) {
        _uiState.value = _uiState.value.copy(nivel = nivel)
        validateCurrentState()
    }
    
    fun setPdfUri(uri: Uri) {
        val fileName = getFileName(uri) ?: "documento.pdf"
        _uiState.value = _uiState.value.copy(
            pdfUri = uri,
            pdfFileName = fileName
        )
        validateCurrentState()
    }
    
    fun setVideoUri(uri: Uri) {
        val fileName = getFileName(uri) ?: "video.mp4"
        _uiState.value = _uiState.value.copy(
            videoUri = uri,
            videoFileName = fileName
        )
        validateCurrentState()
    }
    
    fun setImageUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
        validateCurrentState()
    }
    
    private fun validateCurrentState() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isValid = currentState.validate())
    }
    
    private fun getFileName(uri: Uri): String? {
        return try {
            // Por ahora retornamos un nombre genérico
            // En una implementación real, necesitarías acceso al contexto
            when {
                uri.toString().contains("pdf") -> "documento.pdf"
                uri.toString().contains("mp4") -> "video.mp4"
                uri.toString().contains("jpg") || uri.toString().contains("jpeg") -> "imagen.jpg"
                uri.toString().contains("png") -> "imagen.png"
                else -> "archivo"
            }
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error getting file name", e)
            null
        }
    }
    
    fun publishContent() {
        if (!_uiState.value.isValid) {
            _showMessage.value = "Por favor completa todos los campos requeridos"
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isUploading = true)
                _showMessage.value = "Iniciando subida de archivos..."
                
                val currentState = _uiState.value
                
                when (currentState.contentType) {
                    ContentType.LIBRO -> {
                        publishLibro(currentState)
                    }
                    ContentType.AUDIOLIBRO -> {
                        publishAudioLibro(currentState)
                    }
                }
                
            } catch (e: Exception) {
                Log.e("AdminViewModel", "Error publishing content", e)
                _showMessage.value = "Error al publicar: ${e.message}"
                _uiState.value = _uiState.value.copy(isUploading = false)
            }
        }
    }
    
    private suspend fun publishLibro(state: AdminUiState) {
        try {
            _showMessage.value = "Subiendo imagen de portada..."
            
            // Subir imagen de portada
            val imageUrl = if (state.imageUri != null) {
                uploadImage(state.imageUri!!, "libros/imagenes/${state.titulo.replace(" ", "_")}_${System.currentTimeMillis()}.jpg")
            } else {
                ""
            }
            
            _showMessage.value = "Subiendo archivo PDF..."
            
            // Subir PDF si se seleccionó un archivo
            val pdfUrl = if (state.pdfUri != null) {
                uploadPdf(state.pdfUri!!, "libros/pdfs/${state.titulo.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")
            } else {
                state.urlPdf // Usar URL proporcionada
            }
            
            _showMessage.value = "Guardando en base de datos..."
            
            // Crear objeto Libro
            val libro = Libro(
                idLibro = generateId(),
                nombre = state.titulo,
                autor = state.autor,
                url = pdfUrl,
                imagen = imageUrl,
                cant_lecturas = 0,
                calificacion = 0,
                estado = "activo",
                nivel = state.nivel
            )
            
            // Guardar en Firebase Database
            val librosRef = database.reference.child("libros")
            val newLibroRef = librosRef.push()
            newLibroRef.setValue(libro).await()
            
            _isPublished.value = true
            
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error publishing libro", e)
            throw e
        }
    }
    
    private suspend fun publishAudioLibro(state: AdminUiState) {
        try {
            _showMessage.value = "Subiendo imagen de portada..."
            
            // Subir imagen de portada
            val imageUrl = if (state.imageUri != null) {
                uploadImage(state.imageUri!!, "audiolibros/imagenes/${state.titulo.replace(" ", "_")}_${System.currentTimeMillis()}.jpg")
            } else {
                ""
            }
            
            _showMessage.value = "Subiendo archivo de video..."
            
            // Subir video
            val videoUrl = if (state.videoUri != null) {
                uploadVideo(state.videoUri!!, "audiolibros/videos/${state.titulo.replace(" ", "_")}_${System.currentTimeMillis()}.mp4")
            } else {
                ""
            }
            
            _showMessage.value = "Guardando en base de datos..."
            
            // Crear objeto AudioLibro
            val audioLibro = AudioLibro(
                idAudioLibro = generateId(),
                titulo = state.titulo,
                descripcion = state.descripcion,
                urlVideo = videoUrl,
                urlImagen = imageUrl,
                duracion = "0:00", // Se podría calcular la duración real del video
                nivel = state.nivel,
                fechaCreacion = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                autor = state.autor,
                estado = "activo",
                cantReproducciones = 0,
                calificacion = 0
            )
            
            // Guardar en Firebase Database
            val audiolibrosRef = database.reference.child("audiolibros")
            val newAudioLibroRef = audiolibrosRef.push()
            newAudioLibroRef.setValue(audioLibro).await()
            
            _isPublished.value = true
            
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error publishing audiolibro", e)
            throw e
        }
    }
    
    private suspend fun uploadImage(uri: Uri, path: String): String {
        return try {
            val storageRef = storage.reference.child(path)
            
            // Verificar si el archivo ya existe
            try {
                val existingUrl = storageRef.downloadUrl.await()
                Log.d("AdminViewModel", "Imagen ya existe, usando URL existente: $existingUrl")
                return existingUrl.toString()
            } catch (e: Exception) {
                // El archivo no existe, proceder con la subida
                Log.d("AdminViewModel", "Imagen no existe, subiendo nuevo archivo")
            }
            
            val uploadTask = storageRef.putFile(uri)
            uploadTask.await()
            val downloadUrl = storageRef.downloadUrl.await()
            Log.d("AdminViewModel", "Imagen subida exitosamente: $downloadUrl")
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error subiendo imagen", e)
            throw Exception("Error al subir imagen: ${e.message}")
        }
    }
    
    private suspend fun uploadPdf(uri: Uri, path: String): String {
        return try {
            val storageRef = storage.reference.child(path)
            
            // Verificar si el archivo ya existe
            try {
                val existingUrl = storageRef.downloadUrl.await()
                Log.d("AdminViewModel", "PDF ya existe, usando URL existente: $existingUrl")
                return existingUrl.toString()
            } catch (e: Exception) {
                // El archivo no existe, proceder con la subida
                Log.d("AdminViewModel", "PDF no existe, subiendo nuevo archivo")
            }
            
            val uploadTask = storageRef.putFile(uri)
            uploadTask.await()
            val downloadUrl = storageRef.downloadUrl.await()
            Log.d("AdminViewModel", "PDF subido exitosamente: $downloadUrl")
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error subiendo PDF", e)
            throw Exception("Error al subir PDF: ${e.message}")
        }
    }
    
    private suspend fun uploadVideo(uri: Uri, path: String): String {
        return try {
            val storageRef = storage.reference.child(path)
            
            // Verificar si el archivo ya existe
            try {
                val existingUrl = storageRef.downloadUrl.await()
                Log.d("AdminViewModel", "Video ya existe, usando URL existente: $existingUrl")
                return existingUrl.toString()
            } catch (e: Exception) {
                // El archivo no existe, proceder con la subida
                Log.d("AdminViewModel", "Video no existe, subiendo nuevo archivo")
            }
            
            val uploadTask = storageRef.putFile(uri)
            uploadTask.await()
            val downloadUrl = storageRef.downloadUrl.await()
            Log.d("AdminViewModel", "Video subido exitosamente: $downloadUrl")
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("AdminViewModel", "Error subiendo video", e)
            throw Exception("Error al subir video: ${e.message}")
        }
    }
    
    private fun generateId(): Int {
        return (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    }
}
