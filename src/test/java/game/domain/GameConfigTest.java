package game.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import game.domain.exception.InvalidGameConfigException;
import org.junit.jupiter.api.Test;

class GameConfigTest {

    @Test
    void validConfigIsCreatedSuccessfully() {
        GameConfig config = new GameConfig(5, 4);

        assertThat(config.boardSize()).isEqualTo(5);
        assertThat(config.winLength()).isEqualTo(4);
    }

    @Test
    void winLengthGreaterThanBoardSizeThrowsInvalidGameConfigException() {
        assertThatThrownBy(() -> new GameConfig(3, 4))
            .isInstanceOf(InvalidGameConfigException.class);
    }

    @Test
    void boardSizeLessThanTwoThrowsInvalidGameConfigException() {
        assertThatThrownBy(() -> new GameConfig(1, 1))
            .isInstanceOf(InvalidGameConfigException.class);
    }

    @Test
    void winLengthLessThanTwoThrowsInvalidGameConfigException() {
        assertThatThrownBy(() -> new GameConfig(3, 1))
            .isInstanceOf(InvalidGameConfigException.class);
    }
}
