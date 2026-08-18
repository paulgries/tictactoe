package game.make_human_move;

import game.GameSessionRules;
import game.GameViewModel;
import game.SessionState;
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
        final SessionState session = gameViewModel.getSession();
        final game.domain.GameState state = session.getCurrentGameState();
        if (state == null || state.isGameOver() || GameSessionRules.isAiTurn(session)) {
            return;
        }

        makeHumanMoveUseCase.execute(new MakeHumanMoveInputData(
                state, new Position(row, column)));
    }
}