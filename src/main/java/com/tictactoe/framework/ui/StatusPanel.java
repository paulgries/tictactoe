package com.tictactoe.framework.ui;

import com.tictactoe.game.adapters.GameController;
import com.tictactoe.game.adapters.viewmodel.StatusViewModel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class StatusPanel extends JPanel {

    private GameController controller;
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton restartButton = new JButton("Restart");
    private final JButton changeSettingsButton = new JButton("Change Settings");
    private final JPanel buttonPanel = new JPanel();

    public StatusPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        restartButton.addActionListener(e -> controller.onRestartRequested());
        changeSettingsButton.addActionListener(e -> mainFrame.showSetupScreen());

        buttonPanel.add(restartButton);
        buttonPanel.add(changeSettingsButton);

        add(statusLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    private void applyTheme() {
        setBackground(Theme.panelBackground());
        buttonPanel.setBackground(Theme.panelBackground());
        statusLabel.setForeground(Theme.textColor());
        Theme.styleButton(restartButton);
        Theme.styleButton(changeSettingsButton);
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void render(StatusViewModel status) {
        statusLabel.setText(status.message());
    }
}
