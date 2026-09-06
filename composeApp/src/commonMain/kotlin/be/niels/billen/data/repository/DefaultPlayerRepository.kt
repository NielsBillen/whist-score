package be.niels.billen.data.repository

import be.niels.billen.data.dto.PlayersDto
import be.niels.billen.data.dto.toDto
import be.niels.billen.domain.Game
import be.niels.billen.domain.Players
import be.niels.billen.domain.repository.PlayerRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi

class DefaultPlayerRepository(private val settings: Settings) : PlayerRepository {
    private val _players = MutableStateFlow(settings.players)
    override val players = _players.asStateFlow()

    override fun update(transform: (Players) -> Players) = _players.update {
        transform(it).also { players -> settings.players = players }
    }
}

private const val PLAYERS_KEY = "players"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private var Settings.players: Players
    get() = try {
        decodeValueOrNull<PlayersDto>(key = PLAYERS_KEY)?.value ?: Game.DEFAULT_PLAYERS
    } catch (e: Exception) {
        Game.DEFAULT_PLAYERS
    }
    set(value) = encodeValue(key = PLAYERS_KEY, value.toDto())