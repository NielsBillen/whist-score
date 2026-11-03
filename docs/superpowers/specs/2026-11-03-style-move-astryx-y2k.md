# Handoff — Move app style to `:ui` + adopt astryx **y2k** typography

**Repo:** `/home/niels/workspace/whist-score` (Kotlin Compose Multiplatform desktop app)
**Status:** Design fully settled with the user. **No implementation has been done yet.** This doc is the complete spec to execute.
**Reference repo (astryx):** `~/workspace/astryx`

---

## 1. Task

Two coupled changes, one coherent result:

1. **Move the app's style out of `core` into the `ui` feature module.**
2. **Adopt the astryx `y2k` theme's typography** (font family + weights), delivered as **bundled font resources** (offline).

The theme composable becomes `UiTheme` in `ui`. The duplicated `Style` object is de-duplicated to a single copy in `ui`.

---

## 2. Settled decisions (do not re-litigate)

| # | Decision |
|---|----------|
| 1 | Adopt astryx **y2k** theme typography, fully. |
| 2 | `AppTheme` → rename **`UiTheme`**, lives in `:shared:features:ui:presentation`, replaces the current empty stub. `App.kt` keeps the top-level call, just re-imports. |
| 3 | Typography scope = **font family + weights only**. Keep the existing Material3 `Typography` structure (15 styles) and the existing `FontWeight.Bold` on `titleLarge`/`titleMedium`. Do **not** port astryx's type scale (base size / ratio). |
| 4 | **Keep `ui`'s `Style` object, drop `core`'s.** Re-point every consumer at `feature.ui.Style`; delete the duplicate in core. |
| 5 | Fonts delivered as **bundled `.ttf` resources** in `ui/presentation/composeResources/font/` (offline), registered via `Font(...)` — same mechanism NotoSans uses today. |

### Scope clarification (important)
"Fully adopt y2k" was qualified by the **family + weights only** scope decision, which is binding. Concretely:
- **Bundle only Poppins (Regular + Bold)** — the only family this app actually renders (screen titles + body text).
- **Do NOT bundle or map the y2k display serif (Crimson Text)** — no `displayLarge/Medium/Small` style is ever rendered in this app.
- **Do NOT bundle or map JetBrains Mono (code)** — there is no monospace/code text anywhere in the app.

If the user later wants the theme to *carry* the y2k display serif for the future, that's a follow-up — out of scope now.

---

## 3. The astryx y2k typography (source of truth)

Source: `~/workspace/astryx/packages/themes/y2k/src/y2kTheme.ts` lines ~109–125.

```ts
typography: {
  scale: {base: 16, ratio: 1.25},   // <-- NOT ported (family+weights only)
  body:    { family: 'Poppins', fallbacks: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif' },
  heading: { family: 'Poppins', fallbacks: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif' },
  code:    { family: 'JetBrains Mono', fallbacks: '"SF Mono", Monaco, Consolas, monospace' },  // <-- not used by this app
}
```
- **y2k display sizes use a Crimson Text serif** (component overrides, `y2kTheme.ts` ~lines 317–327) — **not ported** (no display style rendered).

So the effective font for this app is **Poppins** for every text style, with `titleLarge`/`titleMedium` **Bold** (as today).

---

## 4. Current state (verified facts)

### Style lives in `core`
- `shared/features/core/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/core/Style.kt` (package `be.niels.billen.whistscore.feature.core`)
  - `@Composable fun AppTheme(content)` — builds a `Typography` from `NotoSans_Regular`/`NotoSans_Bold`, sets `fontFamily` on all 15 Material3 styles, `fontWeight = Bold` on `titleLarge`/`titleMedium`, and wraps `MaterialTheme(typography=…, colorScheme = darkColorScheme(), content)`.
  - `object Style { object Dimensions { paddingTight=4.dp, paddingSmall=8.dp, paddingMedium=12.dp, paddingLarge=16.dp, paddingExtraExtraLarge=48.dp, radiusMedium=16.dp } ; object Shapes { mediumRoundedCornerShape = RoundedCornerShape(radiusMedium) } }`
  - Font imports: `org.jetbrains.compose.resources.Font`, and `whist_score.shared.features.core.presentation.generated.resources.{NotoSans_Bold, NotoSans_Regular, Res}`.
  - `colorScheme` is `darkColorScheme()` (import `androidx.compose.material3.darkColorScheme`). **Keep this** — it is part of the app's theme, not part of the font change.

### `ui` already has a stub + duplicate Style
- `shared/features/ui/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/ui/Style.kt` (package `be.niels.billen.whistscore.feature.ui`)
  - `@Composable fun UiTheme(content) { content() }` — **empty stub**, to be replaced.
  - `object Style { … }` — **identical** Dimensions/Shapes to core's. `ui`'s `Button.kt` uses `feature.ui.Style`. **Keep this.**

### Theme call site
- `shared/features/app/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/app/App.kt`
  - Line 15: `import be.niels.billen.whistscore.feature.core.AppTheme`
  - Line 24: `AppTheme { Surface(…) { … } }` — wrap `Surface`/`Background`/nav.

### Font resources (dead weight)
- `shared/features/core/presentation/src/commonMain/composeResources/font/` — **72 `.ttf` files**, the whole NotoSans family. Only **2 are referenced** (`NotoSans_Regular`, `NotoSans_Bold`). The other 70 are dead.

### `Style` consumers (re-point `feature.core.Style` → `feature.ui.Style`)
Feature files that `import be.niels.billen.whistscore.feature.core.Style` (9):
1. `shared/features/rounds/presentation/.../rounds/RoundsView.kt`
2. `shared/features/editplayers/presentation/.../editplayers/EditPlayersView.kt`
3. `shared/features/addround/presentation/.../addround/AddRoundPanel.kt`
4. `shared/features/addround/presentation/.../addround/AddRoundView.kt`
5. `shared/features/addround/presentation/.../addround/SlamInputScreen.kt`
6. `shared/features/addround/presentation/.../addround/RoundTypeInputScreen.kt`
7. `shared/features/addround/presentation/.../addround/BidAchievedInput.kt`
8. `shared/features/players/presentation/.../players/PlayersView.kt`
9. `shared/features/overview/presentation/.../overview/Overview.kt`

Plus, inside core (same package `feature.core`, so **no import line today**):
10. `shared/features/core/presentation/.../core/Selectable.kt` — uses `Style.Dimensions.radiusMedium`. When core's `Style` is deleted this breaks; it must `import be.niels.billen.whistscore.feature.ui.Style`.

### Module dependency graph (relevant edges)
- `:shared:features:ui:presentation` **depends on** `:shared:features:core:presentation` (`ui/presentation/build.gradle.kts` line 50).
- `:shared:features:app:presentation` depends on `:shared:features:core:presentation` (line 45) — **does NOT depend on `ui:presentation`** → must add it (it will import `feature.ui.UiTheme`).
- Already depend on `:shared:features:ui:presentation`: **overview, editplayers, addround**.
- Do **NOT** yet depend on `:shared:features:ui:presentation`: **players, rounds, core:presentation** → all three must add it (they consume `feature.ui.Style`).

### What text styles the app actually renders
- **Only `titleLarge` is used explicitly** (3 screen titles: `Overview.kt`, `EditPlayersView.kt`, `AddRoundPanel.kt`), and it is `Bold`.
- Everything else is default `Text` (Material3 `bodyLarge`) plus a handful of explicit `fontWeight` overrides (`FontWeight.Bold`/`ExtraBold`) in `RoundsView`, `PlayersView`, `SummaryScreen`, `RoundTypeInputScreen`.
- **No `displayLarge/Medium/Small` or `headline*` style is ever used.** (This is why the display serif is skipped.)

---

## 5. Implementation plan (vertical slice, verify with `./gradlew build`)

> `./gradlew build` compiles and tests everything; screenshot goldens are excluded. Run it at the end and on any red.

### Step A — Get the fonts
Download **Poppins Regular** and **Poppins Bold** into `shared/features/ui/presentation/src/commonMain/composeResources/font/`.

- Google Fonts CSS API (the same service astryx uses):
  `https://fonts.googleapis.com/css2?family=Poppins:wght@400;700&display=swap`
  Fetch it (UA must include a browser string for TTF; otherwise you may get WOFF2). Extract the two `.ttf` (or `.woff2`) URLs and download the files.
- **Name the files to match the identifiers you register** — Compose resources generate an accessor from the file name with non-alphanumerics stripped. To mirror the existing NotoSans pattern, name them `Poppins-Regular.ttf` and `Poppins-Bold.ttf` (→ `Res.font.Poppins_Regular`, `Res.font.Poppins_Bold`). Verify the generated accessor names after a build and align the `Font(...)` calls to whatever is actually generated.
- **Do not** touch the 72 NotoSans files in this step (removed in Step E).

### Step B — Rewrite `ui/presentation/Style.kt`
- Replace the empty `UiTheme` stub with the real theme: same shape as the current `AppTheme`, but with **Poppins** and the **ui-generated resource namespace**.
- New font imports (mirror NotoSans, new namespace):
  - `import org.jetbrains.compose.resources.Font`
  - `import whist_score.shared.features.ui.presentation.generated.resources.Poppins_Regular`
  - `import whist_score.shared.features.ui.presentation.generated.resources.Poppins_Bold`
  - `import whist_score.shared.features.ui.presentation.generated.resources.Res`
- Build `FontFamily(Poppins_Regular weight=Normal, Poppins_Bold weight=Bold)`; set `fontFamily` on all 15 styles; `fontWeight = Bold` on `titleLarge`/`titleMedium`; wrap `MaterialTheme(typography=…, colorScheme = darkColorScheme(), content)`.
- Keep the existing `object Style { Dimensions; Shapes }` in this file unchanged (it's already correct and used by `Button`).

### Step C — Re-point `Style` consumers (9 feature files + Selectable)
- In each of the 9 feature files: change `import be.niels.billen.whistscore.feature.core.Style` → `import be.niels.billen.whistscore.feature.ui.Style`. Body usages (`Style.Dimensions.…`, `Style.Shapes.…`) stay identical.
- In `core/presentation/.../core/Selectable.kt`: add `import be.niels.billen.whistscore.feature.ui.Style` (it had none, same-package before).

### Step D — Update `App.kt`
- Change `import be.niels.billen.whistscore.feature.core.AppTheme` → `import be.niels.billen.whistscore.feature.ui.UiTheme`.
- Change the call site `AppTheme {` → `UiTheme {`.

### Step E — Delete core's style + dead fonts
- Delete `shared/features/core/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/core/Style.kt`.
- Delete all 72 `.ttf` files in `shared/features/core/presentation/src/commonMain/composeResources/font/` (and the now-empty `font/` dir).
- Confirm no other `core:presentation` file references `Res.font.*` or the deleted `Style`/`AppTheme` (only `Style.kt` referenced the fonts; `Selectable.kt` handled in Step C).

### Step F — Build files
- `shared/features/app/presentation/build.gradle.kts`: add `implementation(projects.shared.features.ui.presentation)` to `commonMain.dependencies`.
- `shared/features/players/presentation/build.gradle.kts`: add `implementation(projects.shared.features.ui.presentation)`.
- `shared/features/rounds/presentation/build.gradle.kts`: add `implementation(projects.shared.features.ui.presentation)`.
- **Do NOT** add a `core:presentation → ui:presentation` edge: `ui:presentation` already depends on `core:presentation`, so that would be a **cycle** (Gradle forbids it). So no change to `core:presentation`'s `build.gradle.kts`.

#### The `Selectable` cycle — resolved
`Selectable.kt` (in `core:presentation`, package `feature.core`) references `Style.Dimensions.radiusMedium`, so deleting core's `Style` breaks it. Its **only 4 callers are all in `:addround:presentation`** (`SlamInputScreen`, `RoundTypeInputScreen`, `BidAchievedInput`, `PlayerSelectionScreen`), and addround **already depends on `:ui:presentation`**.

**Fix (no build-file change needed):** move `Selectable.kt` into `:shared:features:ui:presentation`, changing its package to `be.niels.billen.whistscore.feature.ui`. It then resolves `Style` (same package) with no import. Re-point the 4 addround import lines from `feature.core.Selectable` → `feature.ui.Selectable`. This avoids the cycle entirely and matches the codebase convention that a shared UI composable belongs in the shared `ui` module.

So the only build-file additions are the three in Step F (app, players, rounds).

### Step G — Verify
- `./gradlew build` (screenshot goldens excluded). Must be green.
- Quick visual sanity: the screen titles (`titleLarge`) and body text should now render in Poppins; titles bold.

---

## 6. Risks / things to watch

1. **Module cycle** — avoided by moving `Selectable.kt` into `ui` (Step F); do **not** add a `core:presentation → ui:presentation` edge.
2. **Generated resource accessor names** — Compose generates identifiers from file names; align `Font(Res.font.…)` to the real generated names after a build (Step A).
3. **`darkColorScheme()` must be preserved** — it's the app's existing color theme; the task is typography, not color.
4. **`Selectable.kt`** is same-package today (no import); it moves to `ui` (Step F) rather than gaining a cross-module import.
5. **Ponytail discipline** — keep it minimal: Poppins only, no display serif, no mono, no scale. Don't "improve" unrelated code.

---

## 7. Suggested skills for the implementing session

- **`executing-plans`** — execute this plan in the current session as the implementer (the plan is already written and settled).
- **`tdd` / `test-driven-development`** — the repo mandates TDD; if any behaviour test is warranted (e.g., a compose test asserting the theme resolves), write the failing test first. (Most of this change is a mechanical move + font swap; a compile/build + visual check is the main verification.)
- **`verification-before-completion`** — before claiming done, actually run `./gradlew build` and confirm green; evidence before assertions.
- **`finishing-a-development-branch`** — when implementation is complete and green, decide how to integrate (branch/commit/PR).
- **`ponytail`** — keep the change minimal (Poppins only, no display serif/mono/scale); resist scope creep.

---

## 8. References (do not re-derive)

- Code guidelines (hard rules): `/home/niels/workspace/whist-score/AGENTS.md` → `docs/guidelines/code-guidelines.md`
- astryx y2k theme source: `~/workspace/astryx/packages/themes/y2k/src/y2kTheme.ts`
- Current style to move: `shared/features/core/presentation/.../feature/core/Style.kt`
- Stub to replace: `shared/features/ui/presentation/.../feature/ui/Style.kt`
- Theme call site: `shared/features/app/presentation/.../feature/app/App.kt`
