package be.niels.billen.whistscore.feature.addround

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.RoundType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentHashSetOf

class NextButtonForTest : FreeSpec() {

    private val selectRoundType = AddRoundNavigation.SelectRoundTypeRoute
    private val selectPlayers = AddRoundNavigation.SelectPlayersRoute
    private val selectSlams = AddRoundNavigation.SelectSlamsRoute
    private val selectBidAchieved = AddRoundNavigation.SelectBidAchievedRoute
    private val summary = AddRoundNavigation.SummaryRoute

    private val regularState = AddRoundState(roundType = RoundType.Regular)
    private val trebleState = AddRoundState(roundType = RoundType.Treble)
    private val summaryState = AddRoundState(
        roundType = RoundType.Regular,
        players = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2),
        slams = 7,
    )

    private val onePlayer = persistentHashSetOf(PlayerId.Player1)
    private val twoPlayers = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2)
    private val threePlayers = persistentHashSetOf(PlayerId.Player1, PlayerId.Player2, PlayerId.Player3)

    init {
        "select round type" - {
            "is disabled while no round type is selected" {
                nextButtonFor(
                    current = selectRoundType,
                    state = AddRoundState(),
                    selectedRoundType = null,
                    selectedPlayers = persistentHashSetOf(),
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe false
            }

            "dispatches the selected round type" {
                nextButtonFor(
                    current = selectRoundType,
                    state = AddRoundState(),
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = persistentHashSetOf(),
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ) shouldBe NextButton(
                    enabled = true,
                    step = AddRoundNextStep.Dispatch(AddRoundAction.SetRoundType(RoundType.Regular)),
                )
            }
        }

        "select players" - {
            "is disabled for a regular round with no players" {
                nextButtonFor(
                    current = selectPlayers,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = persistentHashSetOf(),
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe false
            }

            "is disabled for a regular round with three players" {
                nextButtonFor(
                    current = selectPlayers,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = threePlayers,
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe false
            }

            "dispatches the selection for a regular round with one player" {
                nextButtonFor(
                    current = selectPlayers,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = onePlayer,
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ) shouldBe NextButton(
                    enabled = true,
                    step = AddRoundNextStep.Dispatch(AddRoundAction.SetPlayers(onePlayer)),
                )
            }

            "dispatches the selection for a regular round with two players" {
                nextButtonFor(
                    current = selectPlayers,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = twoPlayers,
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe true
            }

            "is disabled for a treble round with one player" {
                nextButtonFor(
                    current = selectPlayers,
                    state = trebleState,
                    selectedRoundType = RoundType.Treble,
                    selectedPlayers = onePlayer,
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe false
            }

            "dispatches the selection for a treble round with two players" {
                nextButtonFor(
                    current = selectPlayers,
                    state = trebleState,
                    selectedRoundType = RoundType.Treble,
                    selectedPlayers = twoPlayers,
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe true
            }
        }

        "select slams" - {
            "is disabled until a slam count is selected" {
                nextButtonFor(
                    current = selectSlams,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = twoPlayers,
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe false
            }

            "dispatches the selected slams" {
                nextButtonFor(
                    current = selectSlams,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = twoPlayers,
                    selectedSlams = 7,
                    selectedBidAchieved = null,
                ) shouldBe NextButton(
                    enabled = true,
                    step = AddRoundNextStep.Dispatch(AddRoundAction.SetSlams(7)),
                )
            }
        }

        "select bid achieved" - {
            "is disabled until a bid outcome is selected" {
                nextButtonFor(
                    current = selectBidAchieved,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = onePlayer,
                    selectedSlams = null,
                    selectedBidAchieved = null,
                ).enabled shouldBe false
            }

            "dispatches the selected bid outcome" {
                nextButtonFor(
                    current = selectBidAchieved,
                    state = regularState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = onePlayer,
                    selectedSlams = null,
                    selectedBidAchieved = true,
                ) shouldBe NextButton(
                    enabled = true,
                    step = AddRoundNextStep.Dispatch(AddRoundAction.SetBidAchieved(true)),
                )
            }
        }

        "summary" - {
            "saves the committed round" {
                nextButtonFor(
                    current = summary,
                    state = summaryState,
                    selectedRoundType = RoundType.Regular,
                    selectedPlayers = twoPlayers,
                    selectedSlams = 7,
                    selectedBidAchieved = null,
                ) shouldBe NextButton(
                    enabled = true,
                    step = AddRoundNextStep.Save(Round.Regular(twoPlayers, slams = 7)),
                )
            }
        }
    }
}
