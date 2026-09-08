package be.niels.billen.whistscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
