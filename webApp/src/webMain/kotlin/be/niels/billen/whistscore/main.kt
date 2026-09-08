package be.niels.billen.whistscore

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import be.niels.billen.whistscore.feature.addround.addRoundModule
import be.niels.billen.whistscore.feature.app.App
import be.niels.billen.whistscore.feature.app.appModule
import be.niels.billen.whistscore.feature.editplayers.editPlayersModule
import be.niels.billen.whistscore.feature.game.gameDataModule
import be.niels.billen.whistscore.feature.overview.overviewModule
import be.niels.billen.whistscore.feature.players.playersDataModule
import be.niels.billen.whistscore.feature.players.playersModule
import be.niels.billen.whistscore.feature.rounds.roundsModule
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        KoinApplication(configuration = koinConfiguration(declaration = {
            modules(
                platformModule,
                gameDataModule,
                playersDataModule,
                appModule,
                overviewModule,
                addRoundModule,
                editPlayersModule,
                playersModule,
                roundsModule,
            )
        }), content = ::App)
    }
}
