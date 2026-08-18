package game.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import game.domain.exception.InvalidMoveException;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    void emptyBoardHasAllCellsEmpty() {
        Board board = new Board(3);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertThat(board.get(new Position(row, column))).isEmpty();
            }
        }
    }

    @Test
    void placeMarkOnEmptyCellReturnsNewBoardWithMarkSet() {
        Board board = new Board(3);
        Position position = new Position(1, 1);

        Board updated = board.placeMark(position, Mark.X);

        assertThat(updated.get(position)).contains(Mark.X);
    }

    @Test
    void placeMarkDoesNotMutateOriginalBoard() {
        Board board = new Board(3);
        Position position = new Position(1, 1);

        board.placeMark(position, Mark.X);

        assertThat(board.get(position)).isEmpty();
    }

    @Test
    void placeMarkOnOccupiedCellThrowsInvalidMoveException() {
        Board board = new Board(3);
        Position position = new Position(0, 0);
        Board updated = board.placeMark(position, Mark.X);

        assertThatThrownBy(() -> updated.placeMark(position, Mark.O))
            .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    void placeMarkOutOfBoundsThrowsInvalidMoveException() {
        Board board = new Board(3);

        assertThatThrownBy(() -> board.placeMark(new Position(3, 0), Mark.X))
            .isInstanceOf(InvalidMoveException.class);
        assertThatThrownBy(() -> board.placeMark(new Position(0, 3), Mark.X))
            .isInstanceOf(InvalidMoveException.class);
    }

    @Test
    void isFullReturnsFalseWhenEmptyCellsRemain() {
        Board board = new Board(2)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.O)
            .placeMark(new Position(1, 0), Mark.X);

        assertThat(board.isFull()).isFalse();
    }

    @Test
    void isFullReturnsTrueWhenAllCellsOccupied() {
        Board board = new Board(2)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(0, 1), Mark.O)
            .placeMark(new Position(1, 0), Mark.O)
            .placeMark(new Position(1, 1), Mark.X);

        assertThat(board.isFull()).isTrue();
    }

    @Test
    void emptyPositionsReturnsAllUnoccupiedCoordinatesInRowMajorOrder() {
        Board board = new Board(2).placeMark(new Position(0, 1), Mark.X);

        assertThat(board.emptyPositions())
            .containsExactly(new Position(0, 0), new Position(1, 0), new Position(1, 1));
    }

    @Test
    void boardsWithSameMarksInSamePlacesAreEqualAndHashEqual() {
        Board a = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(1, 1), Mark.O);
        Board b = new Board(3)
            .placeMark(new Position(0, 0), Mark.X)
            .placeMark(new Position(1, 1), Mark.O);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void boardsDifferingInMarksOrSizeAreNotEqual() {
        Board withX = new Board(3).placeMark(new Position(0, 0), Mark.X);
        Board withO = new Board(3).placeMark(new Position(0, 0), Mark.O);
        Board empty = new Board(3);

        assertThat(withX).isNotEqualTo(withO);
        assertThat(withX).isNotEqualTo(empty);
        assertThat(withX).isNotEqualTo(new Board(4));
        assertThat(withX).isNotEqualTo(null);
    }
}
