package game.request_ai_move;

import static org.assertj.core.api.Assertions.assertThat;

import data_access.InMemoryGameSession;
import game.GameViewModel;
import game.domain.GameConfig;
import game.domain.Position;
import game.request_ai_move.use_case.RequestAiMoveOutputData;
import game.testutil.CapturingUiScheduler;
import game.testutil.GameFixtures;
import java.beans.PropertyChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestAiMovePresenterTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    private GameViewModel gameViewModel;
    private InMemoryGameSession session;
    private CapturingUiScheduler scheduler;
    private RequestAiMovePresenter presenter;
    private int fireCount;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        PropertyChangeListener listener = evt -> fireCount++;
        gameViewModel.addPropertyChangeListener(listener);
        session = new InMemoryGameSession();
        scheduler = new CapturingUiScheduler();
        presenter = new RequestAiMovePresenter(gameViewModel, scheduler, session);
    }

    @Test
    void prepareSuccessView_SessionUnchanged_AppliesMoveOnUiThread() {
        game.domain.GameState base = game.domain.GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0));
        game.domain.GameState moved = base.applyMove(new Position(1, 1));
        session.setCurrentGameState(base);

        presenter.prepareSuccessView(new RequestAiMoveOutputData(moved, base));

        assertThat(scheduler.pendingUiTasks()).isEqualTo(1);
        assertThat(session.getCurrentGameState()).isEqualTo(base);

        scheduler.runNextUiTask();

        assertThat(session.getCurrentGameState()).isEqualTo(moved);
        assertThat(gameViewModel.getState().getStatus().message()).isEqualTo("X's turn");
        assertThat(fireCount).isEqualTo(1);
    }

    @Test
    void prepareSuccessView_SessionMovedOn_DiscardsStaleResult() {
        game.domain.GameState base = game.domain.GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0));
        game.domain.GameState moved = base.applyMove(new Position(1, 1));
        session.setCurrentGameState(base);

        presenter.prepareSuccessView(new RequestAiMoveOutputData(moved, base));

        // the session moved on (e.g. a restart) before the UI task ran
        game.domain.GameState fresh = GameFixtures.wonByX();
        session.setCurrentGameState(fresh);

        scheduler.runNextUiTask();

        assertThat(session.getCurrentGameState()).isEqualTo(fresh);
        assertThat(fireCount).isZero();
    }

    @Test
    void prepareFailView_SetsErrorOnUiThread() {
        presenter.prepareFailView("cannot request an AI move after the game is over");

        assertThat(gameViewModel.getState().getMessage()).isNull();

        scheduler.runNextUiTask();

        assertThat(gameViewModel.getState().getMessage())
            .isEqualTo("cannot request an AI move after the game is over");
        assertThat(fireCount).isEqualTo(1);
    }
}