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

## Follow-up: per-frame AppBuilder + Main in app (PR #7, extended)

Delta analysis vs CAWithBuilder surfaced several remaining gaps; the user
picked "1: per-frame AppBuilder + Main in app".

- `AppBuilder` now has one fluent method per frame and per use case
  (`addGameView()`, `addRequestAiMoveUseCase()`, `addMakeHumanMoveUseCase()`,
  `addStartNewGameUseCase()`), each returning `this`; `build()` returns the
  frame. `requestAiMoveController` is a field because
  `addMakeHumanMoveUseCase()` wires `MakeHumanMovePresenter` with
  `requestAiMoveController::execute` — order matters, as in CAWithBuilder
  (views before use cases).
- `MainFrame.setControllers(...)` split into per-controller setters
  (`setStartNewGameController`, `setMakeHumanMoveController`), mirroring
  CAWithBuilder's `view.setXController(...)`.
- `Main` moved from `framework/` to `app/` (git mv), chains the builder and
  calls `pack()` + `setVisible(true)`; dropped the `SwingUtilities.invokeLater`
  wrapper to match CAWithBuilder. pom `mainClass` updated to `app.Main`.
- Smoke-tested: `mvn exec:java` launches the frame without exceptions.
- 97 tests still green.

Not picked (recorded for later): `firePropertyChanged(String)` overload.

## Commits (follow-up)

4. `f80c7ef` fix: update exec mainClass after com.tictactoe prefix drop
5. `e310f3b` chore: commit AGENTS.md auto-reading check transcript
6. `3f065d7` refactor: wire the frame per use case in AppBuilder and move
   Main to app

## Follow-up: interactor-injected factories + thin controller

User picked "2" from the delta list.

- `GameStateFactory` became a `CommonUserFactory`-style interface with
  `CommonGameStateFactory` as its implementation; the entity's static
  `GameState.newGame` convenience now constructs directly.
- Both `GameStateFactory` and `AiStrategyFactory` are injected into
  `StartNewGameInteractor`'s constructor (CAWithBuilder order: boundary,
  factories). The interactor creates the AI strategy from the input
  difficulty and carries mode + strategy in `StartNewGameOutputData`.
- `StartNewGamePresenter` now stashes mode + strategy in the shared bean;
  `StartNewGameController` shrinks to holding only the `InputBoundary`
  (plus its `lastInput` cache for `restart()`).
- `AppBuilder` holds the factories as fields, as CAWithBuilder holds
  `userFactory`.
- Tests: interactor test adds an AI-mode case and real-factory constructor
  args; controller test drops the session-stash assertions (moved to the
  presenter test); presenter test covers the stash. 99 tests green.

## Follow-up: presenter-driven navigation via a ViewManager

User picked "3" from the delta list.

- Added `framework/ViewManager` and `framework/ViewManagerModel`
  (`extends ViewModel<String>`) copied in behavior from CAWithBuilder.
- `StartNewGamePresenter` navigates: `prepareSuccessView` sets the
  `ViewManagerModel` to `gameViewModel.getViewName()` ("game"); both
  boundaries gained `switchToSetupView()` (interactor pass-through,
  controller delegate) so "Change Settings" in `StatusPanel` navigates
  presenter-driven like CAWithBuilder's `switchToLoginView`.
- Card names come from the views: `SetupPanel.getViewName()` returns
  "setup"; the game card is registered under `gameViewModel.getViewName()`.
  `MainFrame` dropped `showGameScreen()`/`showSetupScreen()` and exposes
  `getCardPanel()`/`getCardLayout()` for the ViewManager; `SetupPanel` and
  `StatusPanel` no longer hold a `MainFrame` reference.
- `AppBuilder.build()` shows the initial setup card via the ViewManagerModel
  (mirroring CAWithBuilder's `build()`).
- Tests: presenter test asserts navigation ("game"/"setup", fail path
  doesn't navigate); controller and interactor tests cover
  `switchToSetupView` wiring. 102 tests green.

## Commits (later follow-ups)

7. `960415a` docs: require commit-message approval in AGENTS.md
8. `744cc41` docs: require explicit request before opening PRs in AGENTS.md
9. `46abbe3` refactor: inject factories into StartNewGameInteractor and thin
   the controller
10. `967df93` refactor: make navigation presenter-driven via a ViewManager

Branch: `presenter-appbuilder`, PR #7 against lindseyshorser/tictactoe.