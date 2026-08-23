blocked-by: 01-deepen-scoring.md
wayfinder:task

## Question

Add pass rounds as a first-class round type and track them across history so the 2× pass-round multiplier is applied automatically based on the previous round — instead of forcing users to remember which round was a pass round.

Details:
- `domain/Round.kt` already has a `passRound` field and `passRoundMultiplier` (2 when pass round, 1 otherwise), but it is per-round only. The multiplier is currently round-local, not history-aware.
- Requirement: a "Pass Round" round type. When added, the *previous* round in the game's history is flagged as a pass round, and subsequent rounds' scoring applies the 2× multiplier accordingly.
- This means `Rounds` must expose whether the prior round was a pass round, and the scoring must consume that history, not just the round's own flag.
- Coordinate with the scoring deepening ticket (`01-deepen-scoring.md`) — the multiplier semantics change here.
