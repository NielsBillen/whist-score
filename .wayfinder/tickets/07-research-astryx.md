wayfinder:research
status: closed

## Question

Determine whether the Astryx design system (`@astryxdesign/core`, React 19 + StyleX, Meta) can be used in this Kotlin Compose Multiplatform app, or what the realistic UI-rebuild path is.

Read the Astryx README and docs (repo at `~/workspace/astryx`, docs at https://astryx.atmeta.com):

Find out:
- What stack Astryx is built on. It ships React components + StyleX — this app is Compose Multiplatform (Android/iOS/Web/Desktop), which has no DOM, no React, and no StyleX runtime.
- Whether Astryx exposes any non-React artifacts (tokens, design system docs, color/spacing tokens, component specs) that a Compose UI can adopt without importing the React components.
- Whether there is any official Compose / Kotlin / Wasm bridge to Astryx. (Likely none.)

Output: an honest verdict. Almost certainly Astryx cannot be consumed as a React library here, but the research should decide whether its *tokens/spec* can inform a Compose theme, and whether a lighter design system (or hand-authored tokens mirroring Astryx's design language) is the correct path. This is the key gate for the "Migrate UI to Astryx" ticket.

## Resolution

Done — findings at [../research/astryx-research.md](../research/astryx-research.md). Verdict: **Astryx cannot be consumed as a library here, but its tokens can drive the Compose theme.** It's React 19 + StyleX with no DOM/Compose/StyleX surface and no official Kotlin/Compose/Wasm bridge (packages are core/cli/build/themes), so `@astryxdesign/core` is off the table. But the token system is plain data — `tokens.stylex.ts` (spacing/radius/type/duration scales), `defineTheme.ts`'s `tokenDefaults` (the full token vocabulary), and `neutralTheme.ts` (concrete light/dark values) — and Astryx ships `resolveThemeTokens(theme, {mode})` in `tokens.ts`, explicitly for "plain JS theme objects for other styling libraries," which returns every token as concrete values. The correct path for ticket 14 is a **hand-authored Compose theme** (`ColorScheme`/`Dimensions`/`Shapes`/`Typography`/motion) mirroring the `neutral` theme token-for-token, not a dependency and not a third-party lighter design system. Only shadows (CSS box-shadow stacks) and the Figtree font need a deliberate Compose-side decision.
