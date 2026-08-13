package com.tictactoe.application;

import com.tictactoe.domain.AiDifficulty;
import com.tictactoe.domain.ai.AiStrategy;
import com.tictactoe.domain.ai.EasyAiStrategy;
import com.tictactoe.domain.ai.MinimaxAiStrategy;
import com.tictactoe.domain.ai.RandomAiStrategy;
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
