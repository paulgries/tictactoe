# Test cleanup: shared game fixtures

Date: 2026-08-18

## Objective

Remove the duplicated win/draw board-building sequences from the test suite
(part of the test cleanup analysis; item #1).

## Change

Added `com.tictactoe.game.testutil.GameFixtures` with:
- `wonByX()` / `drawn()` — 3x3 `GameState` fixtures (won top row / full draw).
- `wonByXBoard()` / `wonByOBoard()` / `drawnBoard()` — 3x3 `Board` fixtures.

Refactored tests to use them:
- `GameStateTest` (wonByX x3, drawn x1)
- `GameViewModelMapperTest` (wonByX x2, drawn x1)
- `MakeHumanMoveInteractorTest`, `RequestAiMoveInteractorTest` (wonByX)
- `BoardEvaluatorTest` (wonByXBoard, wonByOBoard, drawnBoard)
- `WinCheckerTest` (drawnBoard)

`GameControllerTest` builds win/draw boards through controller clicks, so it
keeps its own sequences (integration-style; not replaced).

## Verification

- `mvn clean test`: 107 tests pass.