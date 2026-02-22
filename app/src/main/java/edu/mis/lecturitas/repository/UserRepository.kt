package edu.mis.lecturitas.repository

import edu.mis.lecturitas.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserRepository {
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()
    
    fun setCurrentUser(usuario: Usuario?) {
        println("UserRepository: Estableciendo usuario actual: ${usuario?.user}")
        _currentUser.value = usuario
        println("UserRepository: Usuario establecido. Tipo: ${usuario?.tipo}, Es admin: ${usuario?.tipo == 1}")
    }
    
    fun isAdmin(): Boolean {
        return _currentUser.value?.tipo == 1 // Asumiendo que tipo 1 = admin
    }
    
    fun getCurrentUser(): Usuario? {
        return _currentUser.value
    }
}
