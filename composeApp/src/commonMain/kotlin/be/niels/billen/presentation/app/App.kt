package be.niels.billen.presentation.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.addround.AddRound
import be.niels.billen.presentation.players.PlayersView
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject


@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
            val viewModel: AppViewModel = koinInject()
            val screen = viewModel.screen.collectAsState()

            when (screen.value) {
                AppScreen.OVERVIEW -> Overview { viewModel.onAction(it) }
                AppScreen.ADD_ROUND -> AddRound()
            }
        }
    }
}

@Composable
fun Overview(onAction: (AppAction) -> Unit) {
    Column {
        PlayersView(modifier = Modifier.padding(Style.Dimensions.paddingSmall))
        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier
                .padding(Style.Dimensions.paddingSmall)
                .align(Alignment.End),
            onClick = {
                onAction(AppAction.Navigate(AppScreen.ADD_ROUND))
            }) {
            Text("Add Round")
        }
    }
}
