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
import edu.mis.lecturitas.utils.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class LoginViewModel : ViewModel(), KoinComponent {
    private val _resultadoLogin = MutableLiveData<ResultadoOperacion?>()
    val resultadoLogin: LiveData<ResultadoOperacion?> = _resultadoLogin

    private val _usuarioLogeado = MutableLiveData<Usuario?>()
    val usuarioLogueado: MutableLiveData<Usuario?>
        get() = _usuarioLogeado

    private val _ingresarInvitado = MutableLiveData<Boolean>()
    val ingresarInvitado: MutableLiveData<Boolean>
        get() = _ingresarInvitado

    fun consultarUsuario(userIngresado:Usuario) {
       CoroutineScope(Dispatchers.IO).launch {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.reference.child("usuarios")

            val query = myRef.orderByChild("user").equalTo("${userIngresado.user}")

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.hasChildren()) {
                        // No se encontró el usuario
                        _resultadoLogin.value = ResultadoOperacion(
                            false,
                            "Usuario no encontrado"
                        )
                        return
                    }

                    for (snapshot in dataSnapshot.children) {
                        val usuario: Usuario? = snapshot.getValue(Usuario::class.java)

                        if (usuario != null) {
                            Log.d("Firebase read", "Usuario encontrado: ${usuario.user}")

                            // Verificar contraseña de forma segura
                            val passwordMatch = if (PasswordHasher.isHashed(usuario.pasword)) {
                                // Contraseña ya está hasheada, usar BCrypt para verificar
                                PasswordHasher.checkPassword(userIngresado.pasword, usuario.pasword)
                            } else {
                                // Contraseña antigua en texto plano (compatibilidad temporal)
                                // Comparar directamente
                                val match = usuario.pasword == userIngresado.pasword

                                if (match) {
                                    // Migrar a hash automáticamente en login exitoso
                                    val hashedPassword = PasswordHasher.hashPassword(userIngresado.pasword)
                                    snapshot.ref.child("pasword").setValue(hashedPassword)
                                    Log.i("Security", "Contraseña migrada a hash para usuario: ${usuario.user}")
                                }

                                match
                            }

                            if (passwordMatch) {
                                _usuarioLogeado.value = usuario
                                _resultadoLogin.value = ResultadoOperacion(true, "")
                            } else {
                                _resultadoLogin.value = ResultadoOperacion(
                                    false,
                                    "Contraseña incorrecta"
                                )
                            }
                        } else {
                            _resultadoLogin.value = ResultadoOperacion(
                                false,
                                "Usuario no encontrado"
                            )
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error al realizar la consulta", databaseError.toException())
                    _resultadoLogin.value = ResultadoOperacion(
                        false,
                        "Error de conexión. Intente nuevamente."
                    )
                }
            })
        }
    }

    fun ingresarComoInvitado(value: Boolean) {
        _ingresarInvitado.value = value
    }
}
