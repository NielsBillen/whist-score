package be.niels.billen.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.Font
import wiezen.composeapp.generated.resources.`NotoSans_VariableFont_wdth,wght`
import wiezen.composeapp.generated.resources.Res

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val typography = Typography().run {
        val fontFamily = FontFamily(
            Font(Res.font.`NotoSans_VariableFont_wdth,wght`)
        )

        copy(
            displayLarge = displayLarge.copy(fontFamily = fontFamily),
            displayMedium = displayMedium.copy(fontFamily = fontFamily),
            displaySmall = displaySmall.copy(fontFamily = fontFamily),
            headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
            headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
            headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
            titleLarge = titleLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Bold),
            titleMedium = titleMedium.copy(fontFamily = fontFamily),
            titleSmall = titleSmall.copy(fontFamily = fontFamily),
            bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
            bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
            bodySmall = bodySmall.copy(fontFamily = fontFamily),
            labelLarge = labelLarge.copy(fontFamily = fontFamily),
            labelMedium = labelMedium.copy(fontFamily = fontFamily),
            labelSmall = labelSmall.copy(fontFamily = fontFamily)
        )
    }

    val colorScheme = MaterialTheme.colorScheme.copy(
        surface = Color(0xFF14161a),
        onSurface = Color(0xFFa8a8a8)
    )

    MaterialTheme(typography = typography, colorScheme = colorScheme, content = content)
}


object Style {
    object Dimensions {
        val paddingSmall = 8.dp
        val paddingMedium = 12.dp
        val paddingLarge = 16.dp
        val radiusMedium = 16.dp
    }
}

