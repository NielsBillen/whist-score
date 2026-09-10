package be.niels.billen.whistscore.feature.core

enum class RoundType(
    val displayName: String,
    val description: String,
) {
    Pass("Pass", "None of the player placed a bid"),
    Regular("Regular", "Two players try to achieve 8 slams together or one player tries to achieve 5 slams if no partner is found"),
    Abandonce("Abandonce", "Players bids to achieve between 9 and 12 slams on their own and in the trump of their choice"),
    AbandonceInTrump("Abandonce in Trump", "Players bid to achieve between 9 and 12 slams on their own in the dealt trump suit"),
    Misere("Misere", "One player bids to achieve no slams"),
    Treble("Treble", "The player with three aces teams up with the player with the last ace to achieve 8 slams"),
    OpenMisere("Open Misere", "One player bids to achieve no slams with cards on the table"),
    SoloSlim("Solo Slim", "One player bids to achieve 13 slams");

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