package framework.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

/**
 * Shared color palette for the Swing UI, switchable between Day and Night mode at
 * runtime. Components register a listener at construction time (Theme.addListener) and
 * reapply their own colors whenever the mode changes, rather than Theme reaching into
 * component internals.
 *
 * Buttons need setOpaque/setBorderPainted/setFocusPainted alongside setBackground, or
 * some look-and-feels (notably Aqua) silently ignore the custom background and keep
 * painting their native chrome.
 */
final class Theme {

    enum Mode { DAY, NIGHT }

    private static final Color DAY_PANEL_BACKGROUND = new Color(0xEC, 0xEC, 0xEC);
    private static final Color DAY_BUTTON_BACKGROUND = new Color(0xF0, 0xF0, 0xF0);
    private static final Color DAY_TEXT_COLOR = Color.BLACK;

    private static final Color NIGHT_PANEL_BACKGROUND = new Color(0x2B, 0x2B, 0x2B);
    private static final Color NIGHT_BUTTON_BACKGROUND = new Color(0x3C, 0x3C, 0x3C);
    private static final Color NIGHT_TEXT_COLOR = Color.WHITE;

    private static final List<Runnable> listeners = new ArrayList<>();

    private static Mode mode = Mode.NIGHT;

    private Theme() {
    }

    static Mode mode() {
        return mode;
    }

    static void setMode(Mode newMode) {
        mode = newMode;
        listeners.forEach(Runnable::run);
    }

    static void addListener(Runnable listener) {
        listeners.add(listener);
    }

    static Color panelBackground() {
        return mode == Mode.NIGHT ? NIGHT_PANEL_BACKGROUND : DAY_PANEL_BACKGROUND;
    }

    static Color buttonBackground() {
        return mode == Mode.NIGHT ? NIGHT_BUTTON_BACKGROUND : DAY_BUTTON_BACKGROUND;
    }

    static Color textColor() {
        return mode == Mode.NIGHT ? NIGHT_TEXT_COLOR : DAY_TEXT_COLOR;
    }

    static void styleButton(JButton button) {
        button.setBackground(buttonBackground());
        button.setForeground(textColor());
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }
}
