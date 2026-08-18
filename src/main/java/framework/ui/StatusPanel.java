package framework.ui;

import game.StatusViewModel;
import game.start_new_game.StartNewGameController;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class StatusPanel extends JPanel {

    private StartNewGameController startNewGameController;
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton restartButton = new JButton("Restart");
    private final JButton changeSettingsButton = new JButton("Change Settings");
    private final JPanel buttonPanel = new JPanel();

    public StatusPanel() {
        setLayout(new BorderLayout());

        restartButton.addActionListener(e -> startNewGameController.restart());
        changeSettingsButton.addActionListener(e -> startNewGameController.switchToSetupView());

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

    public void setStartNewGameController(StartNewGameController startNewGameController) {
        this.startNewGameController = startNewGameController;
    }

    public void render(StatusViewModel status) {
        statusLabel.setText(status.message());
    }
}
