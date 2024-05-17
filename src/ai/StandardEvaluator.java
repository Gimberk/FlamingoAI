package ai;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Piece;

public class StandardEvaluator implements Evaluator {
    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, true, depth) - scorePlayer(board, false, depth);
    }

    private int scorePlayer(final Board board, final boolean player, final int depth) {
        return pieceValue(board, player);
    }

    private static int pieceValue(final Board board, final boolean player) {
        int pieceVal = 0;
        for (final Piece piece : BoardUtil.getPieces(player, board)){
            pieceVal += piece.getValue();
        }
        return pieceVal;
    }
}
