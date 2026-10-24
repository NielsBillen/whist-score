wayfinder:grilling
claimed-by: agent (this session)
status: closed

## Question

Let the app manage an extensible roster of players (not just four fixed `PlayerId`s) and start a game with any chosen set of four, so a new game can be built quickly from the roster.

Details:
- `domain/PlayerId.kt` is a 4-way enum (`Player1`–`Player4`); `defaultPlayers` and `DEFAULT_PLAYERS` hardcode four players; `PlayerRepository.players` is a fixed four-entry map; `Rounds`/`Game` scoring iterate `PlayerId.entries`.
- Requirement: a persisted roster of named players; a way to select four of them (or fewer/more supported by a given round) to form a game; and scoring to key off roster IDs, not the enum.
- `Round.multiPlayer` currently enforces `players.size in 1..2` (and Treble `== 2`); the roster expansion must not break the per-round player-count rules that already exist.
- Decide the player-key representation (keep `PlayerId` as a type alias backed by roster entries, or refactor to a real `PlayerId` value type). This decision affects DTOs and the DI bindings.

## Resolution

Resolved. The model splits the two roles the old `PlayerId` enum was silently doing:

- **`Seat` = the 4 fixed table positions** (the current `PlayerId` enum, renamed). Whist is always 4, so this stays a 4-way enum. All per-round count rules (`Pass 4..4`, `Regular 1..2`, single `1..1`, `Treble 2..2`) and dealer/first-lead derivation (ticket 09) keep keying off the 4 seats unchanged.
- **`PlayerId(PlayerUUID)` = a real, stable, extensible identity** for a person in the roster. A `Player` is `PlayerId + name`; the **`Roster`** is the persistent list of `Player`s.
- **`Game.players = Map<Seat, PlayerId>`** — each of the 4 seats references a roster player. **Colors stay bound to the seats**, not the people (the four `DEFAULT_PLAYERS` colors become the four seat colors); a seated player displays with the color of the seat it occupies.

**Roster storage.** The roster is a separate persistent store (the existing `PlayerRepository`, key `players`), now holding `List<Player>` / `Map<PlayerId, Player>` of arbitrary size. Each `Game` stores only its 4 chosen seats (roster ids). This removes the current duplication where the same 4-player map is written both under the `players` key and inside `GameDto`.

**New game.** Starting a game seats a fresh choice of 4 roster players; the picker defaults to the last game's 4. Flow: an Overview **new-game picker** picks exactly 4 from the roster (pre-seeded with the last game's 4); a separate **Roster screen** adds/renames/reorders/removes roster players (the pool is truly extensible).

**The invariant (the 5-and-6 flaw).** A `Round` carries the real `PlayerId`s of its winners. It is `Game` — not `Rounds` — that owns the 4-player set, so `Game` validates on `plus(round)` that every `round.playerId` is one of the game's 4. `Rounds` stays a thin, stateless scoring helper over `(players, rounds)` and does not own the players; the `4-player` invariant lives where the players live. Reseating only remaps the seat→`PlayerId` table (display + future rounds); recorded rounds keep their real `PlayerId`s, so scores follow the people and no points are reassigned (per ticket 09, dealer/lead re-derive from the current seat order at read time).

**Serialization / migration.** `Seat` serializes like the old `PlayerIdDto` (4 values); `PlayerId` (UUID) serializes as a UUID string in `Rounds`/`Game` DTOs. **No data migration** — this is pre-1.0 single-device; the existing `try/catch` decode fallback in `DefaultPlayerRepository`/`DefaultGameRepository` already drops unparseable old data back to defaults, so old saved games are accepted to be lost and the UUID format takes over from now.

**Map impact:** unblocks [11-game-history](tickets/11-game-history.md) (a game is now a well-defined aggregate of 4 roster players + rounds, serializable via the DTO layer) and [12-stats-tracking](tickets/12-stats-tracking.md) (stats key off stable `PlayerId`s). Does not invalidate ticket 09 — dealer/lead derivation from seat order still holds; the seat order is now populated by the roster picker.
