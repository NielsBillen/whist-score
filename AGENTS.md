# AGENTS.md

## What this is

A Kotlin **Compose Multiplatform Desktop** app to easily track score across games.

## Layered module architecture (lasting convention)

Each functional component of the app is its own module tree; **within a
component, each architectural layer is a separate submodule** named `data`,
`domain`, or `presentation`. Feature components live under `features/<name>/`
(path `:features:<name>:<layer>`); the cross-cutting kernel lives under `core/`.
Package names carry the feature but **never the layer** — see
docs/guidelines/code-guidelines.md § "Modules & packages".

Layer dependency rules:

- `domain` depends on nothing (no other layer).
- `data` may depend on `domain`.
- `presentation` may depend on `domain`, and **must NOT** depend on `data`.

## Module map & DAG

`settings.gradle.kts` lists every module.

A feature is `features/<name>/{domain,data,presentation}` — ports+model, adapters, screen+
ViewModel — plus a `testfixtures` sibling per layer holding that layer's fakes and matchers.
A feature ships only the layers it has content for.

**Every fact about a module tree that you would not guess lives in that tree's own `CLAUDE.md`**
(it will be created in each feature and `core` tree as the architecture is built out). It loads
when you touch a file inside, so the detail arrives next to the code instead of in every window.
The index below is only enough to know which tree to open; open it before editing there.

**Cross-module dependency direction** (on top of the layer rules above): a module may depend
on another feature's **same** layer when that module is a shared library (e.g. shared UI components).

**Presentation reaching presentation is allowed and used** — one feature's presentation layer
composes another's composable; the shared one takes plain callbacks, never a specific action type.

## Build & test commands

`./gradlew build` compiles and tests everything, screenshot goldens excluded.

## Guidelines

Follow these as hard rules.

@docs/guidelines/comment-guidelines.md
@docs/guidelines/composable-guidelines.md
@docs/guidelines/code-guidelines.md

### Situational guidelines — not mounted

Three guidelines govern a slice of the work narrow enough that mounting them would cost
every context window a file most of them never need:

- `docs/guidelines/unit-test-guidelines.md` — writing a `*Test.kt`
- `docs/guidelines/view-model-guidelines.md` — writing a `*ViewModel.kt`
- `docs/guidelines/semantic-properties-guidelines.md` — adding or matching a Compose
  semantic property

## Agent skills

### Asking questions

Derive the answer from the code, the ADRs or the issue first; ask only what is left.

Decide the question yourself when one option wins on future-proofness, roadmap fit,
readability, testability or performance — take it and say so in one line.

**Every remaining question goes through `AskUserQuestion`, never as prose.** One question per
decision, each option carrying the trade-off that decides it, so the picker holds everything the
user needs to judge.

**This outranks any skill's own question format.** A skill is read after this file, so its format
is the freshest thing in the window when the questions get written — `grilling`'s numbered prose
with a recommendation per question is the one that keeps winning. It does not win: write the
questions it asks for, then ask them through the picker.
