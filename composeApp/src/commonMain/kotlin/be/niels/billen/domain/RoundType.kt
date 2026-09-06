package be.niels.billen.domain

enum class RoundType {
    Pass,
    Regular,
    Abandonce,
    AbandonceInTrump,
    Misere,
    Treble,
    OpenMisere,
    SoloSlim;

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