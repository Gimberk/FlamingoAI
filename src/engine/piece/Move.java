package engine.piece;

public class Move {
    public final int start;
    public final int end;

    public final Piece piece;
    public final Piece taken;

    public boolean attackMove, illegal;

    public Move(final int start, final int end, final Piece piece, final Piece taken){
        this.start = start; this.end = end; this.piece = piece; this.taken = taken;
        illegal = false;
        attackMove = taken != null;
    }

    public boolean equals(Move other){
        return other.end == end && other.start == start && other.piece == piece;
    }
}
