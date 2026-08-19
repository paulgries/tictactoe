package game.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import game.domain.AiDifficulty;
import org.junit.jupiter.api.Test;

class AiStrategyFactoryTest {

    private final CommonAiStrategyFactory factory = new CommonAiStrategyFactory();

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

    @Test
    void register_ThenCreate_UsesRegisteredStrategy() {
        AiStrategy custom = mock(AiStrategy.class);
        factory.register(AiDifficulty.EASY, () -> custom);

        assertThat(factory.create(AiDifficulty.EASY)).isSameAs(custom);
    }

    @Test
    void create_InvokesSupplierOnEveryCall() {
        factory.register(AiDifficulty.EASY, () -> mock(AiStrategy.class));

        assertThat(factory.create(AiDifficulty.EASY)).isNotSameAs(factory.create(AiDifficulty.EASY));
    }
}