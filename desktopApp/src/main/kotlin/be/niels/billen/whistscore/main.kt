package be.niels.billen.whistscore

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import be.niels.billen.whistscore.feature.addround.addRoundModule
import be.niels.billen.whistscore.feature.app.App
import be.niels.billen.whistscore.feature.app.appModule
import be.niels.billen.whistscore.feature.editplayers.editPlayersModule
import be.niels.billen.whistscore.feature.game.gameDataModule
import be.niels.billen.whistscore.feature.overview.overviewModule
import be.niels.billen.whistscore.feature.players.playersDataModule
import be.niels.billen.whistscore.feature.players.playersModule
import be.niels.billen.whistscore.feature.rounds.roundsModule
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    startKoin {
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
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Wiezen",
    ) {
        App()
    }
}
