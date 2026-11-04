# Composable guidelines

**Switch content through dedicated composables.** A composable that selects
between contents — a `when`/`if` over state — delegates each branch to its own
named composable instead of inlining it. The parent stays a thin dispatcher; each
child owns one rendering responsibility. `AddRoundView` dispatches its screens to
`AddRoundScreens` and its button bar to `ButtonRow`.
