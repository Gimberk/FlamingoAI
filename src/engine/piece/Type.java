package engine.piece;

public enum Type {
    Queen(900),
    Pawn(100),
    King(10000),
    Rook(500),
    Bishop(300),
    Knight(300);

    public final int pieceValue;

    Type(int value) {
        pieceValue = value;
    }
}
