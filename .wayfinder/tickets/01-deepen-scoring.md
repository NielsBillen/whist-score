wayfinder:task

## Question

Deepen the scoring module: give `Round`/`Rounds` a small, well-defined scoring interface that hides the internal multiplier and penalty math, and delete the dead `Game` class (which duplicates `Rounds`'s scoring and its own player colours).

Details:
- `domain/Round.kt` — sealed interface. `points(player)` is computed via the Multi/Single-player branches, `passRoundMultiplier`, and per-type penalty points. `domain/Rounds.kt` duplicates this derivation in `of()` and the private `Map.plus/minus` extensions.
- `domain/Game.kt` — dead: `Game.score` and `defaultPlayers` are never referenced; `DefaultPlayerRepository.DEFAULT_PLAYERS` is the real default colour source.
- Consumers: `PlayersViewModel` combines `rounds.score(id)`; `SummaryScreen` iterates `round.points(playerId)`; `PlayersViewModel` still imports the unused `Game`.
- No automated tests exist. Establish them here — the scoring surface is the highest-risk area in the app.
