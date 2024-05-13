package engine.board;

import engine.piece.*;

import java.util.*;

public class Board {
    public final List<Piece> pieces = new ArrayList<>();
    public final Tile[] tiles = new Tile[64];

    private final char[][] tileGui = new char[BoardUtil.RANKS][BoardUtil.FILES];

    public Board(String fen){
        generateBoard();
        loadFEN(fen);
    }

    public void loadFEN(String fen){
        Map<Character, Type> pieceChars = Map.of(
                'K', Type.King,
                'N', Type.Knight,
                'P', Type.Pawn,
                'R', Type.Rook,
                'Q', Type.Queen,
                'B', Type.Bishop);

        final String board = fen.split(" ")[0];
        int rank = 0, file = 0;
        int index = 0;
        for (char symbol : board.toCharArray()){
            if (symbol == '/'){
                file = 0;
                rank++;
            }
            else {
                if (Character.isDigit(symbol)) file += Character.getNumericValue(symbol);
                else {
                    final boolean alliance = Character.isUpperCase(symbol);
                    final Type type = pieceChars.get(Character.toUpperCase(symbol));

                    tileGui[rank][file] = symbol;

                    final Piece piece = placePiece(type,
                            tiles[BoardUtil.tileIndexFromRankFile(file, rank)],
                            alliance,
                            index);
                    tiles[BoardUtil.tileIndexFromRankFile(file, rank)].update(piece);
                    pieces.add(piece);
                    index++;
                    file++;
                }
            }
        }
    }

    public void showBoard(){
        StringBuilder board = new StringBuilder();
        for (int rank = 0; rank < BoardUtil.RANKS; rank++){
            for (int file = 0; file < BoardUtil.FILES; file++){
                int index = BoardUtil.tileIndexFromRankFile(file, rank);
                board.append(tiles[index].occupied ? tiles[index].piece.identifier : "-").append(" ");
            }
            board.append("\n");
        }
        System.out.println(board);
    }

    public void showBoardWithMoves(Piece piece){
        List<Move> moves = piece.getLegals(this);
        StringBuilder board = new StringBuilder();
        for (int rank = 0; rank < BoardUtil.RANKS; rank++){
            for (int file = 0; file < BoardUtil.FILES; file++){
                int index = BoardUtil.tileIndexFromRankFile(file, rank);
                boolean moveFound = false;
                for (Move move : moves){
                    if(move.end == index){
                        board.append("@").append(" ");
                        moveFound = true;
                        break;
                    }
                }
                if (!moveFound) board.append(tiles[index].occupied ? tiles[index].piece.identifier : "-").append(" ");
            }
            board.append("\n");
        }
        System.out.println(board);
    }

    public void makeMove(final Move move, final boolean test){
        for (final Piece piece : pieces) piece.justMoved = false;

        Tile start = tiles[move.start], end = tiles[move.end];
        if (move.taken != null){
            move.taken.tile.update(null);
        }
        start.update(null);
        end.update(move.piece);

        move.piece.tile = end;

        if (test){
            move.piece.prevMoved = move.piece.moved;
            move.piece.prevJustMoved = move.piece.justMoved;
        }

        move.piece.moved = true;
        move.piece.justMoved = true;

        BoardUtil.turn = !BoardUtil.turn;

        if (move.taken != null) move.taken.dead = true;
    }

    public void unMakeMove(final Move move){
        if (move.taken != null) move.taken.dead = false;

        Tile start = tiles[move.start], end = tiles[move.end];
        start.update(move.piece);
        end.update(null);
        if (move.taken != null){
            move.taken.tile.update(move.taken);
        }

        move.piece.moved = move.piece.prevMoved;
        move.piece.justMoved = move.piece.prevJustMoved;

        BoardUtil.turn = !BoardUtil.turn;

        move.piece.tile = start;
    }

    private static Piece placePiece(final Type type, final Tile tile, final boolean alliance, final int index) {
        switch (type){
            case Pawn -> {
                return new Pawn(index, tile, alliance);
            }
            case Bishop -> {
                return new Bishop(index, tile, alliance);
            }
            case Rook -> {
                return new Rook(index, tile, alliance);
            }
            case Knight -> {
                return new Knight(index, tile, alliance);
            }
            case King -> {
                return new King(index, tile, alliance);
            }
            default -> {
                return new Queen(index, tile, alliance);
            }
        }
    }

    private void generateBoard(){
        boolean color = true;
        int i = 0;
        for (int r = 0; r < BoardUtil.RANKS; r++){
            for (int f = 0; f < BoardUtil.FILES; f++){
                tiles[i] = new Tile(i, color);
                tileGui[r][f] = '-';
                i++;
                color = !color;
            }
            color = !color;
        }
    }
}
