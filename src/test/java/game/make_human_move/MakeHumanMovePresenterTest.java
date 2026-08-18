package game.make_human_move;

import static org.assertj.core.api.Assertions.assertThat;

import game.GameViewModel;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.domain.Position;
import game.make_human_move.use_case.MakeHumanMoveOutputData;
import game.testutil.GameFixtures;
import java.beans.PropertyChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MakeHumanMovePresenterTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    private GameViewModel gameViewModel;
    private MakeHumanMovePresenter presenter;
    private int requestAiMoveCalls;
    private int fireCount;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        PropertyChangeListener listener = evt -> fireCount++;
        gameViewModel.addPropertyChangeListener(listener);
        presenter = new MakeHumanMovePresenter(gameViewModel, () -> requestAiMoveCalls++);
    }

    @Test
    void prepareSuccessView_UpdatesViewModelAndFires() {
        game.domain.GameState updated = game.domain.GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0));

        presenter.prepareSuccessView(new MakeHumanMoveOutputData(updated));

        assertThat(gameViewModel.getSession().getCurrentGameState()).isEqualTo(updated);
        assertThat(gameViewModel.getState().getStatus().message()).isEqualTo("O's turn");
        assertThat(fireCount).isEqualTo(1);
        assertThat(requestAiMoveCalls).isZero();
    }

    @Test
    void prepareSuccessView_AiModeWithAiTurn_RequestsAiMove() {
        gameViewModel.getSession().setMode(GameMode.HUMAN_VS_AI);
        game.domain.GameState afterHumanMove = game.domain.GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0));

        presenter.prepareSuccessView(new MakeHumanMoveOutputData(afterHumanMove));

        assertThat(requestAiMoveCalls).isEqualTo(1);
    }

    @Test
    void prepareSuccessView_AiModeButGameOver_DoesNotRequestAiMove() {
        gameViewModel.getSession().setMode(GameMode.HUMAN_VS_AI);

        presenter.prepareSuccessView(new MakeHumanMoveOutputData(GameFixtures.wonByX()));

        assertThat(requestAiMoveCalls).isZero();
    }

    @Test
    void prepareFailView_SetsErrorAndFires() {
        presenter.prepareFailView("cell already occupied: " + new Position(0, 0));

        assertThat(gameViewModel.getState().getMessage())
            .isEqualTo("cell already occupied: " + new Position(0, 0));
        assertThat(fireCount).isEqualTo(1);
        assertThat(requestAiMoveCalls).isZero();
    }
}