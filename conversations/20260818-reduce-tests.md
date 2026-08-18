# Test cleanup: reduce redundant tests

Date: 2026-08-18

## Objective

Cut the ~5 tests whose behavior is fully duplicated elsewhere in the suite
(107 → 102), keeping boundary-wiring coverage.

## Changes

- `StartNewGameInteractorTest` 2 → 1: single wiring test asserting the fresh
  state is presented (config, board size, X's turn, InProgress) and
  `prepareFailView` never called.
- `MakeHumanMoveInteractorTest` 3 → 2: dropped the occupied-cell fail case
  (same catch→`prepareFailView` translation as the game-over case; the
  domain behavior stays covered by `GameStateTest`).
- `RequestAiMoveInteractorTest` 2 → 1: dropped the success case (covered by
  `GameStateTest.applyMove` + `GameControllerTest` AI mode); strengthened the
  guard test with a mock strategy asserting the AI is never consulted once
  the game is over.
- `GameControllerBuilderTest` 3 → 2: dropped the smoke test (start + move +
  board assertion) which duplicates `GameControllerTest`; keeps the two NPE
  validation tests, which are the builder's real contract.
- `NewGameRequestFactoryTest` 3 → 2: dropped the invalid-config propagation
  test (`GameConfigTest` already covers win-length validation).

## Verification

- `mvn clean test`: 102 tests pass.