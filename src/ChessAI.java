import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Piece;
import gui.GameFrame;

public class ChessAI {
    public final static String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    //public final static String startingFen = "8/8/6q/pk5P/1p3rP1/2p2PK1/8/8 w - - 0 1";

    public static void main(String[] args) {
        GameFrame frame = new GameFrame(startingFen, 3, true, false);
        System.out.println(BoardUtil.getAllianceMoves(true, frame.board).size());
//        for (Piece piece : frame.board.pieces){
//            piece.moved = true;
//            piece.justMoved = false;
//        }

        //

//        BoardUtil.turn = !BoardUtil.turn;
//        BoardUtil.switchTurn(frame.board);
    }
}