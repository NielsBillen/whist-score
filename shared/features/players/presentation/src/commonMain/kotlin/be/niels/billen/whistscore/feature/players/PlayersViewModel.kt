package be.niels.billen.whistscore.feature.players

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.core.Players
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.game.GameRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class PlayersViewModel(
    gameRepository: GameRepository,
    playerRepository: PlayerRepository,
) : ViewModel() {
    val players = combine(gameRepository.game, playerRepository.players, ::makeState)
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = persistentListOf())
}

private fun makeState(game: Game, players: Players): PersistentList<PlayerView> {
    val scores = game.rounds.scores

    return game.order.mapIndexed { index, playerId ->
        val dealer = game.rounds.size % players.size == index
        val player = players.getValue(playerId)
        val score = scores.getValue(playerId)
        PlayerView(name = player.name, color = Color(player.color.argb), score = score, dealer = dealer)
    }.toPersistentList()
}