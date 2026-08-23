# Code Guidelines

**Only two rules scale with reach** — *Name length scales with reach*, and *Never expose a
mutable type*, whose subject is the boundary itself. Every other rule holds at every
visibility, when writing and when reviewing: read "signature" as "any declared type", and
hold a `private` property or an `internal` helper's parameter to the API's standard.

## Before writing code

Stop at the first rung that holds:

1. **Does this need to exist?** Speculative need = skip it (YAGNI).
2. **Already in this codebase?** Reuse it; if unreachable, **extract** it.
3. **Standard library?** Use it.
4. **Native platform feature?** Use it.
5. **Already-installed dependency?** Use it — never add a new one.
6. **Can it be one line?** One line.
7. **Only then:** the minimum code that works.

## Clean code reads like prose

Code is read far more than written: plain, direct, top to bottom. When shorter
fights clearer, choose clearer.

- **One level of abstraction per function.** A high-level step reads as a sentence
  of named calls, never a mix of policy and mechanics. Push detail into a named
  helper one level down.
- **No cleverness for its own sake.** The reader's time costs more than a saved line.

## Immutable collections everywhere

**Every declared type uses `ImmutableList` / `ImmutableSet` / `ImmutableMap` (or the
`Persistent*` forms)** (`kotlinx.collections.immutable`) — never bare `List` / `Set` /
`Map` / `Mutable*`. Build with `persistentListOf(…)` / `persistentHashSetOf(…)` /
`.toImmutableList()`.

Scope is what a type is *held* by and how far it *travels*, never how visible it is:

- **Every property and constructor property is immutable, at every visibility.** A
  `private val wireNames: ImmutableSEt<String>` takes `persistentHashSetOf` — it is held for the
  object's lifetime, it is what Compose stability reads, and `private` does not make a
  wrong type right for the next person to edit that file.
- **Every function signature that leaves its file is immutable**, `internal` included.
- **A file-`private` function may take and return read-only `List` / `Set` / `Map`** —
  never a `Mutable*`. Its whole audience is one file, and a persistent copy per call on a
  poll loop buys nothing a reader can see.
- **A variable inside a function body may be anything**, `mutableListOf` included.

## Never expose a mutable type

**No declaration visible outside its owner has a mutable type** — collections,
flows, builders, arrays, anything named `Mutable*`. A caller that can mutate your
state is an unplanned second writer. Keep the mutable thing `private` and publish a
read-only view; when a caller must cause a change, give it a named method saying
what the change means (`exitWith(code)`, `record(urn)`), not an assignable handle.

```kotlin
private val devices = MutableStateFlow<ImmutableList<Device>>(persistentListOf())
val state: StateFlow<ImmutableList<Device>> = devices.asStateFlow()

private val seen = mutableListOf<Urn>()
val visited: ImmutableList<Urn> get() = seen.toImmutableList()
```

Holds in **test fixtures too** — a fake handing out its mutable innards teaches the
next reader that it is fine.

## Persistence: ports in domain, adapter in data

Data is stored **using multiplatform-settings**, hidden behind repository *ports*.

- **The interface and its return types live in a `domain` module** — the owning
  feature's. Consumers stand it in with a `FakeX` from `testfixtures` (`mockk` is for
  narrow callbacks, not repositories).
- **The multiplatform settngs adapter is confined to `data`, all `internal`** — 
  Domain models carry no annotations to serialize; the repository maps DTO ↔ domain at the boundary.
- **Reads return a cold `Flow`** collections as `Flow<ImmutableList<T>>`; **writes are `suspend`**.

```kotlin
fun worktrees(): Flow<ImmutableMap<WorktreeId, Worktree>>
suspend fun upsert(worktree: Worktree)
```

## kotest for all unit tests

**All test files use `FreeSpec` with the `init { }` body** — never the
lambda-constructor form — and kotest assertions (`shouldBe`, `shouldThrow`, …), on
the JUnit 5 platform (wired in the root `subprojects` block).

```kotlin
class SessionIdTest : FreeSpec() {
    init {
        "SessionId" - {
            "round-trips through its string form" {
                val id = SessionId(UUID.randomUUID())
                SessionId.parse(id.value.toString()) shouldBe id
            }
        }
    }
}
```

Compose UI uses the `androidx.compose.ui.test` headless harness, with kotest
assertions inside it where practical.

## Build in vertical slices

**Implement one behaviour end-to-end through every layer it touches before starting
the next.** Never build a layer to completion first — that commits you to
interfaces before anything real exercises them and hides integration gaps.

- A slice is the smallest change producing working, demonstrable behaviour.
- Finish and verify a slice (tests green through the stack) before the next.
- Vertical means the *order of work*, not permission to skip the structure.

## TDD is mandatory

**Red → green → refactor, every ticket.** Failing test first, watch it fail, then
the minimum code to pass. No production code without a test that demanded it.
Follow the **`/tdd`** skill.

## Tight functions

Short, single-responsibility, strongly-typed — no God-functions. **If a function
needs a comment to explain its second half, it is two functions.** Use the
**`/tight-function`** skill when writing or rewriting any function.

- **Method reference over a forwarding lambda:** `also(Files::createDirectories)`,
  `.map(::transform)`.
- **Don't duplicate a return type the body already spells out.** Drop the
  annotation when the body is a constructor call of the same type:
  `internal fun Worktree.toEntity() = WorktreeEntity(…)`. Keep the explicit type
  when the body is a call chain, a conditional, or anything whose type isn't
  visible where the function is read.

## A one-argument function that acts on its argument is an extension

**When a free function takes one parameter and its whole job is that parameter,
make it an extension on it.** The call site then reads left to right in the order
the work happens, and the name stops repeating the type:

```kotlin
// no — the type is said twice, and the call reads inside-out
fun flattenDescription(description: Description): String
fun descriptionBlocks(description: Description): ImmutableList<DescriptionBlock>
flattenDescription(ticket.description)

// yes
fun Description.flatten(): String
fun Description.toBlocks(): ImmutableList<DescriptionBlock>
ticket.description.flatten()
```

The condition is *acts on*, not merely *takes*. A function whose argument is one
input among several it could have had — a factory, a parser reading a `String`
into something unrelated, a predicate over two things where one happens to be
defaulted — stays free. So does one whose receiver would read as the wrong subject:
`flattenDescription` becomes `Description.flatten()`, but a `render(theme: Theme)`
that draws a screen does not become `Theme.render()`.

Naming follows the receiver, so `toXxx()` for a projection into another type and a
plain verb otherwise.

## Name arguments at every call site that takes two or more

`makeTextStyle(42, 52, weight)` hides which `Int` is which and silently misbehaves
if parameters are reordered — it still compiles. **Name them:**
`makeTextStyle(size = 42, lineHeight = 52, weight = FontWeight.Bold)`.

**Name all of them or none of them.** A call that names its later arguments and
leaves the first positional is the worst of both — the reader now assumes an
unnamed argument is unnamed *for a reason*, and the one value with no label is the
one they have to go and look up:

```kotlin
// no — style and color are labelled, the string is not
Text(stringResource(Res.string.failed, command, exitCode), style = TextStyles.label, color = colors.error)
// yes
Text(text = stringResource(Res.string.failed, command, exitCode), style = TextStyles.label, color = colors.error)
```

A trailing lambda stays exempt, since it has no name to give.

Three exemptions, and nothing else:

- **Self-evident stdlib calls** — `listOf(a, b)`, `mapOf(k to v)`, `a to b`,
  `require(cond) { … }`, single-field `copy(…)`.
- **A single trailing lambda**, which has no name to give.
- **Java callees**, which Kotlin cannot name. Beyond a couple of arguments, add a
  Kotlin wrapper with real parameter names (`Graphics.fillRounded` in
  `TerminalScrollBarUI.kt`) or bind each argument to a named constant
  (`scrollWheelUp` in `JediTermSessionTest.kt`).

## Name length scales with reach

Descriptiveness is proportional to how far from its declaration a name is used.

| Reach | Naming |
|---|---|
| Locals | May be short — the function is the context (`i`, `it`, `val n`). |
| `private` / `internal` | Moderately descriptive; read next to their definition. |
| Public API | Long, fully spelled out, **no abbreviation**: `MergeRequestReviewScreen` not `MrReviewScreen`; `sourceRepositoryPath` not `srcRepoPath`. |

Ubiquitous domain acronyms (`MR` in prose, `id`, `URL`) stay; the rule targets
invented abbreviations crossing a module/type boundary.

## Strongly type over primitives

- **`Duration`** for spans/timeouts, never raw millis; **`Instant`**
  (`kotlin.time`) for a point in time.
- **`Set`** for unordered/unique, immutable **`List`** for ordered, **`Map`** for
  key/value.
- **Hash over linked unless order matters.** Default to `persistentHashMapOf` /
  `persistentHashSetOf`; use ordered `persistentMapOf` / `persistentSetOf` only
  when iteration order is part of the contract.
- **Value classes** (`@JvmInline value class Pid(val value: Long)`) or small data
  classes for ids, ports, tokens, pids. Invariants go in the type's `init`
  (`require(value >= 0)`), not at every call site.
- **Enums / sealed types** for a fixed set of values, over a `String` whose legal
  contents a caller must know.
- **A `Pair` or `Triple` in a declared type is a named type instead** — as a parameter as
  much as a return. `Pair<String, String>` tells the reader nothing about which is which;
  a `PipelineQuery` with one arm per real question tells them everything. Destructuring a
  stdlib call's own `Pair` inside a function body is fine — it never gets a name because it
  never gets a declaration.

## Enforce representable states 

Force correct code by avoiding clients to create unreprentable states. Every combination
of a function should be a representable one. (E.g. Two nullable
parameters offer four; when three mean something and the fourth means nothing, that fourth
is a case every call site rules out by hand — until one does not, and the compiler says
nothing. Model the real cases and the impossible one stops existing.). Model the real cases and
the impossible one stops existing.

## Factory functions in a companion object

**Named `operator fun invoke(…)` (call site `XXX(…)`) or `fun of(…)`** — never an
ad-hoc `fromWire` / `parse` / `create`. **Reach for a factory only when a plain
constructor can't do the job**: pre-computation, validation that can fail, or a
lookup between the caller's argument and the constructor's parameters.


## Avoid reflection

**No runtime reflection** — `KClass`/`Class` token comparisons, `::class`-keyed
maps, member lookup by name. It defeats R8/ProGuard minification and moves errors
to runtime. Model the distinction with a sealed type or an **enum discriminator**
(filter by a `NotificationSourceType`, not by `KClass<out NotificationSource>`).

## Exhaustive `when` over sealed types and enums

**One arm per subclass/entry, no `else`.** A new variant then fails compile at
every such `when` instead of being swallowed by a default. Don't route around it
with `as?` either.

A `when` over an open type (a `String` discriminator read from the DB) is not
exhaustive and still needs an `else`.

## Shape the layers, don't clip them

`Modifier.clip` allocates a graphics layer and forces every descendant through it —
**use it only when content genuinely must be cut off** (an image, a scrolling
child). Rounded corners are not that case: `background(color, shape)`,
`border(width, color, shape)` and `Modifier.interactiveOverlay(interaction, shape = …)`
all take the shape. Give each layer the shape instead of stacking square layers
behind a clip.

An overlay rounded *tighter* than the control it sits in is the trap a clip hides.

## Anything a `Modifier` can carry travels on the `Modifier`

**A composable takes what it draws as parameters, and everything else as its
`modifier`.** A tag, a size, a padding, a click: the caller expresses those through
`Modifier`, and the composable applies the one it was handed rather than growing a
parameter per decoration. A bespoke parameter shadowing a modifier splits one question
over two places and leaves the caller unable to add anything the author did not foresee.

The parameter is named `modifier`, defaults to `Modifier`, and follows every required
parameter — the platform convention, and what makes the default worth having.

## testTag on every interactive node

**Every interactive / user-facing composable carries a stable
`Modifier.testTag("…")`** — the same tags feed compose-ui-test and (F5) the MCP
live driver, so an untagged node is invisible to both.

**A tag is one capitalised prose name: first letter upper case, words separated by
single spaces, nothing else.** No dots, no mashed words, no owner prefix chain, and
never a string built from parts or interpolated. A later word may carry an acronym's
capitals (`"Base URL field"`). Tags come in two tiers:

- an **anchor** — a screen root, dialog root, list root, row or section — carries a
  name that identifies it app-wide (`"Workspace screen"`, `"Commit dialog"`,
  `"Backlog row"`);
- a **leaf** — a control inside an anchor — carries its kind only
  (`"Confirm button"`, `"Message field"`, `"Changed files"`), and the matcher supplies
  the anchor. A leaf several hosts render gets one kind-named constant, not one per
  host.

**Instance identity never enters a tag** — a typed semantic property carries it
(`docs/guidelines/semantic-properties-guidelines.md`).

**Every tag literal lives in exactly one place: a `<Feature>Tags` object in the
presentation module that renders the node.** An inline `testTag("literal")` is a build
failure.

**Tests never name a tag.** Each presentation module's `testfixtures` sibling
publishes an `isXxx()` matcher per kind, a `hasXxx(id)` matcher per identity property,
and an `onXxx(useUnmergedTree: Boolean = false)` lookup per addressable node; anchor a
leaf with `onDescendant` from `:core:domain:testfixtures`.

## Dependency injection: Koin (DSL)

**Wire singletons with Koin's DSL** (`module { single { … } }`), not a hand-rolled
composition root. **Recover compile-time-ish safety with
a resolution test** that resolves every binding. **Keep configuration (paths,
ports) in its own module** so tests swap it without touching the wiring.
**Composables receive dependencies as parameters or via `koinInject()`** — never a
`CompositionLocal` for DI.

**Prefer the constructor-reference DSL** (`singleOf(::Foo)`, `factoryOf(::Foo)`)
over a hand-wired lambda: Koin resolves each parameter by type, so adding a
constructor parameter keeps compiling, where the lambda form silently
under-supplies and breaks at resolve time. **Bind an extra supertype with the
postfix `bind`**: `singleOf(::RoomFoo).bind<FooPort>()`. The trailing options block
is for what only it can carry — a qualifier, `createdAtStart()` — and a binding
spending it on a supertype reads as though it were carrying one of those:
`singleOf(::JiraBoardDirectory) { qualifier = boardDirectoryOf(JIRA) }.bind<BoardDirectory>()`.

## A second caller decides the home

**Before writing a component into a feature module, name every feature that will
call it.** Two or more, and it belongs in the shared module both already depend on —
`:features:<thing>:presentation` for a composable, `:features:<thing>:domain` for a
port, a model, or an orchestrator with no Compose in it.

**The layer follows the component, not its callers:** a shared orchestrator two
ViewModels drive still belongs in `domain` if it names no Compose type.
`WorktreeRemover` serves both the workspace and the projects list and lives in
`:features:workspace:domain`, which owns `OpenWorktreeId`, the key the cascade takes.

**A shared composable takes callbacks, not another feature's action type** — that
is what makes it reachable from the second caller at all:

```kotlin
// yes — in :features:worktrees:presentation
@Composable
fun BlockedRemovalDialog(blocker: RemovalBlocker, onConfirm: () -> Unit, onDismiss: () -> Unit, modifier: Modifier)
// no — pins the component to one feature, so the other copies it
@Composable
internal fun BlockedRemovalDialog(blockedRemoval: BlockedRemoval, onAction: (ProjectsAction) -> Unit)
```

## Modules & packages

- **Package root `be.nielsbillen.whist-score`** — no exceptions.
- **A feature's package is `be.nielsbillen.whist-score.feature.<feature>`** — singular
  `feature`, and **the layer is never part of the package**: all three layer
  modules and their testfixtures share one package.
  Sub-packages are spent on concepts (`…feature.ui.component`), never on layers.
- **No two modules of one feature may contain a same-named file.** A package spans
  several modules, so two `Helpers.kt` would put two `HelpersKt` facades on the
  classpath and one would silently shadow the other.
- **Plural when a feature manages a collection** (`terminals`, `worktrees`,
  `projects`, `notifications`, `sessions`, `tickets`), **singular when it is a
  single place** with one instance (`workspace` — one workspace, many open
  worktrees). Don't apply plural mechanically; `workspaces` would claim something false.
- **Files with a top-level entry point / composable are PascalCase** (`Main.kt`,
  `App.kt`); `@Composable` functions are PascalCase (ktlint allows this).

## One declaration per file for declaration-only files

**A file with only type declarations and no behaviour** — data classes,
DTOs, enums, sealed hierarchies — **holds one top-level type,
named after it** (`WorktreeEntity.kt`, `WorktreeDao.kt`). Never bundle an entity
with its DAO.

**Behaviour files are exempt from the one-*type* rule:** a class with its private
helpers, or a cohesive set of related extension functions, stays together.

## One public type per file, named after the file

**`XXX.kt` publicly defines exactly one top-level type, named `XXX`** — behaviour
files included. It may keep private/internal helpers, top-level `private`/`internal`
functions, constants and `private typealias`es, but **a second public type belongs
in its own file**: `HookRouteRegistrar.kt` / `UiControlRouteRegistrar.kt`, not
bundled into `CallbackServer.kt`.

## Layered modules & layer-dependency enforcement

Every functional component splits into layer submodules named `data`, `domain`,
`presentation` (path suffix `:…:data` etc.). Features live under
`features/<component>/`, the cross-cutting kernel under `core/`.

| Layer | May depend on | Must NOT depend on |
|---|---|---|
| `domain` | — (nothing) | `data`, `presentation` |
| `data` | `domain` | `presentation` |
| `presentation` | `domain` | `data` |

## Dependency versions

**All versions live in `gradle/libs.versions.toml`.** Reference catalog aliases
(`libs.kotest.runner.junit5`); never write a raw version string in a module build script.
