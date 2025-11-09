package be.niels.billen.data.dto

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.Serializable
import be.niels.billen.domain.Player
import be.niels.billen.domain.Players

@Serializable
data class PlayerDto(val name: String, val color: Int) {
    val value by lazy {
        Player(name = name, color = Color(color = color))
    }
}

fun Player.toDto() = PlayerDto(
    name = name,
    color = color.toArgb(),
)

