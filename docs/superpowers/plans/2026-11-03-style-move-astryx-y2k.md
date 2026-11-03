# Move app style to `:ui` + astryx y2k typography — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** The app theme (`UiTheme`) and the shared `Style` object live in `:shared:features:ui:presentation`, rendering in astryx **y2k** typography (Poppins, titles Bold), with bundled offline fonts.

**Architecture:** The existing `AppTheme` in `core` is replaced by a real `UiTheme` in `ui` (same `MaterialTheme` shape, Poppins instead of NotoSans). `Selectable` moves from `core` to `ui` to avoid a `core → ui` module cycle. Every `Style` consumer re-points to `feature.ui.Style`, then core's duplicate `Style.kt` and the 72 dead NotoSans fonts are deleted.

**Tech Stack:** Kotlin, Compose Multiplatform (desktop), Compose resource fonts (`.ttf`), Gradle multi-module.

**Spec:** `docs/superpowers/specs/2026-11-03-style-move-astryx-y2k.md`

## Global Constraints

- Typography scope = **font family + weights only**. Keep the Material3 `Typography` structure (15 styles) and `FontWeight.Bold` on `titleLarge`/`titleMedium`. Do NOT port astryx's type scale, display serif (Crimson Text), or mono (JetBrains Mono).
- **Bundle only Poppins Regular (400) + Bold (700)** as `.ttf` in `ui/presentation/src/commonMain/composeResources/font/`.
- `darkColorScheme()` must be preserved (theme color is not part of this change).
- **No** `core:presentation → ui:presentation` build edge (cycle — `ui:presentation` already depends on `core:presentation`).
- Package names carry the feature, never the layer. `Selectable` moves to package `be.niels.billen.whistscore.feature.ui`.
- Verification command: `./gradlew build` (screenshot goldens excluded). Must be green.
- Ponytail: minimal change; do not "improve" unrelated code.

## Review Focus

- **Generated resource accessor names:** Compose generates `Res.font.*` identifiers from file names; a mismatch between the file name and the `Font(Res.font.…)` call fails compilation — the first `./gradlew build` after Task 1 catches it, but a reviewer should confirm the calls match the generated names (not the guessed ones).
- **Font format:** the bundled files must be TrueType (`.ttf`), not WOFF2 — the CSS2 API serves WOFF2 by default; WOFF2 does not load on the Compose desktop runtime.
- **Missing module edge:** any consumer re-pointed to `feature.ui` whose module lacks the `ui:presentation` dependency fails to compile — app, players and rounds are the three that needed the edge.
- **Dead references after deletion:** after core's `Style.kt` and the NotoSans fonts are deleted, any lingering `feature.core.Style` import, `feature.core.AppTheme` import, or `Res.font.NotoSans_*` reference in `core:presentation` is a compile failure — grep must be clean.
- **Wildcard import:** `PlayerSelectionScreen.kt` imports `feature.core.*`, so the `Selectable` move is invisible in its import list — it needs an explicit `feature.ui.Selectable` import or it fails to compile.

---

### Task 1: Poppins fonts + real `UiTheme` in `:ui:presentation`

**Files:**
- Create: `shared/features/ui/presentation/src/commonMain/composeResources/font/Poppins-Regular.ttf`
- Create: `shared/features/ui/presentation/src/commonMain/composeResources/font/Poppins-Bold.ttf`
- Modify: `shared/features/ui/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/ui/Style.kt` (replace the `UiTheme` stub; keep `object Style` unchanged)
- Test: `shared/features/ui/presentation/src/commonTest/kotlin/be/niels/billen/whistScore/ui/UiThemeTest.kt`

**Interfaces:**
- Consumes: nothing from earlier tasks.
- Produces: `@Composable fun UiTheme(content: @Composable () -> Unit)` in package `be.niels.billen.whistscore.feature.ui` — the theme that `App.kt` (Task 3) will call. `object Style` (Dimensions, Shapes) stays as-is for Task 2 consumers.

- [ ] **Step 1: Write the failing test**

Create `UiThemeTest.kt` (package `be.niels.billen.whistScore.ui`, mirroring `ButtonTest.kt`):

```kotlin
package be.niels.billen.whistScore.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.font.FontFamily
import be.niels.billen.whistscore.feature.ui.UiTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.junit4.createComposeRule

class UiThemeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun uiTheme_setsCustomFontFamilyOnTitleLarge() {
        var titleFontFamily: FontFamily? = null
        composeTestRule.setContent {
            UiTheme {
                titleFontFamily = MaterialTheme.typography.titleLarge.fontFamily
            }
        }
        assertThat(titleFontFamily).isNotNull()
        assertThat(titleFontFamily!!.familyMembers).hasSize(2)
    }
}
```

(The default M3 `titleLarge` carries one font — Roboto. Two members = the Poppins Regular+Bold family is wired. `familyMembers` may be `null`-typed in some Compose versions; if so, assert on `titleFontFamily!!.familyMembers ?: emptyList()`. Adjust to what compiles — the intent is: not the single default font.)

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :shared:features:ui:presentation:jvmTest --tests "be.niels.billen.whistScore.ui.UiThemeTest"`
Expected: FAIL — `UiTheme` is the empty stub, so `titleFontFamily.familyMembers` has size 1, not 2.

- [ ] **Step 3: Add the font files**

Copy the already-downloaded TTFs (TrueType-verified) into place:

```bash
mkdir -p shared/features/ui/presentation/src/commonMain/composeResources/font
cp /tmp/opencode/Poppins-Regular.ttf /tmp/opencode/Poppins-Bold.ttf shared/features/ui/presentation/src/commonMain/composeResources/font/
```

Names mirror the NotoSans pattern (`NotoSans-Bold.ttf` → `NotoSans_Bold`), so the generated accessors are `Res.font.Poppins_Regular` and `Res.font.Poppins_Bold`.

- [ ] **Step 4: Implement `UiTheme` in `ui/.../Style.kt`**

Replace the stub `UiTheme` body with the same shape as core's current `AppTheme`, but Poppins and the ui-generated resource namespace:

- New imports: `org.jetbrains.compose.resources.Font`, `androidx.compose.material3.Typography`, `androidx.compose.material3.darkColorScheme`, `androidx.compose.ui.text.font.FontFamily`, `androidx.compose.ui.text.font.FontStyle`, `androidx.compose.ui.text.font.FontWeight`, and `whist_score.shared.features.ui.presentation.generated.resources.{Poppins_Bold, Poppins_Regular, Res}`.
- Body: `Typography().run { val fontFamily = FontFamily(Font(Res.font.Poppins_Regular, weight = FontWeight.Normal, style = FontStyle.Normal), Font(Res.font.Poppins_Bold, weight = FontWeight.Bold, style = FontStyle.Normal)); copy(…all 15 styles with `fontFamily = fontFamily`, plus `fontWeight = FontWeight.Bold` on `titleLarge`/`titleMedium`…) }` wrapped in `MaterialTheme(typography = typography, colorScheme = darkColorScheme(), content = content)`.
- Keep `object Style { Dimensions; Shapes }` in the file unchanged.

If compilation reports the generated accessor names differ from `Poppins_Regular`/`Poppins_Bold`, align the two `Font(Res.font.…)` calls to the names the compiler actually reports.

- [ ] **Step 5: Run test to verify it passes**

Run: `./gradlew :shared:features:ui:presentation:jvmTest --tests "be.niels.billen.whistScore.ui.UiThemeTest"`
Expected: PASS (1/1).

- [ ] **Step 6: Commit**

```bash
git add shared/features/ui/presentation/src/commonMain/composeResources/font/ \
        shared/features/ui/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/ui/Style.kt \
        shared/features/ui/presentation/src/commonTest/kotlin/be/niels/billen/whistScore/ui/UiThemeTest.kt
git commit -m "feat(ui): UiTheme with bundled Poppins (y2k typography)"
```

---

### Task 2: Re-point `Style` consumers + move `Selectable` to `:ui`

**Files:**
- Modify (import line only, `feature.core.Style` → `feature.ui.Style`):
  - `shared/features/rounds/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/rounds/RoundsView.kt:22`
  - `shared/features/editplayers/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/editplayers/EditPlayersView.kt:13`
  - `shared/features/addround/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/addround/AddRoundPanel.kt:11`
  - `shared/features/addround/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/addround/AddRoundView.kt:12`
  - `shared/features/addround/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/addround/SlamInputScreen.kt:14`
  - `shared/features/addround/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/addround/RoundTypeInputScreen.kt:20`
  - `shared/features/addround/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/addround/BidAchievedInput.kt:11`
  - `shared/features/players/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/players/PlayersView.kt:22`
  - `shared/features/overview/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/overview/Overview.kt:20`
- Move: `shared/features/core/presentation/.../feature/core/Selectable.kt` → `shared/features/ui/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/ui/Selectable.kt`, package changed to `be.niels.billen.whistscore.feature.ui` (file body otherwise unchanged — `Style` resolves same-package).
- Modify (Selectable import, `feature.core.Selectable` → `feature.ui.Selectable`):
  - `shared/features/addround/presentation/.../addround/SlamInputScreen.kt:15`
  - `shared/features/addround/presentation/.../addround/RoundTypeInputScreen.kt:19`
  - `shared/features/addround/presentation/.../addround/BidAchievedInput.kt:12`
- Modify: `shared/features/addround/presentation/.../addround/PlayerSelectionScreen.kt:12` — it uses the wildcard `import be.niels.billen.whistscore.feature.core.*`; **add** `import be.niels.billen.whistscore.feature.ui.Selectable` (keep the wildcard — other `feature.core` symbols may still resolve through it).
- Modify (build deps, add `implementation(projects.shared.features.ui.presentation)` to `commonMain.dependencies`):
  - `shared/features/players/presentation/build.gradle.kts`
  - `shared/features/rounds/presentation/build.gradle.kts`
- Test: `shared/features/ui/presentation/src/commonTest/kotlin/be/niels/billen/whistScore/ui/UiThemeTest.kt` (re-run; unchanged)

**Interfaces:**
- Consumes: `object Style` from `be.niels.billen.whistscore.feature.ui` (Task 1's file, unchanged), `Selectable` (moved, same signature: `selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, radius: Dp = Style.Dimensions.radiusMedium, content: @Composable BoxScope.() -> Unit`).
- Produces: every `Style` and `Selectable` reference in the codebase resolves to `feature.ui.*` — Task 3's deletion of core's `Style.kt` is safe.

- [ ] **Step 1: Re-point the 9 `Style` imports and move `Selectable`**

Mechanical edits exactly as listed in Files. No body changes anywhere: `Style.Dimensions.…` / `Style.Shapes.…` / `Selectable(…)` call sites stay byte-identical.

- [ ] **Step 2: Re-point the 4 addround `Selectable` imports (incl. the wildcard file)**

- [ ] **Step 3: Add the two `ui:presentation` build dependencies**

Players and rounds (addround/overview/editplayers already have the edge). Do NOT touch `core:presentation`'s build file — that edge would be a cycle.

- [ ] **Step 4: Verify**

Run: `./gradlew compileKotlinJvm -q` (or `./gradlew build` if the machine is fast; goldens excluded either way)
Expected: BUILD SUCCESSFUL — both `Style` objects still exist (core's is deleted in Task 3), all re-pointed imports resolve.

- [ ] **Step 5: Commit**

```bash
git add -A shared/features
git commit -m "refactor: Style and Selectable consumers point at :ui"
```

---

### Task 3: `App` uses `UiTheme`; delete core style + dead fonts

**Files:**
- Modify: `shared/features/app/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/app/App.kt:15` (`import be.niels.billen.whistscore.feature.core.AppTheme` → `import be.niels.billen.whistscore.feature.ui.UiTheme`) and `:24` (`AppTheme {` → `UiTheme {`)
- Modify: `shared/features/app/presentation/build.gradle.kts` — add `implementation(projects.shared.features.ui.presentation)` to `commonMain.dependencies`
- Delete: `shared/features/core/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/core/Style.kt`
- Delete: all 72 `.ttf` files in `shared/features/core/presentation/src/commonMain/composeResources/font/` (and the now-empty `font/` dir)
- Test: full `./gradlew build`

**Interfaces:**
- Consumes: `feature.ui.UiTheme` (Task 1) — same signature as the old `AppTheme`.
- Produces: `feature.core.Style` and `feature.core.AppTheme` no longer exist; the app renders in Poppins.

- [ ] **Step 1: Re-point `App.kt` and add the app build dependency**

- [ ] **Step 2: Delete core's `Style.kt` and the 72 NotoSans fonts**

```bash
git rm shared/features/core/presentation/src/commonMain/kotlin/be/niels/billen/whistscore/feature/core/Style.kt
git rm shared/features/core/presentation/src/commonMain/composeResources/font/*.ttf
```

- [ ] **Step 3: Grep for dead references (must be empty)**

Run: `grep -rn "feature.core.Style\|feature.core.AppTheme\|NotoSans" shared/`
Expected: no matches.

- [ ] **Step 4: Verify**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL (screenshot goldens excluded).

- [ ] **Step 5: Commit**

```bash
git add -A shared/features
git commit -m "refactor: app theme is ui.UiTheme; drop core Style and dead NotoSans fonts"
```

---

## Self-Review Notes

- Spec coverage: decisions 1–5 + scope clarification → Task 1 (fonts, theme, scope); decisions 2, 4 → Tasks 2–3; Selectable cycle resolution → Task 2; build edges (app, players, rounds; no core→ui) → Tasks 2–3; dead font removal → Task 3. No gaps.
- Review Focus line "Missing module edge" is pinned by Task 2 Step 4's compile (players/rounds) and Task 3 Step 4's compile (app). "Dead references" pinned by Task 3 Step 3's grep. "Wildcard import" pinned by Task 2 Step 1/4 (PlayerSelectionScreen compiles only with the added import). "Generated accessor names" and "Font format" are compile/runtime-level: Step 5 (test pass) exercises the fonts loading in a real compose test; accessor names fail at Task 1 Step 5's compile.
