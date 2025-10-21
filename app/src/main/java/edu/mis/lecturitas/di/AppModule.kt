package edu.mis.lecturitas.di

import edu.mis.lecturitas.ui.audiolibros.AudioLibrosViewModel
import edu.mis.lecturitas.ui.bookList.BookListViewModel
import edu.mis.lecturitas.ui.juegos.JuegosViewModel
import edu.mis.lecturitas.ui.login.LoginViewModel
import edu.mis.lecturitas.ui.main.MainViewModel
import edu.mis.lecturitas.ui.opciones.OpcionesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module{
    viewModel{ MainViewModel() }
    viewModel{ LoginViewModel() }
    viewModel{ BookListViewModel() }
    viewModel{ PlayReadViewModel() }
    viewModel{ OpcionesViewModel() }
    viewModel{ JuegosViewModel() }
    viewModel{ AudioLibrosViewModel() }
}