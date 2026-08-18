package framework;

import app.AppBuilder;
import javax.swing.SwingUtilities;

/**
 * The entry point. The AppBuilder wires the whole application; this class
 * only schedules the UI startup on the Swing event dispatch thread.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppBuilder().build().setVisible(true));
    }
}