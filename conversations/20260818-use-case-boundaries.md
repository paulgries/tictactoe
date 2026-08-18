# Use-case boundaries refactor (mimic CAWithBuilder)

Date: 2026-08-18

## Objective

Mimic the CAWithBuilder sample's use-case boundary pattern in tictactoe:
per use case an `InputBoundary` / `InputData` / `Interactor` / `OutputBoundary` /
`OutputData`, with the controller depending on input boundaries and acting as the
interim presenter (the dedicated Presenter/State/ViewModel/View rework was
explicitly out of scope).

## Scope (user-selected)

- In: use-case boundaries; retarget use-case tests to the boundaries.
- Out (deferred): package naming (drop `com.tictactoe`), Presenter/State/
  ViewModel/View rework, AppBuilder, `data_access` (nothing persisted).

## Decisions

- All three interactors are void and take an `OutputBoundary` in the constructor,
  mirroring `SignupInteractor` from CAWithBuilder.
- `GameController` implements the three `OutputBoundary` interfaces (interim
  presenter) and constructs its interactors internally with `this`, breaking the
  wiring cycle.
- A `pendingAiBase` field tracks the state an async AI move was computed against;
  `prepareSuccessView(RequestAiMoveOutputData)` runs on the UI thread and applies
  only when `currentState.equals(pendingAiBase)` (discards stale results after a
  restart, matching the existing async tests).
- `GameControllerBuilder` drops the three use-case override methods; the controller
  wires them internally.

## Files changed

- New `use_case/` boundary classes: `StartNewGame{InputBoundary,InputData,Interactor,
  OutputBoundary,OutputData}`, `MakeHumanMove{...}`, `RequestAiMove{...}` (15 files).
- `GameController` rewritten to hold input-boundary fields and implement the three
  output boundaries.
- `GameControllerBuilder` simplified (removed use-case overrides).
- Tests `StartNewGameInteractorTest`, `MakeHumanMoveInteractorTest`,
  `RequestAiMoveInteractorTest` — `@ExtendWith(MockitoExtension)`, `@Mock`
  OutputBoundary, `@BeforeEach`, `ArgumentCaptor` on real effects, `Method_Condition_
  Expectation` names.
- Deleted the three `*UseCase` classes and `*UseCaseTest` classes.

## Branching note (Phase B repair)

Commit `0626349` (package-by-capability) moved files with `git mv` but left the
package declarations inside them stale, so HEAD did not compile. Per user decision,
that repair was committed to `package-by-capability` (ccba73c), and this boundary
work sits on a new `use-case-boundaries` branch stacked on top of it.

## Verification

- `mvn clean test`: 107 tests pass.