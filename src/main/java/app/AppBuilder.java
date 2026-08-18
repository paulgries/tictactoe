package app;

import game.GameViewModel;
import game.ai.AiStrategyFactory;
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
import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Wires the whole application: creates the shared view model, the presenters
 * and controllers for the three use cases, and the Swing view, mirroring the
 * AppBuilder in CAWithBuilder.
 */
public class AppBuilder {

    private final GameViewModel gameViewModel = new GameViewModel();

    public JFrame build() {
        final SwingUiScheduler uiScheduler = new SwingUiScheduler();

        final RequestAiMovePresenter requestAiMovePresenter =
                new RequestAiMovePresenter(gameViewModel, uiScheduler);
        final RequestAiMoveController requestAiMoveController = new RequestAiMoveController(
                new RequestAiMoveInteractor(requestAiMovePresenter), gameViewModel, uiScheduler);

        final MakeHumanMovePresenter makeHumanMovePresenter =
                new MakeHumanMovePresenter(gameViewModel, requestAiMoveController::execute);
        final MakeHumanMoveController makeHumanMoveController = new MakeHumanMoveController(
                new MakeHumanMoveInteractor(makeHumanMovePresenter), gameViewModel);

        final StartNewGamePresenter startNewGamePresenter =
                new StartNewGamePresenter(gameViewModel);
        final StartNewGameController startNewGameController = new StartNewGameController(
                new StartNewGameInteractor(startNewGamePresenter),
                new AiStrategyFactory(), gameViewModel);

        final MainFrame frame = new MainFrame(gameViewModel);
        frame.setControllers(startNewGameController, makeHumanMoveController);

        final EffectOverlayPanel effects = new EffectOverlayPanel();
        frame.setGlassPane(effects);
        effects.setVisible(true);
        frame.setWinEffects(List.of(
                ifEnabled(frame.setupPanel()::isConfettiEffectEnabled, effects::playConfetti),
                ifEnabled(frame.setupPanel()::isFireworksEffectEnabled, effects::playFireworks),
                ifEnabled(frame.setupPanel()::isMarksEffectEnabled, effects::playMarks)));

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