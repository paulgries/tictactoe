package play;

import framework.ViewModel;

/**
 * The ViewModel for the game screen. All the play use cases render through
 * this single view model because the board is one screen. The current game
 * session lives in the application layer ({@link GameSessionDataAccess}),
 * not in the presentation.
 */
public class GameViewModel extends ViewModel<GameRenderState> {

    public GameViewModel() {
        super("game");
        setState(new GameRenderState());
    }
}