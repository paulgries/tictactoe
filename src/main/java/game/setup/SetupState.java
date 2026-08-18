package game.setup;

import game.domain.AiDifficulty;
import game.domain.GameMode;
import java.util.Optional;

/**
 * The state of the setup screen: the settings the player picks before
 * starting a game, plus transient messages (e.g. an invalid configuration)
 * that the view displays. Written by the view's widgets and by presenters;
 * the controller reads the settings from it when the player starts.
 */
public class SetupState {

    private int boardSize = 3;
    private int winLength = 3;
    private GameMode mode = GameMode.TWO_PLAYER;
    private Optional<AiDifficulty> difficulty = Optional.empty();
    private boolean confettiEnabled = true;
    private boolean fireworksEnabled = true;
    private boolean marksEnabled = true;
    private String message;

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getWinLength() {
        return winLength;
    }

    public void setWinLength(int winLength) {
        this.winLength = winLength;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public Optional<AiDifficulty> getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Optional<AiDifficulty> difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isConfettiEnabled() {
        return confettiEnabled;
    }

    public void setConfettiEnabled(boolean confettiEnabled) {
        this.confettiEnabled = confettiEnabled;
    }

    public boolean isFireworksEnabled() {
        return fireworksEnabled;
    }

    public void setFireworksEnabled(boolean fireworksEnabled) {
        this.fireworksEnabled = fireworksEnabled;
    }

    public boolean isMarksEnabled() {
        return marksEnabled;
    }

    public void setMarksEnabled(boolean marksEnabled) {
        this.marksEnabled = marksEnabled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}