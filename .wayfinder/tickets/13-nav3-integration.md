blocked-by: 06-research-nav3.md
wayfinder:task

## Question

Integrate nav3 (`org.jetbrains.androidx.navigation3:navigation3-ui`) for screen management, replacing the current `AnimatedContent`-based dispatch in `AddRoundView`.

Details:
- Depends on the nav3 research (`06-research-nav3.md`) establishing that nav3 actually supports this stack. If the research says no, adopt the recommended alternative before implementing.
- `presentation/app/App.kt` currently hosts the top-level screen (`AppScreen` enum) via a `when`; `AddRoundView.kt` dispatches six sub-screens in one big `when`.
- Map the existing `AppScreen` OVERVIEW / ADD_ROUND / EDIT_PLAYERS and the Add Round sub-screen flow onto nav3's destination/state model.
- This is the UI-architecture backbone for the whole app, so it should be solid before the Astryx visual redesign lands.
