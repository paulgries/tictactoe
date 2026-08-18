package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.CellSymbol;
import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.adapters.viewmodel.StatusViewModel;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.GameState;
import com.tictactoe.domain.Position;
import org.junit.jupiter.api.Test;

class GameViewModelMapperTest {

    private static final GameConfig CONFIG_3X3 = new GameConfig(3, 3);

    @Test
    void toBoardViewModelReflectsSizeCellsAndInProgressOutcomeOnEmptyBoard() {
        GameState state = GameState.newGame(CONFIG_3X3);

        BoardViewModel board = GameViewModelMapper.toBoardViewModel(state);

        assertThat(board.size()).isEqualTo(3);
        assertThat(board.cells()).hasSize(9);
        assertThat(board.cells()).allMatch(cell -> cell.symbol() == CellSymbol.EMPTY);
        assertThat(board.winningLine()).isEmpty();
        assertThat(board.outcome()).isEqualTo(GameOutcomeKind.IN_PROGRESS);
    }

    @Test
    void toBoardViewModelIncludesWinningLineAndWinOutcomeWhenWon() {
        GameState state = GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0)) // X
            .applyMove(new Position(1, 0)) // O
            .applyMove(new Position(0, 1)) // X
            .applyMove(new Position(1, 1)) // O
            .applyMove(new Position(0, 2)); // X wins top row

        BoardViewModel board = GameViewModelMapper.toBoardViewModel(state);

        assertThat(board.outcome()).isEqualTo(GameOutcomeKind.WIN);
        assertThat(board.winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }

    @Test
    void toBoardViewModelHasDrawOutcomeAndNoWinningLineWhenDrawn() {
        // X O X
        // X X O
        // O X O
        GameState state = GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0)) // X
            .applyMove(new Position(0, 1)) // O
            .applyMove(new Position(0, 2)) // X
            .applyMove(new Position(1, 2)) // O
            .applyMove(new Position(1, 0)) // X
            .applyMove(new Position(2, 0)) // O
            .applyMove(new Position(1, 1)) // X
            .applyMove(new Position(2, 2)) // O
            .applyMove(new Position(2, 1)); // X

        BoardViewModel board = GameViewModelMapper.toBoardViewModel(state);

        assertThat(board.outcome()).isEqualTo(GameOutcomeKind.DRAW);
        assertThat(board.winningLine()).isEmpty();
    }

    @Test
    void toStatusViewModelReflectsCurrentTurnWhenInProgress() {
        GameState state = GameState.newGame(CONFIG_3X3);

        StatusViewModel status = GameViewModelMapper.toStatusViewModel(state);

        assertThat(status.kind()).isEqualTo(GameOutcomeKind.IN_PROGRESS);
        assertThat(status.message()).contains("X");
    }

    @Test
    void toStatusViewModelReflectsWinnerWhenWon() {
        GameState state = GameState.newGame(CONFIG_3X3)
            .applyMove(new Position(0, 0)) // X
            .applyMove(new Position(1, 0)) // O
            .applyMove(new Position(0, 1)) // X
            .applyMove(new Position(1, 1)) // O
            .applyMove(new Position(0, 2)); // X wins

        StatusViewModel status = GameViewModelMapper.toStatusViewModel(state);

        assertThat(status.kind()).isEqualTo(GameOutcomeKind.WIN);
        assertThat(status.message()).contains("X");
    }
}
