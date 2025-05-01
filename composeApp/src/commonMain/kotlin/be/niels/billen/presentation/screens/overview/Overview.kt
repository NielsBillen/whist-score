package be.niels.billen.presentation.screens.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.app.AppAction
import be.niels.billen.presentation.app.AppScreen
import be.niels.billen.presentation.screens.overview.players.PlayersView

@Composable
fun Overview(onAction: (AppAction) -> Unit) {
    Column(modifier = Modifier.padding(Style.Dimensions.paddingLarge)) {
        Text("Whist score", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        PlayersView()
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
