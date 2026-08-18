package com.tictactoe.game.ai;

import com.tictactoe.game.domain.Board;
import com.tictactoe.game.domain.GameConfig;
import com.tictactoe.game.domain.Mark;
import com.tictactoe.game.domain.Position;

public interface AiStrategy {

    Position selectMove(Board board, GameConfig config, Mark aiMark);
}
