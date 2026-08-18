package com.tictactoe.game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;
import com.tictactoe.game.testutil.GameFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestAiMoveInteractorTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    private final AiStrategy fixedMoveStrategy = new AiStrategy() {
        @Override
        public Position selectMove(Board board, GameConfig config, Mark aiMark) {
            return new Position(1, 1);
        }
    };

    @Mock
    private RequestAiMoveOutputBoundary presenter;

    private RequestAiMoveInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new RequestAiMoveInteractor(presenter);
    }

    @Test
    void execute_ValidState_AsksStrategyForMoveAndPresentsUpdatedState() {
        GameState state = GameState.newGame(CONFIG_3X3);

        interactor.execute(new RequestAiMoveInputData(state, fixedMoveStrategy));

        ArgumentCaptor<RequestAiMoveOutputData> captor =
            ArgumentCaptor.forClass(RequestAiMoveOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().updatedState().board().get(new Position(1, 1))).contains(Mark.X);
        assertThat(captor.getValue().updatedState().currentTurn()).isEqualTo(Mark.O);
        verify(presenter, never()).prepareFailView(any());
    }

    @Test
    void execute_GameAlreadyOver_PresentsFailView() {
        GameState state = GameFixtures.wonByX();

        interactor.execute(new RequestAiMoveInputData(state, fixedMoveStrategy));

        verify(presenter).prepareFailView("cannot request an AI move after the game is over");
        verify(presenter, never()).prepareSuccessView(any());
    }
}