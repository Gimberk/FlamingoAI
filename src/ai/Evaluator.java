package ai;

import engine.board.Board;

public interface Evaluator {
    public int evaluate(final Board board, final int depth);
}
