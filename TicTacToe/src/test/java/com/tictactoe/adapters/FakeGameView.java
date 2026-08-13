package com.tictactoe.adapters;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.StatusViewModel;

public final class FakeGameView implements GameView {

    private BoardViewModel lastBoard;
    private StatusViewModel lastStatus;
    private String lastError;

    @Override
    public void displayBoard(BoardViewModel board) {
        this.lastBoard = board;
    }

    @Override
    public void displayStatus(StatusViewModel status) {
        this.lastStatus = status;
    }

    @Override
    public void displayError(String message) {
        this.lastError = message;
    }

    public BoardViewModel lastBoard() {
        return lastBoard;
    }

    public StatusViewModel lastStatus() {
        return lastStatus;
    }

    public String lastError() {
        return lastError;
    }
}
