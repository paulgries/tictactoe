package game.make_human_move;

import game.GameRenderState;
import game.GameViewModel;
import game.GameViewModelMapper;
import game.make_human_move.use_case.MakeHumanMoveOutputBoundary;
import game.make_human_move.use_case.MakeHumanMoveOutputData;

/**
 * The Presenter for the Make Human Move Use Case. Renders the updated state
 * from the output data and fires a property change so the view re-renders;
 * when the move leaves the board on the AI's turn, hands off to the AI move
 * request (wired by the AppBuilder).
 */
public class MakeHumanMovePresenter implements MakeHumanMoveOutputBoundary {

    private final GameViewModel gameViewModel;
    private final Runnable requestAiMove;

    public MakeHumanMovePresenter(GameViewModel gameViewModel, Runnable requestAiMove) {
        this.gameViewModel = gameViewModel;
        this.requestAiMove = requestAiMove;
    }

    @Override
    public void prepareSuccessView(MakeHumanMoveOutputData outputData) {
        final GameRenderState render = gameViewModel.getState();
        render.setBoard(GameViewModelMapper.toBoardRenderState(outputData.updatedState()));
        render.setStatus(GameViewModelMapper.toStatusRenderState(outputData.updatedState()));
        render.setMessage(null);
        gameViewModel.firePropertyChanged();

        if (outputData.aiToMoveNext()) {
            requestAiMove.run();
        }
    }

    @Override
    public void prepareFailView(String error) {
        gameViewModel.getState().setMessage(error);
        gameViewModel.firePropertyChanged();
    }
}