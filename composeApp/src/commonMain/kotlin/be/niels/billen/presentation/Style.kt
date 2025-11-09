package be.niels.billen.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.Font
import whist_score.composeapp.generated.resources.NotoSans_Bold
import whist_score.composeapp.generated.resources.NotoSans_Regular
import whist_score.composeapp.generated.resources.Res

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val typography = Typography().run {
        val fontFamily = FontFamily(
            Font(Res.font.NotoSans_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
            Font(Res.font.NotoSans_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)
        )

        copy(
            displayLarge = displayLarge.copy(fontFamily = fontFamily),
            displayMedium = displayMedium.copy(fontFamily = fontFamily),
            displaySmall = displaySmall.copy(fontFamily = fontFamily),
            headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
            headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
            headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
            titleLarge = titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
            titleMedium = titleMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
            titleSmall = titleSmall.copy(fontFamily = fontFamily),
            bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
            bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
            bodySmall = bodySmall.copy(fontFamily = fontFamily),
            labelLarge = labelLarge.copy(fontFamily = fontFamily),
            labelMedium = labelMedium.copy(fontFamily = fontFamily),
            labelSmall = labelSmall.copy(fontFamily = fontFamily)
        )
    }

    MaterialTheme(typography = typography, colorScheme = darkColorScheme(), content = content)
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

