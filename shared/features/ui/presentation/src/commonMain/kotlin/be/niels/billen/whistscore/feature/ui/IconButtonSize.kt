package be.niels.billen.whistscore.feature.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class IconButtonSize(
    val size: Dp,
    val iconSize: Dp,
) {
    Sm(32.dp, 16.dp),
    Md(40.dp, 16.dp),
    Lg(48.dp, 20.dp),
}
