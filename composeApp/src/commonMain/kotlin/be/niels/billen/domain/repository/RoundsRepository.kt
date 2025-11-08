package be.niels.billen.domain.repository

import be.niels.billen.domain.Rounds
import kotlinx.coroutines.flow.Flow

interface RoundsRepository {
    val rounds: Flow<Rounds>

    fun update(transform: (Rounds) -> Rounds)
}