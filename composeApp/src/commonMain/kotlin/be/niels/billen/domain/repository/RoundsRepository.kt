package be.niels.billen.domain.repository

import be.niels.billen.domain.Round
import kotlinx.coroutines.flow.Flow

interface RoundsRepository {
    val rounds : Flow<List<Round>>

    fun update(transform: (List<Round>)->List<Round>)
}