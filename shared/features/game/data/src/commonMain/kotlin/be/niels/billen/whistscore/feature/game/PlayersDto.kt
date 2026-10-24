package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.Players
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.Serializable

// DTO field type stays stdlib Map: kotlinx.serialization cannot serialize Persistent* (issue #63).
@Serializable
data class PlayersDto(
    val players: Map<PlayerIdDto, PlayerDto>,
) : Map<PlayerIdDto, PlayerDto> by players {
    val value by lazy {
        entries.associate { (idDto, playerDto) -> idDto.value to playerDto.value }.toPersistentMap()
    }
}

fun Players.toDto() = PlayersDto(players = entries.associate { (id, player) -> id.toDto() to player.toDto() })