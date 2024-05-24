package ai.iterations;

import ai.MoveOrder;
import ai.Strategy;
import ai.evaluation.Evaluator;
import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Move;

import java.util.List;

public class AlphaBeta implements Strategy {
    private final int maxDepth;
    private final MoveOrder moveOrder;

    public AlphaBeta(final int maxDepth){
        moveOrder = new MoveOrder();
        this.maxDepth = maxDepth;
    }

    @Override
    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis();
        final List<Move> moves = BoardUtil.getAllianceMoves(board.turn, board);

        final boolean alliance = board.turn;

        Move best = moves.getFirst();
        best.evaluation = alliance ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        System.out.println("Starting Search with Depth: "  + maxDepth);
        for (final Move move : moves){
            board.makeMove(move, true);
            move.evaluation = search(maxDepth, board, Integer.MIN_VALUE, Integer.MAX_VALUE, alliance);
            board.unMakeMove(move, true);

            if (alliance){
                if (best.evaluation < move.evaluation) best = move;
            }
            else{
                if (best.evaluation > move.evaluation) best = move;
            }
        }

        final long time = System.currentTimeMillis() - startTime;
        System.out.println("Search Took " + time + " milliseconds and found a best eval of " + best.evaluation);
        return best;
    }

    private int search(final int depth, final Board board, int alpha, int beta, final boolean maximizing){
        if (depth == 0){
            return Evaluator.evaluatePosition(
                    new Board(board.generateFen(), 45, true, true, true));
        }

        final List<Move> moves = BoardUtil.getAllianceMoves(board.turn, board);
        if (moves.isEmpty()){
            if (BoardUtil.isCheck(board.turn, board)){
                return maximizing ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            return 0;
        }

        int bestEval;
        if (maximizing){
            bestEval = Integer.MIN_VALUE;
            for (final Move move : moves){
                board.makeMove(move, true);
                final int eval = search(depth-1, board, alpha, beta, false);
                bestEval = Integer.max(eval, bestEval);
                board.unMakeMove(move, true);
                alpha = Integer.max(alpha, eval);
                if (beta <= alpha) break;
            }
        }
        else {
            bestEval = Integer.MAX_VALUE;
            for (final Move move : moves){
                board.makeMove(move, true);
                final int eval = search(depth-1, board, alpha, beta, true);
                bestEval = Integer.min(eval, bestEval);
                board.unMakeMove(move, true);
                beta = Integer.min(beta, eval);
                if (beta <= alpha) break;
            }
        }
        return bestEval;
    }
}
