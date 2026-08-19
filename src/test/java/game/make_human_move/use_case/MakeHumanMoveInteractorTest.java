package game.make_human_move.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import data_access.InMemoryGameSession;
import game.domain.AiDifficulty;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.domain.GameState;
import game.domain.Mark;
import game.domain.Position;
import game.testutil.GameFixtures;
import java.util.Optional;
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

    private InMemoryGameSession session;
    private MakeHumanMoveInteractor interactor;

    @BeforeEach
    void setUp() {
        session = new InMemoryGameSession();
        interactor = new MakeHumanMoveInteractor(presenter, session);
    }

    @Test
    void execute_ValidMove_PresentsUpdatedStateAndWritesSession() {
        session.setCurrentGame(GameState.newGame(CONFIG_3X3), GameMode.TWO_PLAYER, Optional.empty());

        interactor.execute(new MakeHumanMoveInputData(new Position(0, 0)));

        ArgumentCaptor<MakeHumanMoveOutputData> captor =
            ArgumentCaptor.forClass(MakeHumanMoveOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().updatedState().board().get(new Position(0, 0))).contains(Mark.X);
        assertThat(captor.getValue().updatedState().currentTurn()).isEqualTo(Mark.O);
        assertThat(captor.getValue().aiToMoveNext()).isFalse();
        assertThat(session.getCurrentGameState()).isEqualTo(captor.getValue().updatedState());
    }

    @Test
    void execute_ValidMoveInAiModeWithAiToMoveNext_FlagsAiToMoveNext() {
        session.setCurrentGame(GameState.newGame(CONFIG_3X3), GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

        interactor.execute(new MakeHumanMoveInputData(new Position(1, 1)));

        ArgumentCaptor<MakeHumanMoveOutputData> captor =
            ArgumentCaptor.forClass(MakeHumanMoveOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().aiToMoveNext()).isTrue();
    }

    @Test
    void execute_GameAlreadyOver_IgnoresMove() {
        session.setCurrentGame(GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty());

        interactor.execute(new MakeHumanMoveInputData(new Position(2, 2)));

        verifyNoInteractions(presenter);
        assertThat(session.getCurrentGameState()).isEqualTo(GameFixtures.wonByX());
    }

    @Test
    void execute_AiTurn_IgnoresMove() {
        GameState state = GameState.newGame(CONFIG_3X3).applyMove(new Position(0, 0));
        session.setCurrentGame(state, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.EASY));

        interactor.execute(new MakeHumanMoveInputData(new Position(1, 1)));

        verifyNoInteractions(presenter);
        assertThat(session.getCurrentGameState()).isEqualTo(state);
    }

    @Test
    void execute_NoGameInProgress_IgnoresMove() {
        interactor.execute(new MakeHumanMoveInputData(new Position(0, 0)));

        verifyNoInteractions(presenter);
    }
}