package engine.piece;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.board.Tile;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(final int index, final Tile tile, final boolean alliance) {
        super(index, Type.Pawn, tile, alliance, List.of());
    }

    // pawns are the worst chess piece to code. I hate all their moves and rules, therefore, this is coded in a
    // much different way than the other piece. I handle all the rules separately rather than just using the direction
    // plus for loop idea from other pieces.
    @Override
    public List<Move> getLegals(Board board) {
        final List<Move> moves = new ArrayList<>();
        if (dead) return moves;

        final int modifier = alliance ? -1 : 1;
        final int start = tile.index;
        int end = start + 8 * modifier;

        // single jump
        boolean frontCleared = false;
        if (end > 0 && end < 63){
            if (!board.tiles[end].occupied){
                frontCleared = true;
                moves.add(new Move(start, end, this, null));
            }
        }
        // double jump
        if(!moved && frontCleared){
            moves.add(new Move(start, end + 8 * modifier, this, null));
        }

        // diagonal attacks
        if (!BoardUtil.isFirstFile(start)){
            int leftDiag = alliance ? -9 : 7, rightDiag = alliance ? -7 : 9;
            end = start + leftDiag;
            if (end >= 0 && end <= 63){
                if (board.tiles[end].occupied && board.tiles[end].piece.alliance != alliance){
                    moves.add(new Move(start, end, this, board.tiles[end].piece));
                }
            }
            end = start + rightDiag;
            if (end > -1 && end < 64){
                if (board.tiles[end].occupied && board.tiles[end].piece.alliance != alliance){
                    moves.add(new Move(start, end, this, board.tiles[end].piece));
                }
            }
        }

        // en passant
        if (alliance){
            if (BoardUtil.isFourthRank(start)){
                if (!BoardUtil.isFirstFile(start) &&
                        (board.tiles[start-1].occupied &&
                        board.tiles[start-1].piece.justMoved)){
                    Move enPassant = new Move(start, start-9, this, board.tiles[start-9].piece);
                    enPassant.attackMove = true;
                    if (!BoardUtil.movesContains(moves, enPassant)) moves.add(enPassant);
                }
                if (!BoardUtil.isLastFile(start) &&
                        (board.tiles[start+1].occupied &&
                        board.tiles[start+1].piece.justMoved)){
                    Move enPassant = new Move(start, start-7, this, board.tiles[start-7].piece);
                    enPassant.attackMove = true;
                    if (!BoardUtil.movesContains(moves, enPassant)) moves.add(enPassant);
                }
            }
        }
        else{
            if (BoardUtil.isFifthRank(start)){
                if (!BoardUtil.isFirstFile(start) &&
                        (board.tiles[start-1].occupied &&
                                board.tiles[start-1].piece.justMoved)){
                    Move enPassant = new Move(start, start+7, this, board.tiles[start+7].piece);
                    enPassant.attackMove = true;
                    if (!BoardUtil.movesContains(moves, enPassant)) moves.add(enPassant);
                }
                if (!BoardUtil.isLastFile(start) &&
                        (board.tiles[start+1].occupied &&
                                board.tiles[start+1].piece.justMoved)){
                    Move enPassant = new Move(start, start+9, this, board.tiles[start+9].piece);
                    enPassant.attackMove = true;
                    if (!BoardUtil.movesContains(moves, enPassant)) moves.add(enPassant);
                }
            }
        }

        for (Move move : moves){
            if (move.end < 0 || move.end > 63){
                move.illegal = true;
                continue;
            }
            board.makeMove(move, true);
            if (BoardUtil.isCheck(alliance, board)) move.illegal = true;
            board.unMakeMove(move);
        }

        moves.removeIf(move -> move.illegal);

        return moves;
    }
}
