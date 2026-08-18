package com.tictactoe.game.use_case;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.testutil.GameFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestAiMoveInteractorTest {

    @Mock
    private RequestAiMoveOutputBoundary presenter;

    @Mock
    private AiStrategy strategy;

    private RequestAiMoveInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new RequestAiMoveInteractor(presenter);
    }

    @Test
    void execute_GameAlreadyOver_PresentsFailViewWithoutConsultingStrategy() {
        GameState state = GameFixtures.wonByX();

        interactor.execute(new RequestAiMoveInputData(state, strategy));

        verify(presenter).prepareFailView("cannot request an AI move after the game is over");
        verify(presenter, never()).prepareSuccessView(any());
        verify(strategy, never()).selectMove(any(Board.class), any(GameConfig.class), any(Mark.class));
    }
}