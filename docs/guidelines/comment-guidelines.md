# Comment guidelines

Comments persist the *why* that the code and tests cannot. Swept once per branch by
the `comment-sweep` skill, before review.

- **Comment only what the code cannot say.** If the name, type, or signature
  already conveys it, add nothing. A comment paraphrasing the code is noise —
  delete it.
- **Record *why*, never *what* or *how*.** A comment earns its place by recording
  intent, a constraint, a trade-off, or a non-obvious gotcha.
- **One line wherever it fits.** Terse and current beats thorough and stale.
- **KDoc only for genuinely non-obvious public contracts**, not routine members.
- **No commented-out code, no dead docs** — git keeps history. Mark deliberate
  gaps with `TODO`.
- **Stay inside the layer.** A domain-model comment must not explain how the UI
  consumes it or how the data layer serializes it; cross-cutting rationale
  belongs in the consuming layer, or in commit/ADR history.
