package be.niels.billen.presentation.screens.overview.players

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import be.niels.billen.presentation.Style
import org.koin.compose.koinInject

@Composable
fun PlayersView(modifier: Modifier = Modifier) {
    val viewModel: PlayersViewModel = koinInject()
    val players = viewModel.players.collectAsState().value

    BoxWithConstraints(modifier) {
        val columns = if (maxWidth > maxHeight) 4 else 2

        LazyVerticalGrid(
            modifier = modifier,
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall),
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall),
            columns = GridCells.Fixed(columns)
        ) {
            items(players) { player ->
                PlayerCard(player, Modifier)
            }
        }
    }

}

@Composable
fun PlayerCard(player: PlayerView, modifier: Modifier = Modifier) {
    val textColor = if (player.color.luminance < 0.5f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
    Box(modifier = modifier.background(player.color, RoundedCornerShape(Style.Dimensions.radiusMedium))) {
        Column(Modifier.padding(Style.Dimensions.paddingMedium)) {
            Text(player.name, fontWeight = FontWeight.Bold, color = textColor)
            Text("${player.score}", color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
    }
}

private val Color.luminance: Float
    get() = (0.2126f * red + 0.7152f * green + 0.0722f * blue)