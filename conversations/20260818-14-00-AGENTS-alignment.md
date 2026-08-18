# Session: Align project with AGENTS.md

Date: 2026-08-18

## Goal

Bring this repo into line with `AGENTS.md`: Maven build, JUnit 5 + Mockito
tests, reconciled docs, git discipline.

## Decisions (from scoping)

- Maven project lives at repo root (flattened out of `TicTacToe/`).
- Package-by-capability restructure is DEFERRED to a later pass (incremental).
- Boundary test fakes (GameView, UiScheduler) ported to Mockito.

## Work done

1. Flattened `TicTacToe/` contents to repo root via `git mv`.
2. Removed build artifacts (`out/`), IDE config (`.idea/`, `*.iml`), vendored
   jars (`lib/`), and the plain-`javac` `build.sh`/`test.sh`.
3. Added `pom.xml` (Java 17, surefire, junit-jupiter, mockito-junit-jupiter,
   assertj). Rewrote `run.sh` to `mvn compile exec:java`.
4. Added `.gitignore` (`out/`, `target/`, `.idea/`, `*.iml`, `lib/`).
5. Ported adapter tests to Mockito: deleted `FakeGameView`,
   `ImmediateUiScheduler`, `CapturingUiScheduler`; rewrote `GameControllerTest`,
   `GameControllerBuilderTest`, `GameViewDecoratorTest`, `WinEffectGameViewTest`
   to mock `GameView`/`UiScheduler` boundaries (ArgumentCaptor for real effects).
6. Deleted `CLAUDE.md` (contradicted AGENTS.md); fixed README `DesignPattersn.md`
   typo and stale `TicTacToe/` path references.
7. Verified `mvn clean test`: 107 tests pass.

## Notes / follow-ups

- Mockito logs a self-attachment warning (inline mock maker); not failing.
- Deferred Phase B: package-by-capability restructure with `use_case`
  subpackages.
- `usecase` -> `use_case` package rename deferred with Phase B.