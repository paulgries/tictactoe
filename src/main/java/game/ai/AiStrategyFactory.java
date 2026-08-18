package game.ai;

import game.domain.AiDifficulty;
import game.ai.AiStrategy;
import game.ai.EasyAiStrategy;
import game.ai.MinimaxAiStrategy;
import game.ai.RandomAiStrategy;
import java.util.Random;

public final class AiStrategyFactory {

    public AiStrategy create(AiDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> new RandomAiStrategy(new Random());
            case MEDIUM -> new EasyAiStrategy();
            case DIFFICULT -> new MinimaxAiStrategy();
        };
    }
}
