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

    public Move getMove(final long hash) {
        if (get(hash) != null) return get(hash).move;
        return null;
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
