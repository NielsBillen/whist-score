# AGENTS.md

## What this is

A Kotlin **Compose Multiplatform Desktop** app to easily track score across games.

## Styling

The app uses the **astryx Y2K theme (dark mode)** — its palette and Poppins
typography are mapped onto Material 3 roles in `Style`
(`shared/features/ui/presentation`).

## Layered module architecture (lasting convention)

Each functional component of the app is its own module tree; **within a
component, each architectural layer is a separate submodule** named `data`,
`domain`, or `presentation`. Feature components live under `shared/features/<name>/`
(path `:shared:features:<name>:<layer>`); the cross-cutting kernel lives under
`shared/features/core/`.
Package names carry the feature but **never the layer** — see
docs/guidelines/code-guidelines.md § "Modules & packages".

Layer dependency rules:

- `domain` depends on nothing (no other layer).
- `data` may depend on `domain`.
- `presentation` may depend on `domain`, and **must NOT** depend on `data`.

## Module map & DAG

`settings.gradle.kts` lists every module.

A feature is `shared/features/<name>/{domain,data,presentation}` — ports+model, adapters,
screen+ViewModel. A feature ships only the layers it has content for.
A repository's `FakeX` lives in the owning feature's `domain` module (`commonMain`),
so other features' tests can import it — fakes have no `testfixtures` module of their own.

**Cross-module dependency direction** (on top of the layer rules above): a module may depend
on another feature's **same** layer when that module is a shared library (e.g. shared UI components).

**Presentation reaching presentation is allowed and used** — one feature's presentation layer
composes another's composable; the shared one takes plain callbacks, never a specific action type.

## Build & test commands

`./gradlew build` compiles and tests everything.

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
