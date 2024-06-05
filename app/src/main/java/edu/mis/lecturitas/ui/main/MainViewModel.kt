package edu.mis.lecturitas.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinComponent

class MainViewModel: ViewModel(), KoinComponent {



    private val _openListBook = MutableLiveData<Int?>()
    val openListBook: MutableLiveData<Int?>
        get() = _openListBook

    private val _goBack = MutableLiveData<Boolean>(false)
    val goBack: MutableLiveData<Boolean>
        get() = _goBack
    fun onClickSalaDe3(){
       _openListBook.value=3
    }
    fun onClickSalaDe4(){
        _openListBook.value=4
    }
    fun onClickSalaDe5(){
        _openListBook.value=5
    }

    fun setOpenListBookNull() {
        _openListBook.value = null
    }

    fun backPresed(){
        _goBack.value=true
    }
    fun doneGoback(){
        _goBack.value=false
    }


}