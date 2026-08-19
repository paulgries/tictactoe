package play;

import play.GameOutcomeKind;
import play.GameRenderState;
import play.GameViewModel;
import play.make_human_move.MakeHumanMoveController;
import persistence.save_game.SaveGameController;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The view for the game screen, following the CAWithBuilder pattern: it
 * binds to the shared {@link GameViewModel}, renders the board and status
 * whenever a presenter fires a property change, shows transient messages
 * (e.g. "Game saved"), and runs the configured win effects whenever the
 * status becomes a win.
 */
public final class GamePanel extends JPanel implements PropertyChangeListener {

    private final BoardPanel boardPanel;
    private final StatusPanel statusPanel;
    private final GameViewModel gameViewModel;

    private List<Runnable> winEffects = List.of();

    public GamePanel(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
        gameViewModel.addPropertyChangeListener(this);
        setLayout(new BorderLayout());

        boardPanel = new BoardPanel();
        statusPanel = new StatusPanel();
        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void setMakeHumanMoveController(MakeHumanMoveController makeHumanMoveController) {
        boardPanel.setMakeHumanMoveController(makeHumanMoveController);
    }

    public void setSaveGameController(SaveGameController saveGameController) {
        statusPanel.setSaveGameController(saveGameController);
    }

    public void setStartNewGameController(
            setup.start_new_game.StartNewGameController startNewGameController) {
        statusPanel.setStartNewGameController(startNewGameController);
    }

    public void setWinEffects(List<Runnable> winEffects) {
        this.winEffects = winEffects;
    }

    public String getViewName() {
        return gameViewModel.getViewName();
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