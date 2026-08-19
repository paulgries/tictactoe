package persistence.save_game;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import persistence.save_game.use_case.SaveGameInputBoundary;
import persistence.save_game.use_case.SaveGameInputData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaveGameControllerTest {

    private SaveGameInputBoundary saveGameUseCase;
    private SaveGameController controller;

    @BeforeEach
    void setUp() {
        saveGameUseCase = mock(SaveGameInputBoundary.class);
        controller = new SaveGameController(saveGameUseCase);
    }

    @Test
    void execute_DelegatesToUseCase() {
        controller.execute();

        verify(saveGameUseCase).execute(new SaveGameInputData());
    }
}