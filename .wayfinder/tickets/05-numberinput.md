wayfinder:task
status: closed
claimed-by: agent (this session)

## Question

Extract a deep `NumberInput` component so the near-identical Slam and Bid inputs share one interface instead of two copies.

Details:
- `presentation/screens/addround/slaminput/SlamInputScreen.kt` loops `for (slams in 0..13)` and dispatches `AddRoundAction.SetSlams`.
- `presentation/screens/addround/bidinput/BidInputScreen.kt` loops `for (bid in 9..12)` and dispatches `AddRoundAction.SetBid`.
- Both keep a `remember { mutableStateOf(...) }` draft, gate `nextEnabled` on the draft being non-null, and render a `FlowRow` of `Selectable` tiles with `requiredSize(56.dp)`.
- Only the range and action differ. One component: `NumberInput(initial, range, label, onChoose)`.

Note: the Slam branch is reachable; the Bid branch is dead code (see `03-dead-code.md`). Decide with that ticket whether `NumberInput` should support a `visibleRange` predicate so the dead Bid stage can be dropped from the UI.

## Resolution

**Superseded — do not extract.** The premise no longer holds in the current tree.

- `BidInputScreen`/`SetBid(Int)`/`for (bid in 9..12)` are gone. The "Bid" stage is now the `SELECT_BID_ACHIEVED` screen → `BidAchievedInput.kt`, a **Yes/No Boolean** (`AddRoundAction.SetBidAchieved`, `AddRoundState.playerWon`), not a number range.
- `SlamInputScreen.kt` (`for (slams in 0..13)`) is now the **only** number-range `FlowRow`/`Selectable` input in the feature. A `grep` for a second number-range loop, `SetBid`, `9..12`, or `visibleRange` returns nothing.

So there is no second near-identical input to dedupe: extracting `NumberInput` would give a deep component with exactly one caller — a speculative abstraction. The `visibleRange` predicate in the Note is moot for the same reason (one consumer, no dead range to hide).

**Decision: keep the `SlamInputScreen` loop inline.** No code change.

**Graduation:** this also closes out the `03-dead-code.md` "dead `SELECT_BID` stage" unit — it was replaced by the boolean `BidAchievedInput`, not deleted. No new tickets surface; the map's `Not yet specified` is untouched.
