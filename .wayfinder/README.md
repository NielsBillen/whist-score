# Local ticket tracker

A minimal local ticket system for the whist-score project, following the wayfinder method.

- Map: [MAP.md](./MAP.md) — canonical artifact (label `wayfinder:map`). Index only; decisions live in tickets.
- Tickets: [tickets/](./tickets/) — one file per issue. Filename is `<order>-<slug>.md`; order is just the chart's sequence.
- Each ticket is marked with a wayfinder type at the top: `wayfinder:task`, `wayfinder:research`, `wayfinder:grilling`, or `wayfinder:prototype`.
- Blocking is written as `blocks: <ticket filename>` so the dependency chain is legible without a GUI.

## Labels

| Type | Who drives it | Use when |
|------|---------------|----------|
| `task`    | HITL or AFK  | Manual work that must happen before a decision can be made |
| `research`| AFK          | Reading docs / external resources to fill a knowledge gap |
| `grilling`| HITL         | The default — one decision at a time, in conversation |
| `prototype`| HITL        | Making a rough artifact to react to |

## How it works

1. The map lists the destination, notes, and decisions so far; it points at tickets but never restates them.
2. The frontier = open, unblocked, unclaimed tickets.
3. Work one ticket at a time. Resolve it (answer in a comment, close the file, append to the map's Decisions so far), then graduate whatever the answer made specifiable.
