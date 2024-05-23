package tests.ai;

import ai.evaluation.Evaluator;
import engine.board.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {
    @Test
    public void test(){
        Board board = new Board("r1bq1rk1/ppp2ppp/2n2n2/2b1p3/4P3/2P2N2/PP1P1PPP/RNBQ1RK1 b - - 0 9",
                4, true, true, false);
//        board.showBoard();
//        System.out.println("Overall Evaluation:");
//        int eval1 = Evaluator.evaluatePosition(board);
//        System.out.println("Total: " + eval1 + "\n");
//        assertTrue(eval1 >= 0);
//
        board = new Board("r1bqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                4, true, true, false);
        board.showBoard();
        System.out.println("Overall Evaluation:");
        int eval2 = Evaluator.evaluatePosition(board);
        System.out.println("Total: " + eval2 + "\n");
        assertTrue(eval2 > 0);
//
//        board = new Board("r1bq1rk1/pppp1ppp/2n2n2/2b1p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 0 5",
//                4, true, true, false);
//        board.showBoard();
//        System.out.println("Overall Evaluation:");
//        int eval3 = Evaluator.evaluatePosition(board);
//        System.out.println("Total: " + eval3 + "\n");
//        assertTrue(eval3 < 0);
//
//        board = new Board("r4rk1/1pp1qppp/p1npbn2/4p3/4P3/1BPP1N2/PP3PPP/R1BQR1K1 w - - 0 13",
//                4, true, true, false);
//        board.showBoard();
//        System.out.println("Overall Evaluation:");
//        int eval4 = Evaluator.evaluatePosition(board);
//        System.out.println("Total: " + eval4 + "\n");
//        assertFalse(eval4 > 0); // essentially equal position
//
//        board = new Board("rnb1kbnr/pppp1ppp/8/4p3/6Pq/5P2/PPPPP2P/RNBQKBNR w KQkq - 0 1",
//                4, true, true, false);
//        board.showBoard();
//        System.out.println("Overall Evaluation:");
//        int eval5 = Evaluator.evaluatePosition(board);
//        System.out.println("Total: " + eval5 + "\n");
//        assertTrue(eval5 < 0); // essentially equal position
    }
}
