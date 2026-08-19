package game.ai;

import game.domain.AiDifficulty;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public final class CommonAiStrategyFactory implements AiStrategyFactory {

    private final Map<AiDifficulty, Supplier<AiStrategy>> strategies =
            new EnumMap<>(AiDifficulty.class);

    public CommonAiStrategyFactory() {
        register(AiDifficulty.EASY, () -> new RandomAiStrategy(new Random()));
        register(AiDifficulty.MEDIUM, EasyAiStrategy::new);
        register(AiDifficulty.DIFFICULT, MinimaxAiStrategy::new);
    }

    public void register(AiDifficulty difficulty, Supplier<AiStrategy> strategy) {
        strategies.put(difficulty, strategy);
    }

    @Override
    public AiStrategy create(AiDifficulty difficulty) {
        Supplier<AiStrategy> strategy = strategies.get(difficulty);
        if (strategy == null) {
            throw new IllegalArgumentException("no AI strategy registered for " + difficulty);
        }
        return strategy.get();
    }
}