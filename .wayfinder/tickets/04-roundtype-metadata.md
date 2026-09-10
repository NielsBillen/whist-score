blocked-by: 01-deepen-scoring.md
wayfinder:task
status: closed

## Question

Consolidate the display metadata of `RoundType`: the round type's `displayName`/`title` and `description` are currently re-derived in two screens with divergent meaning — `RoundTypeInputScreen.kt` and `PlayerSelectionScreen.kt`. Give the domain type its canonical name and description so views read one source instead of declaring two.

Details:
- `domain/RoundType.kt` currently owns only `singlePlayer` and `playerCountRange` — no display metadata at all.
- `presentation/screens/addround/roundinput/RoundTypeInputScreen.kt` defines `RoundType.displayName` and `RoundType.description` (game descriptions).
- `presentation/screens/addround/playerselection/PlayerSelectionScreen.kt` defines `RoundType.title` and a second `RoundType.description` (UI guidance like "Choose between one and two players").
- Goal: `RoundType` exposes a single canonical name + description; both screens read them.

## Resolution

Done. `RoundType` (core/domain) now carries `displayName` and `description` as constructor properties — the rule texts previously re-derived in `RoundTypeInputScreen.kt` are its canonical description. Both screens read the domain properties; all four private `RoundType` extensions were deleted.

`PlayerSelectionScreen`'s panel title is now `roundType.displayName` and its description the canonical one. The old "Select player(s)"/"Choose the player that played this round" guidance was screen chrome derived from `playerCountRange` — dropped, since the choice controls already say it and `Pass` never reaches this screen (`AddRoundNavigator` routes it straight to the summary).

`RoundTypeTest` (core/domain) guards that every entry carries non-blank display metadata; the `AppTest` assertion on the player-selection panel title was updated to the new title text.
