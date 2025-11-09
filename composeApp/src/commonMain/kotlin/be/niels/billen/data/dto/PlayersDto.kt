package be.niels.billen.data.dto

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Players

typealias PlayersDto = Map<PlayerId, PlayerDto>

fun Players.toDto() = mapValues { (_, player) -> player.toDto() }

val PlayersDto.value: Players get() = mapValues { (_, dto) -> dto.value }
