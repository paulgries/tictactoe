package game;

import framework.ViewModel;

/**
 * The shared ViewModel for the game screen. All three use cases (start new
 * game, make human move, request AI move) render through this single view
 * model because the board is one screen. It holds two separate states: the
 * {@link SessionState} (session data shared by controllers and presenters)
 * and the {@link GameRenderState} (render data the frame draws).
 */
public class GameViewModel extends ViewModel<GameRenderState> {

    private final SessionState session = new SessionState();

    public GameViewModel() {
        super("game");
        setState(new GameRenderState());
    }

    public SessionState getSession() {
        return session;
    }
}