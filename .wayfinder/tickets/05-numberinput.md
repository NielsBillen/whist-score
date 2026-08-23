wayfinder:task

## Question

Extract a deep `NumberInput` component so the near-identical Slam and Bid inputs share one interface instead of two copies.

Details:
- `presentation/screens/addround/slaminput/SlamInputScreen.kt` loops `for (slams in 0..13)` and dispatches `AddRoundAction.SetSlams`.
- `presentation/screens/addround/bidinput/BidInputScreen.kt` loops `for (bid in 9..12)` and dispatches `AddRoundAction.SetBid`.
- Both keep a `remember { mutableStateOf(...) }` draft, gate `nextEnabled` on the draft being non-null, and render a `FlowRow` of `Selectable` tiles with `requiredSize(56.dp)`.
- Only the range and action differ. One component: `NumberInput(initial, range, label, onChoose)`.

Note: the Slam branch is reachable; the Bid branch is dead code (see `03-dead-code.md`). Decide with that ticket whether `NumberInput` should support a `visibleRange` predicate so the dead Bid stage can be dropped from the UI.
