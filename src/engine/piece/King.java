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

        for (Move move : moves){
            board.makeMove(move, true);
            if (BoardUtil.isCheck(alliance, board)) move.illegal = true;
            board.unMakeMove(move);
        }

        moves.removeIf(move -> move.illegal);

        return moves;
    }
}
