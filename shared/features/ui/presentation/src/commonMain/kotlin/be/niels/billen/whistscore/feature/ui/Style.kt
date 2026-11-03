package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun UiTheme(content: @Composable () -> Unit) {
    content()
}

object Style {
    object Dimensions {
        val paddingTight = 4.dp
        val paddingSmall = 8.dp
        val paddingMedium = 12.dp
        val paddingLarge = 16.dp
        val paddingExtraExtraLarge = 48.dp
        val radiusMedium = 16.dp
    }
    

    object Shapes {
        val mediumRoundedCornerShape = RoundedCornerShape(Dimensions.radiusMedium)
    }
}
