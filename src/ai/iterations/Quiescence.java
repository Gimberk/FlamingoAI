package ai.iterations;

import ai.Strategy;
import ai.TranspositionTable;
import ai.ZobristHash;
import ai.evaluation.Evaluator;
import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Move;

import java.util.List;

public class Quiescence implements Strategy {
    private final int maxDepth;
    private final TranspositionTable ttable;
    private final ZobristHash zobristHash;

    public Quiescence(final int maxDepth){
        zobristHash = new ZobristHash();
        ttable = new TranspositionTable();
        this.maxDepth = maxDepth;
    }

    @Override
    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis();
        final List<Move> moves = BoardUtil.getAllianceMoves(board.turn, board);
        // order moves

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
        final long hash = zobristHash.generateZobristKey(board);
        if (ttable.contains(hash)) return ttable.get(hash).evaluation;

        if (depth == 0){
            final int eval = quiescence(board, alpha, beta);
            ttable.put(hash, eval, depth);
            return eval;
        }

        final List<Move> moves = BoardUtil.getAllianceMoves(board.turn, board);
        if (moves.isEmpty()){
            if (BoardUtil.isCheck(board.turn, board)){
                return maximizing ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            return 0;
        }
        // order moves

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

    private int quiescence (final Board board, int alpha, int beta){
        int eval = Evaluator.evaluatePosition(
                new Board(board.generateFen(), 45, true, true, true));
        if (eval >= beta) return beta;
        if (eval > alpha) alpha = eval;

        final List<Move> moves = BoardUtil.getAllCaptures(board.turn, board);
        for (final Move move : moves){
            board.makeMove(move, true);
            eval = -quiescence(board, -beta, -alpha);
            board.unMakeMove(move, true);

            if (eval >= beta) return beta;
            if (eval > alpha) alpha = eval;
        }
        return alpha;
    }
}