wayfinder:task

## Question

Deepen the Add Round navigation: extract the "what screen comes next" rules into one cohesive navigator module with a small interface, so the navigation logic no longer lives split across the ViewModel's private extensions, `RoundType.screens`, and the monolithic `AddRoundView`.

Details:
- Navigation rules are scattered: `AddRoundViewModel` holds private `nextScreen`/`previousScreen` extensions; `AddRoundState.nextScreen`/`previousScreen` live in `AddRoundState.kt`; `RoundType.screens` lives in `AddRoundViewModel.kt`.
- `AddRoundView.kt` dispatches every sub-screen in one big `when` — the thickest view in the app.
- The `round` derivation (`AddRoundState.round`) already encodes which round type maps to which sub-round inputs; it is reasonable to co-locate the navigator here or alongside it.
- No tests exist. Give the navigator a small, testable interface.
