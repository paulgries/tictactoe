package game.start_new_game.use_case;

import game.domain.AiDifficulty;
import game.domain.GameMode;
import java.util.Optional;

/**
 * The input data for the Start New Game Use Case. Carries the view
 * primitives; the interactor builds the {@code GameConfig} so invalid
 * settings fail through the output boundary, not as an exception to the
 * view.
 */
public record StartNewGameInputData(
        int boardSize, int winLength, GameMode mode, Optional<AiDifficulty> aiDifficulty) {
}