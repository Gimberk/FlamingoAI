package engine.board;

import engine.piece.Piece;

public class Tile {
    public final int index;
    public final boolean color; // true is white

    public Piece piece;
    public boolean occupied;

    public Tile(final int index, final boolean color){
        this.index = index; this.color = color;
    }

    public void update(Piece piece){
        occupied = piece != null;
        this.piece = piece;
    }
}
