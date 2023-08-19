package minigames.client.krumgame;

import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
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
        File backgroundFile = new File(KrumC.imgDir + "chameleon.png");
        backgroundFile = new File(KrumC.imgDir + "ropetestmap.png");
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
        players[0] = new KrumPlayer(235, 0, "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31, true, alphaRaster);
        players[1] = new KrumPlayer(600, 0, "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31, false, alphaRaster);
        playerTurn = 0;
    }

    /**
     * Call this to explode part of the level.
     * 
     * @param x x-coordinate of the centre of the explosion
     * @param y y-coordinate of the centre of the explosion
     */
    void explode(int x, int y, KrumProjectile p) {
        System.out.println("Ex " + p.explosionRadius);
        double z[] = {0};
        for (int i = -(p.explosionRadius); i < p.explosionRadius; i++) {
            if (i + x >= KrumC.RES_X) break;
            if (i + x < 0) continue;
            for (int j = -(p.explosionRadius); j < p.explosionRadius; j++) {
                if (j + y < 0) continue;
                if (j + y >= KrumC.RES_Y) break;
                if (java.lang.Math.sqrt(i * i + j * j) <= p.explosionRadius) {
                    alphaRaster.setPixel(i + x, j + y, z);
                }                    
            }
        }
        for (int i = 0; i < players.length; i++) {
            if (p != null) players[i].knockback(p);  
            if (!players[i].onRope) {
                players[i].airborne = true;                
            }                       
        }
        
        windX = (rand.nextDouble() - 0.5) / 10;
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
                if(p.projectile.collisionCheck()) {
                    explode((int)p.projectile.x, (int)p.projectile.y, p.projectile);
                    p.projectile = null;
                }
                if (p.projectile != null) {
                    int n = p.projectile.playerCollisionCheck(players);
                    if (n >= 0) {
                        explode((int)p.projectile.x, (int)p.projectile.y, p.projectile);
                        p.projectile = null;
                        players[n].hit();
                    }
                }
            }
            if (p.grenade != null) {
                if (p.grenade.timerCheck()) {
                    System.out.println("EX");
                    if (KrumHelpers.distanceBetween(p.grenade.x, p.grenade.y, p.xpos, p.ypos) <= p.grenade.explosionRadius) {
                        p.hit();
                    }
                    explode((int)p.grenade.x, (int)p.grenade.y, p.grenade);
                    p.grenade = null;
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
            if (p.grenade != null) p.grenade.draw(g);
        }        
        g.setColor(Color.red);
        if (windX > 0) g.setColor(Color.blue);        
        g.setFont(new Font("Courier New", 1, 24));
        g.drawString(windString, 300, 25);
    }

    /**
     * Main game loop
     */
    void startGame(){
        for (KrumPlayer p : players) {
            p.setMouseOffsets(panel.getLocationOnScreen().x, panel.getLocationOnScreen().y);
        }
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
        if (e.getButton() == MouseEvent.BUTTON1)
            players[playerTurn].startFire(e);
        else 
            players[playerTurn].startGrenadeFire(e);
    }
    void mouseUp(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            players[playerTurn].endFire(e);
        else 
            players[playerTurn].endGrenadeFire(e);
    }
    void keyDown(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Spacebar
            if (players[playerTurn].airborne) {
                players[playerTurn].fireRope();
            }
            if (players[playerTurn].onRope) {
                players[playerTurn].detachRope();
            }
            else {
                players[playerTurn].startJump(0);
            }            
        }   
        else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { // backspace
            if (players[playerTurn].airborne) {
                players[playerTurn].fireRope();
            }
             if (players[playerTurn].onRope) {
                players[playerTurn].detachRope();
            }
            else {
                players[playerTurn].startJump(1);
            }   
        }  
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            players[playerTurn].leftKeyDownNextFrame = true;            
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            players[playerTurn].rightKeyDownNextFrame = true;       
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            players[playerTurn].enterKeyPressed();
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            players[playerTurn].upArrowKeyDown = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            players[playerTurn].downArrowKeyDown = true;
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
            players[playerTurn].leftKeyDownNextFrame = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            players[playerTurn].rightKeyDownNextFrame = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            players[playerTurn].enterKeyReleased();
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            players[playerTurn].upArrowKeyDown = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            players[playerTurn].downArrowKeyDown = false;
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
                    p.setMouseOffsets(panel.getLocationOnScreen().x, panel.getLocationOnScreen().y);
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
