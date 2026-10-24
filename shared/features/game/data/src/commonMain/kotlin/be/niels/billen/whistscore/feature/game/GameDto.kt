package be.niels.billen.whistscore.feature.game

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.rounds.Rounds
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

// DTO field types stay stdlib List/Map: kotlinx.serialization cannot serialize Persistent* (issue #63).
@Serializable
data class GameDto(val rounds: List<RoundDto>, val order: List<PlayerIdDto>) {
    val value by lazy {
        Game(
            rounds = Rounds(rounds.map(RoundDto::value).toPersistentList()),
            order = order.map(PlayerIdDto::value).toPersistentList()
        )
    }
}

fun Game.toDto() =
    GameDto(
        rounds = rounds.map(Round::toDto),
        order = order.map(PlayerId::toDto)
    )