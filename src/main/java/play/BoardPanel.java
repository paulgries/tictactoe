package play;

import framework.Theme;
import play.BoardRenderState;
import play.CellRenderState;
import play.CellSymbol;
import play.GameOutcomeKind;
import game.domain.Position;
import play.make_human_move.MakeHumanMoveController;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public final class BoardPanel extends JPanel {

    private static final Color WINNING_LINE_COLOR = new Color(144, 238, 144);
    private static final Color WINNING_LINE_TEXT_COLOR = Color.BLACK;
    private static final int GRID_GAP = 2;
    private static final double FONT_TO_CELL_RATIO = 0.42;
    private static final float MIN_FONT_SIZE = 10f;
    private static final float MAX_FONT_SIZE = 48f;
    private static final int FALLBACK_CELL_DIMENSION = 60;

    private MakeHumanMoveController makeHumanMoveController;
    private final Map<Position, JButton> buttons = new HashMap<>();
    private int currentSize = -1;
    private BoardRenderState lastRendered;

    public BoardPanel() {
        Theme.addListener(this::applyTheme);
        applyTheme();
    }

    public void setMakeHumanMoveController(MakeHumanMoveController makeHumanMoveController) {
        this.makeHumanMoveController = makeHumanMoveController;
    }

    public void render(BoardRenderState board) {
        if (board.size() != currentSize) {
            rebuildGrid(board.size());
        }
        lastRendered = board;

        boolean gameOver = board.outcome() != GameOutcomeKind.IN_PROGRESS;

        for (CellRenderState cell : board.cells()) {
            JButton button = buttons.get(cell.position());
            button.setText(symbolText(cell.symbol()));
            button.setEnabled(cell.symbol() == CellSymbol.EMPTY && !gameOver);
            boolean isWinningCell = board.winningLine().contains(cell.position());
            button.setBackground(isWinningCell ? WINNING_LINE_COLOR : Theme.buttonBackground());
            button.setForeground(isWinningCell ? WINNING_LINE_TEXT_COLOR : Theme.textColor());
        }
    }

    private void rebuildGrid(int size) {
        removeAll();
        buttons.clear();
        setLayout(new GridLayout(size, size, GRID_GAP, GRID_GAP));

        float fontSize = cellFontSize(size);

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Position position = new Position(row, column);
                JButton button = new JButton();
                button.setFont(button.getFont().deriveFont(fontSize));
                // strip the look-and-feel's default margin/border insets, which are a
                // roughly fixed number of pixels regardless of cell size and were eating
                // an ever-larger share of the available width as boards got smaller
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setBorder(BorderFactory.createEmptyBorder());
                button.addActionListener(e -> makeHumanMoveController.execute(position.row(), position.column()));
                Theme.styleButton(button);
                buttons.put(position, button);
                add(button);
            }
        }

        currentSize = size;
        revalidate();
        repaint();
    }

    /**
     * The window is a fixed size, so as boardSize grows, GridLayout packs more cells
     * into the same area and each cell shrinks. A font size that fit a small board
     * would overflow a small cell on a large one, so scale it to whatever space this
     * panel actually has (falling back to a sane default before the panel is ever
     * sized, e.g. the very first layout pass).
     */
    private float cellFontSize(int size) {
        int cellWidth = getWidth() > 0 ? getWidth() / size : FALLBACK_CELL_DIMENSION;
        int cellHeight = getHeight() > 0 ? getHeight() / size : FALLBACK_CELL_DIMENSION;
        int cellDimension = Math.min(cellWidth, cellHeight);
        float fontSize = (float) (cellDimension * FONT_TO_CELL_RATIO);
        return Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, fontSize));
    }

    private void applyTheme() {
        // doubles as the grid's cell-separator color, so it always contrasts with
        // Theme.buttonBackground() the same way body text does
        setBackground(Theme.textColor());
        if (lastRendered != null) {
            render(lastRendered);
        } else {
            buttons.values().forEach(Theme::styleButton);
        }
    }

    private String symbolText(CellSymbol symbol) {
        return switch (symbol) {
            case X -> "X";
            case O -> "O";
            case EMPTY -> "";
        };
    }
}
