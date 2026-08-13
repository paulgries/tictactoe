package com.tictactoe.adapters;

import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.StatusViewModel;

public interface GameView {

    void displayBoard(BoardViewModel board);

    void displayStatus(StatusViewModel status);

    void displayError(String message);
}
