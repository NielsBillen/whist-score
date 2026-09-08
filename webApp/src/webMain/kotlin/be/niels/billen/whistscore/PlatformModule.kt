package be.niels.billen.whistscore

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.dsl.bind
import org.koin.dsl.module

val platformModule = module {
    single { StorageSettings() }.bind<Settings>()
}
