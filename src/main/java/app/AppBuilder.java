package app;

import data_access.FileGameDataAccessObject;
import framework.SwingUiScheduler;
import framework.ViewManager;
import framework.ViewManagerModel;
import game.GameViewModel;
import game.ai.AiStrategyFactory;
import game.ai.CommonAiStrategyFactory;
import game.domain.CommonGameStateFactory;
import game.domain.GameStateFactory;
import game.game_view.EffectOverlayPanel;
import game.game_view.GamePanel;
import game.load_game.LoadGameController;
import game.load_game.LoadGamePresenter;
import game.load_game.use_case.LoadGameInteractor;
import game.make_human_move.MakeHumanMoveController;
import game.make_human_move.MakeHumanMovePresenter;
import game.make_human_move.use_case.MakeHumanMoveInteractor;
import game.request_ai_move.RequestAiMoveController;
import game.request_ai_move.RequestAiMovePresenter;
import game.request_ai_move.use_case.RequestAiMoveInteractor;
import game.save_game.SaveGameController;
import game.save_game.SaveGamePresenter;
import game.save_game.use_case.SaveGameInteractor;
import game.setup.SetupPanel;
import game.setup.SetupState;
import game.setup.SetupViewModel;
import game.start_new_game.StartNewGameController;
import game.start_new_game.StartNewGamePresenter;
import game.start_new_game.use_case.StartNewGameInteractor;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.swing.JFrame;

/**
 * Wires the whole application with one fluent method per frame and per use
 * case, mirroring the AppBuilder in CAWithBuilder: the builder creates each
 * view's ViewModel, registers the view on the frame's card panel under its
 * view name, and hands each controller to its view.
 */
public class AppBuilder {

    private final GameViewModel gameViewModel = new GameViewModel();
    private final SetupViewModel setupViewModel = new SetupViewModel();
    private final SwingUiScheduler uiScheduler = new SwingUiScheduler();
    private final GameStateFactory gameStateFactory = new CommonGameStateFactory();
    private final AiStrategyFactory aiStrategyFactory = new CommonAiStrategyFactory();
    private final ViewManagerModel viewManagerModel = new ViewManagerModel();
    private final FileGameDataAccessObject fileGameDataAccessObject = new FileGameDataAccessObject(
            Path.of(System.getProperty("user.home"), ".tictactoe", "saved-game.txt"));

    private MainFrame frame;
    private ViewManager viewManager;
    private SetupPanel setupPanel;
    private GamePanel gamePanel;
    private RequestAiMoveController requestAiMoveController;

    public AppBuilder addSetupView() {
        setupPanel = new SetupPanel(setupViewModel);
        return this;
    }

    public AppBuilder addGameView() {
        frame = new MainFrame();
        viewManager = new ViewManager(
                frame.getCardPanel(), frame.getCardLayout(), viewManagerModel);
        gamePanel = new GamePanel(gameViewModel);
        frame.addView(setupPanel, setupPanel.getViewName());
        frame.addView(gamePanel, gamePanel.getViewName());

        final EffectOverlayPanel effects = new EffectOverlayPanel();
        frame.setGlassPane(effects);
        effects.setVisible(true);
        final SetupState setupState = setupViewModel.getState();
        gamePanel.setWinEffects(List.of(
                ifEnabled(setupState::isConfettiEnabled, effects::playConfetti),
                ifEnabled(setupState::isFireworksEnabled, effects::playFireworks),
                ifEnabled(setupState::isMarksEnabled, effects::playMarks)));
        return this;
    }

    public AppBuilder addRequestAiMoveUseCase() {
        final RequestAiMovePresenter requestAiMovePresenter =
                new RequestAiMovePresenter(gameViewModel, uiScheduler);
        requestAiMoveController = new RequestAiMoveController(
                new RequestAiMoveInteractor(requestAiMovePresenter, aiStrategyFactory),
                gameViewModel, uiScheduler);
        return this;
    }

    public AppBuilder addMakeHumanMoveUseCase() {
        if (requestAiMoveController == null) {
            throw new IllegalStateException(
                "addRequestAiMoveUseCase() must be called before addMakeHumanMoveUseCase()");
        }
        final MakeHumanMovePresenter makeHumanMovePresenter =
                new MakeHumanMovePresenter(gameViewModel, requestAiMoveController::execute);
        final MakeHumanMoveController makeHumanMoveController = new MakeHumanMoveController(
                new MakeHumanMoveInteractor(makeHumanMovePresenter), gameViewModel);
        gamePanel.setMakeHumanMoveController(makeHumanMoveController);
        return this;
    }

    public AppBuilder addStartNewGameUseCase() {
        final StartNewGamePresenter startNewGamePresenter = new StartNewGamePresenter(
                gameViewModel, setupViewModel, viewManagerModel);
        final StartNewGameController startNewGameController = new StartNewGameController(
                new StartNewGameInteractor(startNewGamePresenter, gameStateFactory));
        setupPanel.setStartNewGameController(startNewGameController);
        gamePanel.setStartNewGameController(startNewGameController);
        return this;
    }

    public AppBuilder addSaveGameUseCase() {
        final SaveGamePresenter saveGamePresenter = new SaveGamePresenter(gameViewModel);
        final SaveGameController saveGameController = new SaveGameController(
                new SaveGameInteractor(saveGamePresenter, fileGameDataAccessObject), gameViewModel);
        gamePanel.setSaveGameController(saveGameController);
        return this;
    }

    public AppBuilder addLoadGameUseCase() {
        final LoadGamePresenter loadGamePresenter =
                new LoadGamePresenter(gameViewModel, setupViewModel, viewManagerModel);
        final LoadGameController loadGameController = new LoadGameController(
                new LoadGameInteractor(loadGamePresenter, fileGameDataAccessObject));
        setupPanel.setLoadGameController(loadGameController);
        return this;
    }

    public JFrame build() {
        viewManagerModel.setState(setupViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
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