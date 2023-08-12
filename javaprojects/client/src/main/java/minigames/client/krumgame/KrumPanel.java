package minigames.client.krumgame;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class KrumPanel extends JPanel {
    KrumGame game;
    public KrumPanel(KrumGame g) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                g.mouseDown(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                g.mouseUp(e);
            }
        });
        this.game = g;        
    }    

    public Dimension getPreferredSize() {
        return new Dimension(800,600);
    }

    @Override
    public void paintComponent(Graphics g) {
        //System.out.println("PAINT");
        super.paintComponent(g);     
        game.draw((Graphics2D)g);
    } 

}
