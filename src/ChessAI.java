import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Piece;
import gui.GameFrame;

public class ChessAI {
    //public final static String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public final static String startingFen = "8/8/6q/pk5P/1p3rP1/2p2PK1/8/8 w - - 0 1";

    public static void main(String[] args) {
        //new GameFrame(startingFen);

        Board board = new Board(startingFen);
        board.showBoard();
        for (Piece piece : board.pieces){
            piece.moved = true;
        }

        board.makeMove(BoardUtil.search(board, 1), false);
        board.showBoard();

        board.makeMove(BoardUtil.search(board, 1), false);
        board.showBoard();
    }
}