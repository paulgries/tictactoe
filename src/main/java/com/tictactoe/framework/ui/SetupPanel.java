package com.tictactoe.framework.ui;

import com.tictactoe.game.adapters.GameController;
import com.tictactoe.game.adapters.NewGameRequestFactory;
import com.tictactoe.game.domain.AiDifficulty;
import com.tictactoe.game.domain.GameMode;
import com.tictactoe.game.domain.exception.InvalidGameConfigException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public final class SetupPanel extends JPanel {

    private static final int START_BUTTON_OUTLINE_THICKNESS = 3;

    private final MainFrame mainFrame;
    private GameController controller;

    private final JSpinner boardSizeSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 10, 1));
    private final JSpinner winLengthSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 10, 1));
    private final JRadioButton twoPlayerButton = new JRadioButton("Two Players", true);
    private final JRadioButton vsComputerButton = new JRadioButton("vs Computer");
    private final JComboBox<AiDifficulty> difficultyBox = new JComboBox<>(AiDifficulty.values());
    private final JCheckBox confettiCheckBox = new JCheckBox("Confetti", true);
    private final JCheckBox fireworksCheckBox = new JCheckBox("Fireworks", true);
    private final JCheckBox marksCheckBox = new JCheckBox("X's & O's", true);
    private final JCheckBox nightModeCheckBox = new JCheckBox("Night Mode", Theme.mode() == Theme.Mode.NIGHT);
    private final JButton startButton = new JButton("Start Game");
    private final JPanel startButtonOutline = new JPanel(new BorderLayout());
    private final List<JLabel> labels = new ArrayList<>();

    public SetupPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridLayout(0, 2, 8, 8));

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(twoPlayerButton);
        modeGroup.add(vsComputerButton);

        difficultyBox.setEnabled(false);
        twoPlayerButton.addItemListener(e -> difficultyBox.setEnabled(vsComputerButton.isSelected()));
        vsComputerButton.addItemListener(e -> difficultyBox.setEnabled(vsComputerButton.isSelected()));

        boardSizeSpinner.addChangeListener(e -> clampWinLengthToBoardSize());

        nightModeCheckBox.addItemListener(
            e -> Theme.setMode(nightModeCheckBox.isSelected() ? Theme.Mode.NIGHT : Theme.Mode.DAY));

        startButton.addActionListener(e -> onStartClicked());
        startButtonOutline.setBorder(BorderFactory.createEmptyBorder(
            START_BUTTON_OUTLINE_THICKNESS, START_BUTTON_OUTLINE_THICKNESS,
            START_BUTTON_OUTLINE_THICKNESS, START_BUTTON_OUTLINE_THICKNESS));
        startButtonOutline.add(startButton, BorderLayout.CENTER);

        add(label("Board size:"));
        add(boardSizeSpinner);
        add(label("Win length:"));
        add(winLengthSpinner);
        add(twoPlayerButton);
        add(vsComputerButton);
        add(label("Difficulty:"));
        add(difficultyBox);
        add(label("Win effects:"));
        add(confettiCheckBox);
        add(label(""));
        add(fireworksCheckBox);
        add(label(""));
        add(marksCheckBox);
        add(label("Appearance:"));
        add(nightModeCheckBox);
        add(label(""));
        add(startButtonOutline);

        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        labels.add(label);
        return label;
    }

    private void applyTheme() {
        setBackground(Theme.panelBackground());
        labels.forEach(l -> l.setForeground(Theme.textColor()));
        twoPlayerButton.setForeground(Theme.textColor());
        vsComputerButton.setForeground(Theme.textColor());
        confettiCheckBox.setForeground(Theme.textColor());
        fireworksCheckBox.setForeground(Theme.textColor());
        marksCheckBox.setForeground(Theme.textColor());
        nightModeCheckBox.setForeground(Theme.textColor());
        startButtonOutline.setBackground(Theme.textColor());
        Theme.styleButton(startButton);
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public boolean isConfettiEffectEnabled() {
        return confettiCheckBox.isSelected();
    }

    public boolean isFireworksEffectEnabled() {
        return fireworksCheckBox.isSelected();
    }

    public boolean isMarksEffectEnabled() {
        return marksCheckBox.isSelected();
    }

    private void clampWinLengthToBoardSize() {
        int boardSize = (Integer) boardSizeSpinner.getValue();
        SpinnerNumberModel winLengthModel = (SpinnerNumberModel) winLengthSpinner.getModel();
        winLengthModel.setMaximum(boardSize);
        if ((Integer) winLengthSpinner.getValue() > boardSize) {
            winLengthSpinner.setValue(boardSize);
        }
    }

    private void onStartClicked() {
        int boardSize = (Integer) boardSizeSpinner.getValue();
        int winLength = (Integer) winLengthSpinner.getValue();
        GameMode mode = vsComputerButton.isSelected() ? GameMode.HUMAN_VS_AI : GameMode.TWO_PLAYER;
        Optional<AiDifficulty> difficulty = mode == GameMode.HUMAN_VS_AI
            ? Optional.of((AiDifficulty) difficultyBox.getSelectedItem())
            : Optional.empty();

        try {
            controller.onStartGameRequested(
                NewGameRequestFactory.create(boardSize, winLength, mode, difficulty));
            mainFrame.showGameScreen();
        } catch (InvalidGameConfigException ex) {
            JOptionPane.showMessageDialog(
                this, ex.getMessage(), "Invalid Settings", JOptionPane.ERROR_MESSAGE);
        }
    }
}
