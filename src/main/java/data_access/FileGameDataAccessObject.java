package data_access;

import game.domain.AiDifficulty;
import game.domain.Board;
import game.domain.GameConfig;
import game.domain.GameMode;
import game.domain.GameState;
import game.domain.Mark;
import game.domain.Position;
import game.domain.SavedGame;
import game.domain.WinChecker;
import game.load_game.use_case.LoadGameDataAccess;
import game.save_game.use_case.SaveGameDataAccess;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The file-backed implementation of the save and load persistence
 * boundaries. Owns the text format (key=value lines) and the derivation of
 * data that is not persisted: the current turn follows from the move count
 * (X always moves first) and the status is recomputed with {@link
 * WinChecker}. A missing file means "no saved game"; anything else that does
 * not parse is a corrupt save.
 */
public final class FileGameDataAccessObject implements SaveGameDataAccess, LoadGameDataAccess {

    private static final String EMPTY_CELL = "_";

    private final Path file;

    public FileGameDataAccessObject(Path file) {
        this.file = file;
    }

    @Override
    public void save(SavedGame savedGame) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        final GameState state = savedGame.gameState();
        final GameConfig config = state.config();
        StringBuilder board = new StringBuilder();
        for (int row = 0; row < config.boardSize(); row++) {
            if (row > 0) {
                board.append('/');
            }
            for (int column = 0; column < config.boardSize(); column++) {
                board.append(markToChar(state.board().get(new Position(row, column))));
            }
        }
        StringBuilder lines = new StringBuilder();
        lines.append("boardSize=").append(config.boardSize()).append('\n');
        lines.append("winLength=").append(config.winLength()).append('\n');
        lines.append("mode=").append(savedGame.mode()).append('\n');
        if (savedGame.difficulty().isPresent()) {
            lines.append("difficulty=").append(savedGame.difficulty().get()).append('\n');
        }
        lines.append("board=").append(board);
        Files.write(file, lines.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Optional<SavedGame> load() throws IOException {
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        Map<String, String> fields = readFields();
        try {
            return Optional.of(parseSavedGame(fields));
        } catch (RuntimeException e) {
            throw new IOException("corrupt saved game: " + e.getMessage(), e);
        }
    }

    private Map<String, String> readFields() throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        Map<String, String> fields = new LinkedHashMap<>();
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 0) {
                throw new IOException("corrupt saved game: malformed line '" + line + "'");
            }
            String key = line.substring(0, separator);
            if (fields.containsKey(key)) {
                throw new IOException("corrupt saved game: duplicate key '" + key + "'");
            }
            fields.put(key, line.substring(separator + 1));
        }
        return fields;
    }

    private SavedGame parseSavedGame(Map<String, String> fields) {
        String boardSizeValue = requireField(fields, "boardSize");
        String winLengthValue = requireField(fields, "winLength");
        String modeValue = requireField(fields, "mode");
        String boardValue = requireField(fields, "board");
        GameConfig config =
            new GameConfig(parseInt(boardSizeValue, "boardSize"), parseInt(winLengthValue, "winLength"));
        GameMode mode = parseEnum(GameMode.class, modeValue, "mode");
        Optional<AiDifficulty> difficulty;
        if (fields.containsKey("difficulty")) {
            difficulty = Optional.of(parseEnum(AiDifficulty.class, fields.get("difficulty"), "difficulty"));
        } else {
            difficulty = Optional.empty();
        }
        if (mode == GameMode.HUMAN_VS_AI && difficulty.isEmpty()) {
            throw new IllegalStateException("human-vs-AI game without a difficulty");
        }
        if (mode == GameMode.TWO_PLAYER && difficulty.isPresent()) {
            throw new IllegalStateException("two-player game with a difficulty");
        }
        Board board = parseBoard(boardValue, config.boardSize());
        GameState state = rebuildState(board, config);
        return new SavedGame(state, mode, difficulty);
    }

    private GameState rebuildState(Board board, GameConfig config) {
        int moveCount = 0;
        for (int row = 0; row < config.boardSize(); row++) {
            for (int column = 0; column < config.boardSize(); column++) {
                if (board.get(new Position(row, column)).isPresent()) {
                    moveCount++;
                }
            }
        }
        Mark currentTurn = moveCount % 2 == 0 ? Mark.X : Mark.O;
        return new GameState(board, currentTurn, config, WinChecker.evaluate(board, config));
    }

    private Board parseBoard(String boardValue, int boardSize) {
        String[] rows = boardValue.split("/", -1);
        if (rows.length != boardSize) {
            throw new IllegalArgumentException(
                "board has " + rows.length + " rows, expected " + boardSize);
        }
        Board board = new Board(boardSize);
        for (int row = 0; row < boardSize; row++) {
            if (rows[row].length() != boardSize) {
                throw new IllegalArgumentException(
                    "row " + row + " has " + rows[row].length() + " cells, expected " + boardSize);
            }
            for (int column = 0; column < boardSize; column++) {
                char cell = rows[row].charAt(column);
                if (cell == EMPTY_CELL.charAt(0)) {
                    continue;
                }
                Mark mark = switch (cell) {
                    case 'X' -> Mark.X;
                    case 'O' -> Mark.O;
                    default -> throw new IllegalArgumentException(
                        "unknown cell symbol '" + cell + "'");
                };
                board = board.placeMark(new Position(row, column), mark);
            }
        }
        return board;
    }

    private static String requireField(Map<String, String> fields, String key) {
        if (!fields.containsKey(key)) {
            throw new IllegalArgumentException("missing field '" + key + "'");
        }
        return fields.get(key);
    }

    private static int parseInt(String value, String key) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "field '" + key + "' is not a number: '" + value + "'", e);
        }
    }

    private static <T extends Enum<T>> T parseEnum(
            Class<T> type, String value, String key) {
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "field '" + key + "' has unknown value '" + value + "'", e);
        }
    }

    private static char markToChar(Optional<Mark> mark) {
        if (mark.isEmpty()) {
            return EMPTY_CELL.charAt(0);
        }
        return mark.get() == Mark.X ? 'X' : 'O';
    }
}