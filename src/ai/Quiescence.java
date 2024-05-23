package ai;

import ai.evaluation.Evaluator;
import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Move;
import engine.piece.Type;

import java.util.List;

public class Quiescence implements Strategy {
    private final TranspositionTable ttable;
    private final MoveOrder moveOrder;
    private final ZobristHash zobristHash;

    private int bestEval;
    private Move bestMove;
    boolean searchedOneMove;
    boolean cancelled;
    private int currentDepth;
    private Move bestMoveIteration;
    private int bestEvalIteration;

    private final int maxDepth;

    private final RepetitionTable repetitionTable;

    public Quiescence(final int maxDepth){
        this.maxDepth = maxDepth;
        ttable = new TranspositionTable();
        moveOrder = new MoveOrder();
        zobristHash = new ZobristHash();

        repetitionTable = new RepetitionTable();
    }

    @Override
    public Move execute(final Board board) {
        bestEvalIteration = 0; bestEval = 0;
        bestMove = null; bestMoveIteration = null;
        currentDepth = 0; cancelled = false;

        final boolean alliance = board.turn;

        moveOrder.clearKillers();
        repetitionTable.init(board);

        final long searchStart = System.currentTimeMillis();

        iterativeDeepeningSearch(board);

        if (bestMove == null){
            bestMove = BoardUtil.getAllianceMoves(alliance, board).getFirst();
        }
        System.out.println(bestMove.end);
        cancelled = false;
        return bestMove;
    }

    void iterativeDeepeningSearch(final Board board){
        for (int depth = 1; depth < maxDepth; depth++){ // will exit if we exceed time
            searchedOneMove = false;
            System.out.println("Started Search Depth: " + depth);
            final long iterationStart = System.currentTimeMillis();
            currentDepth = depth;
            minimax(board, depth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, null);

            if (cancelled){
                System.out.println("Search Canceled...");
                if (searchedOneMove){
                    bestMove = bestMoveIteration;
                    bestEval = bestEvalIteration;
                }
                break;
            }

            currentDepth = depth;
            bestMove = bestMoveIteration;
            bestEval = bestEvalIteration;
            System.out.println("Search Depth " + depth + " result: " + bestEval);
            if (isMate(bestMove, board)){
                System.out.println("Found Mate in " + depthToMate(bestEval));
                System.out.println(bestMove.end);
            }
            bestEvalIteration = Integer.MIN_VALUE;
            bestMoveIteration = null;

            if (isMate(bestMove, board) && depthToMate(bestEval) <= depth) break; // we found a mate, go get 'em tiger
        }
    }

    private int minimax(final Board board, final int depth, final int plyFromRoot, int alpha, int beta, final int extensions,
                        final Move previousMove) {
        if (cancelled) return 0;
        final long hash = zobristHash.generateZobristKey(board);
        if (plyFromRoot > 0){
            if (board.fiftyMoveCounter >= 100 || repetitionTable.contains(hash)) return 0;
            alpha = Integer.max(alpha, -100000 + plyFromRoot);
            beta = Integer.min(beta, 100000 - plyFromRoot);
            if (alpha >= beta) return alpha;
        }
        int ttableEval = ttable.lookup(hash, depth, plyFromRoot, alpha, beta);
        if (ttableEval == -1){
            if (plyFromRoot == 0){
                bestEvalIteration = ttableEval;
                bestMoveIteration = ttable.getMove(hash);
            }
        }

        if (depth == 0){
            return quiescenceSearch(alpha, beta, board);
        }

        final List<Move> moves = BoardUtil.getAllianceMoves(board.turn, board);
        final Move prevBest = plyFromRoot == 0 ? bestMove : ttable.getMove(hash);
        moveOrder.orderMoves(prevBest, board, moves, false, plyFromRoot);
        if (moves.isEmpty()){
            if (BoardUtil.isCheck(board.turn, board)) return -(100000 - plyFromRoot); // this puts us in checkmate... bad!!!
            return 0; // stalemate... not too bad
        }

        if (plyFromRoot > 0){
            final boolean pawnMove = previousMove.piece.type == Type.Pawn;
            repetitionTable.put(hash, previousMove.isAttackMove() || pawnMove);
        }

        int evalBound = 2;
        Move bestMovePosition = null;

        for (int i = 0; i < moves.size(); i++){
            final Move move = moves.get(i);
            board.makeMove(move, true);

            int extension = 0;
            if (extensions < 16){
                for (int rank = 0; rank < 8; rank++){
                    for (int file = 0; file < 8; file++){
                        if (board.tiles[BoardUtil.tileIndexFromRankFile(file, rank)].occupied){
                            board.tiles[BoardUtil.tileIndexFromRankFile(file, rank)].piece.x = file;
                            board.tiles[BoardUtil.tileIndexFromRankFile(file, rank)].piece.y = rank;
                        }
                    }
                }
                if (BoardUtil.isCheck(board.turn, board)){
                    extension = 1;
                }
                else if (move.piece.type == Type.Pawn && (move.piece.x == 1 || move.piece.x == 6)) extension = 1;
            }
            boolean fullSearch = true;
            int eval = 0;
            // LMR - Late Move Reduction
            if (extension == 0 && depth >= 3 && i >= 3 && !move.isAttackMove()){
                eval = -minimax(board, depth - 2, plyFromRoot + 1, -alpha - 1, -alpha, extension,
                        move);
                fullSearch = eval > alpha;
            }

            if (fullSearch) eval = -minimax(board, depth - 1 + extension, plyFromRoot + 1, -beta,
                                            -alpha, extensions + extension, move);
            board.unMakeMove(move, true);

            if (cancelled) return 0;

            if (eval >= beta){
                ttable.put(hash, eval, depth, TranspositionTable.NodeType.LOWER_BOUND, move);
                if (!move.isAttackMove()){
                    if (plyFromRoot < 32) moveOrder.killerMoves[plyFromRoot].addMove(move);
                }
                if (plyFromRoot > 0) repetitionTable.get();

                return beta;
            }

            if (eval > alpha){
                evalBound = 0;
                bestMovePosition = moves.get(i);

                alpha = eval;
                if (plyFromRoot == 0){
                    bestEvalIteration = eval;
                    bestMoveIteration = moves.get(i);
                    searchedOneMove = true;
                }
            }
        }

        if (plyFromRoot > 0) repetitionTable.get();

        return alpha;
    }

    private int quiescenceSearch(int alpha, final int beta, final Board board) {
        if (cancelled) return 0;
        int eval = Evaluator.evaluatePosition(board);

        if (eval >= beta) return beta;
        if (eval > alpha) alpha = eval;

        final List<Move> moves = BoardUtil.getAllianceMoves(board.turn, board);
        moveOrder.orderMoves(null, board, moves, true, 0);
        for (int i = 0; i < moves.size(); i++){
            board.makeMove(moves.get(i), true);
            eval = -quiescenceSearch(-beta, -alpha, board);
            board.unMakeMove(moves.get(i), true);

            if (eval >= beta) return beta;
            if (eval > alpha) alpha = eval;
        }

        return alpha;
    }

    public static int depthToMate(int score) { return 100000 - Math.abs(score); }

    public static boolean isMate(final int eval) {
        if (eval == Integer.MIN_VALUE) return false;
        return Math.abs(eval) > 99000;
    }

    public static boolean isMate(final Move eval, final Board board) {
        boolean mate = false;
        board.makeMove(eval, true);
        if (BoardUtil.isCheckMate(board.turn, board)) mate = true;
        board.unMakeMove(eval, true);
        return mate;
    }
}