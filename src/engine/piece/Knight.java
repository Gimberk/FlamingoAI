package engine.piece;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.board.Tile;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(final int index, final Tile tile, final boolean alliance) {
        super(index, Type.Knight, tile, alliance, List.of(-17, -15, -10, -6, 6, 10, 15, 17));
    }

    @Override
    public List<Move> getLegals(Board board) {
        final List<Move> moves = new ArrayList<>();
        if (dead) return moves;

        for (int dir : directions){
            final int start = tile.index;
            if (dir == 17 || dir == -15 || dir == 10 || dir == -6){
                if (BoardUtil.isLastFile(start)) continue;
            }
            else if (dir == -17 || dir == 15 || dir == -10 || dir == 6){
                if (BoardUtil.isFirstFile(start)) continue;
            }

            if (dir == -6 || dir == 10){
                if (BoardUtil.isSeventhFile(start)) continue;
            }
            else if (dir == 6 || dir == -10){
                if (BoardUtil.isSecondFile(start)) continue;
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
