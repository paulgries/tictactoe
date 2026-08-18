package com.tictactoe.game.adapters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.tictactoe.game.adapters.viewmodel.BoardViewModel;
import com.tictactoe.game.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.game.adapters.viewmodel.StatusViewModel;
import java.util.List;
import org.junit.jupiter.api.Test;

class GameViewDecoratorTest {

    private static final class NoOpDecorator extends GameViewDecorator {
        NoOpDecorator(GameView delegate) {
            super(delegate);
        }
    }

    @Test
    void displayBoardDelegatesToWrappedView() {
        GameView delegate = mock(GameView.class);
        GameView decorator = new NoOpDecorator(delegate);
        BoardViewModel board = new BoardViewModel(3, List.of(), List.of(), GameOutcomeKind.IN_PROGRESS);

        decorator.displayBoard(board);

        verify(delegate).displayBoard(board);
    }

    @Test
    void displayStatusDelegatesToWrappedView() {
        GameView delegate = mock(GameView.class);
        GameView decorator = new NoOpDecorator(delegate);
        StatusViewModel status = new StatusViewModel("X's turn", GameOutcomeKind.IN_PROGRESS);

        decorator.displayStatus(status);

        verify(delegate).displayStatus(status);
    }

    @Test
    void displayErrorDelegatesToWrappedView() {
        GameView delegate = mock(GameView.class);
        GameView decorator = new NoOpDecorator(delegate);

        decorator.displayError("cell occupied");

        verify(delegate).displayError("cell occupied");
    }
}