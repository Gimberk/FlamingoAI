package engine;

import engine.board.Board;
import engine.board.BoardUtil;

public class ChessAI {
    //public final static String startingFen = "rnbqkbnr/3p1Qpp/1pp5/p3p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 1";
    public final static String startingFen = "8/8/3K/8/2q1q";

    public static void main(String[] args) {
        Board board = new Board(startingFen);
        board.showBoardWithMoves(board.pieces.getFirst());
        //board.showBoardWithMoves(board.pieces.getFirst());
        System.out.println(BoardUtil.isCheckMate(true, board));
    }
}