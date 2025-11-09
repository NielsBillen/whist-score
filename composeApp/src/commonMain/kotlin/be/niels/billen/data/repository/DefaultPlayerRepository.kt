package be.niels.billen.data.repository

import androidx.compose.ui.graphics.Color
import be.niels.billen.data.dto.PlayersDto
import be.niels.billen.data.dto.toDto
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
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

    companion object {
        val DEFAULT_PLAYERS = mapOf(
            PlayerId.Player1 to Player("Player 1", Color(0xFF3b4863)),
            PlayerId.Player2 to Player("Player 2", Color(0xFFaf945a)),
            PlayerId.Player3 to Player("Player 3", Color(0xFF9a4a4b)),
            PlayerId.Player4 to Player("Player 4", Color(0xFF405850))
        )
    }
}

private const val PLAYERS_KEY = "players"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private var Settings.players: Players
    get() = decodeValueOrNull<PlayersDto>(key = PLAYERS_KEY)?.value ?: DefaultPlayerRepository.DEFAULT_PLAYERS
    set(value) = encodeValue(key = PLAYERS_KEY, value.toDto())