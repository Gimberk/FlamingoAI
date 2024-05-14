package gui.board;

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

public class TilePanel extends JPanel {
    public final Tile tile;

    private final GameFrame frame;
    private final boolean color;

    private final List<TilePanel> highlights = new ArrayList<>();

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
            add(label);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            System.out.println("Invalid Piece set: " + imgPath);
        }
    }

    private void mouseEnter(){
        if (tile.occupied){
            for (final Move move : tile.piece.getLegals(frame.board)){
                frame.boardPanel.tiles.get(move.end).setBackground(frame.highlightColor);
                highlights.add(frame.boardPanel.tiles.get(move.end));
            }
        }
        else setBackground(color ? frame.lightTileHighLightColor : frame.darkTileHighLightColor);
    }

    private void mouseExit(){
        setBackground(color ? frame.lightTileColor : frame.darkTileColor);
        if (tile.occupied){
            for (final TilePanel t : highlights) t.setBackground(t.color ? frame.lightTileColor : frame.darkTileColor);
            highlights.clear();
        }
    }
}