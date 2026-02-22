package edu.mis.lecturitas.utils

import org.mindrot.jbcrypt.BCrypt

/**
 * Utilidad para hashear y verificar contraseñas de forma segura usando BCrypt
 */
object PasswordHasher {

    private const val LOG_ROUNDS = 12 // Factor de complejidad (12 es un buen balance)

    /**
     * Hashea una contraseña usando BCrypt
     * @param plainPassword La contraseña en texto plano
     * @return El hash de la contraseña
     */
    fun hashPassword(plainPassword: String): String {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS))
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash
     * @param plainPassword La contraseña en texto plano a verificar
     * @param hashedPassword El hash almacenado en la base de datos
     * @return true si la contraseña es correcta, false en caso contrario
     */
    fun checkPassword(plainPassword: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(plainPassword, hashedPassword)
        } catch (e: Exception) {
            // Si el hash no es válido (ej: contraseñas antiguas en texto plano),
            // comparamos directamente por compatibilidad temporal
            false
        }
    }

    /**
     * Verifica si una contraseña ya está hasheada con BCrypt
     * @param password La contraseña a verificar
     * @return true si está hasheada, false si es texto plano
     */
    fun isHashed(password: String): Boolean {
        // Los hashes de BCrypt siempre empiezan con "$2a$", "$2b$" o "$2y$"
        return password.startsWith("$2a$") ||
               password.startsWith("$2b$") ||
               password.startsWith("$2y$")
    }

    /**
     * Migra una contraseña en texto plano a hash (para usuarios existentes)
     * @param plainPassword La contraseña en texto plano
     * @return El hash de la contraseña
     */
    fun migratePassword(plainPassword: String): String {
        return if (isHashed(plainPassword)) {
            // Ya está hasheada, no hacer nada
            plainPassword
        } else {
            // Hashear la contraseña en texto plano
            hashPassword(plainPassword)
        }
    }
}
