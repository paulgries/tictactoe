# CAWithBuilder alignment: capability folders + request folding

Date: 2026-08-18

## Objective

Re-compare tictactoe against CAWithBuilder and close the two mechanical
deltas:

1. Per-use-case capability folders (each capability owns its `use_case/`).
2. Fold the `NewGameRequest` middleman: controllers take primitives and
   build `InputData` themselves, as CAWithBuilder controllers do.

## Changes

### #2 Per-use-case capability folders

Moved (git mv + package fix) the shared `game/use_case/` into:

- `game/start_new_game/use_case` — StartNewGame boundaries + interactor
- `game/make_human_move/use_case` — MakeHumanMove boundaries + interactor
- `game/request_ai_move/use_case` — RequestAiMove boundaries + interactor

Interactor tests moved to match. `GameController` imports updated.

### #1 NewGameRequest folding

- `StartNewGameInputData` is now
  `record StartNewGameInputData(GameConfig config, GameMode mode,
  Optional<AiDifficulty> aiDifficulty)`.
- `StartNewGameInteractor` reads `inputData.config()`.
- `GameController.onStartGameRequested(int boardSize, int winLength,
  GameMode, Optional<AiDifficulty>)` builds the input data directly
  (GameConfig constructor still validates); stores the input data for
  `onRestartRequested()`.
- Deleted `NewGameRequest`, `NewGameRequestFactory`,
  `NewGameRequestFactoryTest`.
- `SetupPanel` calls the controller with primitives.
- `GameControllerTest` uses a `startTwoPlayerGame(controller)` helper and
  inline primitive args for the AI mode.

## Verification

- `mvn clean test`: 100 tests pass (was 102; factory test removed).

## Next (deferred, per user decision)

- Presenter/ViewModel/State per use case + `app/AppBuilder` rework.
- Drop `com.tictactoe` package prefix.