package ai;

import engine.board.Board;
import engine.piece.Move;

public interface Strategy {
    Move execute(final Board board, final int depth);
}
