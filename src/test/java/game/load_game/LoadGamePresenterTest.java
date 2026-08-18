package game.load_game;

import static org.assertj.core.api.Assertions.assertThat;

import framework.ViewManagerModel;
import game.CellSymbol;
import game.GameRenderState;
import game.GameViewModel;
import game.domain.AiDifficulty;
import game.domain.GameMode;
import game.domain.SavedGame;
import game.load_game.use_case.LoadGameOutputData;
import game.setup.SetupViewModel;
import game.testutil.GameFixtures;
import java.beans.PropertyChangeListener;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoadGamePresenterTest {

    private GameViewModel gameViewModel;
    private SetupViewModel setupViewModel;
    private ViewManagerModel viewManagerModel;
    private LoadGamePresenter presenter;
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
        presenter = new LoadGamePresenter(gameViewModel, setupViewModel, viewManagerModel);
    }

    @Test
    void prepareSuccessView_RestoresSessionAndRenderAndNavigates() {
        SavedGame savedGame = new SavedGame(
                GameFixtures.wonByX(), GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM));

        presenter.prepareSuccessView(new LoadGameOutputData(savedGame));

        assertThat(gameViewModel.getSession().getCurrentGameState()).isEqualTo(savedGame.gameState());
        assertThat(gameViewModel.getSession().getMode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(gameViewModel.getSession().getDifficulty()).contains(AiDifficulty.MEDIUM);
        GameRenderState render = gameViewModel.getState();
        assertThat(render.getBoard().cells()).hasSize(9);
        assertThat(render.getBoard().cells().get(0).symbol()).isEqualTo(CellSymbol.X);
        assertThat(render.getStatus().message()).isEqualTo("X wins!");
        assertThat(render.getMessage()).isNull();
        assertThat(fireCount).isEqualTo(1);
        assertThat(lastFiredState).isSameAs(render);
        assertThat(viewManagerModel.getState()).isEqualTo("game");
    }

    @Test
    void prepareFailView_SetsMessageOnSetupViewModelAndDoesNotNavigate() {
        presenter.prepareFailView("no saved game found");

        assertThat(setupViewModel.getState().getMessage()).isEqualTo("no saved game found");
        assertThat(fireCount).isZero();
        assertThat(viewManagerModel.getState()).isEmpty();
    }
}