package be.niels.billen.whistscore

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.bind
import org.koin.dsl.module
import java.util.prefs.Preferences

val platformModule = module {
    single { Preferences.userRoot().node("whist-score").node("settings") }.bind<Preferences>()
    single { PreferencesSettings(delegate = get()) }.bind<Settings>()
}
