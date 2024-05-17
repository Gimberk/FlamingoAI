package tests.engine.board;

import engine.board.Board;
import engine.board.BoardUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    public void initialBoard(){
        final Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", 4, true, true);
        assertEquals(BoardUtil.getAllianceMoves(true, board).size(), 20);
        assertEquals(BoardUtil.getAllianceMoves(false, board).size(), 20);
        assertFalse(BoardUtil.isCheck(true, board));
        assertFalse(BoardUtil.isCheckMate(true, board));
        assertTrue(BoardUtil.turn);
        assertFalse(BoardUtil.isCheck(false, board));
        assertFalse(BoardUtil.isCheckMate(false, board));
    }

    @Test
    public void checkmateBoard(){
        final Board board = new Board("K5Q1/PP6/8/8/8/2R2N2/3R3n/1R4k1 w - - 0 1", 4, true, true);
        assertTrue(BoardUtil.isCheck(false, board));
        assertFalse(BoardUtil.isCheck(true, board));
        assertTrue(BoardUtil.isCheckMate(false, board));
        assertFalse(BoardUtil.isCheckMate(true, board));
    }
}