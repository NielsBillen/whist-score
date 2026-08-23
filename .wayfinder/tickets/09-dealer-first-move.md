wayfinder:grilling

## Question

Model dealer assignment and first-move ordering in the whist domain, and record which player won each round so later stats and history can consume it.

Details:
- Currently the domain (`Round`, `Rounds`, `RoundType`) records no `won`/`wonBy` beyond per-round point math, and `Round.won(playerId)` is only the internal win predicate for the current deal — nothing about *who* won is persisted.
- Requirements to resolve: how dealer is chosen and how it rotates between rounds; which player makes the first lead each round; and a `wonBy` record on each `Round` that survives serialization (DTOs) so stats/history can read it.
- The first move in whist is typically "second hand plays first" relative to the dealer — confirm the expected convention and encode it.
- This decision gates the stats (`12-stats-tracking`) and history (`11-game-history`) tickets, which both need per-round win records.
