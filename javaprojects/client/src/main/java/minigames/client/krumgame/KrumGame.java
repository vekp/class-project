package minigames.client.krumgame;

import java.util.Collections;

import io.vertx.core.json.JsonObject;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import java.util.Random;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


public class KrumGame implements GameClient {

    MinigameNetworkClient mnClient;

    GameMetadata gm;

    String player;

    
    final int TARGET_FRAMERATE = 60;
    final long TARGET_FRAMETIME = 1000000000 / TARGET_FRAMERATE;
    final String imgDir = "javaprojects/client/src/main/java/minigames/client/krumgame/";
    KrumPlayer players[];
    int playerTurn;
    double windX;
    double windY;
    BufferedImage background = null;
    WritableRaster alphaRaster;
    KrumPanel panel;
    long lastFrameTime;
    Random rand;
    String windString;
    boolean firstRun;
    boolean running = true;
    private static final Logger logger = LogManager.getLogger(MinigameNetworkClient.class);

    public KrumGame() {
        players = new KrumPlayer[2];
        players[0] = new KrumPlayer(220, 190, imgDir + "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31);
        players[1] = new KrumPlayer(600, 386, imgDir + "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31);
        playerTurn = 0;
        File backgroundFile = new File(imgDir + "chameleon.png");
        //System.out.println(backgroundFile.canRead());
        try {
            background = ImageIO.read(backgroundFile);
        }
        catch (IOException e) {
            System.out.println("error reading background image");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        alphaRaster = background.getAlphaRaster();
        panel = new KrumPanel(this);
        panel.setPreferredSize(panel.getPreferredSize());
        playerTurn = 0;
        windY = 0;
        windX = -0.02;
        rand = new Random();
        windString = "Wind: left 2.00";
        firstRun = true;
    }

    void explode(int x, int y) {
        double z[] = {0};
        for (int i = -20; i < 20; i++) {
            if (i + x >= 800) break;
            if (i + x < 0) continue;
            for (int j = -20; j < 20; j++) {
                if (j + y < 0) continue;
                if (j + y >= 600) break;
                if (java.lang.Math.sqrt(i * i + j * j) <= 20) {
                    alphaRaster.setPixel(i + x, j + y, z);
                }                    
            }
        }
        windX = (rand.nextDouble() - 0.5) / 25;
        windString = "Wind: ";
        windString += windX > 0 ? "right " : "left ";
        windString += Math.round(windX * 10000.0) / 100.0;
    }
    void update() {
        for (KrumPlayer p : players) {
            p.update(windX, windY);
            if (p.projectile != null) {
                if(p.projectile.collisionCheck(alphaRaster)) {
                    explode((int)p.projectile.x, (int)p.projectile.y);
                    p.projectile = null;
                }
                if (p.projectile != null) {
                    int n = p.projectile.playerCollisionCheck(players);
                    if (n >= 0) {
                        explode((int)p.projectile.x, (int)p.projectile.y);
                        p.projectile = null;
                        players[n].hit();
                    }
                }
            }
        }
    }
    void draw(Graphics2D g) {
        g.drawImage(background, null, 0, 0);
        for (KrumPlayer p : players) {
            p.draw(g);
            if (p.projectile != null) p.projectile.draw(g);
        }        
        g.setFont(new Font("Courier New", 1, 24));
        g.drawString(windString, 300, 25);
    }
    /*void start(){
        //if (!SwingUtilities.isEventDispatchThread())
        logger.info("GUI " + (SwingUtilities.isEventDispatchThread() ? "" : "not ") + "created on EDT");
        //JFrame f = new JFrame("arty");
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        //f.add(panel);
        //f.pack();
        //f.setVisible(true);  
        System.out.println(panel.getLocationOnScreen());        
    }*/
    void startGame(){
        //if (!SwingUtilities.isEventDispatchThread())
        System.out.println("game " + (SwingUtilities.isEventDispatchThread() ? "" : "not ") + "created on EDT");
        lastFrameTime = System.nanoTime();
        while (running) {
            if (System.nanoTime() - lastFrameTime >= TARGET_FRAMETIME) {
                lastFrameTime = System.nanoTime();
                update();
                panel.repaint();                
            }            
        }
    }
    void mouseDown(MouseEvent e){
        players[playerTurn].startFire(e);
    }
    void mouseUp(MouseEvent e) {
        players[playerTurn].endFire(e);
    }

    /** 
     * Sends a command to the game at the server.
     */
    public void sendCommand(String weapon, double angle, double power) {
        JsonObject json = new JsonObject().put("weapon", weapon).put("angle", angle).put("power", power);

        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }
 
    @Override
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {

        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
        mnClient.getMainWindow().getFrame().addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                for (KrumPlayer p : players) {
                    p.xoff = panel.getLocationOnScreen().x;
                    p.yoff = panel.getLocationOnScreen().y;
                    System.out.println(panel.getLocationOnScreen());
                }
            }
        });
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(panel);
        mnClient.getMainWindow().pack();
        Thread gameThread = new Thread() {
            public void run(){
                startGame();
            }
        };
        gameThread.start();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        System.out.println("command received" + command + "metadata: " + game);        
    }

    @Override
    public void closeGame() {
               
    }
    
}
