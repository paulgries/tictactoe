# Design Critique (pessimist's lens)

A deliberately hard look at the design and layout against Clean
Architecture, SOLID, coupling/cohesion, and the Dependency Rule. Genuine
problems are separated from defensible trade-offs, with code citations.
Written by a skeptic; take the "violations" as candidate improvements,
not verdicts.

## Dependency Rule

**Clean where it matters.** `game/domain` and `game/ai` import nothing
outside their own packages (`game.*`/`java.*` only), and
`game/GameSessionDataAccess`/`GameSessionRules` import only `game.domain`.
The engine is genuinely pure — no Swing, no I/O, no `data_access`.
Adapters depend inward on ports (`FileGameDataAccessObject` →
`SaveGameDataAccess`/`LoadGameDataAccess`; `InMemoryGameSession` →
`GameSessionDataAccess`). That part is textbook-correct.

**Violation 1 — the presenter writes application state.**
`RequestAiMovePresenter` holds a `GameSessionDataAccess` and does
`session.getCurrentGameState()`/`session.setCurrentGameState(...)`
(play/request_ai_move/RequestAiMovePresenter.java:36-37). The presenter is
an output adapter; in strict CA it consumes `OutputData` and drives the
view, *never* the session. The staleness compare-and-swap is application
logic living in an adapter. Worse, it makes the session-write rule
inconsistent: interactors write the session, **except** the
request_ai_move presenter also does — the boundary has two writers with no
enforced policy.

**Violation 2 — `framework/` isn't framework-agnostic.**
`SwingUiScheduler` (the Swing adapter) sits in the *same package* as the
`UiScheduler` port, `ViewManager` imports `javax.swing.JPanel`, `Theme`
imports Swing. AGENTS.md calls `framework/` "generic, reusable," but the
port and its concrete Swing implementation share a package, so nothing
structurally stops an inner layer from touching `SwingUiScheduler`
directly. The DIP's point is that the abstraction is decoupled from the
implementation; colocating them weakens that guarantee to a convention.

## SOLID

**SRP — `AppBuilder` is a god object.** Ten collaborators, and it
constructs views, wires win-effect `Runnable`s, assembles the use-case
graph, *and* enforces a hand-rolled ordering constraint via
`IllegalStateException` (app/AppBuilder.java:97-100:
`addMakeHumanMoveUseCase()` throws unless `addRequestAiMoveUseCase()` ran
first). Composition roots are naturally wide, but this one encodes a
**temporal coupling** between two sibling use cases that is invisible
anywhere in the type system — see Violation 4 below.

**OCP — `GameViewModelMapper` isn't closed.** `toStatusRenderState` /
`outcomeKindOf` (play/GameViewModelMapper.java:39-59) are
`instanceof Win`/`Draw` chains; adding a status means editing the mapper in
three places. The `sealed` hierarchy bounds the blast radius, so this is
defensible, but it's still open-for-extension only by *editing* the mapper.

**ISP — `GameSessionDataAccess` is a shared-state port with five
consumers.** `start_new_game`, `make_human_move`, `request_ai_move` (both
controller *and* presenter), `save_game`, `load_game` all reach into the
same five-method surface. Small enough to be tolerable, but a presentation
class (the presenter) depends on application state — that's the crux of
Violation 1.

**DIP — `RequestAiMoveController` does two jobs.** It builds `InputData`
*and* directly orchestrates `UiScheduler.runInBackground` and reads the
session (play/request_ai_move/RequestAiMoveController.java:33-38). The
"thin controller builds InputData" rule (AGENTS.md) is violated: this one
owns async-scheduling policy too, which is likely why the presenter then
has to touch the session for the staleness check.

## Coupling / cohesion

**Violation 3 — sibling capabilities coupled through concrete classes.**
`GamePanel` (in `play/`) imports the *concrete*
`persistence.save_game.SaveGameController` and
`setup.start_new_game.StartNewGameController`; `SetupPanel` (in `setup/`)
imports the *concrete* `persistence.load_game.LoadGameController`. Same
ring, so not a ring-to-ring Dependency Rule breach — but it's real
horizontal coupling: the view reaches into two other capabilities'
concrete classes, and every new button means injecting another concrete
controller into the view. There's no per-view controller facade or
interface.

**Violation 4 — the hidden human→AI handoff.** `MakeHumanMovePresenter`
doesn't depend on `request_ai_move` at all — it gets an anonymous
`Runnable` (play/make_human_move/MakeHumanMovePresenter.java:18,34) wired
by the builder. The flow "after a human move, if the AI is to move, request
an AI move" is invisible to the type system; rename/refactor
`request_ai_move` and nothing breaks at compile time, only at runtime via
the builder's `IllegalStateException`. This is Law-of-Demeter-adjacent
hidden coupling and the root of the builder's ordering hack.

**Violation 5 — `play/` is a grab-bag capability.** One folder holds three
distinct responsibilities: shared render beans (`GameViewModel`,
`GameRenderState`, ...), the game-screen views, *and* two whole use cases.
Because `setup/` and `persistence/` presenters both consume `play/`'s
render beans, `play/` is secretly the shared/plumbing layer — so
`setup`/`persistence` depend on `play`, which muddies the
"package-by-capability" story and means the "shared" types aren't in a
shared package.

## The two things worth fixing first

1. **Push the session write out of the presenter.** The staleness check
   belongs in the interactor or a dedicated application-service method, not
   in `RequestAiMovePresenter`. One boundary, one writer.
2. **Replace the `Runnable` handoff with a declared boundary** (e.g.
   `MakeHumanMovePresenter` depends on a `RequestAiMoveInputBoundary` or a
   small `AiMoveRequester` interface), which would let `AppBuilder` drop
   its `IllegalStateException` ordering constraint and make the coupling
   explicit and compiler-checked.

The rest — mapper `instanceof` chains, `play/` as shared layer,
`framework/` colocating the Swing adapter — are real but defensible
trade-offs that match CAWithBuilder's own conventions.