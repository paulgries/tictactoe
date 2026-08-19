package game;

import game.domain.GameMode;
import game.domain.GameState;

/**
 * Game rules shared by the make-human-move use case (ignore moves while the
 * AI is to move, and report whether the AI is to move next). They live in
 * the engine because both the interactor and the controller need them.
 */
public final class GameSessionRules {

    private GameSessionRules() {
    }

    public static boolean isAiTurn(GameMode mode, GameState state) {
        return mode == GameMode.HUMAN_VS_AI
            && state != null
            && !state.isGameOver()
            && state.currentTurn() == GameState.STARTING_MARK.other();
    }
}