package gui;

import engine.board.Board;
import gui.board.BoardPanel;

import javax.swing.*;
import java.awt.*;

public class GameFrame {
    public JFrame frame;

    public final Board board;

    public Color lightTileColor = new Color(224, 186, 135);
    public Color darkTileColor = new Color(105, 72, 27);

    private final Dimension frameDimensions = new Dimension(600, 600);
    public final Dimension boardPanelDimensions = new Dimension(400, 600);
    public final Dimension tilePanelDimensions = new Dimension(30, 30);

    public final JPanel boardPanel;

    public GameFrame(final String fen){
        board = new Board(fen);
        board.showBoardWithMoves(board.pieces.getFirst());

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