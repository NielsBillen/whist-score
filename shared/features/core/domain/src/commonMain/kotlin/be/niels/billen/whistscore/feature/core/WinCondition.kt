package be.niels.billen.whistscore.feature.core

// What a round needs from the players to be finished: the slam count, the bid outcome, or nothing.
enum class WinCondition {
    NONE,
    SLAMS,
    BID_ACHIEVED,
}
