package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.Players
import kotlinx.serialization.Serializable

@Serializable
data class PlayersDto(
    val players: Map<PlayerIdDto, PlayerDto>,
) : Map<PlayerIdDto, PlayerDto> by players {
    val value by lazy {
        entries.associate { (idDto, playerDto) -> idDto.value to playerDto.value }
    }
}

fun Players.toDto() = PlayersDto(players = entries.associate { (id, player) -> id.toDto() to player.toDto() })