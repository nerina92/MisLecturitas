package edu.mis.lecturitas.ui.juegos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class JuegosViewModel : ViewModel(), KoinComponent {

    private val _goBack = MutableLiveData<Boolean>(false)
    val goBack: MutableLiveData<Boolean>
        get() = _goBack

    private val _openFormas = MutableLiveData<Boolean>(false)
    val openFormas: MutableLiveData<Boolean>
        get() = _openFormas

    private val _openPuzzle = MutableLiveData<Boolean>(false)
    val openPuzzle: MutableLiveData<Boolean>
        get() = _openPuzzle

    fun onClickFormas() {
        _openFormas.value = true
    }

    fun onClickPuzzle() {
        _openPuzzle.value = true
    }

    fun backPresed() {
        _goBack.value = true
    }

    fun doneGoback() {
        _goBack.value = false
    }

    fun setOpenFormasFalse() {
        _openFormas.value = false
    }
}
