package edu.mis.lecturitas.model

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
){
    constructor(): this(0,"","","","",0,0,"",0)
}
