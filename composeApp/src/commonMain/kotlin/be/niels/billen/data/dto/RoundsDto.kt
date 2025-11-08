package be.niels.billen.data.dto

import be.niels.billen.domain.Round
import be.niels.billen.domain.Rounds
import kotlinx.serialization.Serializable

@Serializable
data class RoundsDto(val rounds: List<RoundDto>) {
    val value by lazy {
        Rounds.of(rounds.map(RoundDto::value))
    }
}

fun Rounds.toDto() = RoundsDto(rounds = rounds.map(Round::toDto))