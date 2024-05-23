package engine.piece;

import engine.board.Board;
import engine.board.BoardUtil;

public class Move {
    public final int start;
    public final int end;

    public final Piece piece;
    public final Piece taken;

    public boolean attackMove, illegal, castleQ = false, castleK = false, enPassant, promotion;

    public int evaluation = -9999;

    public Move(final int start, final int end, final Piece piece, final Piece taken){
        this.start = start; this.end = end; this.piece = piece; this.taken = taken;
        illegal = false;
        attackMove = taken != null;
        promotion = false;
        enPassant = false;
    }

    public boolean equals(Move other){
        if (other == null) return false;
        return other.end == end && other.start == start && other.piece == piece;
    }

    public boolean isAttackMove(){
        if (castleQ || castleK || illegal) return false;
        return attackMove;
    }

    public boolean isCheckMove(final Board board){
        boolean isCheck;
        if (illegal) return false;
        board.makeMove(this, true);
        isCheck = BoardUtil.isCheck(true, board) || BoardUtil.isCheck(false, board);
        board.unMakeMove(this, false);
        return isCheck;
    }
}
