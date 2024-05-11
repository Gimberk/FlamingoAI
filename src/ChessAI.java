import engine.board.Board;
import engine.board.BoardUtil;

public class ChessAI {
    public final static String startingFen = "rnbqkbnr/ppPPPppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    //public final static String startingFen = "8/7q/3K3q/8/2q1q";

    public static void main(String[] args) {
        Board board = new Board(startingFen);
        board.showBoardWithMoves(board.pieces.get(1));
    }
}