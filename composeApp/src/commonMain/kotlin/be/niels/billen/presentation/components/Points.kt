package be.niels.billen.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.abs

@Composable
fun Points(points: Int, modifier: Modifier = Modifier){
    val pointsSign = when {
        points > 0 -> "+"
        points < 0 -> "-"
        else -> ""
    }
    val pointsColor = when {
        points > 0 -> Color.Green
        points < 0 -> Color.Red
        else -> MaterialTheme.colorScheme.primary
    }

    Text("${pointsSign}${abs(points)}", modifier, color = pointsColor, textAlign = TextAlign.Center)
}