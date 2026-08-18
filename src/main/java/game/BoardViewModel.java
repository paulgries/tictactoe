package game;

import game.domain.Position;
import java.util.List;

public record BoardViewModel(
    int size, List<CellViewModel> cells, List<Position> winningLine, GameOutcomeKind outcome) {
}
