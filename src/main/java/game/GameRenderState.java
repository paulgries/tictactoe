package game;

/**
 * The render part of the game view model: everything the frame needs to
 * draw. Presenters write it; the frame only reads it (except for clearing
 * a shown error). It holds no session data and no game policy.
 */
public class GameRenderState {

    private BoardViewModel board;
    private StatusViewModel status;
    private String error;

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
}