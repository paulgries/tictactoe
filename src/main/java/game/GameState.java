package game;

import game.ai.AiStrategy;
import game.domain.GameMode;
import game.domain.Mark;
import java.util.Optional;

/**
 * The state for the Game View Model: everything the board view needs to render
 * plus the session data the controllers and presenters share across use cases
 * (the current domain state, the game mode, and the AI strategy).
 */
public class GameState {

    private game.domain.GameState currentGameState;
    private GameMode mode;
    private Optional<AiStrategy> aiStrategy = Optional.empty();
    private BoardViewModel board;
    private StatusViewModel status;
    private String error;

    public game.domain.GameState getCurrentGameState() {
        return currentGameState;
    }

    public void setCurrentGameState(game.domain.GameState currentGameState) {
        this.currentGameState = currentGameState;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public Optional<AiStrategy> getAiStrategy() {
        return aiStrategy;
    }

    public void setAiStrategy(Optional<AiStrategy> aiStrategy) {
        this.aiStrategy = aiStrategy;
    }

    public BoardViewModel getBoard() {
        return board;
    }

    public void setBoard(BoardViewModel board) {
        this.board = board;
    }

    public StatusViewModel getStatus() {
        return status;
    }

    public void setStatus(StatusViewModel status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isGameOver() {
        return currentGameState != null && currentGameState.isGameOver();
    }

    public boolean isAiTurn() {
        return currentGameState != null
            && mode == GameMode.HUMAN_VS_AI
            && !currentGameState.isGameOver()
            && currentGameState.currentTurn() == Mark.X.other();
    }
}