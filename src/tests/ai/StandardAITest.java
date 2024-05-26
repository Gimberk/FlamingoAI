package tests.ai;

import ai.iterations.AlphaBeta;
import ai.iterations.Minimax;
import ai.Strategy;
import ai.iterations.Quiescence;
import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StandardAITest {
    @Test
    public void test4MoveMate(){
        final Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", 21, true, true, false);
        final Move m1 = new Move(53, 45, board.tiles[53].piece, null);
        final Move m2 = new Move(12, 28, board.tiles[12].piece, null);
        final Move m3 = new Move(54, 38, board.tiles[54].piece, null);

        board.makeMove(m1, false);
        board.makeMove(m2, false);
        board.makeMove(m3, false);

        final int depth = 4;

        assertFalse(BoardUtil.isCheck(true, board));
        assertFalse(BoardUtil.isCheck(false, board));
        assertFalse(BoardUtil.isCheckMate(false, board));
        assertFalse(BoardUtil.isCheckMate(true, board));
//
        final Strategy minMax = new Quiescence(depth);
        Move minMaxMove = minMax.execute(board);

        final Move bestMove = new Move(3, 39, board.tiles[3].piece, null);
        board.makeMove(minMaxMove, false);
        assertTrue(bestMove.equals(minMaxMove));
        assertTrue(BoardUtil.isCheck(true, board));
        assertTrue(BoardUtil.isCheckMate(true, board));
    }
}
