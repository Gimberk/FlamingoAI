package engine.board;

import engine.piece.Move;
import engine.piece.Piece;
import engine.piece.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardUtil {
    public static final int TILES = 64;
    public static final int FILES = 8;
    public static final int RANKS = 8;

    public static final int pawnValue = 100;
    public static final int knightValue = 300;
    public static final int bishopValue = 300;
    public static final int rookValue = 500;
    public static final int queenValue = 900;

    public static boolean turn = true;

    public static boolean checkingIsCheck = false, gettingAttacks = false;

    public static int tileIndexFromRankFile(final int file, final int rank) { return rank * 8 + file; }

    public static boolean isFirstFile(final int index) { return index % 8 == 0; }
    public static boolean isSecondFile(final int index) { return (index-1) % 8 == 0; }
    public static boolean isSeventhFile(final int index) { return isLastFile(index+1); }
    public static boolean isLastFile(final int index) { return (index-7) % 8 == 0; }

    public static boolean isFourthRank (final int index) { return index >= 24 && index <= 31; }
    public static boolean isFifthRank (final int index) { return index >= 32 && index <= 39; }

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

    public static Move search(final Board board, final int depth){
        final List<Move> positions = getAllianceMoves(turn, board);
        final boolean currTurn = turn;
        for (final Move move : positions){
            board.makeMove(move, true);
            if (depth >= 2){
                final List<Move> responses = getAllianceMoves(turn, board);
                final boolean tt = turn;
                for (final Move response : responses){
                    // play them and get the min eval for black from each move and set that to the white move's overall eval
                    board.makeMove(response, true);
                    if (depth >= 3){
                        final List<Move> opRes = getAllianceMoves(turn, board);
                        final boolean ttt = turn;
                        for (final Move opRe : opRes){
                            board.makeMove(opRe, true);
                            opRe.evaluation = evaluatePosition(board);
                            board.unMakeMove(opRe);
                        }
                        turn = ttt;
                        if (opRes.isEmpty()){
                            response.evaluation = turn ? -9999 : 9999;
                        }
                        else{

                            Move best = opRes.getFirst();
                            for (final Move opRe : opRes){
                                if (turn){
                                    if (opRe.evaluation > best.evaluation) best = opRe;
                                }
                                else{
                                    if (opRe.evaluation < best.evaluation) best = opRe;
                                }
                            }
                            response.evaluation = best.evaluation;
                        }
                    }
                    else response.evaluation = evaluatePosition(board);
                    board.unMakeMove(response);
                }
                turn = tt;
                Move best = responses.getFirst();
                for (final Move response : responses){
                    if (turn){
                        if (response.evaluation > best.evaluation) best = response;
                    }
                    else{
                        if (response.evaluation < best.evaluation) best = response;
                    }
                }
                move.evaluation = best.evaluation;
            }
            else move.evaluation = evaluatePosition(board);
            board.unMakeMove(move);
        }
        turn = currTurn;
        Move best = positions.getFirst();
        for (final Move move : positions){
            System.out.println("Eval: " + move.evaluation);
            if (turn){
                if (move.evaluation > best.evaluation) best = move;
            }
            else{
                if (move.evaluation < best.evaluation) best = move;
            }
        }
        System.out.println("Best Eval: " + best.evaluation);
        return best;
    }

    public static int evaluatePosition(final Board board){
        int whiteEval = countMaterial(board, true);
        int blackEval = countMaterial(board, false);

        return (whiteEval-blackEval)/100;
    }

    private static int countMaterial(final Board board, final boolean alliance){
        int queens = 0, rooks = 0, bishops = 0, knights = 0, pawns = 0;
        for (Piece piece : board.pieces){
            if (piece.dead) continue;
            if (piece.type == Type.King || piece.alliance != alliance) continue;
            switch (piece.type){
                case Bishop -> bishops++;
                case Knight -> knights++;
                case Pawn -> pawns++;
                case Rook -> rooks++;
                case Queen -> queens++;
            }
        }

        int material = 0;
        material += queens * queenValue;
        material += rooks * rookValue;
        material += pawns * pawnValue;
        material += bishops * bishopValue;
        material += knights * knightValue;
        return material;
    }
}