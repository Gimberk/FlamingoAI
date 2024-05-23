package gui;

import engine.board.Board;
import engine.board.PGNWriter;
import gui.board.BoardPanel;
import gui.board.TilePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class GameFrame {
    public final JFrame frame;

    public Board board;

    public EndGameScreen endGame;

    public final Color lightTileColor = new Color(224, 186, 135);
    public final Color darkTileColor = new Color(148, 108, 48);
    public final Color lightTileHighLightColor = new Color(212, 174, 123);
    public final Color darkTileHighLightColor = new Color(136, 96, 36);
    public final Color highlightColor = new Color(255, 63, 63);

    private final Dimension frameDimensions = new Dimension(600, 600);
    public final Dimension boardPanelDimensions = new Dimension(400, 600);
    public final Dimension tilePanelDimensions = new Dimension(30, 30);

    public final BoardPanel boardPanel;

    public final int depth;

    public TilePanel selected = null;

    public final boolean white, black;

    public GameFrame(final String fen, final int depth, final boolean white, final boolean black){
        board = new Board(fen, depth, white, black, false);
        this.depth = depth;
        this.white = white; this.black = black;
        boardPanel = new BoardPanel(this);

        frame = createGameFrame();

        frame.add(boardPanel);
        frame.setJMenuBar(createMenuBar());

        frame.setVisible(true);
    }

    private JFrame createGameFrame(){
        final JFrame game = new JFrame("Flamingos are pretty cool I think (JFlamingo)");
        game.setLayout(new GridLayout(1, 1));

        game.setSize(frameDimensions);
        return game;
    }

    private JMenuBar createMenuBar(){
        final JMenuBar menuBar = new JMenuBar();
        final JMenu menu = new JMenu("File");
        final JMenuItem m1 = new JMenuItem("Load FEN");
        final JMenuItem m2 = new JMenuItem("Save as FEN");
        final JMenuItem m3 = new JMenuItem("Exit");

        m1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    board = new Board(PGNWriter.readFen("saves/FEN.txt"), depth, white, black, false);
                    for (final TilePanel p : boardPanel.tiles){
                        p.tile = board.tiles[p.tile.index];
                        p.removeAll();
                        p.update();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        m2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PGNWriter.saveFen("saves/FEN.txt", board.generateFen(), board);
                    Runtime.getRuntime().exec("explorer.exe /selection, saves");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        m3.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menu.add(m1);
        menu.add(m2);
        menu.add(m3);
        menuBar.add(menu);
        return menuBar;
    }
}