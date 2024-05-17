package ai;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Move;

import java.util.List;

public class MinMax implements Strategy{

    private final Evaluator evaluator;

    public MinMax(){
        evaluator = new StandardEvaluator();
    }

    @Override
    public Move execute(Board board, int depth) {
        final long startTime = System.currentTimeMillis();
        Move bestMove = null;

        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;
        int current;

        System.out.println(BoardUtil.turn ? "White is thinking with depth = " + depth : "Black is thinking with depth = " + depth);

        final List<Move> legals = BoardUtil.getAllianceMoves(BoardUtil.turn, board);
        int numMoves = legals.size();

        for (final Move move : legals){
            board.makeMove(move, true);
            current = BoardUtil.turn ? min(board, depth-1) : max(board, depth-1);
            board.unMakeMove(move);

            if (BoardUtil.turn && current >= highest){
                highest = current;
                bestMove = move;
            }
            else if (!BoardUtil.turn && current <= lowest){
                lowest = current;
                bestMove = move;
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("Think time was: " + executionTime);
        return bestMove;
    }

    public int min(final Board board, final int depth){
        if (depth == 0){
            return evaluator.evaluate(board, depth);
        }

        int best = Integer.MAX_VALUE;
        for (final Move move : BoardUtil.getAllianceMoves(BoardUtil.turn, board)){
            board.makeMove(move, true);
            final int evaluation = max(board, depth-1);
            best = Math.min(evaluation, best);
            board.unMakeMove(move);
        }

        return best;
    }

    public int max(final Board board, final int depth){
        if (depth == 0){
            return evaluator.evaluate(board, depth);
        }

        int best = Integer.MIN_VALUE;
        for (final Move move : BoardUtil.getAllianceMoves(BoardUtil.turn, board)){
            board.makeMove(move, true);
            final int evaluation = min(board, depth-1);
            best = Math.max(evaluation, best);
            board.unMakeMove(move);
        }

        return best;
    }
}
