package com.tictactoe.framework;

import com.tictactoe.game.adapters.GameController;
import com.tictactoe.game.adapters.GameControllerBuilder;
import com.tictactoe.game.adapters.GameView;
import com.tictactoe.game.adapters.WinEffectGameView;
import com.tictactoe.framework.ui.EffectOverlayPanel;
import com.tictactoe.framework.ui.MainFrame;
import com.tictactoe.framework.ui.SwingUiScheduler;
import java.util.function.BooleanSupplier;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();

            EffectOverlayPanel effects = new EffectOverlayPanel();
            frame.setGlassPane(effects);
            effects.setVisible(true);

            GameView celebratingView = new WinEffectGameView(
                new WinEffectGameView(
                    new WinEffectGameView(
                        frame,
                        ifEnabled(frame.setupPanel()::isConfettiEffectEnabled, effects::playConfetti)),
                    ifEnabled(frame.setupPanel()::isFireworksEffectEnabled, effects::playFireworks)),
                ifEnabled(frame.setupPanel()::isMarksEffectEnabled, effects::playMarks));

            GameController controller = new GameControllerBuilder()
                .view(celebratingView)
                .uiScheduler(new SwingUiScheduler())
                .build();
            frame.setController(controller);
            frame.setVisible(true);
        });
    }

    private static Runnable ifEnabled(BooleanSupplier enabled, Runnable effect) {
        return () -> {
            if (enabled.getAsBoolean()) {
                effect.run();
            }
        };
    }
}
