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
}