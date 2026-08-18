package game;

import static org.assertj.core.api.Assertions.assertThat;

import game.BoardViewModel;
import game.CellSymbol;
import game.GameOutcomeKind;
import game.StatusViewModel;
import game.domain.GameConfig;
import game.domain.GameState;
import game.domain.Position;
import game.testutil.GameFixtures;
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
        GameState state = GameFixtures.wonByX();

        BoardViewModel board = GameViewModelMapper.toBoardViewModel(state);

        assertThat(board.outcome()).isEqualTo(GameOutcomeKind.WIN);
        assertThat(board.winningLine()).containsExactlyInAnyOrder(
            new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }

    @Test
    void toBoardViewModelHasDrawOutcomeAndNoWinningLineWhenDrawn() {
        GameState state = GameFixtures.drawn();

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
        GameState state = GameFixtures.wonByX();

        StatusViewModel status = GameViewModelMapper.toStatusViewModel(state);

        assertThat(status.kind()).isEqualTo(GameOutcomeKind.WIN);
        assertThat(status.message()).contains("X");
    }
}
