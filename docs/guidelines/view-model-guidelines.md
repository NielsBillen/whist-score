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

The ViewModel exposes **`val state: StateFlow<State>`** (non-null — it has an initial value
in its constructor or a known empty value) and **`fun onAction(action: Action)`**.

- **`onAction` only dispatches** to private handlers — never inline logic.
- **The `when` is exhaustive without `else`**, so the compiler flags new actions.
- **Never expose `MutableStateFlow`.** Internal mutable state is `private`.

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
  only transient local input keeps its open flag and field contents in
  component-local `remember`, not in `[Feature]State`. MVI governs domain state, not
  modal presentation: such a dialog needs no VM knowledge, is not part of the restore
  contract, and folding it in only grows state and the action surface. Once a
  dialog's content is VM-produced (an async error, validated data), it becomes state.
- **If handling the signal depends on rendered state that may not have caught up,
  use state plus an acknowledge action.** The state carries the pending target until
  the view acts on it and acknowledges with a follow-up action.

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

**The internal state-driven overload takes the effects flow as a parameter**, and
collects it with a plain `LaunchedEffect(effects) { effects.collect { … } }`; the
public ViewModel-injecting overload passes `viewModel.effects`. This keeps effect
handling next to the UI state it manipulates and testable without Koin or a ViewModel.

```kotlin
@Composable
internal fun ExampleView(
    state: ExampleState,
    onAction: (ExampleAction) -> Unit,
    effects: SharedFlow<ExampleEffect>,
) {
    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                is ExampleEffect.NextScreen -> pushScreen(effect.screen)
            }
        }
    }
}
```

- **The `when` is exhaustive without `else`**, like actions.
- **An effect targeting UI the triggering state change is about to add races
  recomposition** — model that as state plus an acknowledge action.

---

## Building view state

### Sharing upstream flows

**Every exposed `StateFlow` is a `stateIn` on `viewModelScope` with
`SharingStarted.WhileSubscribed()`**:

```kotlin
val game = gameRepository.game.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = Game.DEFAULT,
)
```

Join several upstreams with `combine(…)` first, and pick the initial value:

- **A real empty value** where one exists (`Game.DEFAULT`, `persistentListOf()`), so
  the view never renders around a null.
- **Derived flags/scalars gating UI → a conservative default** ("can't until proven
  otherwise"), so a transient pre-subscription state never enables risky UI.

```kotlin
val players = combine(gameRepository.game, playerRepository.players, ::makeState)
    .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = persistentListOf())
```

### The `makeState` free function

**The transform passed to `combine` is a free function, defined outside the class
body**, so it cannot accidentally depend on ViewModel internals. State derivation
stays pure and independently testable.

```kotlin
private fun makeState(game: Game, players: Players): PersistentList<PlayerView> { … }
```

---

## Coroutine scopes

**`viewModelScope`** is the only scope the ViewModels use today — work cancels with
the ViewModel. If a piece of work must outlive the ViewModel (e.g. a repository
write that finishes after the user navigates away), give the ViewModel a
constructor-injected scope and handle its errors inside the `launch`, since
`launch` failures do not propagate.

---

## Dependency injection

**Register with `viewModelOf(::ExampleViewModel)`** — never as a `single`. Koin
resolves the repositories from the graph; call-site parameters (e.g.
`initialState`) come from the call site.

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
instance leaks across screens.

**Collect with `collectAsState()`.** The state is non-null (it has an initial value),
so the view renders from the first emission — there is no null to early-return.

```kotlin
// Injects the ViewModel and delegates
@Composable
fun AddRoundView(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    AddRoundView(
        modifier = modifier,
        onCancel = onCancel,
        onSave = onSave,
        state = state,
        onAction = viewModel::onAction,
        effects = viewModel.effects,
    )
}

// Pure function of state — previewable and testable without a ViewModel
@Composable
internal fun AddRoundView(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
    onAction: (AddRoundAction) -> Unit,
    effects: SharedFlow<AddRoundEffect>,
    state: AddRoundState,
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

**Per-route scoping:** the app's `NavDisplay` carries
`rememberViewModelStoreNavEntryDecorator()`, so each route gets its own
`ViewModelStoreOwner` and popping the route clears the store and destroys its
ViewModels.

---

## Testing ViewModels

**Setup:** kotest `FreeSpec` with `coroutineTestScope = true`; override
`Dispatchers.Main` with `StandardTestDispatcher()` in `beforeSpec`/`afterSpec` to
control `viewModelScope`.

**Fixture:** the owning features' `FakeX` repositories, the ViewModel, and a Turbine
receiver per effect flow under test — started inside `turbineScope` with kotest's
`backgroundScope`.

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class AddRoundViewModelTest : FreeSpec() {

    private class Fixture {
        val gameRepository = FakeGameRepository()
        val playerRepository = FakePlayerRepository()
        val viewModel = AddRoundViewModel(gameRepository, playerRepository)
        lateinit var effects: ReceiveTurbine<AddRoundEffect>
    }

    private suspend fun TestScope.withFixture(block: suspend Fixture.() -> Unit) {
        turbineScope {
            val fixture = Fixture()
            fixture.effects = fixture.viewModel.effects.testIn(backgroundScope)
            fixture.block()
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

**Async work:** `testCoroutineScheduler.runCurrent()` to run pending launches, then
`coVerify`. **Time in `viewModelScope`:** `testCoroutineScheduler.advanceTimeBy(…)`.

**Effects:** start the Turbine receiver **eagerly in `withFixture`, not `by lazy`** —
with `replay = 0`, an effect emitted before the turbine subscribes is dropped and
`awaitItem()` hangs.

**To test the view's *handling* of an effect**, compose the internal
state-driven overload with a `MutableSharedFlow` and emit from the test inside
`runSkikoComposeUiTest`.
