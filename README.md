# Tic-Tac-Toe

A desktop Tic-Tac-Toe game (see the `TicTacToe/` folder), built in Java with a
Swing UI. Play two-player locally or against a computer opponent on a
configurable board, from the classic 3x3 up to a 10x10 board with a longer
line needed to win.

This was developed by Lindsey Shorser with Claude Code on August 12-13, 2026. For a description of the design patterns that were included, see `TicTacToe/DesignPattersn.md`.

## Running it

```
cd TicTacToe
./run.sh
```

## Choosable options

Set on the start screen before each game:

- **Board size** — 2x2 up to 10x10.
- **Win length** — how many marks in a row are needed to win; capped at the
  board size.
- **Game mode** — Two Players (pass and play locally) or vs Computer.
- **AI difficulty** *(vs Computer only)* — Easy (random moves), Medium
  (takes an obvious win or block when available), or Difficult (plays a
  full lookahead search and rarely loses).

## Toggle-able options

Independent on/off switches, also on the start screen:

- **Win effects** — confetti, fireworks, and twinkling X's & O's each play
  when a game is won; toggle any combination on or off.
- **Night Mode** — switches the whole UI between a light "Day Mode" and a
  dark "Night Mode" palette, live, without needing to restart.



