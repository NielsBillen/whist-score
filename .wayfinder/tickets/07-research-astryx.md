wayfinder:research

## Question

Determine whether the Astryx design system (`@astryxdesign/core`, React 19 + StyleX, Meta) can be used in this Kotlin Compose Multiplatform app, or what the realistic UI-rebuild path is.

Read the Astryx README and docs (repo at `~/workspace/astryx`, docs at https://astryx.atmeta.com):

Find out:
- What stack Astryx is built on. It ships React components + StyleX — this app is Compose Multiplatform (Android/iOS/Web/Desktop), which has no DOM, no React, and no StyleX runtime.
- Whether Astryx exposes any non-React artifacts (tokens, design system docs, color/spacing tokens, component specs) that a Compose UI can adopt without importing the React components.
- Whether there is any official Compose / Kotlin / Wasm bridge to Astryx. (Likely none.)

Output: an honest verdict. Almost certainly Astryx cannot be consumed as a React library here, but the research should decide whether its *tokens/spec* can inform a Compose theme, and whether a lighter design system (or hand-authored tokens mirroring Astryx's design language) is the correct path. This is the key gate for the "Migrate UI to Astryx" ticket.
