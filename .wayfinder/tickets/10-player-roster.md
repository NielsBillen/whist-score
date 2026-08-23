wayfinder:grilling

## Question

Let the app manage an extensible roster of players (not just four fixed `PlayerId`s) and start a game with any chosen set of four, so a new game can be built quickly from the roster.

Details:
- `domain/PlayerId.kt` is a 4-way enum (`Player1`–`Player4`); `defaultPlayers` and `DEFAULT_PLAYERS` hardcode four players; `PlayerRepository.players` is a fixed four-entry map; `Rounds`/`Game` scoring iterate `PlayerId.entries`.
- Requirement: a persisted roster of named players; a way to select four of them (or fewer/more supported by a given round) to form a game; and scoring to key off roster IDs, not the enum.
- `Round.multiPlayer` currently enforces `players.size in 1..2` (and Treble `== 2`); the roster expansion must not break the per-round player-count rules that already exist.
- Decide the player-key representation (keep `PlayerId` as a type alias backed by roster entries, or refactor to a real `PlayerId` value type). This decision affects DTOs and the DI bindings.
