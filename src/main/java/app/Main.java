package app;

import javax.swing.JFrame;

/**
 * The entry point. Chains the AppBuilder's per-frame and per-use-case wiring
 * methods, then shows the frame.
 */
public class Main {

    public static void main(String[] args) {
        JFrame application = new AppBuilder()
                .addGameView()
                .addRequestAiMoveUseCase()
                .addMakeHumanMoveUseCase()
                .addStartNewGameUseCase()
                .build();

        application.pack();
        application.setVisible(true);
    }
}