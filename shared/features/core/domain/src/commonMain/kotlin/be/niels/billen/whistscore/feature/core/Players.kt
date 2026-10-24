package be.niels.billen.whistscore.feature.core

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentHashMapOf

typealias Players = PersistentMap<PlayerId, Player>

val DEFAULT_PLAYERS = persistentHashMapOf(
    PlayerId.Player1 to Player(name = "Player 1", color = Color(0xFF3b4863.toInt())),
    PlayerId.Player2 to Player(name = "Player 2", color = Color(0xFFaf945a.toInt())),
    PlayerId.Player3 to Player(name = "Player 3", color = Color(0xFF9a4a4b.toInt())),
    PlayerId.Player4 to Player(name = "Player 4", color = Color(0xFF405850.toInt()))
)

