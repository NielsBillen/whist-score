# Composable guidelines

**Switch content through dedicated composables.** A composable that selects
between contents — a `when`/`if` over state — delegates each branch to its own
named composable instead of inlining it. The parent stays a thin dispatcher; each
child owns one rendering responsibility. `WorkspaceContent` dispatches
`Empty`/`Loaded`; `LoadedWorkspace` dispatches active-open-worktree vs nothing-open.
