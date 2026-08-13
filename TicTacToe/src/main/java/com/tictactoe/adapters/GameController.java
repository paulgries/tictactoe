package com.tictactoe.adapters;

import com.tictactoe.application.AiStrategyFactory;
import com.tictactoe.application.NewGameRequest;
import com.tictactoe.application.usecase.MakeHumanMoveUseCase;
import com.tictactoe.application.usecase.RequestAiMoveUseCase;
import com.tictactoe.application.usecase.StartNewGameUseCase;
import com.tictactoe.domain.GameMode;
import com.tictactoe.domain.GameState;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;
import com.tictactoe.domain.ai.AiStrategy;
import com.tictactoe.domain.exception.InvalidMoveException;
import java.util.Optional;

public final class GameController {

    private static final Mark AI_MARK = GameState.STARTING_MARK.other();

    private final GameView view;
    private final StartNewGameUseCase startNewGameUseCase;
    private final MakeHumanMoveUseCase makeHumanMoveUseCase;
    private final RequestAiMoveUseCase requestAiMoveUseCase;
    private final AiStrategyFactory aiStrategyFactory;
    private final UiScheduler uiScheduler;

    private GameState currentState;
    private NewGameRequest lastRequest;
    private Optional<AiStrategy> aiStrategy = Optional.empty();

    public GameController(
            GameView view,
            StartNewGameUseCase startNewGameUseCase,
            MakeHumanMoveUseCase makeHumanMoveUseCase,
            RequestAiMoveUseCase requestAiMoveUseCase,
            AiStrategyFactory aiStrategyFactory,
            UiScheduler uiScheduler) {
        this.view = view;
        this.startNewGameUseCase = startNewGameUseCase;
        this.makeHumanMoveUseCase = makeHumanMoveUseCase;
        this.requestAiMoveUseCase = requestAiMoveUseCase;
        this.aiStrategyFactory = aiStrategyFactory;
        this.uiScheduler = uiScheduler;
    }

    public void onStartGameRequested(NewGameRequest request) {
        this.lastRequest = request;
        this.currentState = startNewGameUseCase.execute(request);
        this.aiStrategy = request.mode() == GameMode.HUMAN_VS_AI
            ? Optional.of(aiStrategyFactory.create(request.aiDifficulty().orElseThrow()))
            : Optional.empty();
        render();
    }

    public void onCellClicked(int row, int column) {
        if (currentState.isGameOver() || isAiTurn()) {
            return;
        }

        try {
            currentState = makeHumanMoveUseCase.execute(currentState, new Position(row, column));
        } catch (InvalidMoveException e) {
            view.displayError(e.getMessage());
            return;
        }
        render();

        if (isAiTurn()) {
            requestAiMoveAsync();
        }
    }

    public void onRestartRequested() {
        currentState = startNewGameUseCase.execute(lastRequest);
        render();
    }

    private boolean isAiTurn() {
        return !currentState.isGameOver()
            && lastRequest.mode() == GameMode.HUMAN_VS_AI
            && currentState.currentTurn() == AI_MARK;
    }

    private void requestAiMoveAsync() {
        GameState stateBeforeAiMove = currentState;
        AiStrategy strategy = aiStrategy.orElseThrow();
        uiScheduler.runInBackground(() -> {
            GameState updated = requestAiMoveUseCase.execute(stateBeforeAiMove, strategy);
            uiScheduler.runOnUiThread(() -> {
                // discard a stale result if the session moved on (e.g. a restart) while
                // this AI move was being computed in the background
                if (currentState.equals(stateBeforeAiMove)) {
                    currentState = updated;
                    render();
                }
            });
        });
    }

    private void render() {
        view.displayBoard(GameViewModelMapper.toBoardViewModel(currentState));
        view.displayStatus(GameViewModelMapper.toStatusViewModel(currentState));
    }
}
