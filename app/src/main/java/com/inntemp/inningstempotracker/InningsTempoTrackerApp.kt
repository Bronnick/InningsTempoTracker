package com.inntemp.inningstempotracker

import android.app.Application
import com.inntemp.inningstempotracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InningsTempoTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@InningsTempoTrackerApp)
            modules(appModule)
        }
    }
}
