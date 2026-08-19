# AGENTS.md

Project conventions and decisions. Copy this file into new repos (adjusting
repo-specific details) to carry the workflow forward.

## Commits

- Use **Conventional Commits**: `feat`, `fix`, `refactor`, `build`, `test`,
  `chore`, `deps`, `docs`.
- Concise subject; lowercase, no trailing period.
- Body explains the "why" when it isn't obvious.
- **Show the commit message to the user for approval before every commit.**
  Stage the intended files first, then present the message (subject + body)
  and wait for explicit approval; commit only after the user approves.

## Branching & PRs

- Branch off `main` for each piece of work. Branches may be **stacked**
  (branch off the previous branch) when several PRs will merge to `main`
  sequentially.
- Push to the user's fork; open PRs against upstream with `gh`:
  `gh pr create --repo <upstream> --base main --head <fork>:<branch>`.
- **Only submit a PR when the user explicitly requests it.** Never open a PR
  automatically (e.g. after finishing a piece of work or a commit).
- Open a PR to `main`, **Rebase and merge** to keep history linear.
- The user reviews and approves/merges the PR themselves.
- Branches are **kept** (not deleted) as teaching artifacts.
- Use `gh` for PRs, issues, and checks.

## Architecture

- **Clean Architecture**, package-by-capability.
- Structure: capability folder contains its `use_case` subpackage; layers live
  inside capabilities rather than top-level layers.
- Example layout:
  - `game/` (engine: shared domain model and AI strategies, plus
    `GameSessionDataAccess` and `GameSessionRules`)
  - `game/domain/` (shared domain model, incl. `SavedGame` snapshot)
  - `game/ai/` (AI strategies and the `CommonAiStrategyFactory` registry)
  - `setup/` (setup screen view + `SetupViewModel`/`SetupState`)
  - `setup/start_new_game/` (controller/presenter) with `use_case`
  - `play/` (game screen view: `GamePanel`, board/status panels, win-effect
    overlay, and the shared render beans `GameViewModel`/`GameRenderState`)
  - `play/make_human_move/` (controller/presenter) with `use_case`
  - `play/request_ai_move/` (controller/presenter) with `use_case`
  - `persistence/save_game/` (persistence boundary: `SaveGameDataAccess`)
  - `persistence/load_game/` (persistence boundary: `LoadGameDataAccess`)
  - `data_access/` (file-backed `SaveGameDataAccess`/`LoadGameDataAccess`
    implementation `FileGameDataAccessObject`, and the in-memory game session
    `InMemoryGameSession`, like CAWithBuilder's
    `InMemoryUserDataAccessObject`)
  - `app/` (`Main`, `AppBuilder`, window shell `MainFrame`), `framework/`
    (generic, reusable: `ViewModel`, `ViewManagerModel`, `ViewManager`,
    `UiScheduler`/`SwingUiScheduler`, `Theme`)
- Build a `CommonUser`/`CommonUserFactory` style entity for domain models.
- Each use case gets a boundary set under its `use_case` package:
  `InputBoundary`, `InputData`, `Interactor`, `OutputBoundary`, `OutputData`.
  Interactors are `void` and receive the `OutputBoundary` in their
  constructor; the `OutputBoundary` is implemented by the capability's
  **Presenter**, which updates the shared view model state and
  fires a PropertyChange; the `InputBoundary` is held by the capability's thin
  **Controller**, which builds the `InputData` from view primitives.
- View-model pattern (CAWithBuilder): one `ViewModel<T>` per view; beans are
  named `XxxState`, view models `XxxViewModel`, and views bind to their view
  model (register as a PropertyChangeListener and render from
  `evt.getNewValue()`). `ViewModel` fires with a default `"state"` property
  name or a caller-chosen one (`firePropertyChanged(String)`).
  - `SetupViewModel`/`SetupState` hold the setup screen's inputs; the panel
    writes widget values into the state as they change and the controller
    reads from it on Start; presenters put transient messages (e.g. invalid
    config, no saved game) in the state and the panel shows them.
  - `GameViewModel` holds a `GameRenderState` (board/status/message) that
    the game view renders from. Presenters update it and fire one property
    change; `GamePanel` renders from it and shows the message dialog. The
    current domain state, mode and AI difficulty live in the
    application-layer `GameSessionDataAccess` (implemented by
    `data_access/InMemoryGameSession`); interactors read and write it, and
    the `request_ai_move` presenter uses it to discard stale background
    results. Presentation-side rules shared by several use cases (e.g. "is
    the AI to move?") live in a small static helper (`GameSessionRules`),
    not on the state beans, which stay dumb.
- Navigation is presenter-driven: a `framework/ViewManager` +
  `ViewManagerModel` (`extends ViewModel<String>`) switches the card layout,
  presenters navigate by setting the view name (e.g. on success), and pure
  screen switches go through the boundaries as a second method
  (`switchToSetupView`, like CAWithBuilder's `switchToLoginView`). Card
  names come from `getViewName()` on the views (delegating to their view
  models).
- Wiring in `app/AppBuilder` (one fluent method per view and per use case;
  views are registered on the window's card panel under their view name,
  like CAWithBuilder's `addXxxView()`); `Main` stays thin. The window shell
  `app/MainFrame` knows nothing about the game.
- Controllers/presenters may inject a `UiScheduler` (`framework`) to move
  work off the UI thread; stale background results are discarded by comparing
  the session state against the base the result was computed from.
- A one-frame presentation or cross-cutting logic (win effects) that only
  serves the game screen belongs inside the game view (`GamePanel`, e.g. a
  `Runnable` effect list), not in dedicated classes.

## Testing

- **JUnit 5 + Mockito** (`mockito-junit-jupiter`).
- Build the class under test (e.g. interactor) in `@BeforeEach`, after mocks
  are injected.
- Use real entities/implementations where possible; mock only boundaries
  (DAOs, presenters).
- Assert on the real effect (e.g. captured saved user), not just method calls.
- Boundary/interactor tests verify **wiring** (presenter called / never
  called, what was passed) without re-asserting domain mechanics covered by
  domain tests; drop tests that fully duplicate lower-layer coverage (one
  fail path suffices when cases share the same translation).
- Extract repeated fixtures into a shared test helper
  (e.g. `game.testutil.GameFixtures`) instead of duplicating
  board/state-building sequences in each test class.
- Run `mvn clean test` — incremental compilation can report false positives.
  `mvn` output is the source of truth; ignore stale editor/LSP diagnostics
  on in-progress branches.
- Test names: `Method_Condition_Expectation` style.

## Naming conventions

- **Types**: `PascalCase` (`SignupInteractor`).
- **Methods/fields**: `camelCase`.
- **Constants**: `SCREAMING_SNAKE_CASE`.
- **Packages**: all lowercase; capability-based, no underscores/camelCase.
- **Branches**: `kebab-case`, short and descriptive (`add-conversation-log`).
- **Commits**: Conventional Commit types (see above).

## Environment

- macOS, zsh shell.
- **BSD `sed`** — does not support GNU `\b` word boundaries; use `[^...]`
  classes or alternate tools.

## AI-usage tracking

- Commit the live session transcript (`conversations/<session>.md`) as the
  durable record of AI-assisted work; git history is the per-commit record.
- To resume context, use opencode's `/sessions` (or `/compact`), **not** by
  loading the transcript file back in as context.

## Build

- Maven project (`pom.xml`), Java. Verify with `mvn clean test`.