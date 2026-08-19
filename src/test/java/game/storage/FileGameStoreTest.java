package game.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import game.domain.AiDifficulty;
import game.domain.Draw;
import game.domain.GameMode;
import game.domain.GameState;
import game.domain.InProgress;
import game.domain.SavedGame;
import game.domain.Win;
import game.testutil.GameFixtures;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileGameStoreTest {

    @TempDir
    Path tempDir;

    private Path file;
    private FileGameStore store;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("saved-game.txt");
        store = new FileGameStore(file);
    }

    @Test
    void load_SavedGameRoundTrips_ReturnsEqualSavedGame() throws IOException {
        SavedGame savedGame = new SavedGame(
                GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty());

        store.save(savedGame);

        assertThat(store.load()).contains(savedGame);
    }

    @Test
    void load_AiGameWithDifficultyRoundTrips_ReturnsEqualSavedGame() throws IOException {
        SavedGame savedGame = new SavedGame(
                GameState.newGame(new game.domain.GameConfig(3, 3))
                    .applyMove(new game.domain.Position(0, 0)),
                GameMode.HUMAN_VS_AI, Optional.of(AiDifficulty.MEDIUM));

        store.save(savedGame);

        assertThat(store.load()).contains(savedGame);
    }

    @Test
    void save_AiGame_CreatesParentDirectories() throws IOException {
        Path nested = tempDir.resolve("nested").resolve("dir").resolve("saved-game.txt");
        FileGameStore nestedStore = new FileGameStore(nested);

        nestedStore.save(new SavedGame(
                GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty()));

        assertThat(Files.exists(nested)).isTrue();
    }

    @Test
    void load_NoFile_ReturnsEmpty() throws IOException {
        assertThat(store.load()).isEmpty();
    }

    @Test
    void load_HandWrittenFile_DerivesTurnAndStatus() throws IOException {
        Files.writeString(file, String.join("\n",
                "boardSize=3",
                "winLength=3",
                "mode=TWO_PLAYER",
                "board=X__/_O_/___"));

        SavedGame savedGame = store.load().orElseThrow();

        assertThat(savedGame.gameState().currentTurn()).isEqualTo(game.domain.Mark.X);
        assertThat(savedGame.gameState().status()).isInstanceOf(InProgress.class);
        assertThat(savedGame.gameState().board().get(new game.domain.Position(0, 0)))
            .contains(game.domain.Mark.X);
    }

    @Test
    void load_WonGame_RecomputesWinStatus() throws IOException {
        store.save(new SavedGame(GameFixtures.wonByX(), GameMode.TWO_PLAYER, Optional.empty()));

        SavedGame savedGame = store.load().orElseThrow();

        assertThat(savedGame.gameState().status()).isInstanceOf(Win.class);
        assertThat(savedGame.gameState().isGameOver()).isTrue();
    }

    @Test
    void load_DrawnGame_RecomputesDrawStatus() throws IOException {
        store.save(new SavedGame(GameFixtures.drawn(), GameMode.TWO_PLAYER, Optional.empty()));

        SavedGame savedGame = store.load().orElseThrow();

        assertThat(savedGame.gameState().status()).isInstanceOf(Draw.class);
    }

    @Test
    void load_MalformedLine_ThrowsCorruptSave() throws IOException {
        Files.writeString(file, "this is not a key=value line");

        assertThatThrownBy(() -> store.load())
            .isInstanceOf(IOException.class)
            .hasMessageContaining("corrupt saved game");
    }

    @Test
    void load_MissingField_ThrowsCorruptSave() throws IOException {
        Files.writeString(file, String.join("\n",
                "boardSize=3",
                "winLength=3",
                "mode=TWO_PLAYER"));

        assertThatThrownBy(() -> store.load())
            .isInstanceOf(IOException.class)
            .hasMessageContaining("missing field 'board'");
    }

    @Test
    void load_UnknownBoardSymbol_ThrowsCorruptSave() throws IOException {
        Files.writeString(file, String.join("\n",
                "boardSize=3",
                "winLength=3",
                "mode=TWO_PLAYER",
                "board=XZ_/___/___"));

        assertThatThrownBy(() -> store.load())
            .isInstanceOf(IOException.class)
            .hasMessageContaining("unknown cell symbol 'Z'");
    }

    @Test
    void load_AiGameWithoutDifficulty_ThrowsCorruptSave() throws IOException {
        Files.writeString(file, String.join("\n",
                "boardSize=3",
                "winLength=3",
                "mode=HUMAN_VS_AI",
                "board=___/___/___"));

        assertThatThrownBy(() -> store.load())
            .isInstanceOf(IOException.class)
            .hasMessageContaining("without a difficulty");
    }

    @Test
    void load_TwoPlayerGameWithDifficulty_ThrowsCorruptSave() throws IOException {
        Files.writeString(file, String.join("\n",
                "boardSize=3",
                "winLength=3",
                "mode=TWO_PLAYER",
                "difficulty=EASY",
                "board=___/___/___"));

        assertThatThrownBy(() -> store.load())
            .isInstanceOf(IOException.class)
            .hasMessageContaining("with a difficulty");
    }

    @Test
    void load_InvalidBoardSize_ThrowsCorruptSave() throws IOException {
        Files.writeString(file, String.join("\n",
                "boardSize=1",
                "winLength=1",
                "mode=TWO_PLAYER",
                "board=_"));

        assertThatThrownBy(() -> store.load())
            .isInstanceOf(IOException.class)
            .hasMessageContaining("boardSize must be at least 2");
    }
}