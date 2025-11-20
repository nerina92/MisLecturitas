package edu.mis.lecturitas.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.mis.lecturitas.model.ResultadoOperacion
import edu.mis.lecturitas.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent

class LoginViewModel : ViewModel(), KoinComponent {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    
    private val _resultadoLogin = MutableLiveData<ResultadoOperacion?>()
    val resultadoLogin: LiveData<ResultadoOperacion?> = _resultadoLogin

    private val _usuarioLogeado = MutableLiveData<Usuario?>()
    val usuarioLogueado: MutableLiveData<Usuario?>
        get() = _usuarioLogeado

    private val _ingresarInvitado = MutableLiveData<Boolean>()
    val ingresarInvitado: MutableLiveData<Boolean>
        get() = _ingresarInvitado

    fun consultarUsuario(userIngresado: Usuario) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("LoginViewModel", "Iniciando búsqueda de usuario: ${userIngresado.user}")
                
                // Paso 1: Buscar el usuario en la base de datos para obtener su email
                val myRef = database.reference.child("usuarios")
                val query = myRef.orderByChild("user").equalTo(userIngresado.user)
                
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("LoginViewModel", "DataSnapshot exists: ${dataSnapshot.exists()}")
                        Log.d("LoginViewModel", "DataSnapshot children count: ${dataSnapshot.childrenCount}")
                        
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                Log.d("LoginViewModel", "Procesando snapshot: ${snapshot.key}")
                                val usuario: Usuario? = snapshot.getValue(Usuario::class.java)
                                Log.d("LoginViewModel", "Usuario obtenido: $usuario")
                                
                                if (usuario != null) {
                                    // Paso 2: Obtener el email (usar mail si existe, sino crear uno)
                                    val email = if (!usuario.mail.isNullOrEmpty()) {
                                        usuario.mail!!
                                    } else {
                                        "${usuario.user}@mislecturitas.com"
                                    }
                                    
                                    Log.d("LoginViewModel", "Usuario encontrado: ${usuario.user}, email: $email")
                                    
                                    // Paso 3: Autenticar con Firebase Auth
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            auth.signInWithEmailAndPassword(email, userIngresado.pasword).await()
                                            Log.d("LoginViewModel", "Autenticación exitosa con Firebase Auth")
                                            
                                            // Si llegamos aquí, la autenticación fue exitosa
                                            _usuarioLogeado.postValue(usuario)
                                            _resultadoLogin.postValue(ResultadoOperacion(true, ""))
                                        } catch (authException: Exception) {
                                            Log.e("LoginViewModel", "Error en autenticación de Firebase", authException)
                                            _resultadoLogin.postValue(
                                                ResultadoOperacion(false, "Usuario o contraseña incorrectos")
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("LoginViewModel", "Usuario no encontrado en la base de datos")
                            _resultadoLogin.postValue(
                                ResultadoOperacion(false, "Usuario no encontrado")
                            )
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("LoginViewModel", "Error al buscar usuario", databaseError.toException())
                        _resultadoLogin.postValue(
                            ResultadoOperacion(false, "Error de conexión")
                        )
                    }
                })
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error general en login", e)
                _resultadoLogin.postValue(
                    ResultadoOperacion(false, "Error inesperado: ${e.message}")
                )
            }
        }
    }

    fun ingresarComoInvitado(value: Boolean) {
        _ingresarInvitado.value = value
    }
}
