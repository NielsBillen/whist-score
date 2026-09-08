package be.niels.billen.whistscore

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.bind
import org.koin.dsl.module

val platformModule = module {
    single { SharedPreferencesSettings.Factory(context = get()).create() }.bind<Settings>()
}
