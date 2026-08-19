# What is a "bean" in this project?

A **bean** here is a plain, mutable data holder written in JavaBean style:
private fields, a public no-arg constructor, and public getters/setters for
each field, with **no behavior or logic**.

The `XxxState` classes are the beans:

- `SetupState` — board size, win length, mode, difficulty, effect flags, and a
  transient `message` for errors shown on the setup screen.
- `GameRenderState` — holds `BoardRenderState`, `StatusRenderState`, and a
  `message`.
- `BoardRenderState`, `StatusRenderState`, `CellRenderState` — the pieces
  composed inside `GameRenderState`; everything the game frame needs to draw.

## Why the name?

It comes from the **JavaBeans** spec, which is also where the
`PropertyChange`/`PropertyChangeListener` machinery comes from — that is why
the bean conventions matter here.

## How beans are used

The view-model pattern is: one `ViewModel<T>` per view wraps **one bean** `T`.
The view model is the class that fires events; the bean is just the payload it
carries.

- **Views** (widgets) *write* into the bean — e.g. the setup panel calls
  `setupViewModel.getState().setBoardSize(...)` as the user types.
- **Presenters** also *write* into the bean (e.g. putting an error into
  `setMessage`), then call `firePropertyChanged()` on the view model so
  listeners re-render from `evt.getNewValue()`.
- **Controllers** *read* from the bean to build `InputData` when the user
  clicks Start.

## The point

The bean is the dumb "what is on screen right now" data, deliberately kept
free of game policy. Domain rules and session data live elsewhere (the engine
and `GameSessionDataAccess`); only what the frame needs to draw or the form
needs to hold goes in a bean.