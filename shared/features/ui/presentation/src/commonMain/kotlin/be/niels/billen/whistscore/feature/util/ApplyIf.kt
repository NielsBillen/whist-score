package be.niels.billen.whistscore.feature.util

import androidx.compose.ui.Modifier


inline fun Modifier.applyIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier {
    return if (condition) this.block() else this
}