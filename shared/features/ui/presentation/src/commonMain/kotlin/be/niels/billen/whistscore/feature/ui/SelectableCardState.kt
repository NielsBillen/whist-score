package be.niels.billen.whistscore.feature.ui

import androidx.compose.ui.semantics.Role

data class SelectableCardState(
    val selected: Boolean,
    val enabled: Boolean = true,
    val role: Role = Role.RadioButton,
)
