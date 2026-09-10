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

- [Deepen the scoring module](tickets/01-deepen-scoring.md) — done: `Round.points` hides the multiplier, `Rounds` is a real domain type `Game` delegates to, and `RoundTest`/`GameTest` cover the scoring surface; the dead-`Game` deletion is superseded because `Game` became the live `GameRepository` aggregate.
- [RoundType display metadata](tickets/04-roundtype-metadata.md) — done: canonical `displayName`/`description` live on the domain `RoundType`; both add-round screens read them, the screens' divergent `title`/guidance re-derivations are deleted.
- [nav3 usability research](tickets/06-research-nav3.md) — adopt nav3: `navigation3-ui:1.1.1` supports all four targets, is Koin-compatible, and the app's versions are ahead of its requirements; findings in `research/nav3-research.md`.
- [Extract a deep NumberInput component](tickets/05-numberinput.md) — superseded, no code change: the "Bid" stage is now the boolean `BidAchievedInput`, so `SlamInputScreen` is the only number-range input left and a shared `NumberInput` would have one caller; also resolves the `03-dead-code` "dead `SELECT_BID` stage" unit (replaced, not deleted).
- [Astryx UI-rebuild path research](tickets/07-research-astryx.md) — done: Astryx (React 19 + StyleX) can't be consumed as a library in Compose — no DOM/React/StyleX surface and no official Kotlin/Compose/Wasm bridge — but its tokens are plain data and it ships `resolveThemeTokens` to export them as concrete values; the UI path is a hand-authored Compose theme mirroring Astryx's `neutral` theme (not a dependency, not a third-party design system). Unblocks [14-astryx-ui](tickets/14-astryx-ui.md).

## Not yet specified

- How dealer and first-move state model in the domain and survive across round types.
- The shape of game history (a `Game` aggregate? a per-round `win` record needed for stats?) — the current `Round` interface records no `won`/`wonBy` beyond point math, so stats and history may need new fields.
- The exact stats surfaced first (win rate, trick win rate, total points, points per game, win rate over time) and their storage format.

## Out of scope

- Scoring rules for whist variants other than the ones already in `RoundType` (Abandonce, Misere, OpenMisere, SoloSlim, Regular, Treble). Any new card-game rules are out of scope.
- Networking, accounts, cloud sync, or multiplayer. This effort is a local single-device app.
