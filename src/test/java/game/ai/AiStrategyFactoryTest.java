package game.ai;

import static org.assertj.core.api.Assertions.assertThat;

import game.domain.AiDifficulty;
import game.ai.AiStrategy;
import game.ai.EasyAiStrategy;
import game.ai.MinimaxAiStrategy;
import game.ai.RandomAiStrategy;
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
