package data_access;

import static org.assertj.core.api.Assertions.assertThat;

import game.domain.AiDifficulty;
import game.domain.GameMode;
import game.domain.GameState;
import game.testutil.GameFixtures;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryGameSessionTest {

    @Test
    void setCurrentGame_StoresStateAndSettings() {
        InMemoryGameSession session = new InMemoryGameSession();
        GameState state = GameFixtures.wonByX();

        session.setCurrentGame(state, GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM));

        assertThat(session.getCurrentGameState()).isEqualTo(state);
        assertThat(session.getMode()).isEqualTo(GameMode.HUMAN_VS_AI);
        assertThat(session.getAiDifficulty()).contains(AiDifficulty.MEDIUM);
    }

    @Test
    void setCurrentGameState_UpdatesOnlyTheState() {
        InMemoryGameSession session = new InMemoryGameSession();
        session.setCurrentGame(
                GameFixtures.newGame3x3(),
                GameMode.TWO_PLAYER,
                Optional.empty());
        GameState updated = GameFixtures.wonByX();

        session.setCurrentGameState(updated);

        assertThat(session.getCurrentGameState()).isEqualTo(updated);
        assertThat(session.getMode()).isEqualTo(GameMode.TWO_PLAYER);
        assertThat(session.getAiDifficulty()).isEmpty();
    }
}