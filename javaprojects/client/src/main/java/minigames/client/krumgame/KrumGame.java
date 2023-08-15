package minigames.client.krumgame;

import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import io.vertx.core.json.JsonObject;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.util.Collections;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

/**
 * The state of each running game will be represented by an instance of KrumGame on the server and an instance on each participating client
 */
public class KrumGame implements GameClient {
    MinigameNetworkClient mnClient;
    GameMetadata gm;
    String player;   
    final String imgDir = "client/src/main/java/minigames/client/krumgame/";
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

    public KrumGame() {        
        File backgroundFile = new File(imgDir + "chameleon.png");
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
        players = new KrumPlayer[2];
        players[0] = new KrumPlayer(235, 0, imgDir + "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31, true, alphaRaster);
        players[1] = new KrumPlayer(600, 0, imgDir + "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31, false, alphaRaster);
        playerTurn = 0;
    }

    /**
     * Call this to explode part of the level.
     * 
     * @param x x-coordinate of the centre of the explosion
     * @param y y-coordinate of the centre of the explosion
     */
    void explode(int x, int y) {
        double z[] = {0};
        for (int i = -20; i < 20; i++) {
            if (i + x >= 800) break;
            if (i + x < 0) continue;
            for (int j = -20; j < 20; j++) {
                if (j + y < 0) continue;
                if (j + y >= KrumC.RES_Y) break;
                if (java.lang.Math.sqrt(i * i + j * j) <= 20) {
                    alphaRaster.setPixel(i + x, j + y, z);
                }                    
            }
        }
        players[0].airborne = true;
        players[1].airborne = true;
        windX = (rand.nextDouble() - 0.5) / 25;
        windString = "Wind: ";
        windString += windX > 0 ? "right " : "left ";
        windString += Math.round(windX * 10000.0) / 100.0;
    }

    /**
     * Called once per frame to update game state
     */
    void update() {
        for (KrumPlayer p : players) {
            p.update(windX, windY, alphaRaster);
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

    /**
     * Draw loop triggered indirectly by a call to repaint() 
     * @param g
     */
    void draw(Graphics2D g) {
        g.drawImage(background, null, 0, 0);
        for (KrumPlayer p : players) {
            p.draw(g);
            if (p.projectile != null) p.projectile.draw(g);
        }        
        g.setFont(new Font("Courier New", 1, 24));
        g.drawString(windString, 300, 25);
    }

    /**
     * Main game loop
     */
    void startGame(){
        System.out.println("game " + (SwingUtilities.isEventDispatchThread() ? "" : "not ") + "created on EDT");
        lastFrameTime = System.nanoTime();
        while (running) {
            if (System.nanoTime() - lastFrameTime >= KrumC.TARGET_FRAMETIME) {
                lastFrameTime = System.nanoTime();
                update();
                panel.repaint();                
            }            
        }
    }

    // mouse and key Down/Up functions are triggered via the listeners in KrumPanel
    void mouseDown(MouseEvent e){
        players[playerTurn].startFire(e);
    }
    void mouseUp(MouseEvent e) {
        players[playerTurn].endFire(e);
    }
    void keyDown(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Spacebar
            players[playerTurn].startJump(0);
        }   
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            players[playerTurn].walking = true;
            players[playerTurn].setDirection(false, alphaRaster);
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            players[playerTurn].walking = true;
            players[playerTurn].setDirection(true, alphaRaster);            
        }
    }
    void keyUp(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Spacebar
            players[playerTurn].endJump(0);
        }  
        else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { // backspace
            players[playerTurn].endJump(1);
        } 
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            players[playerTurn].walking = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            players[playerTurn].walking = false;
        }
    }

    /** 
     * Sends a command to the game at the server.
     */
    public void sendCommand(String weapon, double angle, double power) {
        JsonObject json = new JsonObject().put("weapon", weapon).put("angle", angle).put("power", power);
        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(json)));
    }

    /**
     * Called when our client is loaded into the main screen
     */ 
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
        // todo: make sure we don't leave any mess
        // should probably remove the component listener we added to the main jframe?
    }

    //Getters and setters:
    public MinigameNetworkClient getMnClient() {
        return mnClient;
    }

    public void setMnClient(MinigameNetworkClient mnClient) {
        this.mnClient = mnClient;
    }

    public GameMetadata getGm() {
        return gm;
    }

    public void setGm(GameMetadata gm) {
        this.gm = gm;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getImgDir() {
        return imgDir;
    }

    public KrumPlayer[] getPlayers() {
        return players;
    }

    public void setPlayers(KrumPlayer[] players) {
        this.players = players;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public double getWindX() {
        return windX;
    }

    public void setWindX(double windX) {
        this.windX = windX;
    }

    public double getWindY() {
        return windY;
    }

    public void setWindY(double windY) {
        this.windY = windY;
    }

    public BufferedImage getBackground() {
        return background;
    }

    public void setBackground(BufferedImage background) {
        this.background = background;
    }

    public WritableRaster getAlphaRaster() {
        return alphaRaster;
    }

    public void setAlphaRaster(WritableRaster alphaRaster) {
        this.alphaRaster = alphaRaster;
    }

    public KrumPanel getPanel() {
        return panel;
    }

    public void setPanel(KrumPanel panel) {
        this.panel = panel;
    }

    public long getLastFrameTime() {
        return lastFrameTime;
    }

    public void setLastFrameTime(long lastFrameTime) {
        this.lastFrameTime = lastFrameTime;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }

    public String getWindString() {
        return windString;
    }

    public void setWindString(String windString) {
        this.windString = windString;
    }

    public boolean isFirstRun() {
        return firstRun;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
    
}
