package edu.mis.lecturitas.ui.playread

class PlayReadViewModel (): ViewModel(), KoinComponent  {
    private val _openCuento = MutableLiveData<boolean>(false)
    val openCuento: LiveData<boolean> = _resultadoLogin

    fun setOpenCuentoFalse() {
        _openCuento.value = false
    }
}