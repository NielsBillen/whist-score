package be.niels.billen.whistscore.feature.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import be.niels.billen.whistscore.feature.ui.Style
import kotlinx.collections.immutable.PersistentList
import org.koin.compose.koinInject

@Composable
fun PlayersView(
    modifier: Modifier = Modifier,
    viewModel: PlayersViewModel = koinInject(),
) {
    val players by viewModel.players.collectAsState()

    PlayersView(players = players, modifier = modifier)
}

@Composable
fun PlayersView(
    players: PersistentList<PlayerView>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val columns = if (maxWidth > maxHeight) 4 else 2

        LazyVerticalGrid(
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall),
            columns = GridCells.Fixed(columns),
        ) {
            items(players) { player ->
                PlayerCard(player = player, modifier = modifier)
            }
        }
    }
}
