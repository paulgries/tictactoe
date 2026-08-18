package game.make_human_move;

import game.GameState;
import game.GameViewModel;
import game.domain.Position;
import game.make_human_move.use_case.MakeHumanMoveInputBoundary;
import game.make_human_move.use_case.MakeHumanMoveInputData;

/**
 * The Controller for the Make Human Move Use Case. Builds the input data from
 * the shared session state and ignores clicks that cannot apply (the game is
 * over, or it is the AI's turn).
 */
public class MakeHumanMoveController {

    private final MakeHumanMoveInputBoundary makeHumanMoveUseCase;
    private final GameViewModel gameViewModel;

    public MakeHumanMoveController(
            MakeHumanMoveInputBoundary makeHumanMoveUseCase,
            GameViewModel gameViewModel) {
        this.makeHumanMoveUseCase = makeHumanMoveUseCase;
        this.gameViewModel = gameViewModel;
    }

    public void execute(int row, int column) {
        final GameState state = gameViewModel.getState();
        if (state.isGameOver() || state.isAiTurn()) {
            return;
        }

        makeHumanMoveUseCase.execute(new MakeHumanMoveInputData(
                state.getCurrentGameState(), new Position(row, column)));
    }
}