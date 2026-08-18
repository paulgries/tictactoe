package app;

import game.GameViewModel;
import game.ai.AiStrategyFactory;
import game.domain.CommonGameStateFactory;
import game.domain.GameStateFactory;
import game.make_human_move.MakeHumanMoveController;
import game.make_human_move.MakeHumanMovePresenter;
import game.make_human_move.use_case.MakeHumanMoveInteractor;
import game.request_ai_move.RequestAiMoveController;
import game.request_ai_move.RequestAiMovePresenter;
import game.request_ai_move.use_case.RequestAiMoveInteractor;
import game.start_new_game.StartNewGameController;
import game.start_new_game.StartNewGamePresenter;
import game.start_new_game.use_case.StartNewGameInteractor;
import framework.ui.EffectOverlayPanel;
import framework.ui.MainFrame;
import framework.ui.SwingUiScheduler;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.swing.JFrame;

/**
 * Wires the whole application with one fluent method per frame and per use
 * case, mirroring the AppBuilder in CAWithBuilder.
 */
public class AppBuilder {

    private final GameViewModel gameViewModel = new GameViewModel();
    private final SwingUiScheduler uiScheduler = new SwingUiScheduler();
    private final GameStateFactory gameStateFactory = new CommonGameStateFactory();
    private final AiStrategyFactory aiStrategyFactory = new AiStrategyFactory();

    private MainFrame frame;
    private RequestAiMoveController requestAiMoveController;

    public AppBuilder addGameView() {
        frame = new MainFrame(gameViewModel);

        final EffectOverlayPanel effects = new EffectOverlayPanel();
        frame.setGlassPane(effects);
        effects.setVisible(true);
        frame.setWinEffects(List.of(
                ifEnabled(frame.setupPanel()::isConfettiEffectEnabled, effects::playConfetti),
                ifEnabled(frame.setupPanel()::isFireworksEffectEnabled, effects::playFireworks),
                ifEnabled(frame.setupPanel()::isMarksEffectEnabled, effects::playMarks)));
        return this;
    }

    public AppBuilder addRequestAiMoveUseCase() {
        final RequestAiMovePresenter requestAiMovePresenter =
                new RequestAiMovePresenter(gameViewModel, uiScheduler);
        requestAiMoveController = new RequestAiMoveController(
                new RequestAiMoveInteractor(requestAiMovePresenter), gameViewModel, uiScheduler);
        return this;
    }

    public AppBuilder addMakeHumanMoveUseCase() {
        final MakeHumanMovePresenter makeHumanMovePresenter =
                new MakeHumanMovePresenter(gameViewModel, requestAiMoveController::execute);
        final MakeHumanMoveController makeHumanMoveController = new MakeHumanMoveController(
                new MakeHumanMoveInteractor(makeHumanMovePresenter), gameViewModel);
        frame.setMakeHumanMoveController(makeHumanMoveController);
        return this;
    }

    public AppBuilder addStartNewGameUseCase() {
        final StartNewGamePresenter startNewGamePresenter =
                new StartNewGamePresenter(gameViewModel);
        final StartNewGameController startNewGameController = new StartNewGameController(
                new StartNewGameInteractor(
                        startNewGamePresenter, gameStateFactory, aiStrategyFactory));
        frame.setStartNewGameController(startNewGameController);
        return this;
    }

    public JFrame build() {
        return frame;
    }

    private static Runnable ifEnabled(BooleanSupplier enabled, Runnable effect) {
        return () -> {
            if (enabled.getAsBoolean()) {
                effect.run();
            }
        };
    }
}