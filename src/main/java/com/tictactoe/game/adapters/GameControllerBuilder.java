package com.tictactoe.game.adapters;

import com.tictactoe.game.ai.AiStrategyFactory;
import com.tictactoe.game.use_case.MakeHumanMoveUseCase;
import com.tictactoe.game.use_case.RequestAiMoveUseCase;
import com.tictactoe.game.use_case.StartNewGameUseCase;
import java.util.Objects;

/**
 * Assembles a {@link GameController} and its dependencies. The view and the UI scheduler
 * are delivery-mechanism specifics with no sensible default in this layer, so they're
 * required; the use cases and the AI strategy factory are stateless and identical across
 * every caller, so they default and only need overriding in tests.
 */
public final class GameControllerBuilder {

    private GameView view;
    private UiScheduler uiScheduler;
    private StartNewGameUseCase startNewGameUseCase = new StartNewGameUseCase();
    private MakeHumanMoveUseCase makeHumanMoveUseCase = new MakeHumanMoveUseCase();
    private RequestAiMoveUseCase requestAiMoveUseCase = new RequestAiMoveUseCase();
    private AiStrategyFactory aiStrategyFactory = new AiStrategyFactory();

    public GameControllerBuilder view(GameView view) {
        this.view = view;
        return this;
    }

    public GameControllerBuilder uiScheduler(UiScheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
        return this;
    }

    public GameControllerBuilder startNewGameUseCase(StartNewGameUseCase startNewGameUseCase) {
        this.startNewGameUseCase = startNewGameUseCase;
        return this;
    }

    public GameControllerBuilder makeHumanMoveUseCase(MakeHumanMoveUseCase makeHumanMoveUseCase) {
        this.makeHumanMoveUseCase = makeHumanMoveUseCase;
        return this;
    }

    public GameControllerBuilder requestAiMoveUseCase(RequestAiMoveUseCase requestAiMoveUseCase) {
        this.requestAiMoveUseCase = requestAiMoveUseCase;
        return this;
    }

    public GameControllerBuilder aiStrategyFactory(AiStrategyFactory aiStrategyFactory) {
        this.aiStrategyFactory = aiStrategyFactory;
        return this;
    }

    public GameController build() {
        Objects.requireNonNull(view, "view must be set");
        Objects.requireNonNull(uiScheduler, "uiScheduler must be set");
        return new GameController(
            view,
            startNewGameUseCase,
            makeHumanMoveUseCase,
            requestAiMoveUseCase,
            aiStrategyFactory,
            uiScheduler);
    }
}