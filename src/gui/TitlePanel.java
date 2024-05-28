package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TitlePanel extends JPanel {
    public TitlePanel(final GameFrame frame){
        super(new GridLayout(4, 3));
        setSize(frame.titlePanelDimensions);
        setBackground(Color.LIGHT_GRAY);

        JButton playBtn = new JButton("Start or whatevs");
        playBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setup.promptUser();
                //frame.play();
            }
        });

        pad(7);
        add(playBtn, BorderLayout.CENTER);
        pad(4);
        playBtn.setPreferredSize(new Dimension(20,20));
    }

    private void pad(final int count){
        for (int i = 0; i < count; i++) add(new JLabel());
    }
}
