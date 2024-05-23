package engine.piece;

public enum Type {
    Queen(900, 9),
    Pawn(100, 1),
    King(100000, 11),
    Rook(500, 7),
    Bishop(300, 5),
    Knight(300, 3);

    public final int pieceValue;
    public final int hash;

    Type(final int value, final int hash) {
        pieceValue = value; this.hash = hash;
    }
}
