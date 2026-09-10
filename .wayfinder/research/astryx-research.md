# Astryx research — can a Compose UI use it?

**Research ticket:** [Astryx UI-rebuild path research](../tickets/07-research-astryx.md) (`wayfinder:research`, AFK)
**Date:** 2026-09-10
**Sources:** local Astryx repo at `~/workspace/astryx` (offline clone) — `README.md`, `CLAUDE.md`, `packages/core/src/theme/{tokens.ts,defineTheme.ts,tokens.stylex.ts}`, `packages/themes/neutral/src/neutralTheme.ts`, `packages/cli/assets/docs/tokens.doc.dense.mjs`. Public docs at https://astryx.atmeta.com not required — the repo is authoritative and current.

## Verdict

**Astryx cannot be consumed as a library here — but its design tokens can, and should, drive the Compose theme.** The correct path for the "Migrate UI to Astryx" ticket is **not** a dependency on `@astryxdesign/core`, and **not** a third-party "lighter design system." It is a **hand-authored Compose theme** (`ColorScheme` + `Dimensions`/`Shapes` + `Typography` + motion) that mirrors Astryx's `neutral` theme token-for-token. Astryx publishes a first-party helper (`resolveThemeTokens`) that returns every token as a flat map of concrete values, explicitly documented for "plain JS theme objects for other styling libraries" — that is the extraction seam, and it makes the mirroring mechanical rather than interpretive.

## 1. Stack — React-only, no Compose surface

- Astryx is **React 19 + StyleX** (README: "built on React 19 and StyleX"; `@astryxdesign/core` has `react`/`react-dom` as peer deps). All 150+ components are React components; styles are authored in StyleX and ship as **pre-built CSS + CSS custom properties**.
- This app is Compose Multiplatform (JVM/Android/iOS/WasmJs). It has **no DOM, no React runtime, no StyleX runtime, no CSS cascade.** There is no mechanism to import `@astryxdesign/core` components or its CSS into a Compose view.
- **No official Compose / Kotlin / Wasm bridge exists.** The published packages are `core`, `cli`, `build`, `themes` (+ canary `charts`/`vega`/`richtext`, unpublished `lab`). There is no `@astryxdesign/kotlin`/`compose`/`wasm` artifact. The only "other styling library" adapters Astryx documents (`tokenVar`'s doc: "Panda, Chakra, MUI, Emotion, styled-components, UnoCSS, CSS Modules") are all **CSS-based**, not Compose. (Grep for `kotlin`/`compose`/`jetbrains` in the repo matches only blog prose and docsite content.)

So: as a *component library*, Astryx is off the table for Compose. That part of the gate is closed.

## 2. The adoptable artifact — tokens, and a first-party extraction seam

What *is* portable is Astryx's **token system** — the color/spacing/radius/typography/shadow/motion scales. They are defined as plain data, not React:

- **`packages/core/src/theme/tokens.stylex.ts`** — the default scales as concrete values:
  - `--spacing-0..--spacing-12` = 0/2/4/6/8/12/16/20/24/28/32/36/40/44/48 px
  - `--size-element-sm/md/lg` = 28/32/36 px
  - `--radius-none..full` = 0 / 4 / 8 / 12 / 28 / 9999 px
  - `--font-size-4xs..5xl` = 6/7/8/10/12/14/17/20/24/29/35/42 px (base 14, ratio 1.2)
  - `--duration-*` (ms) + `--ease-standard` (`cubic-bezier(0.24, 1, 0.4, 1)`)
- **`packages/core/src/theme/defineTheme.ts`** — `tokenDefaults` is the canonical flat list of *every* token name (color, spacing, size, border, focus, radius, shadow, duration, ease, typography, textSize, fontWeight, typeScale + domain tokens). This is the definitive vocabulary to mirror.
- **`packages/themes/neutral/src/neutralTheme.ts`** — the `neutral` theme as a plain object of concrete `[light, dark]` values: full semantic palette (`--color-background-*`, `--color-text-*`, `--color-accent`, `--color-{success,error,warning}`, border, icon), a 10-hue categorical palette (red/orange/yellow/green/teal/cyan/blue/purple/pink/gray), radius, and CSS box-shadow strings. Every value is a hex/oklch/rgba or a px/rem number.

**The extraction seam (the key fact).** `packages/core/src/theme/tokens.ts` exports:

- `resolveThemeTokens(theme, {mode})` → `Record<string, string>` of **concrete raw values**. It resolves `[light, dark]` tuples, `light-dark()`, `color-mix()`, and `var()` references so callers get concrete values, not CSS expressions. Its file header states the purpose verbatim: *"Use these helpers when code outside React hooks needs Astryx token values: build-time theme adapters, … plain JS theme objects for other styling libraries."*
- `tokenVar(name)` / `tokenVars` → the flat name→`var(--name)` map for every token.

So the migration is mechanical: run `resolveThemeTokens(neutralTheme, {mode:'light'})` and `…{mode:'dark')` (in a throwaway Node script against the local repo, or just read `neutralTheme.ts` + `tokens.stylex.ts` by hand), then transcribe the results into Compose:

| Astryx token | Compose home |
|---|---|
| `--color-background-*`, `--color-text-*`, `--color-accent`, `--color-{success,error,warning}`, `--color-border*`, `--color-on-*` | `lightColorScheme()` / `darkColorScheme()` (`primary`, `onSurface`, `surface`, `background`, `secondary`, …) |
| `--spacing-0..12`, `--size-element-*` | a `Spacing`/`Dimensions` object (Dp) |
| `--radius-*` | `Shapes` (`small`/`medium`/`large`) or a `Radius` scale (Dp) |
| `--font-size-*`, `--font-family-*`, `--font-weight-*` | `Typography` (`TextStyle` scale) |
| `--shadow-*` | elevation / custom `Shadow` (approximate the CSS box-shadow stacks) |
| `--duration-*`, `--ease-standard` | animation durations + `Easing` |

Note the two things that do **not** transfer 1:1 and need a decision:
- **Shadows** are CSS multi-layer box-shadows (drop + inset rim). Compose has no CSS shadow stack; map them to Material elevation or a small hand-tuned `drawShadow`/`Modifier.shadow`.
- **Fonts** — Astryx's `neutral` theme uses **Figtree**; font loading is the consumer's responsibility. The Compose app must bundle Figtree (or substitute) for the theme to match.
- The `light-dark()`/`color-mix()` machinery is a CSS feature; in Compose the light/dark split simply becomes the two `ColorScheme`s, and `color-mix`-derived values are already concrete in `resolveThemeTokens` output.

## 3. Recommendation for ticket 14 (Migrate UI to Astryx)

**Adopt Astryx as a design-language reference, not a dependency.** Concretely:

1. Extract the `neutral` theme's resolved token map (both modes) — one throwaway script against the local repo, or a one-time manual transcription from `neutralTheme.ts` + `tokens.stylex.ts`.
2. Author a Compose theme in the `core` layer: a `ColorScheme` pair, a `Spacing`/`Dimensions` scale, `Shapes`, a `Typography` scale, and motion constants — mirroring the Astryx values above. Keep Astryx token names as comments/keys so the mapping stays auditable.
3. Bundle the Figtree font (or pick a substitute) for the `neutral` look.
4. Rebuild the screens against that theme. The nav3 work (ticket 13) and this theming are independent and compose cleanly.

This is a **theming** effort, well inside one session once the extraction is done — not blocked on any external system. Astryx's own tokens are the source of truth; no third-party lighter design system is needed, and pulling in one would only add a dependency to reproduce values Astryx already publishes for free.

**Gate outcome for the map:** the "Migrate UI to Astryx" ticket is unblocked and re-scoped from "integrate the Astryx React library" (impossible) to "mirror Astryx's neutral-theme tokens into a hand-authored Compose theme" (straightforward). No new external dependency, no bridge to wait on.
