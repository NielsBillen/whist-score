# Semantic Properties Guidelines

Not mounted in every context — read this before adding or matching a Compose
semantic property.

## How Compose builds the semantic tree

Compose generates the semantics tree alongside the UI hierarchy by **merging** nodes
from the composition tree ([Testing in Compose: Semantics](https://developer.android.com/develop/ui/compose/testing/semantics)).

**Merging is driven by `mergeDescendants`.** Off by default; interactive components
(`Button`, `Checkbox`) and modifiers like `clickable`/`toggleable` enable it, as does
`Modifier.semantics(mergeDescendants = true) { }`. Once enabled it cannot be disabled
further down.

| Position | Result |
|---|---|
| Outside a `mergeDescendants` ancestor | Nodes with semantic properties are kept; nodes without are collapsed. `Box`/`Column`/`Row` set none; `Text`, `Button`, `LazyColumn`, `clickable`, `toggleable` do. |
| Inside a `mergeDescendants` ancestor | All nodes merge into it — **unless a node sets `mergeDescendants = true` itself**, forming a new boundary: its descendants merge into it, and it survives as a separate node inside the outer ancestor. |

**Each property applies its own merge policy** when nodes merge — parent-wins,
parent-or-else-child, or aggregate. Policies live in
[SemanticsProperties.kt](https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/semantics/SemanticsProperties.kt).

**So the same composable behaves differently depending on where it is used.** A `Text`
with a `testTag` is reachable by that tag standalone; placed inside a `Button` it
merges into the `Button` and the tag is gone, because `testTag`'s policy is
parent-wins.

The boundary case, where the `Row` survives inside the `Button` and the sibling `Text`
does not:

```kotlin
Button(onClick = {}) {
    Column {
        Row(Modifier.semantics(mergeDescendants = true) { }) {
            Text("TopLeft")
            Text("TopRight")
        }
        Text("Bottom")
    }
}
```

```
Node #3  Role = 'Button'  Text = '[Bottom]'  MergeDescendants = 'true'
 |-Node #6  Text = '[TopLeft, TopRight]'  MergeDescendants = 'true'
```

## Standard semantic properties

Full list in the [SemanticsPropertyReceiver reference](https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/SemanticsPropertyReceiver#extension-properties_1);
the ones relevant here:

| Property | Merge policy | When to use |
|---|---|---|
| `testTag` | Parent | Identify a *kind* of node in tests — one capitalised prose name (`"Workspace screen"`, `"Confirm button"`), never an instance. |
| `contentDescription` | Aggregated | Content that may merge into a parent, or an accessibility label for an icon-only component. |
| `role` | Parent | The interactive role of a node (`Role.Button`, `Role.Checkbox`, `Role.Tab`, …). |
| `collectionInfo` / `collectionItemInfo` | Parent ?: child | Expose list/grid structure to tests and accessibility tools. |
| `traversalGroup` / `traversalIndex` | Parent ?: child | Control focus/tab traversal order. |

## Custom semantic properties

**When no standard property fits, define a typed key.** **Keep the key `private`** so it
stays out of the global namespace: the `SemanticsProperties` extension exposes it to
matchers, the `SemanticsPropertyReceiver` extension makes it a `var` inside any
`semantics { … }` block.

```kotlin
// TicketSemantics.kt
private val ticketKeyKey = SemanticsPropertyKey<TicketKey>("TicketKey")
val SemanticsProperties.TicketKey get() = ticketKeyKey
var SemanticsPropertyReceiver.ticketKey by ticketKeyKey

// in a composable
Modifier.semantics { ticketKey = key }

// Matcher.kt, in the module's testfixtures/ sibling
fun hasTicketKey(key: TicketKey) = SemanticsMatcher.expectValue(SemanticsProperties.TicketKey, key)
```

**Each key carries its own one-line why, and nothing else repeats it.** A `<Feature>Tags`
object or a matcher file restating "a tag is a kind, the instance is a property" adds a
copy to keep current; `TestTagFormatArchitectureTest` and `TestTagMatcherArchitectureTest` enforce
that rule already.

The property is named `<Concept>Semantics.kt` in the module that renders the node
(`NavDestinationSemantics.kt` in `:desktopApp`, beside the sidebar that writes it) — or
in the concept's own `domain` module when two features annotate it
(`WorktreeSemantics.kt` in `:features:worktrees:domain`, `ProjectSemantics.kt` in
`:features:projects:domain`, `ConnectionSemantics.kt` in
`:features:connections:domain`).

## Guidelines

### Match against the merged tree

**Tests query the merged tree** (`useUnmergedTree = false`, the default): it is
smaller, so matching is cheaper, and it reflects what accessibility services and a
real user see. **Never reach for `useUnmergedTree = true` to find a node the merge
collapsed.** Instead, a node that must stay addressable inside an interactive ancestor
sets `Modifier.semantics(mergeDescendants = true) { }` to form its own boundary. Fix
the composable's semantics, never the test.

### `clickable` / `toggleable` only on individual interactive components

They set `mergeDescendants = true` and so reshape the semantics of everything inside.
**For a large layout that handles pointer events without being a single interactive
component, use `pointerInput`.**

### Identify the type of a reusable component with `role`

**Use `role` when the behaviour matches a predefined
[role](https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/Role)**
(`Role.Button`, `Role.Checkbox`, `Role.Tab`, …); when none fits, use a custom
property. Tests then identify a node by component type (`hasRole(Role.Button)`,
`isComboBox()`) independently of how a specific instance is identified.

### Identify a specific instance, in order of preference

**A `testTag` never identifies an instance** — it names a kind, and a repeated kind
(every backlog row, every connection card) is disambiguated by a typed property, never
by a tag suffix. A tag built by interpolation fails `TestTagFormatArchitectureTest`
(`docs/guidelines/code-guidelines.md` § testTag on every interactive node). So:

1. **Existing properties** (`role`, `text`) if they already identify it uniquely —
   don't add properties solely for testing when the component is already described
   for accessibility.
2. **A content description**, if it is useful for accessibility.
3. **A typed custom property** carrying the domain key.

**If the node may sit inside an interactive component, set `mergeDescendants = true`**
on it to preserve properties that should not merge into the parent.

**Never annotate purely decorative nodes** (dividers, background icons, illustrations)
— it only adds noise.

### Identify a node by a domain key with a custom property

**Use a strongly typed custom property** (`ticketKey: TicketKey`,
`worktreeId: WorktreeId`, `destination: NavDestination`) — type safety at both the
annotation and the matcher site.

### Identify list and grid items with `collectionInfo`

**Annotate when the content represents a list or grid of data**, not when `Row`,
`Column` or `LazyColumn` is layout only. Generic matchers (`hasRow(index)`,
`hasColumn(index)`, `hasCell(row, column)`) are then defined once and reused for any
collection.

### Expose component state as a custom property

**For a reusable component whose state tests must verify, expose that state via a
custom semantic property instead of asserting on its visual representation** — tests
then don't depend on presentation. Compose does this with `ProgressBarRangeInfo`
(match the range, don't measure the width); apply the same to e.g. a `ComboBox`,
exposing selected and available items so tests need not know whether the options
render as a dropdown or a list.
