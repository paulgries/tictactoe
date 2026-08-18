package com.tictactoe.adapters;

import com.tictactoe.adapters.viewmodel.GameOutcomeKind;
import com.tictactoe.adapters.viewmodel.StatusViewModel;

/**
 * Decorates a GameView with a side effect that fires whenever the status becomes a win.
 * Stack several of these around the same base view to run multiple win effects together.
 */
public final class WinEffectGameView extends GameViewDecorator {

    private final Runnable onWin;

    public WinEffectGameView(GameView delegate, Runnable onWin) {
        super(delegate);
        this.onWin = onWin;
    }

    @Override
    public void displayStatus(StatusViewModel status) {
        super.displayStatus(status);
        if (status.kind() == GameOutcomeKind.WIN) {
            onWin.run();
        }
    }
}
