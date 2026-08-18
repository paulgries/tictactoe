package com.tictactoe.domain.ai;

import com.tictactoe.domain.Board;
import com.tictactoe.domain.GameConfig;
import com.tictactoe.domain.Mark;
import com.tictactoe.domain.Position;

public interface AiStrategy {

    Position selectMove(Board board, GameConfig config, Mark aiMark);
}
