package be.niels.billen.data.dto

import be.niels.billen.domain.Game
import be.niels.billen.domain.Round
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(val players: Map<PlayerIdDto, PlayerDto>, val rounds: List<RoundDto>) {
    val value by lazy {
        Game(
            players = players.entries.associate { (id, player) -> id.value to player.value },
            rounds = rounds.map(RoundDto::value)
        )
    }
}

fun Game.toDto() =
    GameDto(
        players = players.entries.associate { (id, player) -> id.toDto() to player.toDto() },
        rounds = rounds.map(Round::toDto)
    )