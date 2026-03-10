package com.inntemp.inningstempotracker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.inntemp.inningstempotracker.data.datastore.AppPreferences
import com.inntemp.inningstempotracker.data.db.AppDatabase
import com.inntemp.inningstempotracker.data.repository.InningsRepository
import com.inntemp.inningstempotracker.data.repository.InningsRepositoryImpl
import com.inntemp.inningstempotracker.ui.analytics.AnalyticsViewModel
import com.inntemp.inningstempotracker.ui.detail.InningsDetailViewModel
import com.inntemp.inningstempotracker.ui.edit.EditInningViewModel
import com.inntemp.inningstempotracker.ui.home.HomeViewModel
import com.inntemp.inningstempotracker.ui.library.InningsLibraryViewModel
import com.inntemp.inningstempotracker.ui.match.CreateMatchViewModel
import com.inntemp.inningstempotracker.ui.onboarding.OnboardingViewModel
import com.inntemp.inningstempotracker.ui.over.OverInputViewModel
import com.inntemp.inningstempotracker.ui.preloader.PreloaderViewModel
import com.inntemp.inningstempotracker.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "innings_tempo_db")
            .build()
    }
    single { get<AppDatabase>().matchDao() }
    single { get<AppDatabase>().overDao() }

    // DataStore
    single<DataStore<Preferences>> { androidContext().dataStore }
    single { AppPreferences(get()) }

    // Repository
    single<InningsRepository> { InningsRepositoryImpl(get(), get()) }

    // ViewModels
    viewModel { PreloaderViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { CreateMatchViewModel(get()) }
    viewModel { parameters -> OverInputViewModel(get(), parameters.get()) }
    viewModel { InningsLibraryViewModel(get()) }
    viewModel { parameters -> InningsDetailViewModel(get(), parameters.get()) }
    viewModel { parameters -> EditInningViewModel(get(), parameters.get(), parameters.get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
