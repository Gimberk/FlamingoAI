package ai;

import ai.evaluation.PieceSquareTable;
import engine.board.Board;
import engine.board.BoardUtil;
import engine.piece.Move;
import engine.piece.Type;

import java.util.List;

public class MoveOrder {
    private final int[] moveScores;

    public Killer[] killerMoves;

    public MoveOrder(){
        moveScores = new int[218];
        killerMoves = new Killer[32];
    }

    public void clearKillers(){
        killerMoves = new Killer[32];
    }

    public void orderMoves(final Move hashMove, final Board board, final List<Move> moves,
                           final boolean inQuiescenceSearch, final int ply){
        for (int i = 0; i < moves.size(); i++){
            final Move move = moves.get(i);
            if (move.equals(hashMove)){
                moveScores[i] = 100000000; // cuz why not
                continue;
            }
            int score = 0;

            if (move.isAttackMove()){
                int captureMaterial = move.taken.getValue() - move.piece.getValue();
                boolean canRecapture = BoardUtil.getAllianceAttacks(!board.turn, board, true).contains(board.tiles[move.end]);
                if (canRecapture) score += (captureMaterial >= 0 ? 8000000 : 2000000) + captureMaterial;
                else score += 8000000 + captureMaterial;
            }

            if (move.piece.type == Type.Pawn){
                if (move.promotion && !move.isAttackMove()) score += 6000000;
            }
            else if (move.piece.type != Type.King){
                int[] table = switch (move.piece.type){
                    case Rook -> PieceSquareTable.rooks;
                    case Bishop -> PieceSquareTable.bishops;
                    case Queen -> PieceSquareTable.queens;
                    case Knight -> PieceSquareTable.knights;
                    default -> PieceSquareTable.pawns; // should never happen
                };
                final int newPosScore = table[move.end], oldPosScore = table[move.end];
                score += newPosScore - oldPosScore;

                if (BoardUtil.attackedByPawns(board, board.tiles[move.end], board.turn)) score -= 50;
                else if (BoardUtil.getAllianceAttacks(!board.turn, board, false).contains(board.tiles[move.end]))
                    score -= 25;
            }

            if (!move.isAttackMove()){
                final boolean isKiller = !inQuiescenceSearch && ply < 32 && killerMoves[ply] != null && killerMoves[ply].equals(move);
                score += isKiller ? 4000000 : 0;
            }

            moveScores[i] = score;
        }
        sort(moves, moveScores);
    }

    public static void sort(final List<Move> moves, final int[] scores)
    {
        // bubble sort, hell yeah!
        for (int i = 0; i < moves.size() - 1; i++)
        {
            for (int j = i + 1; j > 0; j--)
            {
                int swapIndex = j - 1;
                if (scores[swapIndex] < scores[j])
                {
                    final Move swap = moves.get(swapIndex);
                    moves.set(swapIndex, moves.get(j));
                    moves.set(j, swap);
                }
            }
        }
    }

    public class Killer{
        public Move moveA, moveB;

        public void addMove(final Move move){
            if (move.evaluation == moveA.evaluation) return;
            moveB = moveA;
            moveA = move;
        }

        public boolean match(final Move move) { return move.evaluation == moveA.evaluation
                                                || move.evaluation == moveB.evaluation; }
    }
}