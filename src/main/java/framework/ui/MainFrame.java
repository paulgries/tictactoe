package framework.ui;

import game.GameOutcomeKind;
import game.GameRenderState;
import game.GameViewModel;
import game.load_game.LoadGameController;
import game.make_human_move.MakeHumanMoveController;
import game.save_game.SaveGameController;
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
 * view model fires a property change, shows transient messages, and runs
 * the configured win effects whenever the status becomes a win.
 */
public final class MainFrame extends JFrame implements PropertyChangeListener {

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

        setupPanel = new SetupPanel();
        boardPanel = new BoardPanel();
        statusPanel = new StatusPanel();

        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(statusPanel, BorderLayout.SOUTH);

        cardPanel.add(setupPanel, setupPanel.getViewName());
        cardPanel.add(gamePanel, gameViewModel.getViewName());
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

    public void setSaveGameController(SaveGameController saveGameController) {
        statusPanel.setSaveGameController(saveGameController);
    }

    public void setLoadGameController(LoadGameController loadGameController) {
        setupPanel.setLoadGameController(loadGameController);
    }

    public void setMakeHumanMoveController(MakeHumanMoveController makeHumanMoveController) {
        boardPanel.setMakeHumanMoveController(makeHumanMoveController);
    }

    public void setWinEffects(List<Runnable> winEffects) {
        this.winEffects = winEffects;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public SetupPanel setupPanel() {
        return setupPanel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final GameRenderState state = (GameRenderState) evt.getNewValue();
        if (state.getBoard() != null) {
            boardPanel.render(state.getBoard());
        }
        if (state.getStatus() != null) {
            statusPanel.render(state.getStatus());
        }
        if (state.getMessage() != null) {
            String message = state.getMessage();
            state.setMessage(null);
            JOptionPane.showMessageDialog(
                this, message, "Tic-Tac-Toe", JOptionPane.INFORMATION_MESSAGE);
        }
        if (state.getStatus() != null && state.getStatus().kind() == GameOutcomeKind.WIN) {
            winEffects.forEach(Runnable::run);
        }
    }
}