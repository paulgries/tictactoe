package com.tictactoe.infra;

import com.tictactoe.adapters.GameController;
import com.tictactoe.adapters.GameControllerBuilder;
import com.tictactoe.adapters.GameView;
import com.tictactoe.adapters.WinEffectGameView;
import com.tictactoe.infra.ui.EffectOverlayPanel;
import com.tictactoe.infra.ui.MainFrame;
import com.tictactoe.infra.ui.SwingUiScheduler;
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
