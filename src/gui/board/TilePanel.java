package gui.board;

import engine.board.Board;
import engine.board.BoardUtil;
import engine.board.PGNWriter;
import engine.board.Tile;
import engine.piece.Move;
import engine.piece.Type;
import gui.GameFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TilePanel extends JPanel implements KeyListener {
    public Tile tile;

    private final GameFrame frame;
    private final boolean color;

    public TilePanel(final Tile tile, final boolean color, final GameFrame frame){
        this.tile = tile;
        this.frame = frame;
        this.color = color;

        update();

        setSize(frame.tilePanelDimensions);
        setBackground(color ? frame.lightTileColor : frame.darkTileColor);

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent me) {
                mouseEnter();
            }

            public void mouseExited(MouseEvent me){
                mouseExit();
            }

            public void mouseReleased(MouseEvent me){
                mouseRelease(me);
            }
        });
    }

    public void update(){
        if (!tile.occupied) return;
        String imgPath = "assets/piece/" + frame.pieceSet + "/";
        imgPath += tile.piece.alliance ? "w" : "b";
        imgPath += switch (tile.piece.type) {
            case Queen -> "q";
            case Rook -> "r";
            case King -> "k";
            case Pawn -> "p";
            case Knight -> "n";
            case Bishop -> "b";
        };
        imgPath += ".png";

        try{
            BufferedImage img = ImageIO.read(new File(imgPath));
            int size = 63;
            Image scaledImage = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImage));
            //JLabel label = new JLabel(tile.occupied ? String.valueOf(tile.piece.type) : String.valueOf(tile.index));
            removeAll();
            add(label);
            repaint();
            revalidate();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            System.out.println("Invalid Piece set: " + imgPath);
        }
    }

    private void mouseRelease(MouseEvent me) {
        if (frame.board.turn && !frame.board.whitePlayer) return;
        else if (!frame.board.turn && !frame.board.blackPlayer) return;

        if (me.getButton() == 3) frame.selected = null;
        else if (tile.occupied && tile.piece.alliance == frame.board.turn) frame.selected = this;
        else if (frame.selected != null) {
            Move move = new Move(frame.selected.tile.index, tile.index, frame.selected.tile.piece, tile.piece);
            final List<Move> moves = frame.selected.tile.piece.getLegals(frame.board);
            if (BoardUtil.movesContains(moves, move)){
                for (final Move m : moves) if (m.equals(move)) move = m;
                removeAll();
                frame.selected.removeAll();
                frame.board.makeMove(move, false);
                for (final TilePanel t : frame.boardPanel.tiles){
                    t.removeAll();
                    t.update();
                    t.revalidate(); t.repaint();
                }
                setBackground(color ? frame.lightTileColor : frame.darkTileColor);
                if (tile.occupied && frame.selected != this){
                    for (final TilePanel t : frame.boardPanel.tiles) t.setBackground(t.color ? frame.lightTileColor : frame.darkTileColor);
                }
                frame.selected.revalidate(); frame.selected.repaint(); frame.selected = null;

                for (final TilePanel p : frame.boardPanel.tiles){
                    p.removeAll();
                    p.update();
                    p.repaint();
                    p.revalidate();
                }
            }
        }
    }

    private void mouseEnter(){
        if (getBackground() != frame.highlightColor) setBackground(color ? frame.lightTileHighLightColor : frame.darkTileHighLightColor);
        if (tile.occupied && frame.selected == null){
            setBackground(color ? frame.lightTileHighLightColor : frame.darkTileHighLightColor);
            for (final Move move : tile.piece.getLegals(frame.board)){
                frame.boardPanel.tiles.get(move.end).setBackground(frame.highlightColor);
            }
        }
    }

    private void mouseExit(){
        if (getBackground() != frame.highlightColor) setBackground(color ? frame.lightTileColor : frame.darkTileColor);
        if (tile.occupied && frame.selected == null){
            setBackground(color ? frame.lightTileColor : frame.darkTileColor);
            for (final TilePanel t : frame.boardPanel.tiles) t.setBackground(t.color ? frame.lightTileColor : frame.darkTileColor);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == 'l'){
            try {
                frame.board = new Board(PGNWriter.readFen("saves/FEN.txt"), frame.depth, frame.white, frame.black, false);
                for (final TilePanel p : frame.boardPanel.tiles){
                    p.tile = frame.board.tiles[p.tile.index];
                    p.removeAll();
                    p.update();
                    p.revalidate();
                    p.repaint();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        else if (e.getKeyChar() == 's'){
            try {
                PGNWriter.saveFen("saves/FEN.txt", frame.board.generateFen(), frame.board);
                Runtime.getRuntime().exec("explorer.exe /selection, saves");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}