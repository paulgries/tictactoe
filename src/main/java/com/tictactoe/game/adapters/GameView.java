package com.tictactoe.game.adapters;

import com.tictactoe.game.adapters.viewmodel.BoardViewModel;
import com.tictactoe.game.adapters.viewmodel.StatusViewModel;

public interface GameView {

    void displayBoard(BoardViewModel board);

    void displayStatus(StatusViewModel status);

    void displayError(String message);
}
