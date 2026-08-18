package game.ai;

import game.domain.Board;
import game.domain.GameConfig;
import game.domain.Mark;
import game.domain.Position;

public interface AiStrategy {

    Position selectMove(Board board, GameConfig config, Mark aiMark);
}
