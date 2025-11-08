package be.niels.billen.data.dto

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface RoundDto {
    val value: Round

    @Serializable
    @SerialName("regular")
    data class Regular(
        val players: Set<PlayerId>,
        val slams: Int,
        val passRound: Boolean
    ) : RoundDto {
        override val value by lazy {
            Round.Regular(
                players = players,
                slams = slams,
                passRound = passRound
            )
        }
    }

    @Serializable
    @SerialName("abandonce")
    data class Abandonce(
        val player: PlayerId,
        val playerWon: Boolean,
        val passRound: Boolean
    ) : RoundDto {
        override val value by lazy {
            Round.Abandonce(
                player = player,
                playerWon = playerWon,
                passRound = passRound
            )
        }
    }

    @Serializable
    @SerialName("misere")
    data class Misere(
        val player: PlayerId,
        val playerWon: Boolean,
        val passRound: Boolean
    ) : RoundDto {
        override val value by lazy {
            Round.Misere(
                player = player,
                playerWon = playerWon,
                passRound = passRound
            )
        }
    }

    @Serializable
    @SerialName("openMisere")
    data class OpenMisere(
        val player: PlayerId,
        val playerWon: Boolean,
        val passRound: Boolean
    ) : RoundDto {
        override val value by lazy {
            Round.OpenMisere(
                player = player,
                playerWon = playerWon,
                passRound = passRound
            )
        }
    }

    @Serializable
    @SerialName("soloSlim")
    data class SoloSlim(
        val player: PlayerId,
        val playerWon: Boolean,
        val passRound: Boolean
    ) : RoundDto {
        override val value by lazy {
            Round.SoloSlim(
                player = player,
                playerWon = playerWon,
                passRound = passRound
            )
        }
    }

    @Serializable
    @SerialName("treble")
    data class Treble(
        val players: Set<PlayerId>,
        val slams: Int,
        val passRound: Boolean
    ) : RoundDto {
        override val value by lazy {
            Round.Treble(
                players = players,
                slams = slams,
                passRound = passRound
            )
        }
    }
}

fun Round.toDto(): RoundDto = when (this) {
    is Round.Regular -> toDto()
    is Round.Treble -> toDto()
    is Round.Abandonce -> toDto()
    is Round.Misere -> toDto()
    is Round.OpenMisere -> toDto()
    is Round.SoloSlim -> toDto()
}

private fun Round.Regular.toDto() = RoundDto.Regular(
    players = players,
    slams = slams,
    passRound = passRound
)

private fun Round.Abandonce.toDto() = RoundDto.Abandonce(
    player = player,
    playerWon = playerWon,
    passRound = passRound
)

private fun Round.Misere.toDto() = RoundDto.Misere(
    player = player,
    playerWon = playerWon,
    passRound = passRound
)

private fun Round.OpenMisere.toDto() = RoundDto.OpenMisere(
    player = player,
    playerWon = playerWon,
    passRound = passRound
)

private fun Round.SoloSlim.toDto() = RoundDto.SoloSlim(
    player = player,
    playerWon = playerWon,
    passRound = passRound
)

private fun Round.Treble.toDto() = RoundDto.Treble(
    players = players,
    slams = slams,
    passRound = passRound
)

