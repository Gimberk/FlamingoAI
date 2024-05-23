package engine.piece;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.board.Tile;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
    public King(final int index, final Tile tile, final boolean alliance) {
        super(index, Type.King, tile, alliance, List.of(-9, -7, -8, -1, 1, 7, 8, 9));
    }

    @Override
    public List<Move> getLegals(Board board) {
        final List<Move> moves = new ArrayList<>();
        if (dead) return moves;
        for (int dir : directions){
            final int start = tile.index;
            if (dir == 1 || dir == 9 || dir == -7){
                if (BoardUtil.isLastFile(start)) continue;
            }
            else if (dir == -1 || dir == 7 || dir == -9){
                if (BoardUtil.isFirstFile(start)) continue;
            }
            int end = start + dir;
            if (end < 0 || end > 63) continue;
            Tile tile = board.tiles[end];
            if (tile.occupied && alliance == tile.piece.alliance) continue;
            moves.add(new Move(start, end, this, tile.piece));
        }

        // Castle
        if (!BoardUtil.gettingAttacks) {
            if (!moved) {
                // Kingside
                int end = tile.index + 2;
                int bishop = end - 1;
                int rook = end + 1;
                if (bishop >= 0 && bishop <= 63 && rook >= 0 && rook <= 63 && end >= 0 && end <= 63){
                    if (!board.tiles[bishop].occupied && !board.tiles[end].occupied && board.tiles[rook].occupied) {
                        if (board.tiles[rook].piece.alliance == alliance && board.tiles[rook].piece.type == Type.Rook &&
                                !board.tiles[rook].piece.moved) {
                            List<Tile> attacks = BoardUtil.getAllianceAttacks(!alliance, board, true);
                            boolean attacked = false;
                            for (Tile t : attacks) {
                                if (t.index == bishop || t.index == tile.index || t.index == end) {
                                    attacked = true;
                                    break;
                                }
                            }
                            if (!attacked) {
                                Move kC = new Move(tile.index, end, this, board.tiles[rook].piece);
                                kC.castleK = true;
                                moves.add(kC);
                            }
                        }
                    }
                }

                //Queenside
                if (!BoardUtil.gettingAttacks){
                    end = tile.index - 2;
                    int knight = end - 1, queen = end + 1;
                    rook = end - 2;
                    if (knight >= 0){
                        if (!board.tiles[knight].occupied && !board.tiles[end].occupied && !board.tiles[queen].occupied &&
                                board.tiles[rook].occupied) {
                            if (board.tiles[rook].piece.alliance == alliance && board.tiles[rook].piece.type == Type.Rook &&
                                    !board.tiles[rook].piece.moved){
                                List<Tile> attacks = BoardUtil.getAllianceAttacks(!alliance, board, true);
                                boolean attacked = false;
                                for (Tile t : attacks) {
                                    if (t.index == queen || t.index == tile.index || t.index == end || t.index == knight) {
                                        attacked = true;
                                        break;
                                    }
                                }

                                if (!attacked) {
                                    Move qC = new Move(tile.index, end, this, board.tiles[rook].piece);
                                    qC.castleQ = true;
                                    moves.add(qC);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Move move : moves){
            board.makeMove(move, true);
            if (BoardUtil.isCheck(alliance, board)) move.illegal = true;
            board.unMakeMove(move, false);
        }

        moves.removeIf(move -> move.illegal);

        return moves;
    }
}
