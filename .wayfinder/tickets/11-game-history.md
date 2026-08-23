wayfinder:task

## Question

Persist and browse a history of games, so the app retains past games in addition to the current game.

Details:
- Currently only the current game is tracked (`DefaultRoundsRepository` keeps a single `Rounds` set in `Settings`).
- Requirement: store a series of games. Decide the aggregate that captures one game (players, rounds, scores, winner, date) and how it serializes via the existing `data/dto` layer.
- Decide how the current game and past games are represented together — e.g. a `GameHistory` holding `currentGame` + `pastGames`, or a list with an active pointer.
- This is what makes the current app "basic" — a single-game window with no record of prior play.

Blocks `12-stats-tracking`: win-rate-over-time and points-per-game statistics require persisted games to read from.
