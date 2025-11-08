package be.niels.billen.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    single { SharedPreferencesSettings.Factory(context = get()).create() }.bind<Settings>()
}