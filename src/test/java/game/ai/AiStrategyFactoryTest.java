package com.tictactoe.game.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.game.domain.AiDifficulty;
import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.ai.EasyAiStrategy;
import com.tictactoe.game.ai.MinimaxAiStrategy;
import com.tictactoe.game.ai.RandomAiStrategy;
import org.junit.jupiter.api.Test;

class AiStrategyFactoryTest {

    private final AiStrategyFactory factory = new AiStrategyFactory();

    @Test
    void easyDifficultyCreatesRandomAiStrategy() {
        AiStrategy strategy = factory.create(AiDifficulty.EASY);

        assertThat(strategy).isInstanceOf(RandomAiStrategy.class);
    }

    @Test
    void mediumDifficultyCreatesEasyAiStrategy() {
        AiStrategy strategy = factory.create(AiDifficulty.MEDIUM);

        assertThat(strategy).isInstanceOf(EasyAiStrategy.class);
    }

    @Test
    void difficultDifficultyCreatesMinimaxAiStrategy() {
        AiStrategy strategy = factory.create(AiDifficulty.DIFFICULT);

        assertThat(strategy).isInstanceOf(MinimaxAiStrategy.class);
    }
}
