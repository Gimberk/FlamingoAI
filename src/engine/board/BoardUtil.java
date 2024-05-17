package engine.board;

import engine.piece.Move;
import engine.piece.Piece;
import engine.piece.Type;

import java.util.ArrayList;
import java.util.List;

public class BoardUtil {
    public static final int TILES = 64;
    public static final int FILES = 8;
    public static final int RANKS = 8;

    public static boolean turn = true;

    private static int depth;

    public static void init(final Board board, final int depth_m){
        depth = depth_m;
    }

    public static void switchTurn(final Board board){
        turn = !turn;
//        if (turn && board.whitePlayer || (!turn && board.blackPlayer)) return;
//        final Move best = findBestMove(board, 3);
//        if (best != null){
//            board.makeMove(best, false);
//            board.showBoard();
//
//            for (final TilePanel panel : frame.boardPanel.tiles){
//                panel.removeAll();
//                panel.repaint();
//                panel.revalidate();
//                panel.update();
//            }
//        }
    }

    public static boolean checkingIsCheck = false, gettingAttacks = false;

    public static int tileIndexFromRankFile(final int file, final int rank) { return rank * 8 + file; }

    public static boolean isFirstFile(final int index) { return index % 8 == 0; }
    public static boolean isSecondFile(final int index) { return (index-1) % 8 == 0; }
    public static boolean isSeventhFile(final int index) { return isLastFile(index+1); }
    public static boolean isLastFile(final int index) { return (index-7) % 8 == 0; }

    public static boolean isSecondRank(final int index) { return index >= 8 && index <= 15; }
    public static boolean isFourthRank (final int index) { return index >= 24 && index <= 31; }
    public static boolean isFifthRank (final int index) { return index >= 32 && index <= 39; }
    public static boolean isSeventhRank(final int index) { return index >= 48 && index <= 55; }

    public static boolean movesContains (final List<Move> moves, final Move move){
        for (Move other : moves){
            if (other.equals(move)) return true;
        }

        return false;
    }

    public static List<Tile> getAllianceAttacks(final boolean alliance, final Board board){
        final List<Tile> attacks = new ArrayList<>();
        for (final Piece piece : board.pieces){
            if (piece.alliance != alliance) continue;
            gettingAttacks = true;
            final List<Move> legals = piece.getLegals(board);
            for (final Move move : legals){
                attacks.add(board.tiles[move.end]);
            }
        }
        gettingAttacks = false;
        return attacks;
    }

    public static List<Piece> getPieces(final boolean alliance, final Board board){
        final List<Piece> pieces = board.pieces;
        pieces.removeIf(alliance ? piece -> !piece.alliance : piece -> piece.alliance);
        return pieces;
    }

    public static boolean isCheck(final boolean alliance, final Board board){
        Piece king = null;
        if (checkingIsCheck) return false;
        checkingIsCheck = true;
        for (final Piece piece : board.pieces){
            if (piece.alliance == alliance && piece.type == Type.King){
                king = piece;
                break;
            }
        }
        if (king == null) return false;

        // calculate every square a specific alliance attacks
        final List<Tile> allianceAttacks = getAllianceAttacks(!alliance, board);
        checkingIsCheck = false;
        for (final Tile attackedTile : allianceAttacks){
            if (king.tile.index == attackedTile.index) return true;
        }
        return false;
    }

    public static boolean isCheckMate(final boolean alliance, final Board board){
        final List<Move> allianceMoves = new ArrayList<>();
        for (final Piece piece : board.pieces){
            if (piece.alliance != alliance) continue;
            allianceMoves.addAll(piece.getLegals(board));
        }

        if (!allianceMoves.isEmpty()) return false;

        if (!isCheck(alliance, board)){
            System.out.println("Stalemate!!!!");
        }
        return true;
    }

    public static List<Move> getAllianceMoves(final boolean alliance, final Board board){
        final List<Move> moves = new ArrayList<>();
        for (final Piece piece : board.pieces){
            if (alliance != piece.alliance) continue;
            moves.addAll(piece.getLegals(board));
        }
        return moves;
    }

    public static boolean movePlayed(final Move move, final Board board){
        return movesContains(board.history, move);
    }
}