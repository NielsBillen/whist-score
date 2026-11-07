package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.Color
import be.niels.billen.whistscore.feature.core.Player
import be.niels.billen.whistscore.feature.core.Players
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val name: String,
    val color: Int,
) {
    val value by lazy {
        Player(name = name, color = Color(color))
    }
}

fun Player.toDto() =
    PlayerDto(
        name = name,
        color = color.argb,
    )
