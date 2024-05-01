package edu.mis.lecturitas

import android.app.Application
import edu.mis.lecturitas.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MisLecturitasApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            //androidLogger(Level.DEBUG)
            androidContext(this@MisLecturitasApplication)
           modules(appModule)
        }
    }
}