package be.niels.billen.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    single { StorageSettings() }.bind<Settings>()
}