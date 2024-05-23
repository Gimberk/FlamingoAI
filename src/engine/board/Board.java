package engine.board;

import ai.ZobristHash;
import engine.piece.*;
import gui.AudioManager;
import gui.GameFrame;

import java.util.*;

public class Board {
    public final List<Piece> pieces = new ArrayList<>();
    public final Tile[] tiles = new Tile[64];

    public final boolean whitePlayer;
    public final boolean blackPlayer;

    private final char[][] tileGui = new char[BoardUtil.RANKS][BoardUtil.FILES];

    public final List<Move> history = new ArrayList<>();

    private final int depth;

    public boolean turn = true;

    public final List<Long> repetitionHistory;

    private final ZobristHash hashing = new ZobristHash();

    public int fiftyMoveCounter = 0;
    private int prevMoveCount = fiftyMoveCounter;

    public Board(final String fen, final int depth, final boolean white, final boolean black, final boolean copy){
        repetitionHistory = new ArrayList<>();
        whitePlayer = white; blackPlayer = black;
        this.depth = depth;
        if (!copy){
            BoardUtil.init(this, depth);
        }
        generateBoard();
        loadFEN(fen);

        AudioManager.playStart();
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
        if (!test && turn != move.piece.alliance){
            System.out.println("Not your turn.");
            return;
        }
        for (final Piece piece : pieces) piece.justMoved = false;

        if (move == null) return;

        Tile start = tiles[move.start], end = tiles[move.end];
        if (move.taken != null){
            move.taken.tile.update(null);
            tiles[move.taken.tile.index].update(null);

            if (move.castleK){
                move.taken.tile = tiles[move.start+1];
                tiles[move.start+1].update(move.taken);
            }
            if (move.castleQ){
                move.taken.tile = tiles[move.start-1];
                tiles[move.start-1].update(move.taken);
            }
        }

        start.update(null);
        end.update(move.piece);

        move.piece.tile = end;

        if (test){
            move.piece.prevMoved = move.piece.moved;
            move.piece.prevJustMoved = move.piece.justMoved;
            turn = !turn;
        }

        move.piece.moved = true;
        move.piece.justMoved = true;

        if (move.taken != null && !move.castleK && !move.castleQ) move.taken.dead = true;

        if (move.promotion){
            move.piece.directions = List.of(-9, -8, -7, -1, 1, 7, 8, 9);
            move.piece.type = Type.Queen;
            move.piece.identifier = move.piece.alliance ? 'Q' : 'q';
        }

        history.add(move);
        if (!test && (move.castleK || move.castleQ)){
            if (move.piece.alliance) BoardUtil.whiteCastled = true;
            else BoardUtil.blackCastled = true;
        }
        if (!test) showBoard();

        if (move.piece.type == Type.Pawn || move.isAttackMove()){
            if (!test) repetitionHistory.clear();
            prevMoveCount = fiftyMoveCounter;
            fiftyMoveCounter = 0;
        }

        if (!test){
            repetitionHistory.add(hashing.generateZobristKey(this));
        }

        if (!test){
            if (BoardUtil.isCheckMate(!turn, this)){
                AudioManager.playEnd();
            }
            else{
                turn = !turn;
                if (move.isAttackMove()) AudioManager.playCapture();
                else AudioManager.playMove();

                BoardUtil.switchTurn(this);
            }
        }
    }

    public void unMakeMove(final Move move, final boolean inSearch){
        if (move.taken != null) move.taken.dead = false;

        if (move.promotion){
            move.piece.type = Type.Pawn;
            move.piece.identifier = move.piece.alliance ? 'P' : 'p';
        }

        Tile start = tiles[move.start], end = tiles[move.end];
        start.update(move.piece);
        end.update(null);
        if (move.taken != null && !move.castleK){
            move.taken.tile.update(move.taken);
        }

        move.piece.moved = move.piece.prevMoved;
        move.piece.justMoved = move.piece.prevJustMoved;

        turn = !turn;

        if (move.castleK && move.taken != null){
            move.taken.tile.update(null);
            tiles[move.start+3].update(move.taken);
            move.taken.tile = tiles[move.start+3];
        }
        if (move.castleQ && move.taken != null){
            move.taken.tile.update(null);
            tiles[move.start-4].update(move.taken);
            move.taken.tile = tiles[move.start-4];
        }

        move.piece.tile = start;

        history.remove(move);

        if (!inSearch && !repetitionHistory.isEmpty()){
            repetitionHistory.removeLast();
        }

        fiftyMoveCounter = prevMoveCount;
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

    public String generateFen(){
        StringBuilder fen = new StringBuilder();
        Map<Type, Character> pieceChars = Map.of(
                Type.King, 'K',
                Type.Knight, 'N',
                Type.Pawn, 'P',
                Type.Rook, 'R',
                Type.Queen, 'Q',
                Type.Bishop, 'B');
        int file = 0;
        int space = 0;
        int rank = 0;
        for (final Tile tile : tiles){
            if (tile.occupied){
                if (space != 0) fen.append(space);
                fen.append(tile.piece.alliance ? pieceChars.get(tile.piece.type)
                        : Character.toLowerCase(pieceChars.get(tile.piece.type)));
                space = 0;
            }
            if (file == 7){
                if (space != 0) fen.append(space + 1);
                if (rank < 7) fen.append('/');
                file = 0;
                rank++;
                space = 0;
            }
            else{
                space++;
                file++;
            }

            if (tile.occupied) space = 0;
        }

        return fen.toString();
    }

    public Board copy() {
        Board b = new Board(generateFen(), depth, whitePlayer, blackPlayer, true);
        b.history.addAll(history);
        return b;
    }

    public int getPieceAt(final int index){
        if (!tiles[index].occupied) return 0;

        final Piece piece = tiles[index].piece;
        return piece.alliance ? piece.getHash() : piece.getHash() + 1;
    }
}