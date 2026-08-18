package game.make_human_move.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import game.domain.GameConfig;
import game.domain.GameState;
import game.domain.Mark;
import game.domain.Position;
import game.testutil.GameFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MakeHumanMoveInteractorTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Mock
    private MakeHumanMoveOutputBoundary presenter;

    private MakeHumanMoveInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new MakeHumanMoveInteractor(presenter);
    }

    @Test
    void execute_ValidMove_PresentsUpdatedState() {
        GameState state = GameState.newGame(CONFIG_3X3);

        interactor.execute(new MakeHumanMoveInputData(state, new Position(0, 0)));

        ArgumentCaptor<MakeHumanMoveOutputData> captor =
            ArgumentCaptor.forClass(MakeHumanMoveOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().updatedState().board().get(new Position(0, 0))).contains(Mark.X);
        assertThat(captor.getValue().updatedState().currentTurn()).isEqualTo(Mark.O);
        verify(presenter, never()).prepareFailView(any());
    }

    @Test
    void execute_GameAlreadyOver_PresentsFailView() {
        GameState state = GameFixtures.wonByX();

        interactor.execute(new MakeHumanMoveInputData(state, new Position(2, 2)));

        verify(presenter).prepareFailView("cannot move after the game is over");
        verify(presenter, never()).prepareSuccessView(any());
    }
}