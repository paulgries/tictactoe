package setup;

import framework.Theme;
import game.domain.AiDifficulty;
import game.domain.GameMode;
import persistence.load_game.LoadGameController;
import setup.start_new_game.StartNewGameController;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

/**
 * The view for the setup screen, following the CAWithBuilder pattern: it
 * binds to its {@link SetupViewModel}, writes the widgets' values into the
 * {@link SetupState} as the player changes them, and shows transient
 * messages (e.g. an invalid configuration) that presenters put in the state.
 * Only the night-mode toggle is handled directly, since the theme is global.
 */
public final class SetupPanel extends JPanel implements PropertyChangeListener {

    private static final int START_BUTTON_OUTLINE_THICKNESS = 3;

    private final SetupViewModel setupViewModel;
    private StartNewGameController startNewGameController;
    private LoadGameController loadGameController;

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
    private final JButton resumeButton = new JButton("Resume Saved Game");
    private final JPanel startButtonOutline = new JPanel(new BorderLayout());
    private final List<JLabel> labels = new ArrayList<>();

    public SetupPanel(SetupViewModel setupViewModel) {
        this.setupViewModel = setupViewModel;
        setupViewModel.addPropertyChangeListener(this);
        setLayout(new GridLayout(0, 2, 8, 8));

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(twoPlayerButton);
        modeGroup.add(vsComputerButton);

        difficultyBox.setEnabled(false);
        twoPlayerButton.addItemListener(e -> onModeChanged());
        vsComputerButton.addItemListener(e -> onModeChanged());

        boardSizeSpinner.addChangeListener(e -> {
            clampWinLengthToBoardSize();
            setupViewModel.getState().setBoardSize((Integer) boardSizeSpinner.getValue());
        });
        winLengthSpinner.addChangeListener(e ->
            setupViewModel.getState().setWinLength((Integer) winLengthSpinner.getValue()));
        difficultyBox.addItemListener(e ->
            setupViewModel.getState().setDifficulty(
                vsComputerButton.isSelected()
                    ? Optional.of((AiDifficulty) difficultyBox.getSelectedItem())
                    : Optional.empty()));

        confettiCheckBox.addItemListener(e ->
            setupViewModel.getState().setConfettiEnabled(confettiCheckBox.isSelected()));
        fireworksCheckBox.addItemListener(e ->
            setupViewModel.getState().setFireworksEnabled(fireworksCheckBox.isSelected()));
        marksCheckBox.addItemListener(e ->
            setupViewModel.getState().setMarksEnabled(marksCheckBox.isSelected()));

        nightModeCheckBox.addItemListener(
            e -> Theme.setMode(nightModeCheckBox.isSelected() ? Theme.Mode.NIGHT : Theme.Mode.DAY));

        startButton.addActionListener(e -> onStartClicked());
        resumeButton.addActionListener(e -> loadGameController.execute());
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
        add(label(""));
        add(resumeButton);

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
        Theme.styleButton(resumeButton);
    }

    public void setStartNewGameController(StartNewGameController startNewGameController) {
        this.startNewGameController = startNewGameController;
    }

    public void setLoadGameController(LoadGameController loadGameController) {
        this.loadGameController = loadGameController;
    }

    public String getViewName() {
        return setupViewModel.getViewName();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final SetupState state = setupViewModel.getState();
        if (state.getMessage() != null) {
            String message = state.getMessage();
            state.setMessage(null);
            JOptionPane.showMessageDialog(
                this, message, "Tic-Tac-Toe", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onModeChanged() {
        boolean vsComputer = vsComputerButton.isSelected();
        difficultyBox.setEnabled(vsComputer);
        final SetupState state = setupViewModel.getState();
        state.setMode(vsComputer ? GameMode.HUMAN_VS_AI : GameMode.TWO_PLAYER);
        state.setDifficulty(vsComputer
            ? Optional.of((AiDifficulty) difficultyBox.getSelectedItem())
            : Optional.empty());
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
        final SetupState state = setupViewModel.getState();
        startNewGameController.execute(
            state.getBoardSize(), state.getWinLength(), state.getMode(), state.getDifficulty());
    }
}