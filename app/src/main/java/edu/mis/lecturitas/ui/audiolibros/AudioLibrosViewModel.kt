package edu.mis.lecturitas.ui.audiolibros

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.mis.lecturitas.model.AudioLibro
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class AudioLibrosViewModel : ViewModel(), KoinComponent {

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: MutableLiveData<Boolean>
        get() = _showProgressBar

    private val _listaAudioLibros = MutableLiveData<ArrayList<AudioLibro>?>(ArrayList())
    val listaAudioLibros: MutableLiveData<ArrayList<AudioLibro>?> = _listaAudioLibros

    private val _openAudioLibro = MutableLiveData<AudioLibro?>()
    val openAudioLibro: MutableLiveData<AudioLibro?>
        get() = _openAudioLibro

    private val _goBack = MutableLiveData<Boolean>(false)
    val goBack: MutableLiveData<Boolean>
        get() = _goBack

    fun consultarAudioLibros(nivel: Int) {
        println("Consultando audiolibros para el nivel: $nivel")
        _showProgressBar.value = true
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("audiolibros")
        val listaAux = ArrayList<AudioLibro>()
        
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val audioLibro: AudioLibro? = snapshot.getValue(AudioLibro::class.java)
                    if (audioLibro != null && audioLibro.estado == "activo" && audioLibro.nivel == nivel) {
                        listaAux.add(audioLibro)
                    }
                }
                _listaAudioLibros.value = listaAux
                _showProgressBar.value = false
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error al consultar audiolibros", databaseError.toException())
                _showProgressBar.value = false
            }
        })
    }

    fun consultarTodosLosAudioLibros() {
        Log.i("AudioLibrosViewModel", "Consultando todos los audiolibros")
        _showProgressBar.value = true
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("audiolibros")
        val listaAux = ArrayList<AudioLibro>()
        
        Log.i("AudioLibrosViewModel", "Referencia a Firebase: ${myRef.key}")
        
        // Timeout manual de 10 segundos
        val timeoutRunnable = Runnable {
            Log.w("AudioLibrosViewModel", "Timeout alcanzado - cancelando consulta")
            _showProgressBar.value = false
        }
        
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.postDelayed(timeoutRunnable, 10000) // 10 segundos
        
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                handler.removeCallbacks(timeoutRunnable) // Cancelar timeout
                Log.i("AudioLibrosViewModel", "onDataChange llamado")
                Log.i("AudioLibrosViewModel", "Número de hijos: ${dataSnapshot.childrenCount}")
                
                for (snapshot in dataSnapshot.children) {
                    Log.i("AudioLibrosViewModel", "Procesando snapshot: ${snapshot.key}")
                    val audioLibro: AudioLibro? = snapshot.getValue(AudioLibro::class.java)
                    if (audioLibro != null) {
                        Log.i("AudioLibrosViewModel", "AudioLibro encontrado: ${audioLibro.titulo}, estado: ${audioLibro.estado}")
                        if (audioLibro.estado == "activo") {
                            listaAux.add(audioLibro)
                            Log.i("AudioLibrosViewModel", "AudioLibro agregado a la lista")
                        }
                    } else {
                        Log.w("AudioLibrosViewModel", "No se pudo convertir snapshot a AudioLibro")
                    }
                }
                
                Log.i("AudioLibrosViewModel", "Total de audiolibros activos: ${listaAux.size}")
                _listaAudioLibros.value = listaAux
                _showProgressBar.value = false
                Log.i("AudioLibrosViewModel", "ProgressBar establecido a false, lista actualizada con ${listaAux.size} elementos")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                handler.removeCallbacks(timeoutRunnable) // Cancelar timeout
                Log.e("AudioLibrosViewModel", "Error al consultar todos los audiolibros", databaseError.toException())
                Log.e("AudioLibrosViewModel", "Código de error: ${databaseError.code}")
                Log.e("AudioLibrosViewModel", "Mensaje de error: ${databaseError.message}")
                _showProgressBar.value = false
            }
        })
    }

    fun onClickAudioLibro(audioLibro: AudioLibro) {
        _openAudioLibro.value = audioLibro
    }

    fun setOpenAudioLibroNull() {
        _openAudioLibro.value = null
    }

    fun backPresed() {
        _goBack.value = true
    }

    fun doneGoBack() {
        _goBack.value = false
    }
}
