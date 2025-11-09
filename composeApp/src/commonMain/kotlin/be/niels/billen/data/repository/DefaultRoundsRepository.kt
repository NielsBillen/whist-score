package be.niels.billen.data.repository

import be.niels.billen.data.dto.RoundsDto
import be.niels.billen.data.dto.toDto
import be.niels.billen.domain.Rounds
import be.niels.billen.domain.repository.RoundsRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.ExperimentalSerializationApi

class DefaultRoundsRepository(private val settings: Settings) : RoundsRepository {
    private val _rounds = MutableStateFlow(settings.rounds)
    override val rounds = _rounds.asStateFlow()

    override fun update(transform: (Rounds) -> Rounds) =
        _rounds.update {
            transform(it).also { rounds -> settings.rounds = rounds }
        }
}

private const val ROUNDS_KEY = "rounds"

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
private var Settings.rounds: Rounds
    get() = decodeValueOrNull<RoundsDto>(key = ROUNDS_KEY)?.value ?: Rounds.EMPTY
    set(value) = encodeValue(key = ROUNDS_KEY, value.toDto())