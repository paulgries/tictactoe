package com.tictactoe.adapters;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.StatusViewModel;

/**
 * Base for GameView decorators: forwards every call to the wrapped view unchanged.
 * Subclasses override only the methods where they add behavior.
 */
public abstract class GameViewDecorator implements GameView {

    private final GameView delegate;

    protected GameViewDecorator(GameView delegate) {
        this.delegate = delegate;
    }

    @Override
    public void displayBoard(BoardViewModel board) {
        delegate.displayBoard(board);
    }

    @Override
    public void displayStatus(StatusViewModel status) {
        delegate.displayStatus(status);
    }

    @Override
    public void displayError(String message) {
        delegate.displayError(message);
    }
}
