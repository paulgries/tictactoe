package game.make_human_move.use_case;

import game.domain.exception.InvalidMoveException;

/**
 * The Interactor for the Make Human Move Use Case.
 */
public final class MakeHumanMoveInteractor implements MakeHumanMoveInputBoundary {

    private final MakeHumanMoveOutputBoundary presenter;

    public MakeHumanMoveInteractor(MakeHumanMoveOutputBoundary presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute(MakeHumanMoveInputData inputData) {
        try {
            presenter.prepareSuccessView(new MakeHumanMoveOutputData(
                inputData.state().applyMove(inputData.position())));
        } catch (InvalidMoveException e) {
            presenter.prepareFailView(e.getMessage());
        }
    }
}