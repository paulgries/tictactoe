package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.CellSymbol;
import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.application.NewGameRequest;
import com.tictactoe.domain.AiDifficulty;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameMode;
import com.tictactoe.domain.Position;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    private GameController newController(FakeGameView view) {
        return newController(view, new ImmediateUiScheduler());
    }

    private GameController newController(FakeGameView view, UiScheduler scheduler) {
        return new GameControllerBuilder()
            .view(view)
            .uiScheduler(scheduler)
            .build();
    }

    private NewGameRequest twoPlayerRequest() {
        return new NewGameRequest(CONFIG_3X3, GameMode.TWO_PLAYER, Optional.empty());
    }

    private CellSymbol cellAt(BoardViewModel board, int row, int column) {
        return board.cells().stream()
            .filter(cell -> cell.position().equals(new Position(row, column)))
            .findFirst()
            .orElseThrow()
            .symbol();
    }

    @Test
    void onStartGameRequestedRendersInitialBoardAndInProgressStatus() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);

        controller.onStartGameRequested(twoPlayerRequest());

        assertThat(view.lastBoard().cells()).hasSize(9);
        assertThat(view.lastBoard().cells()).allMatch(cell -> cell.symbol() == CellSymbol.EMPTY);
        assertThat(view.lastStatus().kind()).isEqualTo(GameOutcomeKind.IN_PROGRESS);
    }

    @Test
    void onCellClickedAppliesHumanMoveAndRendersUpdatedBoard() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());

        controller.onCellClicked(0, 0);

        assertThat(cellAt(view.lastBoard(), 0, 0)).isEqualTo(CellSymbol.X);
    }

    @Test
    void onCellClickedOnOccupiedCellShowsErrorAndDoesNotChangeBoard() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());
        controller.onCellClicked(0, 0);
        BoardViewModel boardAfterFirstMove = view.lastBoard();

        controller.onCellClicked(0, 0);

        assertThat(view.lastError()).isNotNull();
        assertThat(view.lastBoard()).isEqualTo(boardAfterFirstMove);
    }

    @Test
    void onCellClickedIgnoredWhenGameIsOver() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());
        controller.onCellClicked(0, 0); // X
        controller.onCellClicked(1, 0); // O
        controller.onCellClicked(0, 1); // X
        controller.onCellClicked(1, 1); // O
        controller.onCellClicked(0, 2); // X wins
        BoardViewModel boardAfterWin = view.lastBoard();

        controller.onCellClicked(2, 2);

        assertThat(view.lastBoard()).isEqualTo(boardAfterWin);
        assertThat(view.lastError()).isNull();
    }

    @Test
    void onCellClickedInVsAiModeAutomaticallyTriggersAiMoveAfterHumanMove() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));

        controller.onCellClicked(0, 0);

        // MEDIUM now maps to EasyAiStrategy; X at (0,0) leaves no win/block available yet,
        // so it falls back to the center of the (odd-sized) empty board.
        assertThat(cellAt(view.lastBoard(), 0, 0)).isEqualTo(CellSymbol.X);
        assertThat(cellAt(view.lastBoard(), 1, 1)).isEqualTo(CellSymbol.O);
    }

    @Test
    void onCellClickedInVsAiModeDoesNotBlockUntilTheAiMoveCompletes() {
        FakeGameView view = new FakeGameView();
        CapturingUiScheduler scheduler = new CapturingUiScheduler();
        GameController controller = newController(view, scheduler);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));

        controller.onCellClicked(0, 0);

        // the human move is rendered immediately; the AI move hasn't run yet
        assertThat(cellAt(view.lastBoard(), 0, 0)).isEqualTo(CellSymbol.X);
        assertThat(cellAt(view.lastBoard(), 1, 1)).isEqualTo(CellSymbol.EMPTY);

        scheduler.runNextBackgroundTask();
        scheduler.runNextUiTask();

        assertThat(cellAt(view.lastBoard(), 1, 1)).isEqualTo(CellSymbol.O);
    }

    @Test
    void onCellClickedIsIgnoredWhileAnAiMoveIsPending() {
        FakeGameView view = new FakeGameView();
        CapturingUiScheduler scheduler = new CapturingUiScheduler();
        GameController controller = newController(view, scheduler);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));
        controller.onCellClicked(0, 0); // AI's background move is captured, not yet run

        controller.onCellClicked(2, 2); // it's O's (the AI's) turn; this should be ignored

        assertThat(cellAt(view.lastBoard(), 2, 2)).isEqualTo(CellSymbol.EMPTY);
    }

    @Test
    void onRestartRequestedDuringPendingAiMoveDiscardsTheStaleAiMoveResult() {
        FakeGameView view = new FakeGameView();
        CapturingUiScheduler scheduler = new CapturingUiScheduler();
        GameController controller = newController(view, scheduler);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));
        controller.onCellClicked(0, 0); // AI's background move is captured, not yet run

        controller.onRestartRequested();
        scheduler.runNextBackgroundTask();
        scheduler.runNextUiTask();

        assertThat(view.lastBoard().cells()).allMatch(cell -> cell.symbol() == CellSymbol.EMPTY);
    }

    @Test
    void onRestartRequestedResetsToFreshBoardWithSameConfig() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());
        controller.onCellClicked(0, 0);

        controller.onRestartRequested();

        assertThat(view.lastBoard().cells()).allMatch(cell -> cell.symbol() == CellSymbol.EMPTY);
        assertThat(view.lastStatus().kind()).isEqualTo(GameOutcomeKind.IN_PROGRESS);
    }

    @Test
    void onGameWonDisplaysStatusWithWinnerAndHighlightedLine() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());

        controller.onCellClicked(0, 0); // X
        controller.onCellClicked(1, 0); // O
        controller.onCellClicked(0, 1); // X
        controller.onCellClicked(1, 1); // O
        controller.onCellClicked(0, 2); // X wins top row

        assertThat(view.lastStatus().kind()).isEqualTo(GameOutcomeKind.WIN);
        assertThat(view.lastStatus().message()).contains("X");
        assertThat(view.lastBoard().winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }

    @Test
    void onGameDrawnDisplaysDrawStatus() {
        FakeGameView view = new FakeGameView();
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());

        // X O X
        // X X O
        // O X O
        controller.onCellClicked(0, 0); // X
        controller.onCellClicked(0, 1); // O
        controller.onCellClicked(0, 2); // X
        controller.onCellClicked(1, 2); // O
        controller.onCellClicked(1, 0); // X
        controller.onCellClicked(2, 0); // O
        controller.onCellClicked(1, 1); // X
        controller.onCellClicked(2, 2); // O
        controller.onCellClicked(2, 1); // X

        assertThat(view.lastStatus().kind()).isEqualTo(GameOutcomeKind.DRAW);
    }
}
