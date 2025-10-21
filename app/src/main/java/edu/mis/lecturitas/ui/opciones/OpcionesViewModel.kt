package edu.mis.lecturitas.ui.opciones

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class OpcionesViewModel : ViewModel(), KoinComponent {

    private val _goBack = MutableLiveData<Boolean>(false)
    val goBack: MutableLiveData<Boolean>
        get() = _goBack

    private val _openJuegos = MutableLiveData<Boolean>(false)
    val openJuegos: MutableLiveData<Boolean>
        get() = _openJuegos

    private val _openAudiolibros = MutableLiveData<Boolean>(false)
    val openAudiolibros: MutableLiveData<Boolean>
        get() = _openAudiolibros

    fun onClickJuegos() {
        _openJuegos.value = true
    }

    fun onClickAudiolibros() {
        _openAudiolibros.value = true
    }

    fun backPresed() {
        _goBack.value = true
    }

    fun doneGoback() {
        _goBack.value = false
    }

    fun setOpenJuegosFalse() {
        _openJuegos.value = false
    }

    fun setOpenAudiolibrosFalse() {
        _openAudiolibros.value = false
    }
}
