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

/**
 * The panel containing our game
 */
public class KrumPanel extends JPanel {
    boolean gameActive;
    KrumGame game;
    KeyEventDispatcher eventDispatcher;
    public KrumPanel(KrumGame g) {
        gameActive = false;
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
                 g.keyDown(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                g.keyUp(e);
            }
        });


        // Ensure keyboard events reach this panel
        JPanel t = this;
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        eventDispatcher = new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if(focusManager.getFocusOwner()!=t){
                                focusManager.redispatchEvent(t,e);
                                return gameActive;}
                else return false;
            }
        };
        focusManager.addKeyEventDispatcher(eventDispatcher);
               
        this.setFocusable(true);
        
        this.game = g;        
    }    

    public void removeEventDispatcher() {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.removeKeyEventDispatcher(eventDispatcher);
    }

    public Dimension getPreferredSize() {
        return new Dimension(KrumC.RES_X,KrumC.RES_Y);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);     
        game.draw((Graphics2D)g);
    } 

}
