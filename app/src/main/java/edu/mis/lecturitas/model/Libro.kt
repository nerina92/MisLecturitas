package edu.mis.lecturitas.model

import java.io.Serializable

data class Libro(
    val idLibro : Int,
    val nombre: String,
    val autor: String,
    val url : String,
    val imagen : String,
    val cant_lecturas: Int?,
    val calificacion:Int?,
    val estado:String,
    val nivel : Int,
) : Serializable {
    constructor(): this(0,"","","","",0,0,"",0)
}
