package com.tictactoe.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.adapters.viewmodel.StatusViewModel;
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
        FakeGameView delegate = new FakeGameView();
        GameView decorator = new NoOpDecorator(delegate);
        BoardViewModel board = new BoardViewModel(3, List.of(), List.of(), GameOutcomeKind.IN_PROGRESS);

        decorator.displayBoard(board);

        assertThat(delegate.lastBoard()).isEqualTo(board);
    }

    @Test
    void displayStatusDelegatesToWrappedView() {
        FakeGameView delegate = new FakeGameView();
        GameView decorator = new NoOpDecorator(delegate);
        StatusViewModel status = new StatusViewModel("X's turn", GameOutcomeKind.IN_PROGRESS);

        decorator.displayStatus(status);

        assertThat(delegate.lastStatus()).isEqualTo(status);
    }

    @Test
    void displayErrorDelegatesToWrappedView() {
        FakeGameView delegate = new FakeGameView();
        GameView decorator = new NoOpDecorator(delegate);

        decorator.displayError("cell occupied");

        assertThat(delegate.lastError()).isEqualTo("cell occupied");
    }
}
