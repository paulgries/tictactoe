package game.ai;

import game.domain.AiDifficulty;

public interface AiStrategyFactory {

    AiStrategy create(AiDifficulty difficulty);
}