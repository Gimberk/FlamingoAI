package ai;

import engine.piece.Move;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    public static class TTEntry{
        public final int evaluation;
        public final int depth;
        public final NodeType type;
        public final Move move;
        public final long hash;

        TTEntry(final int evaluation, final int depth, final NodeType type, final long hash, final Move move) {
            this.evaluation = evaluation;
            this.depth = depth;
            this.type = type;
            this.hash = hash;
            this.move = move;
        }
    }

    public enum NodeType {
        EXACT, LOWER_BOUND, UPPER_BOUND
    }

    private final Map<Long, TTEntry> table;

    public TranspositionTable() {
        table = new HashMap<>();
    }

    public int lookup(long hash, int depth, int plyFromRoot, int alpha, int beta)
    {
        final TTEntry entry = get(hash);
        if (entry == null) return -1;
        if (entry.depth >= depth)
        {
            final int correctedScore = correctRetrievedMateScore(entry.evaluation, plyFromRoot);
            if (entry.type == NodeType.EXACT || (entry.type == NodeType.LOWER_BOUND && correctedScore >= beta)
                || (entry.type == NodeType.UPPER_BOUND && correctedScore <= alpha)) return correctedScore;
        }
        return -1; // lookup failed
    }

    public Move getMove(final long hash) {
        if (get(hash) != null) return get(hash).move;
        return null;
    }

    public int correctMateScore(final int score, final int plySearched){
        if (Quiescence.isMate(score)){
            final int signum = (int)Math.signum(score);
            return (score * signum - plySearched) * signum;
        }
        return score;
    }

    public int correctRetrievedMateScore(final int score, final int plySearched){
        if (Quiescence.isMate(score)){
            final int signum = (int)Math.signum(score);
            return (score * signum + plySearched) * signum;
        }
        return score;
    }

    public TTEntry get(long hash) {
        return table.get(hash);
    }

    public void put(final long hash, final int evaluation, final int depth, final NodeType type, final Move move) {
        table.put(hash, new TTEntry(evaluation, depth, type, depth, move));
    }

    public boolean contains(long hash) {
        return table.containsKey(hash);
    }
}
