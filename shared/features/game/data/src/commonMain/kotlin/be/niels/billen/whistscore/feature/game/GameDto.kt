package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.rounds.Rounds
import kotlinx.collections.immutable.toPersistentHashMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

// DTO field types stay stdlib List/Map: kotlinx.serialization cannot serialize Persistent* (issue #63).
@Serializable
data class GameDto(val players: Map<PlayerIdDto, PlayerDto>, val rounds: List<RoundDto>) {
    val value by lazy {
        Game(
            players = players.entries.associate { (id, player) -> id.value to player.value }.toPersistentHashMap(),
            rounds = Rounds(rounds.map(RoundDto::value).toPersistentList())
        )
    }
}

fun Game.toDto() =
    GameDto(
        players = players.entries.associate { (id, player) -> id.toDto() to player.toDto() },
        rounds = rounds.map(Round::toDto)
    )