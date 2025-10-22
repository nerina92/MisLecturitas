package edu.mis.lecturitas.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class MainViewModel: ViewModel(), KoinComponent {



    private val _openListBook = MutableLiveData<Int?>()
    val openListBook: MutableLiveData<Int?>
        get() = _openListBook


    private val _openOpciones = MutableLiveData<Boolean>(false)
    val openOpciones: MutableLiveData<Boolean>
        get() = _openOpciones

    private val _goBack = MutableLiveData<Boolean>(false)
    val goBack: MutableLiveData<Boolean>
        get() = _goBack

    private val _openAdmin = MutableLiveData<Boolean>(false)
    val openAdmin: MutableLiveData<Boolean>
        get() = _openAdmin
    fun onClickSalaDe3(){
       _openListBook.value=3
    }
    fun onClickSalaDe4(){
        _openListBook.value=4
    }
    fun onClickSalaDe5(){
        _openListBook.value=5
    }

    fun onClickJugar(){
        _openOpciones.value=true
    }
    
    fun onClickAdmin(){
        _openAdmin.value=true
    }
    fun setOpenListBookNull() {
        _openListBook.value = null
    }
    
    fun setOpenOpcionesFalse() {
        _openOpciones.value = false
    }
    
    fun setOpenAdminFalse() {
        _openAdmin.value = false
    }

    fun backPresed(){
        _goBack.value=true
    }
    fun doneGoback(){
        _goBack.value=false
    }

    fun onClickSalaDe5Play() {
        TODO("Not yet implemented")
    }

    fun onClickSalaDe5Read() {
        TODO("Not yet implemented")
    }


}