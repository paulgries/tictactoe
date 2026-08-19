package persistence.load_game;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import persistence.load_game.use_case.LoadGameInputBoundary;
import persistence.load_game.use_case.LoadGameInputData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoadGameControllerTest {

    private LoadGameInputBoundary loadGameUseCase;
    private LoadGameController controller;

    @BeforeEach
    void setUp() {
        loadGameUseCase = mock(LoadGameInputBoundary.class);
        controller = new LoadGameController(loadGameUseCase);
    }

    @Test
    void execute_Always_DelegatesWithEmptyInput() {
        controller.execute();

        verify(loadGameUseCase).execute(new LoadGameInputData());
    }
}