package game.start_new_game;

import static org.assertj.core.api.Assertions.assertThat;

import framework.ViewManagerModel;
import game.CellSymbol;
import game.GameRenderState;
import game.GameViewModel;
import game.domain.GameConfig;
import game.setup.SetupViewModel;
import game.start_new_game.use_case.StartNewGameOutputData;
import game.testutil.GameFixtures;
import java.beans.PropertyChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StartNewGamePresenterTest {

    private GameViewModel gameViewModel;
    private SetupViewModel setupViewModel;
    private ViewManagerModel viewManagerModel;
    private StartNewGamePresenter presenter;
    private GameRenderState lastFiredState;
    private int fireCount;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        setupViewModel = new SetupViewModel();
        viewManagerModel = new ViewManagerModel();
        PropertyChangeListener listener = evt -> {
            lastFiredState = (GameRenderState) evt.getNewValue();
            fireCount++;
        };
        gameViewModel.addPropertyChangeListener(listener);
        presenter = new StartNewGamePresenter(gameViewModel, setupViewModel, viewManagerModel);
    }

    @Test
    void prepareSuccessView_PresentsFreshGameStateAndFires() {
        game.domain.GameState fresh = game.domain.GameState.newGame(new GameConfig(3, 3));

        presenter.prepareSuccessView(new StartNewGameOutputData(fresh));

        GameRenderState render = gameViewModel.getState();
        assertThat(render.getBoard().cells()).hasSize(9);
        assertThat(render.getBoard().cells().get(0).symbol()).isEqualTo(CellSymbol.EMPTY);
        assertThat(render.getStatus().message()).isEqualTo("X's turn");
        assertThat(fireCount).isEqualTo(1);
        assertThat(lastFiredState).isSameAs(render);
        assertThat(viewManagerModel.getState()).isEqualTo("game");
    }

    @Test
    void prepareSuccessView_ReflectsWinOutcome() {
        game.domain.GameState won = GameFixtures.wonByX();

        presenter.prepareSuccessView(new StartNewGameOutputData(won));

        assertThat(gameViewModel.getState().getStatus().message()).isEqualTo("X wins!");
    }

    @Test
    void prepareFailView_SetsMessageOnSetupViewModelAndFires() {
        presenter.prepareFailView("boom");

        assertThat(setupViewModel.getState().getMessage()).isEqualTo("boom");
        assertThat(fireCount).isZero();
        assertThat(viewManagerModel.getState()).isEmpty();
    }

    @Test
    void switchToSetupView_NavigatesToSetupView() {
        presenter.switchToSetupView();

        assertThat(viewManagerModel.getState()).isEqualTo("setup");
    }
}