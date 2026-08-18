package game;

import game.domain.GameMode;

/**
 * Presentation-side game rules shared by the make-human-move controller
 * (ignore clicks while the AI is to move) and presenter (hand off to the
 * AI request after a human move).
 */
public final class GameSessionRules {

    private GameSessionRules() {
    }

    public static boolean isAiTurn(SessionState session) {
        return session.getMode() == GameMode.HUMAN_VS_AI
            && session.getCurrentGameState() != null
            && !session.getCurrentGameState().isGameOver()
            && session.getCurrentGameState().currentTurn()
                == game.domain.GameState.STARTING_MARK.other();
    }
}