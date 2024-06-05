package edu.mis.lecturitas.model

import java.io.Serializable

class Usuario: Serializable{
    var idUser : Int? = null
    var user : String = ""
    var pasword : String = ""
    var name : String = ""
    var mail : String? = ""
    var tipo : Int = 0
    var state : Boolean? = true

    constructor()

    constructor(user : String, password : String) {
        this.idUser = null
        this.user = user
        this.pasword = password
    }
    constructor(idUser: Int, user : String, password : String, nombre : String, mail : String, tipo: Int, estado : Boolean) {
        this.idUser = idUser
        this.user = user
        this.pasword = password
        this.name = nombre
        this.mail = mail
        this.tipo = tipo
        this.state = estado
    }

    override fun toString(): String {
        return "Usuario(id=$idUser, user='$user', name='$name, password='$pasword', mail='$mail', tipo=$tipo, state=$state')"
    }

}
