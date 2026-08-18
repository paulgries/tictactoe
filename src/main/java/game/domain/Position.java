package game.domain;

public record Position(int row, int column) {

    public Position {
        if (row < 0) {
            throw new IllegalArgumentException("row must not be negative: " + row);
        }
        if (column < 0) {
            throw new IllegalArgumentException("column must not be negative: " + column);
        }
    }
}
