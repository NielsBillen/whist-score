blocked-by: 01-deepen-scoring.md
wayfinder:task

## Question

Consolidate the display metadata of `RoundType`: the round type's `displayName`/`title` and `description` are currently re-derived in two screens with divergent meaning — `RoundTypeInputScreen.kt` and `PlayerSelectionScreen.kt`. Give the domain type its canonical name and description so views read one source instead of declaring two.

Details:
- `domain/RoundType.kt` currently owns only `singlePlayer` and `playerCountRange` — no display metadata at all.
- `presentation/screens/addround/roundinput/RoundTypeInputScreen.kt` defines `RoundType.displayName` and `RoundType.description` (game descriptions).
- `presentation/screens/addround/playerselection/PlayerSelectionScreen.kt` defines `RoundType.title` and a second `RoundType.description` (UI guidance like "Choose between one and two players").
- Goal: `RoundType` exposes a single canonical name + description; both screens read them.
