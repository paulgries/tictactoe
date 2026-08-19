# Design Patterns in This Codebase

This document catalogs the design patterns implemented in this Tic-Tac-Toe
project, with pointers to the exact classes and a short explanation of how
each one works *in this codebase specifically* (not just the textbook
definition). It's written for software design students who want to see
these patterns in a small, complete, real system rather than an isolated
toy example.

The codebase follows Clean Architecture (see [`AGENTS.md`](AGENTS.md)): a `game` engine
(`game/domain` plus `game/ai`) sits at the center, the capabilities
(`setup/`, `play/`, `persistence/`) each hold their own controllers,
presenters, and `use_case` packages around it, and `data_access`,
`framework`, and `app` sit at the edges. Dependencies only ever point
inward toward `game/domain`. Several of the patterns below exist
specifically *because of* that constraint — for example, `Adapter` is how
the inner layers stay ignorant of Swing and of the file system.

Patterns are grouped by GoF category, followed by a section on
architectural idioms that show up alongside them.

---

## Creational Patterns

### Factory Method / Simple Factory

**Intent:** Centralize the decision of *which concrete class to instantiate*
behind a single method, so callers depend on an abstraction instead of a
list of constructors.

**Where:**
- [`AiStrategyFactory`](src/main/java/game/ai/AiStrategyFactory.java) (interface) + [`CommonAiStrategyFactory`](src/main/java/game/ai/CommonAiStrategyFactory.java) (implementation) — a *registry* factory mapping each `AiDifficulty` to a `Supplier<AiStrategy>`
- [`GameStateFactory`](src/main/java/game/domain/GameStateFactory.java) (interface) + [`CommonGameStateFactory`](src/main/java/game/domain/CommonGameStateFactory.java) (implementation) — builds the `GameState` aggregate, injected into the `start_new_game` interactor the way CAWithBuilder injects `CommonUserFactory`
- [`GameState.newGame(GameConfig)`](src/main/java/game/domain/GameState.java#L11) — a *static factory method* (Effective Java, Item 1), a lighter cousin of the full Factory Method pattern: an alternative named constructor rather than a class whose whole job is choosing between subtypes.

```java
public final class CommonAiStrategyFactory implements AiStrategyFactory {
    private final Map<AiDifficulty, Supplier<AiStrategy>> strategies =
            new EnumMap<>(AiDifficulty.class);

    public CommonAiStrategyFactory() {
        register(AiDifficulty.EASY, () -> new RandomAiStrategy(new Random()));
        register(AiDifficulty.MEDIUM, EasyAiStrategy::new);
        register(AiDifficulty.DIFFICULT, MinimaxAiStrategy::new);
    }
    ...
}
```

`CommonAiStrategyFactory` is the clearest example: the
[`RequestAiMoveInteractor`](src/main/java/play/request_ai_move/use_case/RequestAiMoveInteractor.java)
never constructs an `AiStrategy` implementation directly — it asks the
factory for one given an `AiDifficulty`, so adding a fourth difficulty later
means registering one more `Supplier` instead of touching every call site.
The registry also lets tests substitute strategies or add custom ones
without touching the factory's constructor.

---

### Builder

**Intent:** Separate the *construction* of a complex object from its
representation, especially when it has several dependencies, some
optional, that would otherwise force a long or repeated constructor call.

**Where:** [`AppBuilder`](src/main/java/app/AppBuilder.java)

```java
JFrame application = new AppBuilder()
        .addSetupView()
        .addGameView()
        .addRequestAiMoveUseCase()
        .addMakeHumanMoveUseCase()
        .addStartNewGameUseCase()
        .addSaveGameUseCase()
        .addLoadGameUseCase()
        .build();
```

The whole application graph has one shared `GameViewModel`,
`SetupViewModel`, `ViewManagerModel`, `GameSessionDataAccess` (an
`InMemoryGameSession`), and file DAO that must be threaded through every
interactor and controller. Each use case also has an *ordering*
constraint — `addMakeHumanMoveUseCase()` throws unless the request-AI-move
use case was wired first, because the human-move presenter hands off to it.
`AppBuilder`'s one-fluent-method-per-use-case shape (mirroring
CAWithBuilder) keeps that assembly in a single readable place, and both
[`Main.java`](src/main/java/app/Main.java) (production) and tests share the
same path instead of repeating the wiring. This is exactly why the pattern
earns its keep here: no single object is hard to build, but the *whole
graph* is, and the builder makes the ordering constraints impossible to
get subtly wrong.

---

## Structural Patterns

### Decorator

**Intent:** Attach additional behavior to an object dynamically by wrapping
it in another object that implements the same interface, so behaviors can
be layered and composed without subclassing every combination.

**Where:**
- [`GamePanel`](src/main/java/play/GamePanel.java) holds a `List<Runnable> winEffects` and runs every effect when the status becomes a `WIN` ([`GamePanel.java#L77-L79`](src/main/java/play/GamePanel.java#L77-L79)).
- [`AppBuilder.ifEnabled(...)`](src/main/java/app/AppBuilder.java#L142-L148) — a *functional* decorator that wraps a `Runnable` and adds the "only run if enabled" behavior.
- The effects themselves live in [`EffectOverlayPanel`](src/main/java/play/EffectOverlayPanel.java) (`playConfetti`, `playFireworks`, `playMarks`).

```java
gamePanel.setWinEffects(List.of(
        ifEnabled(setupState::isConfettiEnabled, effects::playConfetti),
        ifEnabled(setupState::isFireworksEnabled, effects::playFireworks),
        ifEnabled(setupState::isMarksEnabled, effects::playMarks)));

private static Runnable ifEnabled(BooleanSupplier enabled, Runnable effect) {
    return () -> {
        if (enabled.getAsBoolean()) {
            effect.run();
        }
    };
}
```

Three win-celebration effects (confetti, fireworks, twinkling X's/O's) are
composed into a list, each wrapped by `ifEnabled`, and `GamePanel` runs
them all whenever a game is won. `GamePanel` has no idea which effects are
switched on or how many there are — it just iterates the list. This is
Decorator in its functional form: instead of a chain of nested view objects,
each `ifEnabled` wrapper adds behavior around a `Runnable` and the collection
is passed in whole. Supporting "confetti only," "fireworks only," or "all
three together" is now just a question of which wrappers AppBuilder puts in
the list, with no new subclasses anywhere.

---

### Adapter

**Intent:** Convert the interface of one class into an interface a client
expects, so two otherwise-incompatible pieces can work together — commonly
used to keep a framework-specific implementation behind a
framework-agnostic port.

**Where:**
- [`UiScheduler`](src/main/java/framework/UiScheduler.java) (port) ↔ [`SwingUiScheduler`](src/main/java/framework/SwingUiScheduler.java) (adapter)
- [`SaveGameDataAccess`](src/main/java/persistence/save_game/use_case/SaveGameDataAccess.java) / [`LoadGameDataAccess`](src/main/java/persistence/load_game/use_case/LoadGameDataAccess.java) (ports) ↔ [`FileGameDataAccessObject`](src/main/java/data_access/FileGameDataAccessObject.java) (adapter)
- [`GameSessionDataAccess`](src/main/java/game/GameSessionDataAccess.java) (port) ↔ [`InMemoryGameSession`](src/main/java/data_access/InMemoryGameSession.java) (adapter)

```java
public final class SwingUiScheduler implements UiScheduler {
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor(...);

    @Override
    public void runInBackground(Runnable task) {
        backgroundExecutor.execute(task);
    }

    @Override
    public void runOnUiThread(Runnable task) {
        SwingUtilities.invokeLater(task);
    }
}
```

The `request_ai_move` presenter needs to run an AI move search off the UI
thread and then hop back onto it — but the presentation layer must never
import Swing. `UiScheduler` is the port it depends on instead;
`SwingUiScheduler` adapts Swing's `SwingUtilities.invokeLater` and an
`ExecutorService` to that port. The persistence and session ports are the
same idea applied to storage: the use cases depend on
`SaveGameDataAccess`/`LoadGameDataAccess` and `GameSessionDataAccess`
interfaces, and `data_access` provides the file-backed and in-memory
adapters. This is what makes the Clean Architecture layering (see
[`AGENTS.md`](AGENTS.md)) actually enforceable at compile time, not just a convention:
the capabilities physically cannot `import javax.swing.*` or open files.

---

## Behavioral Patterns

### Strategy

**Intent:** Define a family of interchangeable algorithms behind a common
interface, and let the algorithm vary independently of the code that uses
it.

**Where:** [`AiStrategy`](src/main/java/game/ai/AiStrategy.java), implemented by [`RandomAiStrategy`](src/main/java/game/ai/RandomAiStrategy.java), [`EasyAiStrategy`](src/main/java/game/ai/EasyAiStrategy.java), and [`MinimaxAiStrategy`](src/main/java/game/ai/MinimaxAiStrategy.java)

```java
public interface AiStrategy {
    Position selectMove(Board board, GameConfig config, Mark aiMark);
}
```

[`RequestAiMoveInteractor`](src/main/java/play/request_ai_move/use_case/RequestAiMoveInteractor.java)
is handed an `AiStrategy` (from the factory above) and calls `selectMove`
without knowing or caring whether it's picking a random empty cell, checking
for an immediate win/block, or running minimax with alpha-beta pruning. Each
implementation is a fully independent algorithm — this is what distinguishes
Strategy from Template Method below: here the *entire* algorithm varies, not
just a few steps of a shared skeleton.

---

### Command (Interactor)

**Intent:** Encapsulate a request — or in this case, a single use case —
as an object with a uniform "execute" entry point, decoupling the thing
that triggers an operation from the code that performs it.

**Where:** [`StartNewGameInteractor`](src/main/java/setup/start_new_game/use_case/StartNewGameInteractor.java), [`MakeHumanMoveInteractor`](src/main/java/play/make_human_move/use_case/MakeHumanMoveInteractor.java), [`RequestAiMoveInteractor`](src/main/java/play/request_ai_move/use_case/RequestAiMoveInteractor.java)

```java
public final class MakeHumanMoveInteractor implements MakeHumanMoveInputBoundary {
    private final MakeHumanMoveOutputBoundary presenter;
    private final GameSessionDataAccess session;

    @Override
    public void execute(MakeHumanMoveInputData inputData) {
        final GameState current = session.getCurrentGameState();
        if (current == null || current.isGameOver() || GameSessionRules.isAiTurn(...)) {
            return;
        }
        final GameState updated = current.applyMove(inputData.position());
        session.setCurrentGameState(updated);
        presenter.prepareSuccessView(new MakeHumanMoveOutputData(updated, ...));
    }
}
```

Clean Architecture calls these "Interactors" or "Use Cases," but
structurally each one *is* the Command pattern: a single class wrapping a
single operation behind one `execute(...)` method, with no other public
behavior. Each capability's thin controller holds its `InputBoundary` and
calls `execute` on whichever one a UI event demands, without needing to
know how any of them work internally. Interactors stay `void` and report
through the injected `OutputBoundary` (the presenter) — the view never
learns how a move was applied.

---

### State (via a sealed type hierarchy)

**Intent:** Let an object's behavior change based on which of a fixed set
of states it's in, typically by giving each state its own class.

**Where:** [`GameStatus`](src/main/java/game/domain/GameStatus.java) — a sealed interface permitting only [`InProgress`](src/main/java/game/domain/InProgress.java), [`Win`](src/main/java/game/domain/Win.java), and [`Draw`](src/main/java/game/domain/Draw.java)

```java
public sealed interface GameStatus permits InProgress, Win, Draw {}
public record Win(Mark winner, List<Position> winningLine) implements GameStatus {}
```

This is the modern Java take on State: instead of giving each state a
virtual method that the client calls polymorphically (the classic GoF
shape), the *client* pattern-matches on which state it has, and the
compiler guarantees every `instanceof` chain is exhaustive because the
interface is `sealed`. See it consumed in
[`GameViewModelMapper`](src/main/java/play/GameViewModelMapper.java)
and
[`MinimaxAiStrategy`](src/main/java/game/ai/MinimaxAiStrategy.java#L49-L52).
It's worth comparing this to classic State: here the *data* differs per
state (a `Win` carries a winner and a winning line; `InProgress` and
`Draw` carry nothing), which is awkward to express with polymorphic
methods but natural with a sealed hierarchy of records.

---

### Template Method

**Intent:** Define the skeleton of an algorithm in a base class method,
deferring specific steps to subclasses, so the overall structure can't
drift between implementations even as the details vary.

**Where:** `WinEffect` (private abstract nested class) in [`EffectOverlayPanel`](src/main/java/play/EffectOverlayPanel.java#L104-L143), extended by `ConfettiEffect`, `FireworksEffect`, and `MarksEffect`

```java
private abstract static class WinEffect {
    final void play(int width, int height) { spawnParticles(width, height); active = true; }
    final void tick(long now) {
        if (!active) return;
        advance(now);
        if (isExpired(now)) { clearParticles(); active = false; }
    }
    final void paint(Graphics2D g2, long now) { if (active) paintParticles(g2, now); }

    abstract void spawnParticles(int width, int height);
    abstract void advance(long now);
    abstract boolean isExpired(long now);
    abstract void paintParticles(Graphics2D g2, long now);
    abstract void clearParticles();
}
```

All three win-celebration effects share an identical lifecycle — spawn
particles once, advance and check expiry on every animation tick, paint
while active — but differ in *how* they spawn, advance, and paint.
`play()`, `tick()`, and `paint()` are `final`: no subclass can reorder or
skip a step of that lifecycle. This is a good contrast with Strategy
above — Strategy is for when the *whole* algorithm varies; Template
Method is for when the *skeleton* is identical and only a few steps
change. Before this refactor, `EffectOverlayPanel` managed three parallel
particle lists with the same tick/paint logic copy-pasted three times;
Template Method is what let that collapse to one loop over
`List<WinEffect>`.

---

### Observer

**Intent:** Let one or more observers register interest in an object and
be notified automatically when it changes, without the subject needing
to know anything about its observers beyond the notification interface.

**Where:**
- The view-model pattern: views register as a `PropertyChangeListener` on their view model — [`SetupPanel`](src/main/java/setup/SetupPanel.java#L58) on `SetupViewModel`, [`GamePanel`](src/main/java/play/GamePanel.java#L32) on `GameViewModel` — and render from `evt.getNewValue()` whenever a presenter calls `firePropertyChanged()`.
- Native Swing listeners in [`SetupPanel`](src/main/java/setup/SetupPanel.java) (`ItemListener`, `ChangeListener`, `ActionListener`), [`BoardPanel`](src/main/java/play/BoardPanel.java) (`ActionListener` per cell button), and [`StatusPanel`](src/main/java/play/StatusPanel.java) (`ActionListener` on Save/Restart/Change Settings).

```java
boardSizeSpinner.addChangeListener(e -> clampWinLengthToBoardSize());
```

The Swing listeners are the textbook shape of Observer — a `JSpinner`
(subject) notifies a registered `ChangeListener` (observer) whenever its
value changes, with no compile-time dependency between them beyond the
listener interface. The view-model pattern is the same idea at the
application level: a presenter mutates the shared `GameRenderState` and
fires one property change; every registered view re-renders from the new
value. This is what lets `SetupPanel` and `GamePanel` react to changes
produced by presenters in entirely different capabilities without either
side holding a reference to the other.

---

## Architectural Idioms Worth Knowing Alongside These

These aren't in the classic 23 GoF patterns, but they show up constantly
in real codebases next to the patterns above, and several are visible
here.

### Mediator / Presenter (MVP)

Each capability follows the CAWithBuilder shape: a thin **controller**
builds `InputData` from view primitives, the **interactor** runs the use
case, and the **presenter** renders the `OutputData` into the shared view
model and fires a property change. Navigation is presenter-driven through
[`ViewManager`](src/main/java/framework/ViewManager.java) +
[`ViewManagerModel`](src/main/java/framework/ViewManagerModel.java).
Views like [`GamePanel`](src/main/java/play/GamePanel.java) and
[`SetupPanel`](src/main/java/setup/SetupPanel.java) are entirely passive —
they only write widget values into the state bean and render what the
presenter publishes. That makes this Model-View-**Presenter** rather than
classic MVC (where the view often talks to the model directly): the
presenter owns all coordination logic, which is also what lets tests
exercise a whole use case with a real view model and a mock presenter,
with no Swing component in sight.

### Mapper / DTO

[`GameViewModelMapper`](src/main/java/play/GameViewModelMapper.java)
translates the domain's `GameState` into presentation-facing
`BoardRenderState` and `StatusRenderState` records. This keeps
`game/domain` ignorant of anything presentation-related (no `GameState`
field ever needs to know it might someday be rendered as a color or a
string), and keeps the mapping logic itself in one testable, static-method
place rather than scattered across Swing components.

### Immutable Value Object / Persistent Data Structure

[`Board.placeMark()`](src/main/java/game/domain/Board.java#L34)
copies its backing array and returns a *new* `Board` rather than mutating
in place; [`GameState.applyMove()`](src/main/java/game/domain/GameState.java#L20)
likewise returns a new `GameState`. Combined with `Position`, `GameConfig`,
and every render type (`BoardRenderState`, `StatusRenderState`,
`CellRenderState`, `CellSymbol`) being a Java `record`, this gives the
whole domain model value semantics: nothing holding a reference to a
`GameState` needs to worry about another part of the program mutating it
out from under them — which is exactly what lets the async AI move share a
state snapshot safely across threads.

### Non-instantiable Utility Class

[`WinChecker`](src/main/java/game/domain/WinChecker.java),
[`LineGenerator`](src/main/java/game/domain/LineGenerator.java),
[`BoardEvaluator`](src/main/java/game/ai/BoardEvaluator.java),
and [`MoveOrderer`](src/main/java/game/ai/MoveOrderer.java)
are all `final` classes with a private no-arg constructor and only static
methods — pure functions grouped by topic, not objects with identity or
state. Not the GoF Singleton (there's no lazy instantiation, no instance
at all, and nothing controls *access* to a shared instance) — just the
"this doesn't need to be an object" idiom (Effective Java, Item 4).

### Test Double (Mock / Fake / Stub / Spy)

- **Mocks** for the boundaries: tests `mock(...)` the `OutputBoundary`
  (presenter) and the data-access ports, then `verify` what was passed —
  e.g. [`LoadGameInteractorTest`](src/test/java/persistence/load_game/use_case/LoadGameInteractorTest.java)
  asserts the presenter got the loaded `SavedGame` *and* that the real
  `InMemoryGameSession` actually received the restored state.
- [`CapturingUiScheduler`](src/test/java/game/testutil/CapturingUiScheduler.java)
  queues background and UI tasks so a test can step through them one at a
  time and assert on in-between states (a **Spy**).

All of these exist only because `UiScheduler`, `GameSessionDataAccess`,
`SaveGameDataAccess`, and `LoadGameDataAccess` are ports (see Adapter,
above) — without those abstraction boundaries, tests would have no seam to
substitute a double into.

---

## A Note on What's *Not* Here

It's worth noticing what's absent, and why forcing a pattern in would have
been the wrong call:

- **No Singleton.** Nothing in this program needs global, controlled
  access to a single instance — every collaborator is constructed once
  and passed in explicitly (see [`AppBuilder`](src/main/java/app/AppBuilder.java) and [`Main.java`](src/main/java/app/Main.java)).
- **No Composite or Visitor**, despite `GameStatus` being a sealed
  hierarchy — the hierarchy is flat (three leaf types, no tree structure)
  and consumers already get exhaustive, type-safe dispatch for free from
  `sealed` + `instanceof` pattern matching, so a Visitor would only add
  ceremony.
- **No Chain of Responsibility.** There's no pipeline of handlers that
  might or might not process a request and pass it along.
- **The win effects are no longer a Decorator chain of views.** An earlier
  version wrapped `GameView` in nested `WinEffectGameView` decorators; the
  refactor replaced that with a plain `List<Runnable>` of effects (see
  Decorator above) because the effects only ever fired on the game screen
  — a one-screen concern belongs in the view, not in a view hierarchy.

A small codebase doesn't need every pattern — the value of each pattern
above is that it was solving a real, specific problem (avoiding a
subclass explosion, keeping a layer boundary honest, letting tests share
a construction path) rather than being added for its own sake.