package com.tictactoe.domain;

import com.tictactoe.domain.exception.InvalidMoveException;
import java.util.List;
import java.util.Optional;

public record GameState(Board board, Mark currentTurn, GameConfig config, GameStatus status) {

    public static final Mark STARTING_MARK = Mark.X;

    public static GameState newGame(GameConfig config) {
        return new GameState(new Board(config.boardSize()), STARTING_MARK, config, new InProgress());
    }

    public boolean isGameOver() {
        return !(status instanceof InProgress);
    }

    public GameState applyMove(Position position) {
        if (isGameOver()) {
            throw new InvalidMoveException("cannot move after the game is over");
        }

        Board updatedBoard = board.placeMark(position, currentTurn);
        Optional<List<Position>> winningLine =
            WinChecker.findWinningLineThrough(updatedBoard, config, position);

        GameStatus newStatus;
        if (winningLine.isPresent()) {
            newStatus = new Win(currentTurn, winningLine.get());
        } else if (updatedBoard.isFull()) {
            newStatus = new Draw();
        } else {
            newStatus = new InProgress();
        }

        return new GameState(updatedBoard, currentTurn.other(), config, newStatus);
    }
}
