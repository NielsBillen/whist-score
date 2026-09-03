package be.niels.billen.presentation.app

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import be.niels.billen.domain.Rounds
import be.niels.billen.domain.repository.FakeRoundsRepository
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class AppViewModelTest : FreeSpec() {

    private val round = Round.Regular(
        players = setOf(PlayerId.Player1, PlayerId.Player2),
        slams = 8,
    )

    init {
        "a saved round is added to the rounds repository" {
            val roundsRepository = FakeRoundsRepository()
            val viewModel = AppViewModel(roundsRepository)

            viewModel.onAction(AppAction.AddRound(round))

            roundsRepository.rounds.value.rounds shouldBe listOf(round)
        }

        "a reset clears all the rounds" {
            val roundsRepository = FakeRoundsRepository(Rounds.of(listOf(round)))
            val viewModel = AppViewModel(roundsRepository)

            viewModel.onAction(AppAction.ResetGame)

            roundsRepository.rounds.value shouldBe Rounds.EMPTY
        }
    }
}
