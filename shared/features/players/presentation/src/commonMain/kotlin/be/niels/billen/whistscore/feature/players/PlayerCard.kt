package be.niels.billen.whistscore.feature.players

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.niels.billen.whistscore.feature.core.icons.PlayingCard
import be.niels.billen.whistscore.feature.ui.Style

@Composable
internal fun PlayerCard(player: PlayerView, modifier: Modifier = Modifier) {
    val textColor =
        if (player.color.luminance < 0.5f) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
    Column(
        modifier = modifier

            .background(player.color, Style.Shapes.mediumRoundedCornerShape)
            .padding(Style.Dimensions.paddingMedium)
    ) {
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