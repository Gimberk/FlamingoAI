package ai.evaluation;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.King;
import engine.piece.Piece;
import engine.piece.Type;

import java.util.concurrent.atomic.AtomicInteger;

public class Evaluator {

    private static final int[] passedPawnBonuses = { 0, 120, 80, 50, 30, 15, 15 };
    private static final int[] isolatedPawnPenaltyByCount = { 0, -10, -25, -50, -75, -75, -75, -75, -75 };
    private static final int pawnShieldBonus = 5;

    private static int numQueens = 0, numRooks = 0, numBishops = 0, numKnights = 0;
    private static int opNumQueens = 0, opNumRooks = 0, opNumBishops = 0, opNumKnights = 0;

    public static int evaluatePosition(final Board board){
        int white = 0, black = 0;

        // Material
        int whiteMaterial = getMaterial(true, board);
        int blackMaterial = getMaterial(false, board);

        white += whiteMaterial;
        black += blackMaterial;

        final int eval = white - black;
        int perspective = board.turn ? 1 : -1;
        return eval * perspective;
    }

    private static int evaluatePawnShield(final Board board, final boolean alliance,
                                          final float opposingEndGame, int opposingPos) {
        // throw out protecting the king in an end game, we gotta go.
        if (opposingEndGame >= 1) return 0;
        int penalty = 0, unCastledKingPenalty = 0, kingFile = 0;
        King king = null;
        for (int rank = 0; rank < 8; rank++){
            for (int file = 0; file < 8; file++){
                final Piece piece = board.tiles[BoardUtil.tileIndexFromRankFile(file, rank)].piece;
                if (piece == null || piece.type != Type.King || piece.alliance != alliance) continue;
                kingFile = file;
                king = (King) piece;
                king.x = file; king.y = rank;
                break;
            }
        }
        if (king == null) return 0;

        // punish king for being in the center
        if (kingFile <= 2 || kingFile >= 5){
            int tiles = alliance ? getPawnShield(true, board, king) :
                    getPawnShield(false, board, king);
            penalty += pawnShieldBonus * tiles;
            penalty *= penalty;
        }
        else {
            float opponentDevelopment = Math.clamp((opposingPos + 10) / 130, 0, 1);
            unCastledKingPenalty = (int) (50 * opponentDevelopment);
        }

        int openFilePenalty = 0;
        if (opNumRooks > 1 || (opNumRooks > 0 && opNumQueens > 0))
            openFilePenalty = evaluateOpenFilesAndDiagonals(board, king.x, king.y, alliance);

        float pawnShieldWeight = 1 - opposingEndGame;
        if (opNumQueens == 0)
        {
            pawnShieldWeight *= 0.6f;
        }

        return (int) ((-penalty - unCastledKingPenalty - openFilePenalty) * pawnShieldWeight);
    }

    private static int evaluateOpenFilesAndDiagonals(final Board board, final int kingX,
                                                     final int kingY, final boolean alliance) {
        int penalty = 0;

        for (int x = Math.max(0, kingX - 1); x <= Math.min(7, kingX + 1); x++) {
            if (isFileOpen(board, x)) {
                penalty -= x == kingX ? 25 : 20;
            }
        }

        final int perspective = alliance ? -1 : 1;
        final int[] dirs = {7, 9};
        for (final int dir : dirs){
            int real = dir * perspective;
            int kingIndex = BoardUtil.tileIndexFromRankFile(kingX, kingY);
            final int start = kingIndex;
            boolean open = true;
            if (!BoardUtil.isFirstFile(kingIndex) && (real == -9 || real == 7)){
                while ((kingIndex + real) >= 0 && (kingIndex + real < 64)){
                    kingIndex += real;
                    if (board.tiles[kingIndex].occupied && board.tiles[kingIndex].piece.type == Type.Pawn){
                        open = false;
                        kingIndex = start;
                        break;
                    }
                }
            }
            else open = false;

            if(open) penalty -= 15;
            open = true;

            if (!BoardUtil.isLastFile(kingIndex) && (real == 9 || real == -7)){
                while (kingIndex+real >= 0 && kingIndex + real < 64){
                    kingIndex += real;
                    if (board.tiles[kingIndex].occupied && board.tiles[kingIndex].piece.type == Type.Pawn){
                        open = false;
                        break;
                    }
                }
            }
            else open = false;

            if(open) penalty -= 15;
        }

        return penalty;
    }

    private static boolean isFileOpen(Board board, int file) {
        for (int rank = 0; rank < 8; rank++) {
            final Piece piece = board.tiles[BoardUtil.tileIndexFromRankFile(file, rank)].piece;
            if (piece != null && piece.type == Type.Pawn) {
                return false;
            }
        }
        return true;
    }

    public static int getPawnShield(final boolean alliance, final Board board, final King king){
        final boolean first = BoardUtil.isFirstFile(king.tile.index), last = BoardUtil.isLastFile(king.tile.index);
        int count = 0;

        int rank = alliance ? king.y - 1 : king.y + 1;
        for (int i = -1; i < 2; i++){
            if (first && i == -1 || (last && i == 1)) continue;
            int index = BoardUtil.tileIndexFromRankFile(king.x + i, rank);
            if (board.tiles[index].occupied && board.tiles[index].piece.type == Type.Pawn) count++;
        }
        return count;
    }

    private static int pieceSquareTables(final Board board, final boolean alliance, final float endgame) {
        int score = 0;
        // these piece square tables never change throughout the game
        score += PieceSquareTable.pieceSquareTable(board, Type.Queen, PieceSquareTable.queens, alliance);
        score += PieceSquareTable.pieceSquareTable(board, Type.Rook, PieceSquareTable.rooks, alliance);
        score += PieceSquareTable.pieceSquareTable(board, Type.Knight, PieceSquareTable.knights, alliance);
        score += PieceSquareTable.pieceSquareTable(board, Type.Bishop, PieceSquareTable.bishops, alliance);

        // pawns are strange as always
        int pawnEarly = PieceSquareTable.pieceSquareTable(board, Type.Pawn, PieceSquareTable.pawns, alliance);
        int pawnEnd = PieceSquareTable.pieceSquareTable(board, Type.Pawn, PieceSquareTable.pawnsEnd, alliance);
        score += (int) (pawnEarly * (1 - endgame));
        score += (int) (pawnEnd * endgame);

        // in the endgame, the king becomes immensely useful
        int kingEarly = PieceSquareTable.pieceSquareTable(board, Type.King, PieceSquareTable.KingStart, alliance);
        score += (int) (kingEarly * (1-endgame));
        int kingEnd = PieceSquareTable.pieceSquareTable(board, Type.King, PieceSquareTable.KingEnd, alliance);
        score += (int) (kingEnd * endgame);

        return score;
    }

    private static int evaluateEndgame(final Board board, final boolean alliance, final int friendlyScore,
                                       final int opposingScore, final float endgameWeight) {
        int score = 0;
        if (friendlyScore > opposingScore + Type.Pawn.pieceValue * 2 && endgameWeight > 0){
            int friendlyKingTile;
            int oppsingKingTile;

            for (final Piece piece : board.pieces){
                if (piece.type == Type.King){
                    if (piece.alliance == alliance) friendlyKingTile = piece.tile.index;
                    else oppsingKingTile = piece.tile.index;
                }
            }

            return 0; // compute rook and king evaluations during end game here
        }
        return 0;
    }

    private static float endgamePhase(final Board board, final boolean alliance){
        final int queenEndgameWeight = 45;
        final int rookEndgameWeight = 20;
        final int bishopEndgameWeight = 10;
        final int knightEndgameWeight = 10;

        int numQueens = 0, numRooks = 0, numBishops = 0, numKnights = 0;
        for (final Piece piece : BoardUtil.getPieces(alliance, board)){
            switch (piece.type){
                case Knight -> numKnights++;
                case Queen -> numQueens++;
                case Bishop -> numBishops++;
                case Rook -> numRooks++;
            }
        }

        final int endgameStartWeight = 2 * rookEndgameWeight + 2 * bishopEndgameWeight + 2 * knightEndgameWeight + queenEndgameWeight;
        int endgameWeightSum = numQueens *
                queenEndgameWeight + numRooks * rookEndgameWeight + numBishops *
                bishopEndgameWeight + numKnights * knightEndgameWeight;
        return 1 - Math.min(1, endgameWeightSum / (float)endgameStartWeight);
    }

    private static int getMaterial(final boolean alliance, final Board board){
        AtomicInteger score = new AtomicInteger();
        BoardUtil.getPieces(alliance, board).forEach(piece -> score.addAndGet(piece.getValue()));
        return score.get();
    }
}
