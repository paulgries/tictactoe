# Presenter/AppBuilder rework + com.tictactoe prefix drop

Date: 2026-08-18

## Objective

Close the remaining CAWithBuilder deltas (user-picked "2" = the substantive
rework):

1. Per-use-case `Presenter` (implements the `OutputBoundary`, updates the
   shared `GameViewModel`/`GameState` bean, fires a PropertyChange) and thin
   `Controller` (holds the `InputBoundary`, builds `InputData` from view
   primitives).
2. `app/AppBuilder` replaces `GameControllerBuilder`; `Main` stays thin.
3. Drop the `com.tictactoe` package prefix; move sources into top-level
   capability packages (`framework/`, `game/`, `app/`).

## Changes

### Controllers and presenters per use case

- `game/start_new_game/`: `StartNewGameController` (primitives + mode +
  difficulty, stashes mode/AI strategy in the session before executing,
  `restart()` re-runs the last input) and `StartNewGamePresenter`.
- `game/make_human_move/`: `MakeHumanMoveController` (guards
  `isGameOver()`/`isAiTurn()`) and `MakeHumanMovePresenter` (fires
  `requestAiMove` when the session is AI-vs-human and it is now the AI's
  turn).
- `game/request_ai_move/`: `RequestAiMoveController` (marks
  `pendingAiBase`, runs the interactor in the background on that snapshot
  with the stashed strategy) and `RequestAiMovePresenter` (applies the
  result on the UI thread, discarding stale results).
- Deleted: `GameController`, `GameControllerBuilder`, `GameView`,
  `GameViewDecorator`, `WinEffectGameView` and their tests. Win effects are
  now a `Runnable` list on `MainFrame`.

### View-state model

- `framework/ViewModel<T>` base (copied from CAWithBuilder).
- `game/GameViewModel` extends `ViewModel<GameState>`.
- `game/GameState` bean holds both render data (board/status) and session
  data (current domain state, mode, AI strategy, `pendingAiBase`); includes
  `isGameOver()`, `isAiTurn()`, `isStaleAiResult()`.
- `MainFrame` is a `PropertyChangeListener`: renders board/status, shows
  errors (clearing `state.error`), runs `winEffects` on WIN.

### Stale-result semantics (fixed during testing)

`isStaleAiResult()` originally compared the result (base + AI move) against
`pendingAiBase` (base) — never equal, so every result was discarded. It now
compares the session's current state against `pendingAiBase`: the result is
stale only if the session moved on while the AI computed.

### Prefix drop

`sed` stripped `com.tictactoe.` from all `.java` files; the tree was moved
via `git mv` (`com/tictactoe/framework*` -> `framework/`, plus the rest into
`game/`). `pom.xml` coordinates were left untouched. `UiScheduler` moved to
`framework/`; view-model records and `GameViewModelMapper` moved to `game/`.

### Tests

- New: `StartNewGameControllerTest`, `StartNewGamePresenterTest`,
  `MakeHumanMoveControllerTest`, `MakeHumanMovePresenterTest`,
  `RequestAiMoveControllerTest`, `RequestAiMovePresenterTest` (assert on the
  real view-model effect; `CapturingUiScheduler` drives background/UI tasks;
  stale tests mutate the session between present and UI-task run).
- New test utils: `game/testutil/ImmediateUiScheduler`,
  `game/testutil/CapturingUiScheduler`.
- Deleted: `GameControllerTest` (11), `GameControllerBuilderTest` (2),
  `GameViewDecoratorTest` (3), `WinEffectGameViewTest` (5).
- `mvn clean test`: 97 tests, all green (was 100).

### AGENTS.md

Architecture section now records the presenter/controller split, the shared
`GameViewModel`/`GameState` bean, `AppBuilder` wiring, `UiScheduler` + stale
result discard, and frame-local win effects; example layout updated to the
real capability layout.

## Commits

1. `3076b32` refactor: align presenters, controllers, and wiring with
   CAWithBuilder
2. `162ff23` test: cover per-use-case controllers and presenters
3. `90d2b85` docs: record presenter/controller and view-model conventions in
   AGENTS.md

Branch: `presenter-appbuilder`, PR #7 against lindseyshorser/tictactoe.