package engine.piece;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.board.Tile;
import engine.piece.Move;
import engine.piece.Piece;
import engine.piece.Type;

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
        if (type == Type.Queen){
            final List<Move> moves = new ArrayList<>();
            if (dead) return moves;

            for (int dir : directions){
                final int start = tile.index;
                int end = start;

                if (dir == 1 || dir == 9 || dir == -7){
                    if (BoardUtil.isLastFile(start)) continue;
                }
                else if (dir == -1 || dir == 7 || dir == -9){
                    if (BoardUtil.isFirstFile(start)) continue;
                }

                while (end >= 0 && end <= 63){
                    end += dir;
                    if (end < 0 || end > 63) break;
                    Tile tile = board.tiles[end];
                    if (tile.occupied && alliance == tile.piece.alliance) break;
                    moves.add(new Move(start, end, this, tile.piece));
                    if (tile.occupied) break;
                    if (dir == 1 || dir == 9 || dir == -7){
                        if (BoardUtil.isLastFile(end)) break;
                    }
                    else if (dir == -1 || dir == 7 || dir == -9){
                        if (BoardUtil.isFirstFile(end)) break;
                    }
                }
            }

            for (Move move : moves){
                board.makeMove(move, true);
                if (BoardUtil.isCheck(alliance, board)) move.illegal = true;
                board.unMakeMove(move);
            }

            moves.removeIf(move -> move.illegal);

            return moves;
        }

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
                Move move = new Move(start, end, this, null);
                if (alliance && BoardUtil.isSecondRank(start) || (!alliance && BoardUtil.isSeventhRank(start))) move.promotion = true;
                moves.add(move);
            }
        }
        // double jump
        if(!moved && frontCleared && !board.tiles[end+8*modifier].occupied && (alliance && BoardUtil.isSeventhRank(start) || (!alliance && BoardUtil.isSecondRank(start)))){
            moves.add(new Move(start, end + 8 * modifier, this, null));
        }

        // diagonal attacks
        if (!BoardUtil.isFirstFile(start)){
            int leftDiag = alliance ? -9 : 7;
            end = start + leftDiag;
            if (end >= 0 && end <= 63){
                if (board.tiles[end].occupied && board.tiles[end].piece.alliance != alliance){
                    final Move move = new Move(start, end, this, board.tiles[end].piece);
                    moves.add(move);
                    if (alliance && BoardUtil.isSecondRank(start) || (!alliance && BoardUtil.isSeventhRank(start))) move.promotion = true;
                }
            }
        }
        if (!BoardUtil.isLastFile(start)){
            int rightDiag = alliance ? -7 : 9;
            end = start + rightDiag;
            if (end >=0 && end <= 63){
                if (board.tiles[end].occupied && board.tiles[end].piece.alliance != alliance){
                    final Move move = new Move(start, end, this, board.tiles[end].piece);
                    moves.add(move);
                    if (alliance && BoardUtil.isSecondRank(start) || (!alliance && BoardUtil.isSeventhRank(start))) move.promotion = true;
                }
            }
        }

        // en passant
        if (alliance && !board.history.isEmpty()){
            if (BoardUtil.isFourthRank(start)){
                if (!BoardUtil.isFirstFile(start) &&
                        (board.tiles[start-1].occupied &&
                                board.history.getLast().piece == board.tiles[start-1].piece)){
                    Move enPassant = new Move(start, start-9, this, board.tiles[start-1].piece);
                    enPassant.attackMove = true;
                    enPassant.enPassant = true;

                    if (!BoardUtil.movesContains(moves, enPassant)) moves.add(enPassant);
                }
                if (!BoardUtil.isLastFile(start) &&
                        (board.tiles[start+1].occupied &&
                                board.history.getLast().piece == board.tiles[start+1].piece)){
                    Move enPassant = new Move(start, start-7, this, board.tiles[start+1].piece);
                    enPassant.attackMove = true;
                    enPassant.enPassant = true;
                    if (!BoardUtil.movesContains(moves, enPassant)) moves.add(enPassant);
                }
            }
        }
        else if (!board.history.isEmpty()){
            if (BoardUtil.isFifthRank(start)){
                if (!BoardUtil.isFirstFile(start) &&
                        (board.tiles[start-1].occupied &&
                                board.history.getLast().piece == board.tiles[start-1].piece)){
                    Move enPassant = new Move(start, start+7, this, board.tiles[start-1].piece);
                    enPassant.attackMove = true;
                    enPassant.enPassant = true;
                    if (!BoardUtil.movesContains(moves, enPassant)) moves.add(enPassant);
                }
                if (!BoardUtil.isLastFile(start) &&
                        (board.tiles[start+1].occupied &&
                                board.history.getLast().piece == board.tiles[start+1].piece)){
                    Move enPassant = new Move(start, start+9, this, board.tiles[start+1].piece);
                    enPassant.attackMove = true;
                    enPassant.enPassant = true;
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
