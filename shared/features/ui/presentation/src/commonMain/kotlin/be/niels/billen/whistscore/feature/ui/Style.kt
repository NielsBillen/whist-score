package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.Font
import whist_score.shared.features.ui.presentation.generated.resources.Poppins_Bold
import whist_score.shared.features.ui.presentation.generated.resources.Poppins_Medium
import whist_score.shared.features.ui.presentation.generated.resources.Poppins_Regular
import whist_score.shared.features.ui.presentation.generated.resources.Res

@Composable
fun UiTheme(content: @Composable () -> Unit) {
    val typography = Typography().run {
        val fontFamily = FontFamily(
            Font(Res.font.Poppins_Regular, weight = FontWeight.Normal, style = FontStyle.Normal),
            Font(Res.font.Poppins_Medium, weight = FontWeight.Medium, style = FontStyle.Normal),
            Font(Res.font.Poppins_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)
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
            labelLarge = labelLarge.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
            labelMedium = labelMedium.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium),
            labelSmall = labelSmall.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium)
        )
    }

    // astryx y2k theme, dark: semantic tokens mapped onto M3 roles.
    val colorScheme = darkColorScheme(
        primary = Style.Colors.textPrimary,
        onPrimary = Style.Colors.onAccent,
        secondary = Style.Colors.neonGreen,
        onSecondary = Style.Colors.onNeonGreen,
        surface = Style.Colors.surface,
        onSurface = Style.Colors.textPrimary,
        onSurfaceVariant = Style.Colors.textSecondary,
        surfaceVariant = Style.Colors.elevated,
        surfaceBright = Style.Colors.elevated,
        surfaceContainerHigh = Style.Colors.elevated,
        outline = Style.Colors.border,
        outlineVariant = Style.Colors.border,
    )

    MaterialTheme(typography = typography, colorScheme = colorScheme) {
        // M3 1.12+ no longer provides a default content color (it falls back to black);
        // restore it so uncoloured Text stays readable on the dark surfaces.
        CompositionLocalProvider(LocalContentColor provides colorScheme.onSurface) {
            content()
        }
    }
}

object Style {
    object Dimensions {
        val paddingTight = 4.dp
        val paddingSmall = 8.dp
        val paddingMedium = 12.dp
        val paddingLarge = 16.dp
        val paddingExtraExtraLarge = 48.dp
        // astryx neutral --radius-element (0.625rem), the theme used for the
        // official icon-button visual baselines.
        val radiusSmall = 10.dp
        val radiusMedium = 16.dp
    }

    // astryx y2k theme, dark mode
    object Colors {
        val body = Color(0xFF0E0F1A) // --color-background-body
        val surface = Color(0xFF16182B) // --color-background-surface / -card
        val elevated = Color(0xFF1F2238) // --color-background-muted / -popover

        val textPrimary = Color(0xFFEDEFFC) // --color-text-primary
        val textSecondary = Color(0xFFA6ACD6) // --color-text-secondary

        val onAccent = Color(0xFF16182B) // --color-on-accent
        val neonGreen = Color(0xFFC5E17A) // --color-background-green
        val onNeonGreen = Color(0xFF2A3600) // --color-text-green

        val border = Color(0xFF636889) // --color-border-emphasized
    }


    object Shapes {
        val smallRoundedCornerShape = RoundedCornerShape(Dimensions.radiusSmall)
        val mediumRoundedCornerShape = RoundedCornerShape(Dimensions.radiusMedium)
    }
}
