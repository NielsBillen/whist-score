package be.niels.billen.whistscore.data.repository

import be.niels.billen.whistscore.data.dto.GameDto
import be.niels.billen.whistscore.data.dto.toDto
import be.niels.billen.whistscore.domain.Game
import be.niels.billen.whistscore.domain.repository.GameRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi

class DefaultGameRepository(private val settings: Settings) : GameRepository {
    private val _game = MutableStateFlow(settings.game)
    override val game = _game.asStateFlow()

    override fun update(transform: (Game) -> Game) =
        _game.update {
            transform(it).also { game -> settings.game = game }
        }
}

private const val GAME_KEY = "game"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private var Settings.game: Game
    get() = try {
        decodeValueOrNull<GameDto>(key = GAME_KEY)?.value ?: Game.DEFAULT
    } catch (_: Exception) {
        Game.DEFAULT
    }
    set(value) = encodeValue(key = GAME_KEY, value.toDto())

