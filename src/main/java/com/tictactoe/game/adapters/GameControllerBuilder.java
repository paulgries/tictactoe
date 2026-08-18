package com.tictactoe.game.adapters;

import com.tictactoe.game.ai.AiStrategyFactory;
import java.util.Objects;

/**
 * Assembles a {@link GameController} and its dependencies. The view and the UI scheduler
 * are delivery-mechanism specifics with no sensible default in this layer, so they're
 * required; the AI strategy factory is stateless and identical across every caller, so it
 * defaults and only needs overriding in tests. The use cases are wired internally by the
 * controller.
 */
public final class GameControllerBuilder {

    private GameView view;
    private UiScheduler uiScheduler;
    private AiStrategyFactory aiStrategyFactory = new AiStrategyFactory();

    public GameControllerBuilder view(GameView view) {
        this.view = view;
        return this;
    }

    public GameControllerBuilder uiScheduler(UiScheduler uiScheduler) {
        this.uiScheduler = uiScheduler;
        return this;
    }

    public GameControllerBuilder aiStrategyFactory(AiStrategyFactory aiStrategyFactory) {
        this.aiStrategyFactory = aiStrategyFactory;
        return this;
    }

    public GameController build() {
        Objects.requireNonNull(view, "view must be set");
        Objects.requireNonNull(uiScheduler, "uiScheduler must be set");
        return new GameController(view, aiStrategyFactory, uiScheduler);
    }
}