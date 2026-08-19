package game.load_game.use_case;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import data_access.InMemoryGameSession;
import game.domain.GameMode;
import game.domain.SavedGame;
import game.testutil.GameFixtures;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoadGameInteractorTest {

    private LoadGameOutputBoundary presenter;
    private LoadGameDataAccess loadGameDataAccess;
    private InMemoryGameSession session;
    private LoadGameInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = mock(LoadGameOutputBoundary.class);
        loadGameDataAccess = mock(LoadGameDataAccess.class);
        session = new InMemoryGameSession();
        interactor = new LoadGameInteractor(presenter, loadGameDataAccess, session);
    }

    @Test
    void execute_SavedGameExists_PresentsItAndRestoresSession() throws IOException {
        SavedGame savedGame = new SavedGame(GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty());
        when(loadGameDataAccess.load()).thenReturn(Optional.of(savedGame));

        interactor.execute(new LoadGameInputData());

        verify(presenter).prepareSuccessView(new LoadGameOutputData(savedGame));
        verify(presenter, never()).prepareFailView(org.mockito.ArgumentMatchers.anyString());
        assertThat(session.getCurrentGameState()).isEqualTo(savedGame.gameState());
        assertThat(session.getMode()).isEqualTo(savedGame.mode());
        assertThat(session.getAiDifficulty()).isEqualTo(savedGame.difficulty());
    }

    @Test
    void execute_NoSavedGame_PresentsFailure() throws IOException {
        when(loadGameDataAccess.load()).thenReturn(Optional.empty());

        interactor.execute(new LoadGameInputData());

        verify(presenter).prepareFailView("no saved game found");
        verify(presenter, never()).prepareSuccessView(org.mockito.ArgumentMatchers.any());
        assertThat(session.getCurrentGameState()).isNull();
    }

    @Test
    void execute_DataAccessThrows_PresentsFailure() throws IOException {
        when(loadGameDataAccess.load()).thenThrow(new IOException("boom"));

        interactor.execute(new LoadGameInputData());

        verify(presenter).prepareFailView("could not load the saved game: boom");
        verify(presenter, never()).prepareSuccessView(org.mockito.ArgumentMatchers.any());
    }
}