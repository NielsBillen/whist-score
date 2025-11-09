package be.niels.billen.data.dto

import be.niels.billen.domain.PlayerId
import kotlinx.serialization.Serializable

@Serializable
enum class PlayerIdDto {
    Player1,
    Player2,
    Player3,
    Player4;

    val value: PlayerId
        get() = when (this) {
            Player1 -> PlayerId.Player1
            Player2 -> PlayerId.Player2
            Player3 -> PlayerId.Player3
            Player4 -> PlayerId.Player4
        }
}

fun PlayerId.toDto(): PlayerIdDto = when (this) {
    PlayerId.Player1 -> PlayerIdDto.Player1
    PlayerId.Player2 -> PlayerIdDto.Player2
    PlayerId.Player3 -> PlayerIdDto.Player3
    PlayerId.Player4 -> PlayerIdDto.Player4
}