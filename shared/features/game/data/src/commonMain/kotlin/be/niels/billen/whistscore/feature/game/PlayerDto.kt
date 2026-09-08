package be.niels.billen.whistscore.feature.game

import kotlinx.serialization.Serializable
import be.niels.billen.whistscore.feature.core.Color
import be.niels.billen.whistscore.feature.core.Player
import be.niels.billen.whistscore.feature.core.Players

@Serializable
data class PlayerDto(val name: String, val color: Int) {
    val value by lazy {
        Player(name = name, color = Color(color))
    }
}

fun Player.toDto() = PlayerDto(
    name = name,
    color = color.argb,
)

