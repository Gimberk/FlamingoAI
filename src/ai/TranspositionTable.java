package ai;

import engine.piece.Move;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
    public static class TTEntry{
        public final int evaluation;
        public final int depth;
        public final long hash;

        TTEntry(final int evaluation, final int depth, final long hash) {
            this.evaluation = evaluation;
            this.depth = depth;
            this.hash = hash;
        }
    }

    private final Map<Long, TTEntry> table;

    public TranspositionTable() {
        table = new HashMap<>();
    }

    public TTEntry get(long hash) {
        return table.get(hash);
    }

    public void put(final long hash, final int evaluation, final int depth) {
        table.put(hash, new TTEntry(evaluation, depth, depth));
    }

    public boolean contains(long hash) {
        return table.containsKey(hash);
    }
}
