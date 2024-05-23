package tests.engine.board;

import engine.board.Board;
import engine.board.BoardUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    @Test
    public void initialBoard(){
        final Board board = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", 4, true, true, false);
        assertEquals(BoardUtil.getAllianceMoves(true, board).size(), 20);
        assertEquals(BoardUtil.getAllianceMoves(false, board).size(), 20);
        assertFalse(BoardUtil.isCheck(true, board));
        assertFalse(BoardUtil.isCheckMate(true, board));
        assertTrue(board.turn);
        assertFalse(BoardUtil.isCheck(false, board));
        assertFalse(BoardUtil.isCheckMate(false, board));
    }

    @Test
    public void checkmateBoard(){
        final Board board = new Board("K5Q1/PP6/8/8/8/2R2N2/3R3n/1R4k1 w - - 0 1", 4, true, true, false);
        assertTrue(BoardUtil.isCheck(false, board));
        assertFalse(BoardUtil.isCheck(true, board));
        assertTrue(BoardUtil.isCheckMate(false, board));
        assertFalse(BoardUtil.isCheckMate(true, board));
        assertFalse(BoardUtil.canQueenCastle(true, board));
        assertFalse(BoardUtil.canKingCastle(true, board));
        assertFalse(BoardUtil.canKingCastle(false, board));
        assertFalse(BoardUtil.canQueenCastle(false, board));
    }

    @Test
    public void castleBoard(){
        final Board board = new Board("r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R", 4, true, true, false);
        assertFalse(BoardUtil.isCheck(false, board));
        assertFalse(BoardUtil.isCheck(true, board));
        assertFalse(BoardUtil.isCheckMate(false, board));
        assertFalse(BoardUtil.isCheckMate(true, board));
        assertTrue(BoardUtil.canQueenCastle(true, board));
        assertTrue(BoardUtil.canKingCastle(true, board));
        assertTrue(BoardUtil.canKingCastle(false, board));
        assertTrue(BoardUtil.canQueenCastle(false, board));
    }
}