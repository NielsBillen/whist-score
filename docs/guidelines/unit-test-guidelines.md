# Unit Test Guidelines

For ViewModel testing (Turbine, test dispatchers, fixtures) see
[view-model-guidelines.md](view-model-guidelines.md).

## Framework

Every test class MUST use **Kotest `FreeSpec`, with tests and groups in the `init`
block**. Group related tests hierarchically under a shared description.

```kotlin
class ExampleTest : FreeSpec() {
    init {
        "grouped tests" - {
            "test 1" { }
            "test 2" { }
        }
    }
}
```

## One test class per class under test

**Named `[ClassUnderTest]Test`**, with the instance under test named after its type
(`val viewModel`, `val deviceRepository`, `val ipAddress`) — never `sut`, `subject`,
or `useCase`.

## Shared constants — class-level, not `companion object`

**Reusable test data goes in `private` class-level fields.**  A `companion object` is
retained for the JVM lifetime, surviving long after the spec finished; class-level
fields are released with the spec instance.

## Test names

**Read as sentences describing the asserted behaviour.** Avoid `"test …"`,
`"should …"`, and method-name echoes.

```kotlin
"returns an empty list when no devices are online" { }
"emits the new selection when the user selects a device" { }
```

## Test through the public API

**Drive every test through the public API.** No reflection into private methods, no
test-only state mutators. A behaviour unobservable through the public API is a
design signal — extract it to a collaborator whose own API can be tested.

**Narrow exception:** a self-contained pure function worth a focused test but not
worth extracting may be exposed `@VisibleForTesting internal` (never widened to
public). Use it to *observe* a computation — never to *inject* internal state.

## Meaningful tests only

**A test must verify behaviour another component or the user depends on.** Skip
trivial getters, setters, and one-line delegations whose only failure mode is a typo.

**When fixing a bug, write the reproducing failing test first** — it gives a
pass/fail signal and locks the fix against regression.

## Brevity

**Every test reads arrange → act → assert** (GIVEN the setup, WHEN the call under
test, THEN the assertions), one behaviour per test, small enough that intent is
obvious at a glance.

- **Keep act and assert inline** — never behind a helper. Only *arrange* may move
  out, into `withFixture` or a helper.
- **Keep the act focused** — only the calls triggering the behaviour under test.
- **Reuse the class-level constants** in arrange rather than building a fresh object
  inline.
- **Extract duplicated arrange** — widen a `withFixture` parameter or extract a helper.

## Test independence

**Each test produces its own fresh state.** No top-level `var`s in `init`, no
mutating class-level fields from a test. Create fakes/spies/instances per test,
inline or via `withFixture`.

## The `withFixture` pattern

**When setup grows beyond a couple of lines, encapsulate it in `withFixture`.**
Leading parameters describe the **state before** (with defaults, so each test
overrides only what it cares about); the final lambda exercises the system and
asserts on the **state after**. Each call constructs a fresh `Fixture`, which is
what keeps tests independent.

**Use a concrete `Fixture` class, constructed directly.** Make it `abstract` and
instantiate as `object : Fixture(…) { }` **only** when tests supply per-call
overrides — as ViewModel fixtures do for their Turbine receivers
([view-model-guidelines.md](view-model-guidelines.md)).

```kotlin
class FoobarTest : FreeSpec() {

    private class Fixture(deviceRepository: DeviceRepository) {
        val foobar = Foobar(deviceRepository)
    }

    private suspend fun TestScope.withFixture(
        devices: Collection<Device> = emptyList(),
        block: suspend Fixture.() -> Unit,
    ) {
        Fixture(FakeDeviceRepository(devices)).block()
    }

    init {
        coroutineTestScope = true

        "does something with the given devices" {
            withFixture(devices = listOf(luminode)) {
                foobar.doSomething()
                // assertions on state-after
            }
        }
    }
}
```

**Don't force a large test class through one fixture.** Tailor setup per group —
multiple `withFixture` overloads (richer ones delegating to a base) or distinct
`Fixture` classes. Only the arrange differs; act and assert stay in the test body.

## Determinism

**No real wall-clock, filesystem, network, or unseeded random.**

| Dependency | Test substitute |
|---|---|
| Coroutines | `coroutineTestScope = true`, the spec's `backgroundScope` and `testCoroutineScheduler` — never `Thread.sleep` or a real-dispatcher `delay` |
| Time | inject a `Clock`, pass a fixed one |
| Randomness | inject a `Random`, pass a seeded one |
| IO | in-memory fake repository/client — never a real socket or disk |

## Avoid Koin in unit tests

**Wire dependencies by hand.** Narrow exception: a component composing children that
each resolve their own ViewModel through Koin (e.g. a tabbed container) may set up a
Koin scope to satisfy those lookups. Tests for the individual tabs still wire by hand.

## Fakes vs mocks

- **Default to a `FakeX`** for stateful, multi-method collaborators (repositories,
  clients, event sources). It mirrors the real contract, so a breaking API change
  becomes a compile error instead of a stale mock that keeps "working".
- **Reserve `mockk<T>(relaxed = true)`** for narrow callbacks
  (`(Action) -> Unit`, `() -> Unit`) and collaborators touched in one or two places.
- **To verify a call on top of a stateful fake, wrap it: `spyk(FakeX())`** and use
  `verify`/`coVerify`. **Never add tracking properties** (`var lastCalledWith`,
  `val invocations`) to a fake for this.

```kotlin
val deviceRepository = spyk(FakeDeviceRepository())
val onAction = mockk<(DevicesAction) -> Unit>(relaxed = true)

coVerify { deviceRepository.setName(listOf(deviceId), "Foo") }
verify { onAction(DevicesAction.SelectDevice(deviceId)) }
```

Asserting a call did **not** happen: `verify { onAction wasNot Called }` for a
lambda; `verify(exactly = 0) { deviceRepository.setName(any(), any()) }` for a
specific method. **Avoid `wasNot Called` on a whole collaborator** — a later
unrelated call fails the test for the wrong reason.

## Assertions

- **Prefer kotest's matchers** (`shouldContain`, `shouldHaveSize`, `shouldBeEmpty`,
  `shouldBeInstanceOf`) over hand-rolled equivalents — richer failure messages.
- **Asserting every property: compare the whole object in one go** — explicit
  intent, complete diff, and it catches accidental new properties.
- **Asserting several but not all: group them in `assertSoftly`** so every failure
  is reported at once.

```kotlin
assertSoftly(device) {
    name shouldBe "GigaCore 26i"
    ipAddress shouldBe IpAddress.parse("10.0.0.1")
    isOnline shouldBe true
}
```

## Shared fakes live beside their port

**A fake consumed by more than one module lives in the owning feature's
`domain` module (`commonMain`)**, next to the port it stands in — e.g.
`FakeGameRepository` in `:shared:features:game:domain`. Only `commonMain` code
there is visible to consumers.

## Compose UI tests

**Compose UI tests run on the `jvm` target** via `runSkikoComposeUiTest`
(`androidx.compose.ui.test.v2`), with kotest assertions inside them, and match
nodes by their visible text (`onNodeWithText`) or content description
(`onNodeWithContentDescription`) — never by tags.
