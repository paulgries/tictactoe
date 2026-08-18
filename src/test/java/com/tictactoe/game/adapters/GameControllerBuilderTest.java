package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.CellSymbol;
import com.tictactoe.application.NewGameRequest;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameMode;
import com.tictactoe.domain.Position;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class GameControllerBuilderTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Test
    void buildWithoutViewThrows() {
        GameControllerBuilder builder = new GameControllerBuilder()
            .uiScheduler(mock(UiScheduler.class));

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class);
    }

    @Test
    void buildWithoutUiSchedulerThrows() {
        GameControllerBuilder builder = new GameControllerBuilder()
            .view(mock(GameView.class));

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class);
    }

    @Test
    void buildWithOnlyRequiredDependenciesDefaultsTheUseCasesAndFactory() {
        GameView view = mock(GameView.class);
        UiScheduler scheduler = mock(UiScheduler.class);

        GameController controller = new GameControllerBuilder()
            .view(view)
            .uiScheduler(scheduler)
            .build();
        controller.onStartGameRequested(new NewGameRequest(CONFIG_3X3, GameMode.TWO_PLAYER, Optional.empty()));
        controller.onCellClicked(0, 0);

        ArgumentCaptor<BoardViewModel> captor = ArgumentCaptor.forClass(BoardViewModel.class);
        verify(view, atLeastOnce()).displayBoard(captor.capture());
        CellSymbol placed = captor.getValue().cells().stream()
            .filter(cell -> cell.position().equals(new Position(0, 0)))
            .findFirst()
            .orElseThrow()
            .symbol();
        assertThat(placed).isEqualTo(CellSymbol.X);
    }
}