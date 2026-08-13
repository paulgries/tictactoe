package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tictactoe.adapters.viewmodel.CellSymbol;
import com.tictactoe.application.NewGameRequest;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameMode;
import com.tictactoe.domain.Position;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GameControllerBuilderTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Test
    void buildWithoutViewThrows() {
        GameControllerBuilder builder = new GameControllerBuilder()
            .uiScheduler(new ImmediateUiScheduler());

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class);
    }

    @Test
    void buildWithoutUiSchedulerThrows() {
        GameControllerBuilder builder = new GameControllerBuilder()
            .view(new FakeGameView());

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class);
    }

    @Test
    void buildWithOnlyRequiredDependenciesDefaultsTheUseCasesAndFactory() {
        FakeGameView view = new FakeGameView();

        GameController controller = new GameControllerBuilder()
            .view(view)
            .uiScheduler(new ImmediateUiScheduler())
            .build();
        controller.onStartGameRequested(new NewGameRequest(CONFIG_3X3, GameMode.TWO_PLAYER, Optional.empty()));
        controller.onCellClicked(0, 0);

        CellSymbol placed = view.lastBoard().cells().stream()
            .filter(cell -> cell.position().equals(new Position(0, 0)))
            .findFirst()
            .orElseThrow()
            .symbol();
        assertThat(placed).isEqualTo(CellSymbol.X);
    }
}
