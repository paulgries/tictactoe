# Design Patterns in This Codebase

This document catalogs the design patterns implemented in this Tic-Tac-Toe
project, with pointers to the exact classes and a short explanation of how
each one works *in this codebase specifically* (not just the textbook
definition). It's written for software design students who want to see
these patterns in a small, complete, real system rather than an isolated
toy example.

The codebase follows Clean Architecture (see `CLAUDE.md`): `domain` →
`application` → `adapters` → `infra`, with dependencies only ever pointing
inward. Several of the patterns below exist specifically *because of* that
constraint — for example, `Adapter` is how the inner layers stay ignorant
of Swing.

Patterns are grouped by GoF category, followed by a section on
architectural idioms that show up alongside them.

---

## Creational Patterns

### Factory Method / Simple Factory

**Intent:** Centralize the decision of *which concrete class to instantiate*
behind a single method, so callers depend on an abstraction instead of a
list of constructors.

**Where:**
- [`AiStrategyFactory.create(AiDifficulty)`](src/main/java/com/tictactoe/application/AiStrategyFactory.java)
- [`NewGameRequestFactory.create(...)`](src/main/java/com/tictactoe/adapters/NewGameRequestFactory.java)
- [`GameState.newGame(GameConfig)`](src/main/java/com/tictactoe/domain/GameState.java) — a *static factory method* (Effective Java, Item 1), a lighter cousin of the full Factory Method pattern: an alternative named constructor rather than a class whose whole job is choosing between subtypes.

```java
public AiStrategy create(AiDifficulty difficulty) {
    return switch (difficulty) {
        case EASY -> new RandomAiStrategy(new Random());
        case MEDIUM -> new EasyAiStrategy();
        case DIFFICULT -> new MinimaxAiStrategy();
    };
}
```

`AiStrategyFactory` is the clearest example: `GameController` never
constructs an `AiStrategy` implementation directly — it asks the factory
for one given an `AiDifficulty`, so adding a fourth difficulty later means
touching one `switch` expression instead of every call site.

---

### Builder

**Intent:** Separate the *construction* of a complex object from its
representation, especially when it has several dependencies, some
optional, that would otherwise force a long or repeated constructor call.

**Where:** [`GameControllerBuilder`](src/main/java/com/tictactoe/adapters/GameControllerBuilder.java)

```java
GameController controller = new GameControllerBuilder()
    .view(frame)
    .uiScheduler(new SwingUiScheduler())
    .build();
```

`GameController` has six dependencies. Two of them — the `GameView` and
the `UiScheduler` — are delivery-mechanism specifics with no sensible
default and are required. The other four (`StartNewGameUseCase`,
`MakeHumanMoveUseCase`, `RequestAiMoveUseCase`, `AiStrategyFactory`) are
stateless and identical for every caller, so the builder defaults them
and only tests need to override one. This is why the pattern earns its
keep here: both [`Main.java`](src/main/java/com/tictactoe/infra/Main.java)
(production) and
[`GameControllerTest`](src/test/java/com/tictactoe/adapters/GameControllerTest.java)
(tests) share the same assembly path instead of duplicating a
six-argument constructor call, only varying the one or two fields each
caller actually cares about.

---

## Structural Patterns

### Decorator

**Intent:** Attach additional behavior to an object dynamically by
wrapping it in another object that implements the same interface, so
behaviors can be layered and composed without subclassing every
combination.

**Where:**
- [`GameViewDecorator`](src/main/java/com/tictactoe/adapters/GameViewDecorator.java) — abstract base that forwards every `GameView` call to a wrapped delegate.
- [`WinEffectGameView`](src/main/java/com/tictactoe/adapters/WinEffectGameView.java) — concrete decorator that runs an extra `Runnable` whenever the status becomes a `WIN`.
- Wired together in [`Main.java`](src/main/java/com/tictactoe/infra/Main.java)

```java
GameView celebratingView = new WinEffectGameView(
    new WinEffectGameView(
        new WinEffectGameView(
            frame, ifEnabled(frame::isConfettiEffectEnabled, effects::playConfetti)),
        ifEnabled(frame::isFireworksEffectEnabled, effects::playFireworks)),
    ifEnabled(frame::isMarksEffectEnabled, effects::playMarks));
```

Three win-celebration effects (confetti, fireworks, twinkling X's/O's) are
stacked around the base `MainFrame` view, one `WinEffectGameView` per
effect. `MainFrame` itself is completely unaware this feature exists —
every layer just forwards to the one inside it, and only adds behavior
when the status is a win. This is the textbook motivating case for
Decorator: without it, supporting "confetti only," "fireworks only," and
"all three together" would mean a new subclass for every combination.

---

### Adapter

**Intent:** Convert the interface of one class into an interface a client
expects, so two otherwise-incompatible pieces can work together — commonly
used to keep a framework-specific implementation behind a
framework-agnostic port.

**Where:**
- [`UiScheduler`](src/main/java/com/tictactoe/adapters/UiScheduler.java) (port) ↔ [`SwingUiScheduler`](src/main/java/com/tictactoe/infra/ui/SwingUiScheduler.java) (adapter)
- [`GameView`](src/main/java/com/tictactoe/adapters/GameView.java) (port) ↔ [`MainFrame`](src/main/java/com/tictactoe/infra/ui/MainFrame.java) (adapter)

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

`GameController` (in `adapters`) needs to run AI move calculations off the
UI thread and then hop back onto it — but `adapters` must never import
Swing. `UiScheduler` is the port it depends on instead; `SwingUiScheduler`
(in `infra`) adapts Swing's `SwingUtilities.invokeLater` and an
`ExecutorService` to that port. This is what makes the codebase's Clean
Architecture layering (see `CLAUDE.md`) actually enforceable at compile
time, not just a convention: `adapters` physically cannot `import
javax.swing.*`.

---

## Behavioral Patterns

### Strategy

**Intent:** Define a family of interchangeable algorithms behind a common
interface, and let the algorithm vary independently of the code that uses
it.

**Where:** [`AiStrategy`](src/main/java/com/tictactoe/domain/ai/AiStrategy.java), implemented by [`RandomAiStrategy`](src/main/java/com/tictactoe/domain/ai/RandomAiStrategy.java), [`EasyAiStrategy`](src/main/java/com/tictactoe/domain/ai/EasyAiStrategy.java), and [`MinimaxAiStrategy`](src/main/java/com/tictactoe/domain/ai/MinimaxAiStrategy.java)

```java
public interface AiStrategy {
    Position selectMove(Board board, GameConfig config, Mark aiMark);
}
```

[`RequestAiMoveUseCase`](src/main/java/com/tictactoe/application/usecase/RequestAiMoveUseCase.java)
is handed an `AiStrategy` and calls `selectMove` without knowing or caring
whether it's picking a random empty cell, checking for an immediate
win/block, or running minimax with alpha-beta pruning. Each
implementation is a fully independent algorithm — this is what
distinguishes Strategy from Template Method below: here the *entire*
algorithm varies, not just a few steps of a shared skeleton.

---

### Command (Interactor)

**Intent:** Encapsulate a request — or in this case, a single use case —
as an object with a uniform "execute" entry point, decoupling the thing
that triggers an operation from the code that performs it.

**Where:** [`StartNewGameUseCase`](src/main/java/com/tictactoe/application/usecase/StartNewGameUseCase.java), [`MakeHumanMoveUseCase`](src/main/java/com/tictactoe/application/usecase/MakeHumanMoveUseCase.java), [`RequestAiMoveUseCase`](src/main/java/com/tictactoe/application/usecase/RequestAiMoveUseCase.java)

```java
public final class MakeHumanMoveUseCase {
    public GameState execute(GameState state, Position position) {
        return state.applyMove(position);
    }
}
```

Clean Architecture calls these "Interactors" or "Use Cases," but
structurally each one *is* the Command pattern: a single class wrapping a
single operation behind one `execute(...)` method, with no other public
behavior. `GameController` holds references to all three and calls
`execute` on whichever one a UI event demands, without needing to know
how any of them work internally.

---

### State (via a sealed type hierarchy)

**Intent:** Let an object's behavior change based on which of a fixed set
of states it's in, typically by giving each state its own class.

**Where:** [`GameStatus`](src/main/java/com/tictactoe/domain/GameStatus.java) — a sealed interface permitting only [`InProgress`](src/main/java/com/tictactoe/domain/InProgress.java), [`Win`](src/main/java/com/tictactoe/domain/Win.java), and [`Draw`](src/main/java/com/tictactoe/domain/Draw.java)

```java
public sealed interface GameStatus permits InProgress, Win, Draw {}
public record Win(Mark winner, List<Position> winningLine) implements GameStatus {}
```

This is the modern Java take on State: instead of giving each state a
virtual method that the client calls polymorphically (the classic GoF
shape), the *client* pattern-matches on which state it has, and the
compiler guarantees every `instanceof` chain is exhaustive because the
interface is `sealed`. See it consumed in
[`GameViewModelMapper`](src/main/java/com/tictactoe/adapters/GameViewModelMapper.java)
and
[`MinimaxAiStrategy`](src/main/java/com/tictactoe/domain/ai/MinimaxAiStrategy.java#L47-L52).
It's worth comparing this to classic State: here the *data* differs per
state (a `Win` carries a winner and a winning line; `InProgress` and
`Draw` carry nothing), which is awkward to express with polymorphic
methods but natural with a sealed hierarchy of records.

---

### Template Method

**Intent:** Define the skeleton of an algorithm in a base class method,
deferring specific steps to subclasses, so the overall structure can't
drift between implementations even as the details vary.

**Where:** `WinEffect` (private abstract nested class) in [`EffectOverlayPanel`](src/main/java/com/tictactoe/infra/ui/EffectOverlayPanel.java), extended by `ConfettiEffect`, `FireworksEffect`, and `MarksEffect`

```java
private abstract static class WinEffect {
    final void play(int width, int height) { spawnParticles(width, height); active = true; }
    final void tick(long now) {
        if (!active) return;
        advance(now);
        if (isExpired(now)) { clearParticles(); active = false; }
    }
    final void paint(Graphics2D g2) { if (active) paintParticles(g2); }

    abstract void spawnParticles(int width, int height);
    abstract void advance(long now);
    abstract boolean isExpired(long now);
    abstract void paintParticles(Graphics2D g2);
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
- Native Swing listeners in [`SetupPanel`](src/main/java/com/tictactoe/infra/ui/SetupPanel.java) (`ItemListener`, `ChangeListener`, `ActionListener`) and [`BoardPanel`](src/main/java/com/tictactoe/infra/ui/BoardPanel.java) (`ActionListener` per cell button).
- A lighter, one-way variant: [`GameController`](src/main/java/com/tictactoe/adapters/GameController.java) pushes render calls to whatever `GameView` it holds via `displayBoard`/`displayStatus`/`displayError`.

```java
boardSizeSpinner.addChangeListener(e -> clampWinLengthToBoardSize());
```

The Swing listeners are the textbook shape of Observer — a `JSpinner`
(subject) notifies a registered `ChangeListener` (observer) whenever its
value changes, with no compile-time dependency between them beyond the
listener interface. The controller→view relationship is a related but
weaker idea: there's exactly one observer (`GameView`), it's supplied at
construction rather than registered dynamically, and there's no
subscribe/unsubscribe — closer to a "push-based callback" than full
Observer, but built from the same motivation (decouple the producer of a
change from whoever needs to react to it).

---

## Architectural Idioms Worth Knowing Alongside These

These aren't in the classic 23 GoF patterns, but they show up constantly
in real codebases next to the patterns above, and several are visible
here.

### Mediator / Presenter (MVP)

[`GameController`](src/main/java/com/tictactoe/adapters/GameController.java)
sits between the passive `GameView`, the three use cases, and the
`AiStrategy`, coordinating every interaction between them. `MainFrame`
never calls a use case directly — it only renders whatever view model the
controller hands it. That makes this Model-View-**Presenter** rather than
classic MVC (where the view often talks to the model directly): the view
here is entirely passive, and the controller/presenter owns all
coordination logic, which is also what makes `GameControllerTest` able to
exercise the whole app without a single Swing component.

### Mapper / DTO

[`GameViewModelMapper`](src/main/java/com/tictactoe/adapters/GameViewModelMapper.java)
translates the domain's `GameState` into UI-facing `BoardViewModel` and
`StatusViewModel` records. This keeps `domain` ignorant of anything
presentation-related (no `GameState` field ever needs to know it might
someday be rendered as a color or a string), and keeps the mapping logic
itself in one testable, static-method place rather than scattered across
Swing components.

### Immutable Value Object / Persistent Data Structure

[`Board.placeMark()`](src/main/java/com/tictactoe/domain/Board.java#L30-L40)
copies its backing array and returns a *new* `Board` rather than mutating
in place; [`GameState.applyMove()`](src/main/java/com/tictactoe/domain/GameState.java#L18-L31)
likewise returns a new `GameState`. Combined with `Position`, `GameConfig`,
`NewGameRequest`, and every `viewmodel` type being a Java `record`, this
gives the whole domain model value semantics: nothing holding a reference
to a `GameState` needs to worry about another part of the program
mutating it out from under them.

### Non-instantiable Utility Class

[`WinChecker`](src/main/java/com/tictactoe/domain/WinChecker.java),
[`LineGenerator`](src/main/java/com/tictactoe/domain/LineGenerator.java),
[`BoardEvaluator`](src/main/java/com/tictactoe/domain/ai/BoardEvaluator.java),
and [`MoveOrderer`](src/main/java/com/tictactoe/domain/ai/MoveOrderer.java)
are all `final` classes with a private no-arg constructor and only static
methods — pure functions grouped by topic, not objects with identity or
state. Not the GoF Singleton (there's no lazy instantiation, no instance
at all, and nothing controls *access* to a shared instance) — just the
"this doesn't need to be an object" idiom (Effective Java, Item 4).

### Test Double (Fake / Stub / Spy)

[`FakeGameView`](src/test/java/com/tictactoe/adapters/FakeGameView.java)
records whatever was last rendered instead of drawing anything (a
**Fake**); [`ImmediateUiScheduler`](src/test/java/com/tictactoe/adapters/ImmediateUiScheduler.java)
runs "background" and "UI thread" work synchronously on the calling
thread (a **Stub**); [`CapturingUiScheduler`](src/test/java/com/tictactoe/adapters/CapturingUiScheduler.java)
queues tasks so a test can step through them one at a time and assert on
in-between states (a **Spy**). All three exist only because `GameView`
and `UiScheduler` are ports (see Adapter, above) — without that
abstraction boundary, tests would have no seam to substitute a double
into.

---

## A Note on What's *Not* Here

It's worth noticing what's absent, and why forcing a pattern in would have
been the wrong call:

- **No Singleton.** Nothing in this program needs global, controlled
  access to a single instance — every collaborator is constructed once
  and passed in explicitly (see `GameControllerBuilder` and `Main.java`).
- **No Composite or Visitor**, despite `GameStatus` being a sealed
  hierarchy — the hierarchy is flat (three leaf types, no tree structure)
  and consumers already get exhaustive, type-safe dispatch for free from
  `sealed` + `instanceof` pattern matching, so a Visitor would only add
  ceremony.
- **No Chain of Responsibility.** There's no pipeline of handlers that
  might or might not process a request and pass it along.

A small codebase doesn't need every pattern — the value of each pattern
above is that it was solving a real, specific problem (avoiding a
subclass explosion, keeping a layer boundary honest, letting tests share
a construction path) rather than being added for its own sake.
