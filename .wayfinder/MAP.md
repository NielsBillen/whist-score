# whist-score → robust whist app

## Destination

Reaching the end of this map looks like a robust whist application with: correct, tested score computation; automatic pass-round multiplier; dealer and first-move tracking; an extensible player roster; a persistent game history; global and per-game statistics; and a revised UI built on a proper navigation library and the Astryx design system. Currently the app only tracks a single game's four fixed players — this is the route from here to there.

## Notes

- The codebase is Kotlin Compose Multiplatform (Android/iOS/Web/Desktop), domain model in `domain/`, presentation in `presentation/`, Koin for DI, `Settings`-backed persistence. No automated tests exist yet — establish them before/while shipping features.
- Scoring math is the highest-risk area and is entirely untested. It is also the deepest lever — deepen it first.
- External systems named by the user: **nav3** (`org.jetbrains.androidx.navigation3:navigation3-ui`, Compose Multiplatform navigation, ~1.1.1) and **Astryx** (`@astryxdesign/core`, React/StyleX design system). The Astryx-on-Compose question needs research before it becomes a task — it is built on a different stack than this app.
- Reference the domain in its own words: `Round`, `Rounds`, `RoundType`, `Player`, `PlayerId`.

## Decisions so far

<!-- populated as tickets close -->

## Not yet specified

- How dealer and first-move state model in the domain and survive across round types.
- The shape of game history (a `Game` aggregate? a per-round `win` record needed for stats?) — the current `Round` interface records no `won`/`wonBy` beyond point math, so stats and history may need new fields.
- Whether nav3 fits Compose Multiplatform's current release line or whether an alternative navigation approach is warranted.
- Which Astryx components apply to a Compose UI, or whether the Astryx integration should target a lighter design system.
- The exact stats surfaced first (win rate, trick win rate, total points, points per game, win rate over time) and their storage format.

## Out of scope

- Scoring rules for whist variants other than the ones already in `RoundType` (Abandonce, Misere, OpenMisere, SoloSlim, Regular, Treble). Any new card-game rules are out of scope.
- Networking, accounts, cloud sync, or multiplayer. This effort is a local single-device app.
