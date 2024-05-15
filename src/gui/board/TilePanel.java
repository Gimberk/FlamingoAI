package gui.board;

import engine.board.BoardUtil;
import engine.board.Tile;
import engine.piece.Move;
import engine.piece.Type;
import gui.GameFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static engine.board.BoardUtil.*;

public class TilePanel extends JPanel {
    public final Tile tile;

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
        String imgPath = "assets/piece/neo/";
        imgPath += switch (tile.piece.type) {
            case Queen -> "queen";
            case Rook -> "rook";
            case King -> "king";
            case Pawn -> "pawn";
            case Knight -> "knight";
            case Bishop -> "bishop";
        };

        imgPath += tile.piece.alliance ? "_w" : "_b";
        imgPath += ".png";

        try{
            BufferedImage img = ImageIO.read(new File(imgPath));
            final int size = 63;
            Image scaledImage = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImage));
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

    private void mouseRelease(MouseEvent me){
        if (turn && !frame.board.whitePlayer) return;
        else if (!turn && !frame.board.blackPlayer) return;

        if (me.getButton() == 3) frame.selected = null;
        else if (tile.occupied && tile.piece.alliance == turn) frame.selected = this;
        else if (frame.selected != null) {
            final Move move = new Move(frame.selected.tile.index, tile.index, frame.selected.tile.piece, tile.piece);
            final List<Move> moves = frame.selected.tile.piece.getLegals(frame.board);
            if (BoardUtil.movesContains(moves, move)){
                removeAll();
                frame.selected.removeAll();
                frame.board.makeMove(move, false);
                for (final TilePanel t : frame.boardPanel.tiles) t.update();
                setBackground(color ? frame.lightTileColor : frame.darkTileColor);
                if (tile.occupied && frame.selected != this){
                    for (final TilePanel t : frame.boardPanel.tiles) t.setBackground(t.color ? frame.lightTileColor : frame.darkTileColor);
                }
                frame.selected.revalidate();
                frame.selected.repaint();
                frame.selected = null;
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
}