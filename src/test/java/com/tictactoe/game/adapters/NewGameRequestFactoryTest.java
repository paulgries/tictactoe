package com.tictactoe.game.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.game.NewGameRequest;
import com.tictactoe.game.domain.AiDifficulty;
import com.tictactoe.game.domain.GameMode;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NewGameRequestFactoryTest {

    @Test
    void createBuildsRequestWithGivenConfigModeAndDifficulty() {
        NewGameRequest request = NewGameRequestFactory.create(
            4, 3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

        assertThat(request.config().boardSize()).isEqualTo(4);
        assertThat(request.config().winLength()).isEqualTo(3);
        assertThat(request.mode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(request.aiDifficulty()).contains(AiDifficulty.EASY);
    }

    @Test
    void createBuildsTwoPlayerRequestWithEmptyDifficulty() {
        NewGameRequest request = NewGameRequestFactory.create(
            3, 3, GameMode.TWO_PLAYER, Optional.empty());

        assertThat(request.mode()).isEqualTo(GameMode.TWO_PLAYER);
        assertThat(request.aiDifficulty()).isEmpty();
    }
}
