package framework.ui;

import game.GameState;
import game.GameOutcomeKind;
import game.GameViewModel;
import game.make_human_move.MakeHumanMoveController;
import game.start_new_game.StartNewGameController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The main window. Renders the board and status whenever the shared game
 * view model fires a property change, shows errors, and runs the configured
 * win effects whenever the status becomes a win.
 */
public final class MainFrame extends JFrame implements PropertyChangeListener {

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
    private final GameViewModel gameViewModel;

    private List<Runnable> winEffects = List.of();

    public MainFrame(GameViewModel gameViewModel) {
        super("Tic-Tac-Toe");
        this.gameViewModel = gameViewModel;
        gameViewModel.addPropertyChangeListener(this);

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

    public void setStartNewGameController(StartNewGameController startNewGameController) {
        setupPanel.setStartNewGameController(startNewGameController);
        statusPanel.setStartNewGameController(startNewGameController);
    }

    public void setMakeHumanMoveController(MakeHumanMoveController makeHumanMoveController) {
        boardPanel.setMakeHumanMoveController(makeHumanMoveController);
    }

    public void setWinEffects(List<Runnable> winEffects) {
        this.winEffects = winEffects;
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
    public void propertyChange(PropertyChangeEvent evt) {
        final GameState state = (GameState) evt.getNewValue();
        if (state.getBoard() != null) {
            boardPanel.render(state.getBoard());
        }
        if (state.getStatus() != null) {
            statusPanel.render(state.getStatus());
        }
        if (state.getError() != null) {
            String error = state.getError();
            state.setError(null);
            JOptionPane.showMessageDialog(
                this, error, "Invalid Move", JOptionPane.ERROR_MESSAGE);
        }
        if (state.getStatus() != null && state.getStatus().kind() == GameOutcomeKind.WIN) {
            winEffects.forEach(Runnable::run);
        }
    }
}