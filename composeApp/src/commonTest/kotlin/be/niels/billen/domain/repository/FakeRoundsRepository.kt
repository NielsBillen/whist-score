package be.niels.billen.domain.repository

import be.niels.billen.domain.Rounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeRoundsRepository(initial: Rounds = Rounds.EMPTY) : RoundsRepository {
    private val _rounds = MutableStateFlow(initial)
    override val rounds = _rounds.asStateFlow()

    override fun update(transform: (Rounds) -> Rounds) = _rounds.update { transform(it) }
}
