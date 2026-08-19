package game.save_game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import data_access.InMemoryGameSession;
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
    private InMemoryGameSession session;
    private SaveGameInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = mock(SaveGameOutputBoundary.class);
        saveGameDataAccess = mock(SaveGameDataAccess.class);
        session = new InMemoryGameSession();
        interactor = new SaveGameInteractor(presenter, saveGameDataAccess, session);
    }

    @Test
    void execute_GameInProgress_PersistsSessionAndPresentsSuccess() throws IOException {
        SavedGame expected = new SavedGame(
                GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty());
        session.setCurrentGame(expected.gameState(), expected.mode(), expected.difficulty());

        interactor.execute(new SaveGameInputData());

        ArgumentCaptor<SavedGame> captor = ArgumentCaptor.forClass(SavedGame.class);
        verify(saveGameDataAccess).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(expected);
        verify(presenter).prepareSuccessView(new SaveGameOutputData());
        verify(presenter, never()).prepareFailView(anyString());
    }

    @Test
    void execute_NoGameInProgress_PresentsFailure() {
        interactor.execute(new SaveGameInputData());

        verify(presenter).prepareFailView("no game in progress to save");
        verify(presenter, never()).prepareSuccessView(any());
        verifyNoInteractions(saveGameDataAccess);
    }

    @Test
    void execute_DataAccessThrows_PresentsFailure() throws IOException {
        session.setCurrentGame(GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty());
        doThrow(new IOException("boom")).when(saveGameDataAccess).save(any());

        interactor.execute(new SaveGameInputData());

        verify(presenter).prepareFailView("could not save the game: boom");
        verify(presenter, never()).prepareSuccessView(any());
    }
}