blocked-by: 01-deepen-scoring.md
wayfinder:task
status: closed

## Question

Add pass rounds as a first-class round type and track them across history so the 2× pass-round multiplier is applied automatically based on the previous round — instead of forcing users to remember which round was a pass round.

Details:
- `domain/Round.kt` already has a `passRound` field and `passRoundMultiplier` (2 when pass round, 1 otherwise), but it is per-round only. The multiplier is currently round-local, not history-aware.
- Requirement: a "Pass Round" round type. When added, the *previous* round in the game's history is flagged as a pass round, and subsequent rounds' scoring applies the 2× multiplier accordingly.
- This means `Rounds` must expose whether the prior round was a pass round, and the scoring must consume that history, not just the round's own flag.
- Coordinate with the scoring deepening ticket (`01-deepen-scoring.md`) — the multiplier semantics change here.

## Resolution

Done — the round-local behavior is the accepted final semantics. A `PassRound` is a zero-point placeholder: it keeps the round sequence intact but contributes nothing and does **not** double the previous or next round (see `GameTest` "a pass round contributes nothing to any score"). The `Round.points(playerId, passRound)` / `passRoundMultiplier` (2×) machinery and the unused `AddRoundState.passRound` field remain in place but are inert — no real round is ever doubled. If a pass round must actually double the prior deal, that is new work; it is not part of this resolution.
