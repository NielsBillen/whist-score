package be.niels.billen.whistscore.feature.core

enum class RoundType(
    val displayName: String,
    val description: String,
) {
    Pass(
        displayName = "Pass",
        description = "None of the player placed a bid"
    ),
    Regular(
        displayName = "Regular",
        description = "Two players try to achieve 8 slams together or one player tries to achieve 5 slams if no partner is found"
    ),
    Abandonce(
        displayName = "Abandonce",
        description = "Players bids to achieve between 9 and 12 slams on their own and in the trump of their choice"
    ),
    AbandonceInTrump(
        displayName = "Abandonce in Trump",
        description = "Players bid to achieve between 9 and 12 slams on their own in the dealt trump suit"
    ),
    Misere(
        displayName = "Misere",
        description = "One player bids to achieve no slams"
    ),
    Treble(
        displayName = "Treble",
        description = "The player with three aces teams up with the player with the last ace to achieve 8 slams"
    ),
    OpenMisere(
        displayName = "Open Misere",
        description = "One player bids to achieve no slams with cards on the table"
    ),
    SoloSlim(
        displayName = "Solo Slim",
        description = "One player bids to achieve 13 slams"
    );

    val singlePlayer: Boolean
        get() = when (this) {
            Pass, Regular, Treble -> false
            Abandonce, AbandonceInTrump, Misere, OpenMisere, SoloSlim -> true
        }

    val playerCountRange: IntRange
        get() = when (this) {
            Pass -> 4..4
            Regular -> 1..2
            Abandonce, AbandonceInTrump, Misere, OpenMisere, SoloSlim -> 1..1
            Treble -> 2..2
        }

}