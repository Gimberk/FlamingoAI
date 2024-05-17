package gui;

import engine.board.Board;
import gui.board.BoardPanel;
import gui.board.TilePanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame {
    public final JFrame frame;

    public final Board board;

    public final Color lightTileColor = new Color(224, 186, 135);
    public final Color darkTileColor = new Color(148, 108, 48);
    public final Color lightTileHighLightColor = new Color(212, 174, 123);
    public final Color darkTileHighLightColor = new Color(136, 96, 36);
    public final Color highlightColor = new Color(255, 63, 63);

    private final Dimension frameDimensions = new Dimension(600, 600);
    public final Dimension boardPanelDimensions = new Dimension(400, 600);
    public final Dimension tilePanelDimensions = new Dimension(30, 30);

    public final BoardPanel boardPanel;

    public TilePanel selected = null;

    public GameFrame(final String fen, final int depth, final boolean white, final boolean black){
        board = new Board(fen, depth, white, black);

        boardPanel = new BoardPanel(this);

        frame = createGameFrame();

        frame.add(boardPanel);

        frame.setVisible(true);
    }

    private JFrame createGameFrame(){
        JFrame game = new JFrame("Flamingos are pretty cool I think");
        game.setLayout(new GridLayout(1, 1));

        game.setSize(frameDimensions);
        return game;
    }
}