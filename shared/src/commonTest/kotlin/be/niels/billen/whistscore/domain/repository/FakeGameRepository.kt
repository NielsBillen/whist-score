package be.niels.billen.whistscore.domain.repository

import androidx.compose.ui.graphics.Color
import be.niels.billen.whistscore.domain.Game
import be.niels.billen.whistscore.domain.Player
import be.niels.billen.whistscore.domain.PlayerId
import be.niels.billen.whistscore.domain.Players
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeGameRepository(
    initial: Game = Game.DEFAULT,
) : GameRepository {
    private val _game = MutableStateFlow(initial)
    override val game = _game.asStateFlow()

    override fun update(transform: (Game) -> Game) = _game.update(transform)
}
