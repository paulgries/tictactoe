package game;

import game.domain.Position;
import java.util.List;

public record BoardRenderState(
    int size, List<CellRenderState> cells, List<Position> winningLine, GameOutcomeKind outcome) {
}
