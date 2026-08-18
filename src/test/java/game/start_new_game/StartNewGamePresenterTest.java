package game.start_new_game;

import static org.assertj.core.api.Assertions.assertThat;

import game.CellSymbol;
import game.GameState;
import game.GameViewModel;
import game.ai.EasyAiStrategy;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.start_new_game.use_case.StartNewGameOutputData;
import game.testutil.GameFixtures;
import java.beans.PropertyChangeListener;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StartNewGamePresenterTest {

    private GameViewModel gameViewModel;
    private StartNewGamePresenter presenter;
    private GameState lastFiredState;
    private int fireCount;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        PropertyChangeListener listener = evt -> {
            lastFiredState = (GameState) evt.getNewValue();
            fireCount++;
        };
        gameViewModel.addPropertyChangeListener(listener);
        presenter = new StartNewGamePresenter(gameViewModel);
    }

    @Test
    void prepareSuccessView_PresentsFreshGameStateAndFires() {
        game.domain.GameState fresh = game.domain.GameState.newGame(new GameConfig(3, 3));

        presenter.prepareSuccessView(new StartNewGameOutputData(
                fresh, GameMode.TWO_PLAYER, Optional.empty()));

        GameState state = gameViewModel.getState();
        assertThat(state.getCurrentGameState()).isEqualTo(fresh);
        assertThat(state.getBoard().cells()).hasSize(9);
        assertThat(state.getBoard().cells().get(0).symbol()).isEqualTo(CellSymbol.EMPTY);
        assertThat(state.getStatus().message()).isEqualTo("X's turn");
        assertThat(state.getMode()).isEqualTo(GameMode.TWO_PLAYER);
        assertThat(state.getAiStrategy()).isEmpty();
        assertThat(fireCount).isEqualTo(1);
        assertThat(lastFiredState).isSameAs(state);
    }

    @Test
    void prepareSuccessView_AiMode_StashesStrategy() {
        game.domain.GameState fresh = game.domain.GameState.newGame(new GameConfig(3, 3));

        presenter.prepareSuccessView(new StartNewGameOutputData(
                fresh, GameMode.HUMAN_VS_AI, Optional.of(new EasyAiStrategy())));

        GameState state = gameViewModel.getState();
        assertThat(state.getMode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(state.getAiStrategy()).isPresent();
    }

    @Test
    void prepareSuccessView_ReflectsWinOutcome() {
        game.domain.GameState won = GameFixtures.wonByX();

        presenter.prepareSuccessView(new StartNewGameOutputData(
                won, GameMode.TWO_PLAYER, Optional.empty()));

        assertThat(gameViewModel.getState().getStatus().message()).isEqualTo("X wins!");
    }

    @Test
    void prepareFailView_SetsErrorAndFires() {
        presenter.prepareFailView("boom");

        assertThat(gameViewModel.getState().getError()).isEqualTo("boom");
        assertThat(fireCount).isEqualTo(1);
    }
}