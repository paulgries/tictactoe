package com.tictactoe.game.ai;

import com.tictactoe.game.domain.AiDifficulty;
import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.ai.EasyAiStrategy;
import com.tictactoe.game.ai.MinimaxAiStrategy;
import com.tictactoe.game.ai.RandomAiStrategy;
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
