# AGENTS.md

Project conventions and decisions. Copy this file into new repos (adjusting
repo-specific details) to carry the workflow forward.

## Commits

- Use **Conventional Commits**: `feat`, `fix`, `refactor`, `build`, `test`,
  `chore`, `deps`, `docs`.
- Concise subject; lowercase, no trailing period.
- Body explains the "why" when it isn't obvious.

## Branching & PRs

- Branch off `main` for each piece of work.
- Open a PR to `main`, **Rebase and merge** to keep history linear.
- The user reviews and approves/merges the PR themselves.
- Branches are **kept** (not deleted) as teaching artifacts.
- Use `gh` for PRs, issues, and checks.

## Architecture

- **Clean Architecture**, package-by-capability.
- Structure: capability folder contains its `use_case` subpackage; layers live
  inside capabilities rather than top-level layers.
- Example layout:
  - `account/signup/use_case`
  - `account/change_password/use_case`
  - `authentication/login/use_case`
  - `user/` (shared domain entity)
  - `data_access/`, `framework/`, `app/`
- Build a `CommonUser`/`CommonUserFactory` style entity for domain models.

## Testing

- **JUnit 5 + Mockito** (`mockito-junit-jupiter`).
- Build the class under test (e.g. interactor) in `@BeforeEach`, after mocks
  are injected.
- Use real entities/implementations where possible; mock only boundaries
  (DAOs, presenters).
- Assert on the real effect (e.g. captured saved user), not just method calls.
- Run `mvn clean test` — incremental compilation can report false positives.
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