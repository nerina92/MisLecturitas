package edu.mis.lecturitas.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.mis.lecturitas.model.ResultadoOperacion
import edu.mis.lecturitas.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class LoginViewModel : ViewModel(), KoinComponent {
    private val _resultadoLogin = MutableLiveData<ResultadoOperacion?>()
    val resultadoLogin: LiveData<ResultadoOperacion?> = _resultadoLogin

   /* private val _usuarioLogeado = MutableLiveData<Usuario?>()
    val usuarioLogueado: MutableLiveData<Usuario?>
        get() = _usuarioLogeado*/
    fun consultarUsuario(userIngresado:Usuario) {
       CoroutineScope(Dispatchers.IO).launch {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.reference.child("usuarios")

            val query = myRef.orderByChild("user").equalTo("${userIngresado.user}")

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        var usuario: Usuario? = snapshot.getValue(Usuario::class.java)
                        //val idUs = dataSnapshot.key
                        //usuario?.id=idUs
                        if (usuario != null) {
                            Log.d("Firebase read", "Usuario coincident encontrado: $usuario")
                            Log.d("User ingresado", "Usuario ingresado: $userIngresado")
                            if (usuario.pasword == userIngresado.pasword) {
                               // _usuarioLogeado.postValue(usuario)
                                _resultadoLogin.value=ResultadoOperacion(true, "")
                            } else {
                                _resultadoLogin.postValue(
                                    ResultadoOperacion(
                                        false,
                                        "Contraseña incorrecta"
                                    )
                                )
                            }
                        } else {
                            _resultadoLogin.postValue(
                                ResultadoOperacion(
                                    false,
                                    "Usuario no encontrado"
                                )
                            )
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Manejar errores si es necesario
                    Log.e("Firebase", "Error al realizar la consulta", databaseError.toException())
                }
            })
        }
    }
}