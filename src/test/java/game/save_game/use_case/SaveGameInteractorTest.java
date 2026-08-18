package game.save_game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import game.domain.GameMode;
import game.domain.SavedGame;
import game.testutil.GameFixtures;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SaveGameInteractorTest {

    private SaveGameOutputBoundary presenter;
    private SaveGameDataAccess saveGameDataAccess;
    private SaveGameInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = mock(SaveGameOutputBoundary.class);
        saveGameDataAccess = mock(SaveGameDataAccess.class);
        interactor = new SaveGameInteractor(presenter, saveGameDataAccess);
    }

    @Test
    void execute_SessionSnapshot_PersistsAndPresentsSuccess() throws IOException {
        SavedGame expected = new SavedGame(
                GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty());

        interactor.execute(new SaveGameInputData(
                expected.gameState(), expected.mode(), expected.difficulty()));

        ArgumentCaptor<SavedGame> captor = ArgumentCaptor.forClass(SavedGame.class);
        verify(saveGameDataAccess).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(expected);
        verify(presenter).prepareSuccessView(new SaveGameOutputData());
        verify(presenter, never()).prepareFailView(anyString());
    }

    @Test
    void execute_DataAccessThrows_PresentsFailure() throws IOException {
        doThrow(new IOException("boom")).when(saveGameDataAccess).save(any());

        interactor.execute(new SaveGameInputData(
                GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty()));

        verify(presenter).prepareFailView("could not save the game: boom");
        verify(presenter, never()).prepareSuccessView(any());
    }
}