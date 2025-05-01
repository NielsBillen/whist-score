package be.niels.billen.domain

enum class RoundType {
    Regular,
    Abandonce,
    Misere,
    Treble;

    val singlePlayer: Boolean
        get() = when (this) {
            Regular, Treble -> false
            Abandonce, Misere -> true
        }

    val playerCountRange: IntRange
        get()= when(this) {
            Regular->1..2
            Abandonce,Misere->1..1
            Treble->2..2
        }
}