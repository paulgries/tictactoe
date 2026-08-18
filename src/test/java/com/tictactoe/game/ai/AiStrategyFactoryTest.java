package com.tictactoe.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.domain.AiDifficulty;
import com.tictactoe.domain.ai.AiStrategy;
import com.tictactoe.domain.ai.EasyAiStrategy;
import com.tictactoe.domain.ai.MinimaxAiStrategy;
import com.tictactoe.domain.ai.RandomAiStrategy;
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
