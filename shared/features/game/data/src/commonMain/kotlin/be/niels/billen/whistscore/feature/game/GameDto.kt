package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.Players
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.rounds.Rounds
import kotlinx.serialization.Serializable

@Serializable
data class GameDto(val players: Map<PlayerIdDto, PlayerDto>, val rounds: List<RoundDto>) {
    val value by lazy {
        Game(
            players = Players(players.entries.associate { (id, player) -> id.value to player.value }),
            rounds = Rounds(rounds.map(RoundDto::value))
        )
    }
}

fun Game.toDto() =
    GameDto(
        players = players.entries.associate { (id, player) -> id.toDto() to player.toDto() },
        rounds = rounds.map(Round::toDto)
    )