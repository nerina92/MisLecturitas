package edu.mis.lecturitas.di

import edu.mis.lecturitas.ui.login.LoginViewModel
import edu.mis.lecturitas.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module{
    viewModel{ MainViewModel() }
    viewModel{ LoginViewModel() }
}