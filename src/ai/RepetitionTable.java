package ai;

import engine.board.Board;

import java.util.List;

public class RepetitionTable {
    private final long[] hashes;
    private final int[] indices;
    private int count;

    public RepetitionTable(){
        hashes = new long[256];
        indices = new int[hashes.length+1];
    }

    public void init(final Board board){
        final List<Long> initialHashes = board.repetitionHistory.reversed();
        count = initialHashes.size();

        for (int i = 0; i < initialHashes.size(); i++){
            hashes[i] = initialHashes.get(i);
            indices[i] = 0;
        }
        indices[count] = 0;
    }

    public void put(final long hash, final boolean reset){
        if (count < hashes.length){
            hashes[count] = hash;
            indices[count + 1] = reset ? count : indices[count];
        }
        count++;
    }

    public void get(){
        count = Integer.max(0, count-1);
    }

    public boolean contains(final long hash){
        final int s = indices[count];
        for (int i = s; i < count - 1; i++){
            if (hashes[i] == hash) return  true;
        }
        return false;
    }
}
