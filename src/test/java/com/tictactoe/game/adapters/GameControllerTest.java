package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.CellSymbol;
import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.adapters.viewmodel.StatusViewModel;
import com.tictactoe.application.NewGameRequest;
import com.tictactoe.domain.AiDifficulty;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameMode;
import com.tictactoe.domain.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class GameControllerTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    private GameController newController(GameView view) {
        return newController(view, immediateScheduler());
    }

    private GameController newController(GameView view, UiScheduler scheduler) {
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

    private BoardViewModel lastBoard(GameView view) {
        ArgumentCaptor<BoardViewModel> captor = ArgumentCaptor.forClass(BoardViewModel.class);
        verify(view, atLeastOnce()).displayBoard(captor.capture());
        List<BoardViewModel> all = captor.getAllValues();
        return all.get(all.size() - 1);
    }

    private StatusViewModel lastStatus(GameView view) {
        ArgumentCaptor<StatusViewModel> captor = ArgumentCaptor.forClass(StatusViewModel.class);
        verify(view, atLeastOnce()).displayStatus(captor.capture());
        List<StatusViewModel> all = captor.getAllValues();
        return all.get(all.size() - 1);
    }

    private String lastError(GameView view) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).displayError(captor.capture());
        List<String> all = captor.getAllValues();
        return all.get(all.size() - 1);
    }

    private UiScheduler immediateScheduler() {
        UiScheduler scheduler = mock(UiScheduler.class);
        doAnswer(invocation -> {
            invocation.<Runnable>getArgument(0).run();
            return null;
        }).when(scheduler).runInBackground(any());
        doAnswer(invocation -> {
            invocation.<Runnable>getArgument(0).run();
            return null;
        }).when(scheduler).runOnUiThread(any());
        return scheduler;
    }

    private static final class CapturingScheduler {
        private final UiScheduler scheduler = mock(UiScheduler.class);
        private final List<Runnable> backgroundTasks = new ArrayList<>();
        private final List<Runnable> uiTasks = new ArrayList<>();

        private CapturingScheduler() {
            doAnswer(invocation -> {
                backgroundTasks.add(invocation.getArgument(0));
                return null;
            }).when(scheduler).runInBackground(any());
            doAnswer(invocation -> {
                uiTasks.add(invocation.getArgument(0));
                return null;
            }).when(scheduler).runOnUiThread(any());
        }

        private void runNextBackgroundTask() {
            backgroundTasks.remove(0).run();
        }

        private void runNextUiTask() {
            uiTasks.remove(0).run();
        }
    }

    @Test
    void onStartGameRequestedRendersInitialBoardAndInProgressStatus() {
        GameView view = mock(GameView.class);
        GameController controller = newController(view);

        controller.onStartGameRequested(twoPlayerRequest());

        BoardViewModel board = lastBoard(view);
        assertThat(board.cells()).hasSize(9);
        assertThat(board.cells()).allMatch(cell -> cell.symbol() == CellSymbol.EMPTY);
        assertThat(lastStatus(view).kind()).isEqualTo(GameOutcomeKind.IN_PROGRESS);
    }

    @Test
    void onCellClickedAppliesHumanMoveAndRendersUpdatedBoard() {
        GameView view = mock(GameView.class);
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());

        controller.onCellClicked(0, 0);

        assertThat(cellAt(lastBoard(view), 0, 0)).isEqualTo(CellSymbol.X);
    }

    @Test
    void onCellClickedOnOccupiedCellShowsErrorAndDoesNotChangeBoard() {
        GameView view = mock(GameView.class);
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());
        controller.onCellClicked(0, 0);
        BoardViewModel boardAfterFirstMove = lastBoard(view);

        controller.onCellClicked(0, 0);

        assertThat(lastError(view)).isNotNull();
        assertThat(lastBoard(view)).isEqualTo(boardAfterFirstMove);
    }

    @Test
    void onCellClickedIgnoredWhenGameIsOver() {
        GameView view = mock(GameView.class);
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());
        controller.onCellClicked(0, 0); // X
        controller.onCellClicked(1, 0); // O
        controller.onCellClicked(0, 1); // X
        controller.onCellClicked(1, 1); // O
        controller.onCellClicked(0, 2); // X wins
        BoardViewModel boardAfterWin = lastBoard(view);

        controller.onCellClicked(2, 2);

        assertThat(lastBoard(view)).isEqualTo(boardAfterWin);
        verify(view, never()).displayError(any());
    }

    @Test
    void onCellClickedInVsAiModeAutomaticallyTriggersAiMoveAfterHumanMove() {
        GameView view = mock(GameView.class);
        GameController controller = newController(view);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));

        controller.onCellClicked(0, 0);

        // MEDIUM now maps to EasyAiStrategy; X at (0,0) leaves no win/block available yet,
        // so it falls back to the center of the (odd-sized) empty board.
        assertThat(cellAt(lastBoard(view), 0, 0)).isEqualTo(CellSymbol.X);
        assertThat(cellAt(lastBoard(view), 1, 1)).isEqualTo(CellSymbol.O);
    }

    @Test
    void onCellClickedInVsAiModeDoesNotBlockUntilTheAiMoveCompletes() {
        GameView view = mock(GameView.class);
        CapturingScheduler scheduler = new CapturingScheduler();
        GameController controller = newController(view, scheduler.scheduler);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));

        controller.onCellClicked(0, 0);

        // the human move is rendered immediately; the AI move hasn't run yet
        assertThat(cellAt(lastBoard(view), 0, 0)).isEqualTo(CellSymbol.X);
        assertThat(cellAt(lastBoard(view), 1, 1)).isEqualTo(CellSymbol.EMPTY);

        scheduler.runNextBackgroundTask();
        scheduler.runNextUiTask();

        assertThat(cellAt(lastBoard(view), 1, 1)).isEqualTo(CellSymbol.O);
    }

    @Test
    void onCellClickedIsIgnoredWhileAnAiMoveIsPending() {
        GameView view = mock(GameView.class);
        CapturingScheduler scheduler = new CapturingScheduler();
        GameController controller = newController(view, scheduler.scheduler);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));
        controller.onCellClicked(0, 0); // AI's background move is captured, not yet run

        controller.onCellClicked(2, 2); // it's O's (the AI's) turn; this should be ignored

        assertThat(cellAt(lastBoard(view), 2, 2)).isEqualTo(CellSymbol.EMPTY);
    }

    @Test
    void onRestartRequestedDuringPendingAiMoveDiscardsTheStaleAiMoveResult() {
        GameView view = mock(GameView.class);
        CapturingScheduler scheduler = new CapturingScheduler();
        GameController controller = newController(view, scheduler.scheduler);
        controller.onStartGameRequested(
            new NewGameRequest(CONFIG_3X3, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM)));
        controller.onCellClicked(0, 0); // AI's background move is captured, not yet run

        controller.onRestartRequested();
        scheduler.runNextBackgroundTask();
        scheduler.runNextUiTask();

        assertThat(lastBoard(view).cells()).allMatch(cell -> cell.symbol() == CellSymbol.EMPTY);
    }

    @Test
    void onRestartRequestedResetsToFreshBoardWithSameConfig() {
        GameView view = mock(GameView.class);
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());
        controller.onCellClicked(0, 0);

        controller.onRestartRequested();

        assertThat(lastBoard(view).cells()).allMatch(cell -> cell.symbol() == CellSymbol.EMPTY);
        assertThat(lastStatus(view).kind()).isEqualTo(GameOutcomeKind.IN_PROGRESS);
    }

    @Test
    void onGameWonDisplaysStatusWithWinnerAndHighlightedLine() {
        GameView view = mock(GameView.class);
        GameController controller = newController(view);
        controller.onStartGameRequested(twoPlayerRequest());

        controller.onCellClicked(0, 0); // X
        controller.onCellClicked(1, 0); // O
        controller.onCellClicked(0, 1); // X
        controller.onCellClicked(1, 1); // O
        controller.onCellClicked(0, 2); // X wins top row

        assertThat(lastStatus(view).kind()).isEqualTo(GameOutcomeKind.WIN);
        assertThat(lastStatus(view).message()).contains("X");
        assertThat(lastBoard(view).winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }

    @Test
    void onGameDrawnDisplaysDrawStatus() {
        GameView view = mock(GameView.class);
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

        assertThat(lastStatus(view).kind()).isEqualTo(GameOutcomeKind.DRAW);
    }
}