package game.domain;

import game.domain.exception.InvalidGameConfigException;

public record GameConfig(int boardSize, int winLength) {

    public GameConfig {
        if (boardSize < 2) {
            throw new InvalidGameConfigException("boardSize must be at least 2: " + boardSize);
        }
        if (winLength < 2) {
            throw new InvalidGameConfigException("winLength must be at least 2: " + winLength);
        }
        if (winLength > boardSize) {
            throw new InvalidGameConfigException(
                "winLength (" + winLength + ") must not exceed boardSize (" + boardSize + ")");
        }
    }
}
