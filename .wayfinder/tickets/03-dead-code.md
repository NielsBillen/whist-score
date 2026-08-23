wayfinder:task

## Question

Delete the dead modules. The deletion test applies: removing each concentrates no complexity because the live modules already cover the behaviour.

Dead units:
- `domain/Game.kt` — never instantiated; its scoring and `defaultPlayers` duplicate `Rounds` and `DefaultPlayerRepository.DEFAULT_PLAYERS`.
- `presentation/screens/overview/overview/OverviewAction.kt` — the `OverviewAction` sealed interface is never dispatched; reset is handled by `AppViewModel` via `AppAction.ResetGame`.
- The entire `SELECT_BID → BidInputScreen` stage is unreachable: `RoundType.screens` in `AddRoundViewModel` never lists `SELECT_BID`, so `BidInputScreen`, `AddRoundAction.SetBid`, and `AddRoundState.bid` form a dead branch.

Remove these. Confirm nothing else references them.
