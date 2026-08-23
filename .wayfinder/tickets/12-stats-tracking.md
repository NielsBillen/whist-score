blocked-by: 11-game-history.md, 01-deepen-scoring.md
wayfinder:task

## Question

Add statistics: global win rate, per-trick win rate, total points, points per game, and win rate over time.

Details:
- Stats read from two sources that must exist first: the scoring module (`01-deepen-scoring.md`) for reliable, tested score derivation, and game history (`11-game-history.md`) for anything beyond the current game.
- Stats to implement (user's list): global win rate, per-trick (per-deal) win rate, total points across games, points per game, and win rate over time (a time-series of win rate).
- The per-trick win rate requires a per-round win record — resolved by the dealer/first-move ticket (`09-dealer-first-move.md`), which adds `wonBy` to `Round`.
- Decisions to make: where stats are computed (a stats aggregator over `GameHistory`), how win-rate-over-time is stored/computed, and how to present them (screen, or a stats view on the overview).
