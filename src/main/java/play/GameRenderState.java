package play;

/**
 * The render part of the game view model: everything the frame needs to
 * draw. Presenters write it; the frame only reads it (except for clearing
 * a shown message). It holds no session data and no game policy.
 */
public class GameRenderState {

    private BoardRenderState board;
    private StatusRenderState status;
    private String message;

    public BoardRenderState getBoard() {
        return board;
    }

    public void setBoard(BoardRenderState board) {
        this.board = board;
    }

    public StatusRenderState getStatus() {
        return status;
    }

    public void setStatus(StatusRenderState status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}