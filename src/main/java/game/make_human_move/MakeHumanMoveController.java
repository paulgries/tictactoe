package game.make_human_move;

import game.domain.Position;
import game.make_human_move.use_case.MakeHumanMoveInputBoundary;
import game.make_human_move.use_case.MakeHumanMoveInputData;

/**
 * The Controller for the Make Human Move Use Case. Builds the input data
 * from the clicked cell; the interactor enforces the game's preconditions
 * (no move after the game is over or while the AI is to move).
 */
public class MakeHumanMoveController {

    private final MakeHumanMoveInputBoundary makeHumanMoveUseCase;

    public MakeHumanMoveController(MakeHumanMoveInputBoundary makeHumanMoveUseCase) {
        this.makeHumanMoveUseCase = makeHumanMoveUseCase;
    }

    public void execute(int row, int column) {
        makeHumanMoveUseCase.execute(new MakeHumanMoveInputData(new Position(row, column)));
    }
}