package app;

import framework.Theme;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The application window: a card panel hosting the views, which the
 * AppBuilder registers one by one under their view names (as in
 * CAWithBuilder, where the builder owns the card panel). The window itself
 * knows nothing about the game: no view models, controllers, or rendering.
 */
public final class MainFrame extends JFrame {

    private static final int WINDOW_WIDTH = 520;
    private static final int WINDOW_HEIGHT = 600;
    private static final double BORDER_FRACTION = 0.05;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public MainFrame() {
        super("Tic-Tac-Toe");
        int horizontalBorder = (int) (WINDOW_WIDTH * BORDER_FRACTION);
        int verticalBorder = (int) (WINDOW_HEIGHT * BORDER_FRACTION);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(
            verticalBorder, horizontalBorder, verticalBorder, horizontalBorder));

        Theme.addListener(this::applyTheme);
        applyTheme();

        setContentPane(cardPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
    }

    public void addView(JPanel view, String viewName) {
        cardPanel.add(view, viewName);
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    private void applyTheme() {
        cardPanel.setBackground(Theme.panelBackground());
        cardPanel.revalidate();
        cardPanel.repaint();
    }
}