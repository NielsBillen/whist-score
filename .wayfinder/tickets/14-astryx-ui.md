blocked-by: 07-research-astryx.md
wayfinder:task

## Question

Rebuild the UI on the chosen design system, applying the Astryx design language to this app's Compose screens.

Details:
- Depends on `07-research-astryx.md`, which decides whether Astryx can be consumed here. The strong likelihood is that the Astryx React components cannot run in a Compose (non-DOM, no React) app — the research should instead settle whether to adopt Astryx's tokens/design language via a hand-authored or lighter Compose theme, or a different design system.
- `presentation/Style.kt` holds the current styling (`AppTheme`, `Style.Dimensions`, padding/radius). The redesign should replace or feed into this.
- Covers the existing screens: Overview, Add Round flow, Edit Players, Players, Rounds, (future) Stats.
- Should sit on top of nav3 (`13-nav3-integration.md`) already handling navigation.
