package edu.mis.lecturitas.ui.playread

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import org.koin.core.component.KoinComponent

class PlayReadViewModel : ViewModel(), KoinComponent {
    private val _openCuento = MutableLiveData<Boolean>(false)
    val openCuento: LiveData<Boolean> = _openCuento

    fun setOpenCuentoFalse() {
        _openCuento.value = false
    }
}