package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TitlePanel extends JPanel {
    //animation!!!!!!
    private BufferedImage myImage;
    private Graphics myBuffer;
    private final Timer t;
    private final ArrayList<Animatable> animationObjects;

    private final Color bg = Color.GRAY;

    private final GameFrame frame;

    public TitlePanel(final GameFrame frame) {
        this.frame = frame;
        setSize(frame.titlePanelDimensions);
        setBackground(bg);

        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent me){
                frame.setup.promptUser();
            }
        });

        // anima table ig
        myImage = new BufferedImage(frame.titlePanelDimensions.width, frame.titlePanelDimensions.height,
                BufferedImage.TYPE_INT_RGB);
        myBuffer = myImage.getGraphics();
        myBuffer.setColor(bg);
        myBuffer.fillRect(0,0,frame.titlePanelDimensions.width, frame.titlePanelDimensions.height);

        animationObjects = new ArrayList<>();
        animationObjects.add(new TextIG());

        t = new Timer(5, new AnimationListener());
        t.start();
    }

    public void paintComponent(Graphics g)
    {
        g.drawImage(myImage, 0, 0, getWidth(), getHeight(), null);
    }

    public void animate()
    {
        myBuffer.setColor(bg);
        myBuffer.fillRect(0,0,frame.titlePanelDimensions.width, frame.titlePanelDimensions.height);
        for(Animatable animationObject : animationObjects){
            animationObject.step();
            animationObject.drawMe(myBuffer);
        }
        repaint();
    }

    private class AnimationListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            animate();
        }
    }
}

class TextIG implements Animatable{
    private int dy;
    private int x, y;
    public TextIG() {
        dy = 1;
        x = 220; y = 500;
    }

    @Override
    public void step() {
        if (y < 435 || y > 505) dy*=-1;
        y += dy;
    }

    @Override
    public void drawMe(Graphics g) {
        g.setColor(Color.WHITE);
        g.drawString("Click anywhere to continue...", x, y);
    }
}

interface Animatable
{
    void step();
    void drawMe(Graphics g);
}