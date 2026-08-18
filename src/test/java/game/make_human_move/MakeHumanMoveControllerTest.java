package game.make_human_move;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import game.GameViewModel;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.domain.Position;
import game.make_human_move.use_case.MakeHumanMoveInputBoundary;
import game.make_human_move.use_case.MakeHumanMoveInputData;
import game.testutil.GameFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MakeHumanMoveControllerTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Mock
    private MakeHumanMoveInputBoundary makeHumanMoveUseCase;

    private GameViewModel gameViewModel;
    private MakeHumanMoveController controller;

    @BeforeEach
    void setUp() {
        gameViewModel = new GameViewModel();
        controller = new MakeHumanMoveController(makeHumanMoveUseCase, gameViewModel);
    }

    @Test
    void execute_PassesCurrentStateAndPosition() {
        game.domain.GameState current = game.domain.GameState.newGame(CONFIG_3X3);
        gameViewModel.getSession().setCurrentGameState(current);

        controller.execute(1, 2);

        ArgumentCaptor<MakeHumanMoveInputData> captor =
            ArgumentCaptor.forClass(MakeHumanMoveInputData.class);
        verify(makeHumanMoveUseCase).execute(captor.capture());
        assertThat(captor.getValue().state()).isEqualTo(current);
        assertThat(captor.getValue().position()).isEqualTo(new Position(1, 2));
    }

    @Test
    void execute_GameOver_IgnoresClick() {
        gameViewModel.getSession().setCurrentGameState(GameFixtures.wonByX());

        controller.execute(0, 0);

        verify(makeHumanMoveUseCase, never()).execute(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void execute_AiTurn_IgnoresClick() {
        gameViewModel.getSession().setMode(GameMode.HUMAN_VS_AI);
        gameViewModel.getSession().setCurrentGameState(
            game.domain.GameState.newGame(CONFIG_3X3).applyMove(new Position(0, 0)));

        controller.execute(1, 1);

        verify(makeHumanMoveUseCase, never()).execute(org.mockito.ArgumentMatchers.any());
    }
}