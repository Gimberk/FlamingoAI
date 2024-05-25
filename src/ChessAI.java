import ai.evaluation.Evaluator;
import engine.board.Board;
import engine.board.BoardUtil;
import engine.board.PGNWriter;
import gui.GameFrame;

import java.io.IOException;

public class ChessAI {
    //public final static String startingFen = "rn2k1n1/pppp1ppp/8/bqp2r2/1P1bP3/2P5/P6P/4K3 w q - 0 1";
    //public final static String startingFen = "8/8/6q/pk5P/1p3rP1/2p2PK/8/8 w - - 0 1";
    //public final static String startingFen = "1k6/ppp2P2/8/8/8/8/1p3PPP/6K1 w - - 0 1";
    public final static String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    //public final static String startingFen = "r3k2r/pppppppp/8/8/8/8/PPPPPPPP/R3K2R";

    public static void main(String[] args) {
        // Move Ordering
        // LMR
        // Iterative Deepening
        // Piece-Square Tables (and other eval function stuff)
        // Main Screen
        // End Game Screen

        GameFrame frame = new GameFrame(startingFen, 4, true, false);
        frame.board.turn = true;
    }
}