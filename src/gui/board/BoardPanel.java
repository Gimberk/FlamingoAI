package gui.board;

import engine.board.Tile;
import gui.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {
    public final List<TilePanel> tiles = new ArrayList<>();

    private final GameFrame frame;

    public BoardPanel(final GameFrame frame){
        super(new GridLayout(8, 8));

        this.frame = frame;
        setSize(frame.boardPanelDimensions);
        setBackground(frame.lightTileColor);

        generateTiles();
    }

    private void generateTiles() {
        boolean color = true;
        for (int i = 0; i < 64; i++){
            final TilePanel panel = new TilePanel(frame.board.tiles[i], color, frame);
            add(panel);
            tiles.add(panel);
            if ((i+1) % 8 != 0) color = !color;
        }
    }
}