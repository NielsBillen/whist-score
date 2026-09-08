# Feature-module reorganization of `shared/`

## Goal

Split the single `:shared` multiplatform module into a tree of layer submodules so that each
feature's `domain`, `data`, and `presentation` layers are separate Gradle modules, following
the layered-module convention in `AGENTS.md` / `docs/guidelines/code-guidelines.md`
("Modules & packages", "Layered modules & layer-dependency enforcement").

## Decisions (user-approved)

1. **Module paths** keep the `shared/` root: each layer module lives at
   `shared/features/<feature>/<layer>` and is named `:shared:features:<feature>:<layer>`.
   `:shared` itself remains in `settings.gradle.kts` as a shell module with no sources.
2. **DI ownership**: platform apps keep their own `startKoin` wiring. Feature modules publish
   their own small Koin modules; there is no single `appModule` composition root in `shared`.
3. **Feature breakdown**: `core`, `app`, `game`, `rounds`, `players`, `editplayers`, `addround`,
   `overview`.
4. **App shell** (navigation root) lives in a dedicated `app` feature, not in `core` and not in
   `overview`.
5. **Cross-feature presentation** may compose other features' presentation layers, but shared /
   composed-into screens take **plain callbacks**, never another feature's action type — this
   prevents cycles (e.g. `overview` does not take `AppAction` / `AppNavigation`).

## Module tree

| Module | Contents | Depends on |
|---|---|---|
| `:shared:features:core:domain` | `PlayerId`, `Player`, `Players`, `Round` sealed hierarchy + scoring, `RoundType` | — |
| `:shared:features:core:presentation` | `AppTheme`, `Style`, `Background`, `Points`, `Selectable`, theme icons, compose resources (fonts, suit SVGs) | — |
| `:shared:features:app:presentation` | `App`, `AppNavigation`, `AppViewModel`, `AppAction` | core:domain, core:presentation, game:domain, overview/addround/editplayers presentation |
| `:shared:features:game:domain` | `Game`, `GameRepository` port | core:domain |
| `:shared:features:game:data` | `DefaultGameRepository`, `GameDto`, `PlayerIdDto` | game:domain, core:domain |
| `:shared:features:players:domain` | `PlayerRepository` port | core:domain |
| `:shared:features:players:data` | `DefaultPlayerRepository`, `PlayerDto`, `PlayersDto` | players:domain, core:domain |
| `:shared:features:players:presentation` | `PlayersViewModel`, `PlayersView`, `PlayerView`, `PlayerCard` | core:domain, core:presentation, game:domain |
| `:shared:features:rounds:domain` | `Rounds` (real value type, currently commented out in `Game.kt`'s sibling file) | core:domain |
| `:shared:features:rounds:presentation` | `RoundsViewModel`, `RoundsView`, `RoundsViewAction` | core:domain, core:presentation, game:domain |
| `:shared:features:editplayers:presentation` | `EditPlayersView`, `EditPlayersViewModel`, `EditPlayersAction` | core:domain, core:presentation, players:domain |
| `:shared:features:addround:presentation` | full add-round wizard: `AddRoundAction`, `AddRoundPanel`, `AddRoundScreen` (enum), `AddRoundState`, `AddRoundView`, `AddRoundViewModel`, `RoundTypeInputScreen`, `PlayerSelectionScreen`, `SlamInputScreen`, `BidInputScreen`, `BidAchievedInput`, `SummaryScreen` | core:domain, core:presentation, game:domain |
| `:shared:features:overview:presentation` | `Overview`, `OverviewViewModel`, `OverviewAction` | core:domain, core:presentation, game:domain, players:presentation, rounds:presentation |

There is no `core:data` — nothing requires it (YAGNI). `features/core/` ships only `domain`
and `presentation`.

### Package naming

- Every module keeps package root `be.niels.billen.whistscore`.
- A feature's package is `be.niels.billen.whistscore.feature.<feature>` (singular `feature`);
  the layer is never part of the package. All three layer modules of one feature (and their
  test fixtures) share that one package.
- No two modules of one feature may contain a same-named file.

## Dependency rules

Per the layer rules in `AGENTS.md`:

- `domain` depends on nothing.
- `data` depends on `domain` only.
- `presentation` depends on `domain` only — **never** on `data`.
- Cross-feature: a feature may depend on another feature's **same** layer (shared UI
  components) or on another feature's `domain`.

Resulting DAG (arrows = "depends on"):

```
core:domain ─────────────────────────────────────────────┐
core:presentation ◄── rounds:presentation ◄──────────────┤
      ▲                       ▲   ▲                      │
      │                       │   │                      │
game:domain ─► game:data      │   │                      │
      │            │           │   │                      │
      ▼            │           │   │                      │
players:domain ◄── players:data          │              │
      │                                 │                │
players:presentation ◄─────────────────┤                │
      ▲                                 │                │
      │                                ▼                │
rounds:presentation ───────────────► overview:presentation
                                          ▲
      addround:presentation ─────────────┤
      editplayers:presentation ──────────┤
                                          │
app:presentation ◄───────────────────────┘
      (depends on: core:domain, core:presentation, game:domain,
       overview:presentation, addround:presentation, editplayers:presentation)
```

Apps (`androidApp`, `desktopApp`, `webApp`) depend on:
- `:shared:features:app:presentation` (the shell; transitively pulls the other presentation
  modules, core modules, and the domain modules)
- `:shared:features:game:data` and `:shared:features:players:data` **explicitly** (presentation
  must not depend on data, so the adapters are wired at the app level)
- their platform `PlatformModule` (see DI below)

## DI

- `game:data` exposes `gameDataModule`: `singleOf(::DefaultGameRepository).bind<GameRepository>()`.
- `players:data` exposes `playersDataModule`: `singleOf(::DefaultPlayerRepository).bind<PlayerRepository>()`.
- Each presentation module exposes a Koin module of its own ViewModels
  (`appModule` for `app`, `overviewModule`, `addRoundModule`, `editPlayersModule`,
  `playersModule`, `roundsModule`).
- The per-platform `platformModule` (expect/actual `Settings` binding — `SharedPreferencesSettings`
  on android, `PreferencesSettings` on jvm, `StorageSettings` on js/wasmJs) moves from
  `shared` into each platform app's own sources.
- Apps call `startKoin(listOf(platformModule, gameDataModule, playersDataModule,
  appModule, overviewModule, addRoundModule, editPlayersModule, playersModule, roundsModule))`.
- `AppTest` (jvmTest) keeps its own hand-built Koin module; it only gains new import paths.

## Platform apps

- `androidApp`, `desktopApp`, `webApp`: replace `implementation(project(":shared"))` with the
  explicit feature-module dependencies listed above; update imports from
  `be.niels.billen.whistscore.di.appModule` to the per-module Koin modules, and
  `be.niels.billen.whistscore.presentation.app.App` to the `app` feature's package.
- `iosApp`: the iOS target in `shared/build.gradle.kts` is currently commented out;
  `Platform.ios.kt` and `MainViewController.kt` move to `iosApp` sources (no functional change).

## Build mechanics

- `settings.gradle.kts` includes every new module; `:shared` stays included as a shell.
- `shared/build.gradle.kts` becomes a shell module: same plugins (multiplatform, compose,
  serialization, kotest, ksp) but no source sets / no code.
- Each layer module gets its own `build.gradle.kts` with the same targets as today
  (android, jvm, js, wasmJs) and layer-scoped dependencies.
- `gradle/libs.versions.toml` is unchanged (versions already centralized there).
- Success criterion: `./gradlew build` is green (compiles all targets + all tests).

## Behaviour changes (intentional, minimal)

1. `Rounds.kt` in `rounds:domain` becomes a **real** `Rounds` value type (the currently
   commented-out code) wrapping a `List<Round>` with `removeAt`, `+`, `score`, and `isEmpty`
   semantics; `Game` delegates round operations to it. `Game`'s public API is unchanged.
2. `Overview`'s public composable takes plain callbacks (`onResetGame: () -> Unit`,
   `onAddRound: () -> Unit`) instead of `AppAction` / `AppNavigation` — this is what breaks the
   app↔overview dependency cycle.
3. `AppViewModel` / `App` keep their current behaviour; only their package and imports change.

## Explicitly out of scope

- No new features, no new behaviour, no UI changes.
- No changes to the iOS target (still commented out).
- No changes to `gradle/libs.versions.toml` dependency versions.
- No introduction of `mockk` or other new test dependencies.

## Test strategy

- Existing tests move with their code: `RoundTest` → `core:domain` test source set
  (it tests `Round` scoring), `FakeGameRepository` / `FakePlayerRepository` → the respective
  feature's `domain` test source sets (per AGENTS.md: fakes live in the owning feature's
  test fixtures), `AppTest` → `app:presentation` test source set.
- All tests keep kotest `FreeSpec` style.
- `./gradlew build` must pass with all tests green.
