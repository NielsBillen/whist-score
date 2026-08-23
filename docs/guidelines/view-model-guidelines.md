# ViewModel Guidelines

## MVI

**Every ViewModel follows Model-View-Intent:** the UI renders state, interactions
produce actions, the ViewModel reduces actions into new state. Unidirectional flow
makes the UI a pure function of state.

**State** is an `@Immutable` data class holding the complete view state.
**Action** is a sealed interface whose subclasses are every user intent.

```kotlin
@Immutable
data class ExampleState(
    val deviceIds: List<DeviceUrn>,
    val selectedDeviceId: DeviceUrn?,
    val isBlinking: Boolean = false,
)

sealed interface ExampleAction {
    data class SelectDevice(val deviceId: DeviceUrn) : ExampleAction
    data object Blink : ExampleAction
}
```

### State and action dispatch

The ViewModel exposes **`val state: StateFlow<State?>`** and **`fun onAction(action: Action)`**.

- **`onAction` only dispatches** to private handlers — never inline logic.
- **The `when` is exhaustive without `else`**, so the compiler flags new actions.
- **Never expose `MutableStateFlow`.** Internal mutable state is `private`.

A ViewModel may have extra action handlers where shared behaviour was extracted
(reusable table or expandable-section behaviour).

```kotlin
fun onAction(action: ExampleAction) {
    when (action) {
        is ExampleAction.SelectDevice -> selectDevice(action)
        is ExampleAction.Blink -> blink()
    }
}
```

---

## One-shot effects

### When to use an effect

**Most ViewModel output is state**, re-rendered idempotently. **An effect is for a
signal the view must *do* exactly once** — typically invoking a caller-supplied
callback. Use one only when both hold:

- The signal is one-shot and not reflected in what the UI looks like (e.g.
  dismissing a dialog after an async success, where dismissal is the caller's
  `onDismissRequest`).
- The view can act on it immediately when it arrives.

**Prefer state otherwise:**

- Anything the UI renders is state, even when transient (an error dialog carrying a
  VM-produced message).
- **Exception — self-contained modals.** A dialog opened by a UI affordance holding
  only transient local input (`NewWorktreeDialog`, `RemoveWorktreeDialog`) keeps its
  open flag, target and field contents in component-local `remember`, not in
  `[Feature]State`. MVI governs domain state, not modal presentation: such a dialog
  needs no VM knowledge, is not part of the restore contract, and folding it in only
  grows `combine` and the action surface. Distinct from ADR-0004's tree expansion,
  which *is* VM state because it must survive restart. Once a dialog's content is
  VM-produced (an async error, server-validated data), it becomes state.
- **If handling the signal depends on rendered state that may not have caught up,
  use state plus an acknowledge action.** Locating the current project must scroll to
  a row that may not be composed yet, so `ManageProjectsViewModel` keeps
  `locateTarget` in state until the view scrolls and acknowledges with
  `ManageProjectsAction.LocateHandled`.

### Declaring and emitting

**A sealed `[Feature]Effect` in its own file in `presentation/`.** The ViewModel owns
a private `MutableSharedFlow` and exposes a `SharedFlow`, mirroring the state rule.
**`extraBufferCapacity = 1` is standard**, so `emit` doesn't suspend while the
collector is briefly busy.

```kotlin
private val _effects = MutableSharedFlow<ExampleEffect>(extraBufferCapacity = 1)
val effects: SharedFlow<ExampleEffect> = _effects.asSharedFlow()
```

**Delivery is not guaranteed:** `replay = 0`, and the view only collects while
composed, so an effect emitted with nobody collecting is dropped, not queued. **Only
emit in reaction to an action from a live view.** A signal that may fire when the
view is gone must be state.

### Collecting in the view

**The internal `state`/`onAction` overload takes the effects flow as a parameter,
defaulting to `emptyFlow()`**, and collects it with the shared `CollectEffects`
helper; the public ViewModel-injecting overload passes `viewModel.effects`. This
keeps effect handling next to the UI state it manipulates (a `FocusRequester`, a
`LazyListState`, a snackbar host) and testable without Koin or a ViewModel, while the
default keeps previews and rendering-only tests unaffected.

`CollectEffects` owns the `rememberUpdatedState` + `LaunchedEffect(effects)` plumbing
so each call site keeps only its exhaustive `when`:

```kotlin
@Composable
fun <Effect> CollectEffects(effects: Flow<Effect>, onEffect: (Effect) -> Unit) {
    val currentOnEffect by rememberUpdatedState(onEffect)
    LaunchedEffect(effects) { effects.collect { currentOnEffect(it) } }
}

@Composable
internal fun ExampleView(
    state: ExampleState,
    onAction: (ExampleAction) -> Unit,
    onDismissRequest: () -> Unit,
    effects: Flow<ExampleEffect> = emptyFlow(),
) {
    CollectEffects(effects) { effect ->
        when (effect) {
            is ExampleEffect.Dismiss -> onDismissRequest()
        }
    }
}
```

- Referencing a hoisted callback directly inside the `when` is safe —
  `CollectEffects` calls the latest lambda without restarting.
- **The `when` is exhaustive without `else`**, like actions.
- **An effect targeting UI the triggering state change is about to add races
  recomposition** — model that as state plus an acknowledge action.

---

## Building view state

### `stateWhileSubscribed`

**Every exposed `StateFlow` is shared with `stateWhileSubscribed(initialValue)`** from
**`:core:presentation`** (`be.nielsbillen.aid.core`) — never a hand-written `stateIn`. It takes
the ViewModel as a **context parameter**, so the scope (`viewModelScope`) and the 5-second
`WhileSubscribed` grace are its decision, not the call site's, and no ViewModel carries its own
copy of that timeout.

Join several upstreams with `combine(…)` first, and pick the initial value:

- **Main view state → `null`.** The type becomes `StateFlow<State?>` and the composable
  early-returns until all upstreams emitted.
- **A real empty value** where one exists (`WorkspaceState.Empty`,
  `NotificationCenterState.EMPTY`), so the view never renders around a null.
- **Derived flags/scalars gating UI → a conservative default** ("can't until proven
  otherwise"), so a transient pre-subscription state never enables risky UI.

```kotlin
val state: StateFlow<WorkspaceState> =
    combine(
        projectRepository.projects(),
        worktreeRepository.worktrees(),
        workspaceRepository.openWorktrees(),
        activation.history,
        expandedProjects,
        blockedRemoval,
        ::makeState,
    ).stateWhileSubscribed(initialValue = WorkspaceState.Empty)

val unreadCount: StateFlow<Int> = repository.unreadCount().stateWhileSubscribed(initialValue = 0)
```

kotlinx types `combine` up to five flows; `:core:domain` adds the six-, seven- and eight-flow
overloads — the six-flow one is what the example above uses.

An upstream-typed variant that computes the initial value synchronously from each `.value` (so the
state needs no initial value and can never be null) is only possible when **every** upstream is a
`StateFlow`. No screen is in that position — repository reads are cold `Flow`s by design — so no
such helper exists. Add one when a ViewModel appears that combines nothing but `StateFlow`s.

### The `makeState` free function

**The transform passed to `combine` is a free function, defined outside the class
body**, so it cannot accidentally depend on ViewModel internals. State derivation
stays pure and independently testable.

```kotlin
private fun makeState(
    paneState: ExamplePaneState,
    devices: Map<DeviceUrn, Device>,
    isBlinking: Boolean,
) = ExampleState(
    deviceIds = devices.keys.toList(),
    selectedDeviceId = paneState.selectedDeviceId,
    isBlinking = isBlinking,
)
```

---

## Coroutine scopes

| Scope | Use for |
|---|---|
| **`viewModelScope`** | work that should be cancelled with the ViewModel — transient UI effects, animations, one-shot observations |
| **`backgroundScope`** (constructor-injected) | work that must survive ViewModel destruction — e.g. a repository write when the user closes the pane mid-save |

`backgroundScope` is a Koin singleton with a `SupervisorJob`, so one failure doesn't
cancel others. **Errors in `backgroundScope.launch` don't propagate** — handle them
inside the launch if the user must be notified.

---

## Dependency injection

**Register with `viewModelOf(::ExampleViewModel)`** — never as a `single`. Koin
resolves repositories and `backgroundScope` from the graph; call-site parameters
(`paneId`) come from the call site.

---

## Using ViewModels in composables

**The public entry composable takes no state arguments:** it injects its ViewModel as
a default parameter and delegates to an internal, stateless overload. The shell never
threads a screen's state down to it, and **a composable never reads a repository
directly** — the ViewModel is the only thing the UI talks to.

**Always `koinViewModel()` for a ViewModel**, never `koinInject()`: `koinViewModel`
resolves from the nearest `LocalViewModelStoreOwner`, so the instance is cached per
owner and its `viewModelScope` is cancelled when that owner clears. `koinInject()`
returns a raw graph instance with no owner, so the scope never clears and the
instance leaks across screens. Pass call-site parameters with
`koinViewModel(parameters = { parametersOf(paneId) })`.

**Collect with `collectAsStateWithLifecycle()`**, not `collectAsState()`: it stops
collecting when the lifecycle is inactive, which together with `stateWhileSubscribed`
stops the upstreams too, and keeps the codebase consistent across targets.
**When state is `null`, render nothing — early return.**

```kotlin
// Injects the ViewModel and delegates
@Composable
internal fun ExampleView(
    paneId: PaneUrn,
    viewModel: ExampleViewModel = koinViewModel(parameters = { parametersOf(paneId) }),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value ?: return
    ExampleView(state = state, onAction = viewModel::onAction, effects = viewModel.effects)
}

// Pure function of state — previewable and testable without a ViewModel
@Composable
internal fun ExampleView(
    state: ExampleState,
    onAction: (ExampleAction) -> Unit,
    modifier: Modifier = Modifier,
)
```

---

## Lifecycle

**No global state in ViewModels.** They are scoped to their `ViewModelStoreOwner`;
global application state belongs in repositories or other DI singletons.

**Each `koinViewModel` call resolves from the nearest `LocalViewModelStoreOwner`**,
whose `ViewModelStore` caches instances — composables under the same owner share one
instance, composables under different owners each get their own. Clearing the store
destroys them (`viewModelScope` cancelled, `onCleared()` called).

**Scoping to a pane:** each pane gets its own owner keyed by `PaneUrn`, so removing
the pane clears the store and destroys its ViewModels.

```kotlin
val viewModelStoreOwner = rememberViewModelStoreOwner(id)
CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
    paneView(id)
}
```

---

## Testing ViewModels

**Setup:** kotest `FreeSpec` with `coroutineTestScope = true`; override
`Dispatchers.Main` with `StandardTestDispatcher()` to control `viewModelScope`; pass
kotest's `backgroundScope` to the ViewModel.

**Fixture:** fakes/spies for dependencies, the ViewModel, and a Turbine receiver per
`StateFlow` under test.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleViewModelTest : FreeSpec() {

    abstract inner class Fixture(initialPaneState: ExamplePaneState, backgroundScope: CoroutineScope) {
        val paneId = PaneUrn(1u)
        val paneStateRepository = FakePaneStateRepository(mapOf(paneId to initialPaneState))
        val deviceRepository = spyk(FakeDeviceRepository())
        val viewModel = ExampleViewModel(paneId, paneStateRepository, deviceRepository, backgroundScope)
        abstract val viewState: ReceiveTurbine<ExampleState>
    }

    suspend fun TestScope.withFixture(
        initialPaneState: ExamplePaneState = ExamplePaneState(),
        block: suspend Fixture.() -> Unit,
    ) {
        turbineScope {
            object : Fixture(initialPaneState, backgroundScope) {
                override val viewState by lazy { viewModel.state.filterNotNull().testIn(backgroundScope) }
            }.block()
        }
    }

    init {
        coroutineTestScope = true
        beforeSpec { Dispatchers.setMain(StandardTestDispatcher()) }
        afterSpec { Dispatchers.resetMain() }
    }
}
```

**State emission:** `awaitItem()` to assert, `skipItems(n)` to skip.
**`expectNoEvents()` only checks that instant** — to verify nothing is emitted over a
period, emit something after it and `awaitItem()`; an unexpected emission in between
fails the test.

**`backgroundScope` work:** `testCoroutineScheduler.runCurrent()`, then `coVerify`.
**Time in `viewModelScope`:** `testCoroutineScheduler.advanceTimeBy(…)`.

```kotlin
"set name" {
    withFixture {
        coEvery { deviceRepository.setName(any(), any()) } returns true
        viewModel.onAction(ExampleAction.SetName(deviceId, "Foo"))
        testCoroutineScheduler.runCurrent()
        coVerify { deviceRepository.setName(listOf(deviceId), "Foo") }
    }
}
```

**Effects:** add a Turbine receiver **eagerly, not `by lazy`** — with `replay = 0`, an
effect emitted before the turbine subscribes is dropped and `awaitItem()` hangs.

```kotlin
override val effectsTurbine = viewModel.effects.testIn(backgroundScope) // eager
```

**To test the view's *handling* of an effect**, compose the internal overload with a
`MutableSharedFlow` and emit from the test:

```kotlin
runComposeUiTest {
    setContent { ExampleView(state = state, onAction = {}, onDismissRequest = onDismissRequest, effects = effects) }
    effects.tryEmit(ExampleEffect.Dismiss)
    waitForIdle()
    verify { onDismissRequest() }
}
```
