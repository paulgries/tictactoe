package com.tictactoe.game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tictactoe.game.NewGameRequest;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.GameMode;
import com.tictactoe.game.domain.InProgress;
import com.tictactoe.game.domain.Mark;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StartNewGameInteractorTest {

    @Mock
    private StartNewGameOutputBoundary presenter;

    private StartNewGameInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new StartNewGameInteractor(presenter);
    }

    @Test
    void execute_ValidRequest_PresentsConfiguredBoardSizeAndWinLength() {
        GameConfig config = new GameConfig(4, 3);
        NewGameRequest request = new NewGameRequest(config, GameMode.TWO_PLAYER, Optional.empty());

        interactor.execute(new StartNewGameInputData(request));

        ArgumentCaptor<StartNewGameOutputData> captor =
            ArgumentCaptor.forClass(StartNewGameOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().gameState().config()).isEqualTo(config);
        assertThat(captor.getValue().gameState().board().emptyPositions()).hasSize(16);
        verify(presenter, never()).prepareFailView(any());
    }

    @Test
    void execute_ValidRequest_PresentsEmptyBoardXsTurnAndInProgressStatus() {
        GameConfig config = new GameConfig(3, 3);
        NewGameRequest request = new NewGameRequest(config, GameMode.HUMAN_VS_AI, Optional.empty());

        interactor.execute(new StartNewGameInputData(request));

        ArgumentCaptor<StartNewGameOutputData> captor =
            ArgumentCaptor.forClass(StartNewGameOutputData.class);
        verify(presenter).prepareSuccessView(captor.capture());
        assertThat(captor.getValue().gameState().currentTurn()).isEqualTo(Mark.X);
        assertThat(captor.getValue().gameState().status()).isInstanceOf(InProgress.class);
    }
}