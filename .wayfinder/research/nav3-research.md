# nav3 research — is it usable here?

**Research ticket:** [nav3 usability research](../tickets/06-research-nav3.md) (`wayfinder:research`, AFK)
**Date:** 2026-08-27
**Sources:** [Navigation 3 in Compose Multiplatform](https://kotlinlang.org/docs/multiplatform/compose-navigation-3.html) (official docs, 07 Jul 2026); [Maven Central `navigation3-ui` metadata + module artifacts](https://repo1.maven.org/maven2/org/jetbrains/androidx/navigation3/navigation3-ui/1.1.1/); this project's `libs.versions.toml`, `composeApp/build.gradle.kts`, `App.kt`, `AddRoundView.kt`, `AppAction.kt`.

## Verdict

**Adopt nav3.** `org.jetbrains.androidx.navigation3:navigation3-ui:1.1.1` is the current
stable release, supports **all four** of this app's targets (Android, iOS, Desktop, WasmJs), is
DI-agnostic (Koin stays), and the project's Compose Multiplatform / Kotlin versions are ahead of
nav3's requirements. No alternative navigation approach is warranted.

## 1. Version & coordinates

- **`1.1.1` is current.** Maven Central `maven-metadata.xml` reports `<release>1.1.1</release>`.
  The `1.2.0-alpha01` / `1.2.0-alpha02` entries are **alpha pre-releases**, so nothing newer than
  `1.1.1` is stable. The user's `1.1.1` is up to date.
- **It's one artifact, not a BOM.** nav3 ships as `navigation3-ui` (with `navigation3-common`
  pulled in transitively). There is **no** `cmpNavigation3` BOM — add a single version ref:
  ```toml
  [versions]
  navigation3-ui = "1.1.1"
  [libraries]
  navigation3-ui = { module = "org.jetbrains.androidx.navigation3:navigation3-ui", version.ref = "navigation3-ui" }
  ```
  and add `implementation(libs.navigation3.ui)` to `composeApp` commonMain.
- **Compatibility headroom.** nav3-ui 1.1.1's `metadataApiElements` requires Compose
  `animation`/`runtime`/`ui` at **1.10.0** and `kotlin-stdlib` at **2.2.20**. The app is on
  **CMP 1.11.1** and **Kotlin 2.4.10** — comfortably ahead. `lifecycle-runtime:2.10.0` is pulled
  transitively; the app already uses `lifecycle:2.11.0`, which Gradle resolves upward. Fine.
- **Optional extras (not required for core nav3).** `adaptive-navigation3:1.3.0-beta02`
  (Material3 Adaptive) and `lifecycle-viewmodel-navigation3:2.10.0`. Only needed if you want the
  adaptive multi-destination scaffolding or nav3-scoped ViewModels. Core nav3 works without them.
  `adaptive-navigation3` is still beta — skip it unless the redesign wants the adaptive layout.

## 2. Target support — all four are published

Definitive proof is the `navigation3-ui-1.1.1.module` Gradle Module Metadata, which declares
runtime variants for exactly these targets:

| App target (in `composeApp/build.gradle.kts`) | nav3 artifact published | nav3 variant key |
|---|---|---|
| `androidLibrary` (Android) | `navigation3-ui` (aar) | `androidRuntimeElements` |
| `iosArm64()`, `iosSimulatorArm64()` (iOS) | `navigation3-ui-iosarm64`, `navigation3-ui-iossimulatorarm64` | `iosArm64…` / `iosSimulatorArm64…` |
| `jvm()` (Desktop) | `navigation3-ui-desktop` | `desktopRuntimeElements` |
| `wasmJs` (Web) | **`navigation3-ui-wasm-js`** | `wasmJsRuntimeElements` |

**The original risk — nav3 being desktop/JVM-only — is cleared.** `wasmJs` *is* published
(`org.jetbrains.kotlin.platform.type: wasm`, `navigation3-ui-wasm-js`), so the WasmJs web target
is covered. (nav3 also publishes `macosArm64`; the app doesn't target it, but that's fine.)

**Caveat — non-JVM destination-key serialization.** On Android/JVM nav3 uses reflection-based
serialization. On the non-JVM targets (iOS, WasmJs) you must use the `rememberNavBackStack`
overload that takes a `SavedStateConfiguration` with a `SerializersModule` for polymorphic
`NavKey` resolution. This is a **one-time** setup, not per-route. The app already has the
`kotlin.serialization` plugin and `multiplatform-settings-serialization`, so kotlinx-serialization
is available.

## 3. AppScreen → nav3 destination mapping

Current state:
- `App.kt` hosts one screen via a `when` on `viewModel.screen.collectAsState()`; `AppScreen` has
  `OVERVIEW`, `ADD_ROUND`, `EDIT_PLAYERS` (`AppAction.kt`).
- `AddRoundView.kt` dispatches six sub-screens via `AnimatedContent` + `when` over
  `AddRoundScreen` (`SELECT_ROUND_TYPE → SELECT_PLAYERS → SELECT_SLAMS → SELECT_BID →
  SELECT_BID_ACHIEVED → SUMMARY`).

nav3's model: a **user-owned** `SnapshotStateList<NavKey>` back stack + a `NavDisplay` that shows
the entry for the current key.

Mapping:
- The three `AppScreen` states become three top-level `@Serializable sealed` `NavKey` routes in a
  sealed `AppRoute` (`Overview`, `AddRound`, `EditPlayers`). The top-level `when` in `App.kt` is
  replaced by a `NavDisplay` keyed on the back-stack entry.
- The `AddRoundView` flow maps to a **nested** nav3 back stack — a separate sealed `AddRoundRoute`
  of `NavKey`s, managed inside the Add Round screen, separate from the top-level stack. This is a
  clean fit for nav3's two-backstack shape.
- `SELECT_BID` + `SELECT_BID_ACHIEVED` are dead code (see ticket 03); drop them from the sealed
  `AddRoundRoute` when integration runs — the flow collapses to four live stages.
- nav3 replaces state-driven `when` *display* with key-driven display, but the **what-comes-next
  logic stays in the ViewModel**. So nav3 is the surface; the navigator extracted in ticket 02
  stays the logic. They compose rather than compete.

## 4. Koin — no conflict

nav3-ui does not impose or forbid a DI framework. Its only DI-adjacent transitive is
`navigationevent-compose:1.0.1` (navigation events), which is unrelated to Koin. Koin stays.

The app already uses `koin-compose` + `koin-compose-viewmodel` + `lifecycle-viewmodel`. nav3's
user-owned back stack lives in a ViewModel, so it slots into `lifecycle-viewmodel` directly. If
the integration later wants nav3-scoped ViewModels, the optional
`lifecycle-viewmodel-navigation3:2.10.0` artifact slots in with the existing Koin compose
ViewModels — an optional detail, not a blocker.

## Caveats for the integration ticket (13)

- nav3 is an **architectural** change (ViewModel-owned back stack + `SnapshotStateList`), not a
  drop-in. Pair it with the navigator extraction (ticket 02) and the dead-Bid cleanup (ticket 03).
- One-time `SavedStateConfiguration` / `SerializersModule` setup for the sealed routes on
  non-JVM targets (single-module `subclassesOfSealed<AppRoute>()` pattern fits — all routes live
  in the presentation layer).
- Skip `adaptive-navigation3` (beta) unless the redesign wants its multi-destination adaptive
  layout; core nav3 does not need it.
