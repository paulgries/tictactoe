package com.tictactoe.infra.ui;

import com.tictactoe.adapters.GameController;
import com.tictactoe.adapters.GameView;
import com.tictactoe.adapters.viewmodel.BoardViewModel;
import com.tictactoe.adapters.viewmodel.StatusViewModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public final class MainFrame extends JFrame implements GameView {

    private static final String SETUP_CARD = "setup";
    private static final String GAME_CARD = "game";
    private static final int WINDOW_WIDTH = 520;
    private static final int WINDOW_HEIGHT = 600;
    private static final double BORDER_FRACTION = 0.05;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JPanel gamePanel = new JPanel(new BorderLayout());
    private final SetupPanel setupPanel;
    private final BoardPanel boardPanel;
    private final StatusPanel statusPanel;

    public MainFrame() {
        super("Tic-Tac-Toe");

        setupPanel = new SetupPanel(this);
        boardPanel = new BoardPanel();
        statusPanel = new StatusPanel(this);

        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(statusPanel, BorderLayout.SOUTH);

        cardPanel.add(setupPanel, SETUP_CARD);
        cardPanel.add(gamePanel, GAME_CARD);
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

    private void applyTheme() {
        cardPanel.setBackground(Theme.panelBackground());
        gamePanel.setBackground(Theme.panelBackground());
    }

    public void setController(GameController controller) {
        setupPanel.setController(controller);
        boardPanel.setController(controller);
        statusPanel.setController(controller);
    }

    public void showGameScreen() {
        cardLayout.show(cardPanel, GAME_CARD);
    }

    public void showSetupScreen() {
        cardLayout.show(cardPanel, SETUP_CARD);
    }

    public SetupPanel setupPanel() {
        return setupPanel;
    }

    @Override
    public void displayBoard(BoardViewModel board) {
        boardPanel.render(board);
    }

    @Override
    public void displayStatus(StatusViewModel status) {
        statusPanel.render(status);
    }

    @Override
    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Move", JOptionPane.ERROR_MESSAGE);
    }
}
