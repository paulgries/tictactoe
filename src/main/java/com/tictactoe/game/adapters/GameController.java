package com.tictactoe.game.adapters;

import com.tictactoe.game.ai.AiStrategy;
import com.tictactoe.game.ai.AiStrategyFactory;
import com.tictactoe.game.NewGameRequest;
import com.tictactoe.game.domain.GameMode;
import com.tictactoe.game.domain.GameState;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;
import com.tictactoe.game.use_case.MakeHumanMoveInputBoundary;
import com.tictactoe.game.use_case.MakeHumanMoveInputData;
import com.tictactoe.game.use_case.MakeHumanMoveInteractor;
import com.tictactoe.game.use_case.MakeHumanMoveOutputBoundary;
import com.tictactoe.game.use_case.MakeHumanMoveOutputData;
import com.tictactoe.game.use_case.RequestAiMoveInputBoundary;
import com.tictactoe.game.use_case.RequestAiMoveInputData;
import com.tictactoe.game.use_case.RequestAiMoveInteractor;
import com.tictactoe.game.use_case.RequestAiMoveOutputBoundary;
import com.tictactoe.game.use_case.RequestAiMoveOutputData;
import com.tictactoe.game.use_case.StartNewGameInputBoundary;
import com.tictactoe.game.use_case.StartNewGameInputData;
import com.tictactoe.game.use_case.StartNewGameInteractor;
import com.tictactoe.game.use_case.StartNewGameOutputBoundary;
import com.tictactoe.game.use_case.StartNewGameOutputData;
import java.util.Optional;

public final class GameController implements
        StartNewGameOutputBoundary, MakeHumanMoveOutputBoundary, RequestAiMoveOutputBoundary {

    private static final Mark AI_MARK = GameState.STARTING_MARK.other();

    private final GameView view;
    private final StartNewGameInputBoundary startNewGameUseCase;
    private final MakeHumanMoveInputBoundary makeHumanMoveUseCase;
    private final RequestAiMoveInputBoundary requestAiMoveUseCase;
    private final AiStrategyFactory aiStrategyFactory;
    private final UiScheduler uiScheduler;

    private GameState currentState;
    private NewGameRequest lastRequest;
    private GameState pendingAiBase;
    private Optional<AiStrategy> aiStrategy = Optional.empty();

    public GameController(
            GameView view,
            AiStrategyFactory aiStrategyFactory,
            UiScheduler uiScheduler) {
        this.view = view;
        this.startNewGameUseCase = new StartNewGameInteractor(this);
        this.makeHumanMoveUseCase = new MakeHumanMoveInteractor(this);
        this.requestAiMoveUseCase = new RequestAiMoveInteractor(this);
        this.aiStrategyFactory = aiStrategyFactory;
        this.uiScheduler = uiScheduler;
    }

    @Override
    public void prepareSuccessView(StartNewGameOutputData outputData) {
        currentState = outputData.gameState();
        render();
    }

    @Override
    public void prepareSuccessView(MakeHumanMoveOutputData outputData) {
        currentState = outputData.updatedState();
        render();

        if (isAiTurn()) {
            requestAiMoveAsync();
        }
    }

    @Override
    public void prepareSuccessView(RequestAiMoveOutputData outputData) {
        uiScheduler.runOnUiThread(() -> {
            // discard a stale result if the session moved on (e.g. a restart) while
            // this AI move was being computed in the background
            if (currentState.equals(pendingAiBase)) {
                currentState = outputData.updatedState();
                render();
            }
        });
    }

    @Override
    public void prepareFailView(String error) {
        view.displayError(error);
    }

    public void onStartGameRequested(NewGameRequest request) {
        this.lastRequest = request;
        this.aiStrategy = request.mode() == GameMode.HUMAN_VS_AI
            ? Optional.of(aiStrategyFactory.create(request.aiDifficulty().orElseThrow()))
            : Optional.empty();
        startNewGameUseCase.execute(new StartNewGameInputData(request));
    }

    public void onCellClicked(int row, int column) {
        if (currentState.isGameOver() || isAiTurn()) {
            return;
        }

        makeHumanMoveUseCase.execute(new MakeHumanMoveInputData(currentState, new Position(row, column)));
    }

    public void onRestartRequested() {
        startNewGameUseCase.execute(new StartNewGameInputData(lastRequest));
    }

    private boolean isAiTurn() {
        return !currentState.isGameOver()
            && lastRequest.mode() == GameMode.HUMAN_VS_AI
            && currentState.currentTurn() == AI_MARK;
    }

    private void requestAiMoveAsync() {
        pendingAiBase = currentState;
        AiStrategy strategy = aiStrategy.orElseThrow();
        uiScheduler.runInBackground(() -> {
            requestAiMoveUseCase.execute(new RequestAiMoveInputData(pendingAiBase, strategy));
        });
    }

    private void render() {
        view.displayBoard(GameViewModelMapper.toBoardViewModel(currentState));
        view.displayStatus(GameViewModelMapper.toStatusViewModel(currentState));
    }
}