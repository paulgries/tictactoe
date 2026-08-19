# Testing structure in this project

The tests mirror `src/main` package-for-package, so the structure follows the
capability split: `game/` (engine), `setup/`, `play/`, `persistence/`,
`data_access/`, and `framework/`.

## Test tiers

- `game/domain/`, `game/ai/` — pure unit tests of the engine
  (`BoardTest`, `GameStateTest`, `MinimaxAiStrategyTest`, ...). Real domain
  objects, no mocks.
- `framework/ViewModelTest`, `data_access/` tests — infrastructure: the
  `ViewModel` bean, `FileGameDataAccessObject` (file round-trip),
  `InMemoryGameSession`.
- Each capability package (`setup/`, `play/`, `persistence/`) has three
  tiers, matching the layers of a use case:
  - `use_case/` — **interactor tests** (`*InteractorTest`): mock the
    `OutputBoundary` and the `DataAccess`, use the *real*
    `InMemoryGameSession`, and assert both wiring (`verify(presenter)
    .prepareSuccessView(...)`, never `prepareFailView`) *and* the real side
    effect (e.g. `LoadGameInteractorTest` checks the session state actually
    got restored).
  - capability root — **presenter tests** (`*PresenterTest`): real view
    models plus a real `PropertyChangeListener` that captures the fired
    value, asserting the render state contents, that exactly one property
    change fired, and that navigation happened.
  - capability root — **controller tests** (`*ControllerTest`): mock the
    `InputBoundary`, `verify` it got called with the right `InputData`.
    These are the "thin, delegate-only" layer.

## Shared test helpers

`game/testutil/` holds:

- `GameFixtures` — shared board/state builders (`wonByX()`, `drawn()`,
  `newGame3x3()`), so domain-heavy setups aren't duplicated per test class.
- `CapturingUiScheduler` — a fake `UiScheduler` for presenter/controller
  tests that run async work synchronously.

## Conventions

- JUnit 5 + Mockito, class-under-test built in `@BeforeEach` after mocks are
  injected.
- Real implementations preferred over mocks; mock only boundaries (DAOs,
  presenters).
- Boundary/interactor tests check wiring only (presenter called / never
  called, what was passed) without re-asserting domain mechanics covered by
  domain tests.
- Test names: `Method_Condition_Expectation` style.
- Run `mvn clean test` — incremental compilation can report false positives;
  `mvn` output is the source of truth.