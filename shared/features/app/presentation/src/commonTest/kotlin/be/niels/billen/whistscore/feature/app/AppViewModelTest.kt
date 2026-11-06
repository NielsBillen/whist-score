package be.niels.billen.whistscore.feature.app

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.game.FakeGameRepository
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class AppViewModelTest : FreeSpec() {

    init {
        "adding a round adds its points to the game score" {
            val gameRepository = FakeGameRepository()
            val viewModel = AppViewModel(gameRepository)

            viewModel.onAction(AppAction.AddRound(Round.Abandonce(PlayerId.Player1)))

            PlayerId.entries.associateWith { gameRepository.game.value.score(it) } shouldBe mapOf(
                PlayerId.Player1 to 9,
                PlayerId.Player2 to -3,
                PlayerId.Player3 to -3,
                PlayerId.Player4 to -3,
            )
        }

        "resetting the game clears every round and score" {
            val gameRepository = FakeGameRepository()
            val viewModel = AppViewModel(gameRepository)

            viewModel.onAction(AppAction.AddRound(Round.Abandonce(PlayerId.Player1)))
            viewModel.onAction(AppAction.ResetGame)

            PlayerId.entries.associateWith { gameRepository.game.value.score(it) } shouldBe mapOf(
                PlayerId.Player1 to 0,
                PlayerId.Player2 to 0,
                PlayerId.Player3 to 0,
                PlayerId.Player4 to 0,
            )
        }
    }
}
