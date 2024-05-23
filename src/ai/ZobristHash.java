package ai;

import engine.board.Board;
import engine.piece.Piece;

import java.util.Random;

public class ZobristHash {
    private long[][] zobristTable;

    public ZobristHash(){
        initZobristTable();
    }

    private void initZobristTable(){
        zobristTable = new long[12][64];
        final Random random = new Random(548);
        for (int piece = 0; piece < 12; piece++){
            for (int square = 0; square < 64; square++){
                zobristTable[piece][square] = random.nextLong();
            }
        }
    }

    public long generateZobristKey(final Board board){
        long hash = 0;
        for (int square = 0; square < 64; square++){
            final int piece = board.getPieceAt(square);
            if (piece != 0){
                hash ^= zobristTable[piece-1][square]; // fancy xor business. Biden's coming for putin's bussy.
            }
        }
        return hash;
    }
}
