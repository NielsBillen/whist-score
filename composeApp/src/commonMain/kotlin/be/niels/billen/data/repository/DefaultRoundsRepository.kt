package be.niels.billen.data.repository

import be.niels.billen.domain.Round
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultRoundsRepository : RoundsRepository {
    private val _rounds = MutableStateFlow<List<Round>>(emptyList())
    override val rounds = _rounds.asStateFlow()

    override fun update(transform: (List<Round>) -> List<Round>) {
        _rounds.update(transform)
    }
}