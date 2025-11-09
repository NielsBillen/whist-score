package be.niels.billen.data.dto

import be.niels.billen.domain.Players
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