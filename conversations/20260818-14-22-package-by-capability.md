# Session: Restructure to package-by-capability

Date: 2026-08-18

## Goal

Execute Phase B of the AGENTS.md alignment: move from package-by-layer
(`domain`/`application`/`adapters`/`infra`) to package-by-capability with a
`use_case` subpackage.

## Decision

Single `game` capability + top-level `framework` shell. AI is a domain
sub-package (`game/ai`) rather than a peer capability, because the strategies
operate on `Board`/`GameState` — making AI a peer would create a package
cycle (`GameController` uses AI, AI uses game domain).

## Work done

- `git mv` main + test sources into:
  - `game/domain`, `game/domain/exception`
  - `game/ai` (strategies + `AiStrategyFactory`)
  - `game/use_case` (`StartNewGameUseCase`, `MakeHumanMoveUseCase`,
    `RequestAiMoveUseCase`)
  - `game/adapters`, `game/adapters/viewmodel`, `game/NewGameRequest`
  - `framework/`, `framework/ui`
- Ordered sed of `package`/`import` declarations (specific prefixes before
  general; e.g. `domain.ai` before `domain`).
- Special-cased `AiStrategyFactory` (main + test) package -> `game.ai`.
- Updated `pom.xml` `exec:java` `mainClass` -> `com.tictactoe.framework.Main`.
- Added `GameStateFactory` in `game/domain` (AGENTS.md CommonXxxFactory
  convention); `GameState.newGame` now delegates to it and
  `StartNewGameUseCase` uses the factory.
- Verified `mvn clean test`: 107 tests pass.

## Notes

- `conversations/` historical transcripts left untouched (durable records);
  only pom.xml's mainClass referenced an old package among live files.
- Mockito self-attach warning persists; not failing.