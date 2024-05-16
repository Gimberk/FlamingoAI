package engine.piece;

import engine.board.Board;
import engine.board.Tile;

import java.util.List;

public abstract class Piece {
    public final int index;
    public Type type;
    public final boolean alliance;
    public char identifier;
    public List<Integer> directions;

    public Tile tile;
    public boolean moved, justMoved, dead, prevMoved, prevJustMoved;

    public Piece(final int index, final Type type, final Tile tile, final boolean alliance, final List<Integer> directions){
        char idTemp;
        this.index = index; this.type = type; this.alliance = alliance; this.directions = directions; this.tile = tile;
        moved = false; justMoved = false; dead = false;
        
        switch(type){
            case Knight -> idTemp = 'N';
            case King -> idTemp = 'K';
            case Pawn -> idTemp = 'P';
            case Rook -> idTemp = 'R';
            case Queen -> idTemp = 'Q';
            default -> idTemp = 'B';
        }

        if (!alliance) idTemp = Character.toLowerCase(idTemp);
        identifier = idTemp;
    }

    public boolean equals(Piece other){
        return other.index == index;
    }

    public abstract List<Move> getLegals(Board board);
}
