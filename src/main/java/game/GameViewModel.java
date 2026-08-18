package game;

import framework.ViewModel;

/**
 * The shared ViewModel for the game screen. All three use cases (start new
 * game, make human move, request AI move) render through this single view
 * model because the board is one screen.
 */
public class GameViewModel extends ViewModel<GameState> {

    public GameViewModel() {
        super("game");
        setState(new GameState());
    }
}