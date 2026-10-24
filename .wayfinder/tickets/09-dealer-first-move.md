wayfinder:grilling
claimed-by: agent (this session)
status: closed

## Question

Model dealer assignment and first-move ordering in the whist domain, and record which player won each round so later stats and history can consume it.

Details:
- Currently the domain (`Round`, `Rounds`, `RoundType`) records no `won`/`wonBy` beyond per-round point math, and `Round.won(playerId)` is only the internal win predicate for the current deal — nothing about *who* won is persisted.
- Requirements to resolve: how dealer is chosen and how it rotates between rounds; which player makes the first lead each round; and a `wonBy` record on each `Round` that survives serialization (DTOs) so stats/history can read it.
- The first move in whist is typically "second hand plays first" relative to the dealer — confirm the expected convention and encode it.
- This decision gates the stats (`12-stats-tracking`) and history (`11-game-history`) tickets, which both need per-round win records.

## Resolution

Resolved by derivation — nothing is stored on `Round`/`Game`, and no DTO field is added.

- **Winners are derived, not stored.** No `wonBy` field on `Round`. Every consumer computes "who won deal N" from `Round.won(playerId)` (multi-player: `playerIds` if `playersWon` else the other two; single-player: `playerId` if `bidAchieved` else the other three; `PassRound`: no winner). So `12-stats-tracking` and `11-game-history` no longer depend on 09 for a `wonBy` field — the per-trick win rate reads winners straight from the round.
- **Dealer & first-lead are derived from the seat order** (the Edit-Players player list, seat 0→3); they are never persisted:
  - Deal *n* (1-indexed) is dealt by seat `((n−1) mod 4)`.
  - The first lead is the next seat, `n mod 4` — "second hand plays first" (deal 1: seat 0 deals, seat 1 leads).
  - The deal advances one seat every deal, passes included.
- **Reordering is seat identity.** The seat order is persisted as the player list. Reordering moves players to different seats; dealer/lead for every round re-derives from the current seating at read time, while already-recorded points are untouched (scoring stays keyed to player identity).

**Decision (Full):** ship a reorderable Edit Players screen (the seat order, persisted) + dealer & first-lead shown per round on the Rounds/Overview UI.

**Map impact:** resolves the fog patch "How dealer and first-move state model in the domain and survive across round types"; drops 09's gate on `11`/`12` (winners are derived, not a `wonBy` field).
