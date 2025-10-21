package edu.mis.lecturitas.model

import java.io.Serializable

data class AudioLibro(
    val idAudioLibro: Int,
    val titulo: String,
    val descripcion: String,
    val urlVideo: String, // URL del video MP4 en Firebase Storage
    val urlImagen: String, // URL de la imagen de portada
    val duracion: String, // Duración del video (ej: "5:30")
    val nivel: Int, // Nivel educativo (3, 4, 5)
    val fechaCreacion: String,
    val autor: String, // Nombre del niño o grupo que creó el video
    val estado: String, // "activo", "inactivo"
    val cantReproducciones: Int?,
    val calificacion: Int?
) : Serializable {
    constructor(): this(0, "", "", "", "", "", 0, "", "", "", 0, 0)
}
