package minigames.client.krumgame;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;

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
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println(e);
                g.keyDown(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                g.keyUp(e);
            }
        });
        JPanel t = this;
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if(focusManager.getFocusOwner()!=t){
                                focusManager.redispatchEvent(t,e);
                                return true;}
                else return false;
            }
        });
        this.setFocusable(true);
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
