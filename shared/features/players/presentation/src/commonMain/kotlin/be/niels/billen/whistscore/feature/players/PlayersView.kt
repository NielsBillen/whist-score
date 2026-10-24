package be.niels.billen.whistscore.feature.players

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.niels.billen.whistscore.feature.core.Icons
import be.niels.billen.whistscore.feature.core.Style
import be.niels.billen.whistscore.feature.core.icons.ArrowDown
import be.niels.billen.whistscore.feature.core.icons.PlayingCard
import kotlinx.collections.immutable.PersistentList
import org.koin.compose.koinInject

@Composable
fun PlayersView(
    modifier: Modifier = Modifier,
    viewModel: PlayersViewModel = koinInject()
) {
    val players by viewModel.players.collectAsState()

    PlayersView(players = players, modifier = modifier)
}

@Composable
fun PlayersView(players: PersistentList<PlayerView>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier) {
        val columns = if (maxWidth > maxHeight) 4 else 2

        LazyVerticalGrid(
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
    val textColor =
        if (player.color.luminance < 0.5f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
    Box(modifier = modifier.background(player.color, RoundedCornerShape(Style.Dimensions.radiusMedium))) {
        Column(Modifier.padding(Style.Dimensions.paddingMedium)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(player.name, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.weight(1f))

                DealerIcon(isDealer = player.dealer, color = textColor)
            }

            Text("${player.score}", color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }
    }
}

@Composable
private fun DealerIcon(isDealer: Boolean, color: Color) {
    Box(modifier = Modifier.requiredSize(24.dp)) {
        if (isDealer) {
            Icon(
                imageVector = PlayingCard,
                contentDescription = null,
                tint = color,
                modifier = Modifier.requiredSize(24.dp)
            )
        }
    }
}

private val Color.luminance: Float
    get() = (0.2126f * red + 0.7152f * green + 0.0722f * blue)