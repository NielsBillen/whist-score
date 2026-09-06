package be.niels.billen.data.dto

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RoundDto {
    val value: Round

    @Serializable
    @SerialName("pass")
    data object PassRound : RoundDto {
        override val value: Round get() = Round.PassRound
    }

    @Serializable
    @SerialName("regular")
    data class Regular(
        val players: Set<PlayerId>,
        val slams: Int,
    ) : RoundDto {
        override val value by lazy {
            Round.Regular(
                playerIds = players,
                slams = slams,
            )
        }
    }

    @Serializable
    @SerialName("abandonce")
    data class Abandonce(
        val playerId: PlayerId,
        val playerWon: Boolean,
    ) : RoundDto {
        override val value by lazy {
            Round.Abandonce(
                playerId = playerId,
                playerWon = playerWon,
            )
        }
    }

    @Serializable
    @SerialName("misere")
    data class Misere(
        val playerId: PlayerId,
        val playerWon: Boolean,
    ) : RoundDto {
        override val value by lazy {
            Round.Misere(
                playerId = playerId,
                playerWon = playerWon,
            )
        }
    }

    @Serializable
    @SerialName("openMisere")
    data class OpenMisere(
        val playerId: PlayerId,
        val playerWon: Boolean,
    ) : RoundDto {
        override val value by lazy {
            Round.OpenMisere(
                playerId = playerId,
                playerWon = playerWon,
            )
        }
    }

    @Serializable
    @SerialName("soloSlim")
    data class SoloSlim(
        val playerId: PlayerId,
        val playerWon: Boolean,
    ) : RoundDto {
        override val value by lazy {
            Round.SoloSlim(
                playerId = playerId,
                playerWon = playerWon,
            )
        }
    }

    @Serializable
    @SerialName("treble")
    data class Treble(
        val playerIds: Set<PlayerId>,
        val slams: Int,
    ) : RoundDto {
        override val value by lazy {
            Round.Treble(
                playerIds = playerIds,
                slams = slams,
            )
        }
    }
}

fun Round.toDto(): RoundDto = when (this) {
    is Round.PassRound -> RoundDto.PassRound
    is Round.Regular -> toDto()
    is Round.Treble -> toDto()
    is Round.Abandonce -> toDto()
    is Round.Misere -> toDto()
    is Round.OpenMisere -> toDto()
    is Round.SoloSlim -> toDto()
}

private fun Round.Regular.toDto() = RoundDto.Regular(
    players = playerIds,
    slams = slams,
)

private fun Round.Abandonce.toDto() = RoundDto.Abandonce(
    playerId = playerId,
    playerWon = playerWon,
)

private fun Round.Misere.toDto() = RoundDto.Misere(
    playerId = playerId,
    playerWon = playerWon,
)

private fun Round.OpenMisere.toDto() = RoundDto.OpenMisere(
    playerId = playerId,
    playerWon = playerWon,
)

private fun Round.SoloSlim.toDto() = RoundDto.SoloSlim(
    playerId = playerId,
    playerWon = playerWon,
)

private fun Round.Treble.toDto() = RoundDto.Treble(
    playerIds = playerIds,
    slams = slams,
)

