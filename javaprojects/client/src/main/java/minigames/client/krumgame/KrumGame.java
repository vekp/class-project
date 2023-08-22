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
import io.vertx.core.json.JsonObject;
import java.util.Random;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Collections;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.commands.CommandPackage;

import java.awt.geom.Point2D;

import minigames.client.krumgame.components.Background;
/**
 * The state of each running game will be represented by an instance of KrumGame on the server and an instance on each participating client
 */
public class KrumGame implements GameClient {

    // Networking
    MinigameNetworkClient mnClient;

    // Game metadata and state
    GameMetadata gm;
    KrumPlayer players[];
    int playerTurn;
    boolean turnOver = false;
    boolean readyToStartTurn = false;

    // Player information
    String player;

    // Wind properties
    double windX;
    double windY;
    String windString;

    // Graphic and rendering
    Background backgroundComponent;
    BufferedImage background = null;
    WritableRaster alphaRaster;
    KrumPanel panel;
    long lastFrameTime;

    // Random number generator
    Random rand;

    // Game loop and update controls
    boolean firstRun;
    boolean running = true;
    long updateCount = 0;

    // Turn recording and playback
    boolean startRecordingTurn = false;
    boolean recordingTurn = false;
    boolean playBackTurn = false;
    boolean stopRecordingTurn = false;
    boolean playingBackTurn = false;
    int playBackFrame = 0;
    long turnOverFrame;
    long turnEndFrame;
    KrumTurn[] savedTurns;
    KrumTurn currentTurn;
    final int MAX_TURN_GAP_FRAMES = 600;

    // Explosions details for the draw loop
    ArrayList<ExplosionDetails> explosions;

    public KrumGame() {  
        rand = new Random(); 
        firstRun = true;
        updateCount = 0;
        // Initializing the background image
        //backgroundComponent = new Background("chameleon.png");
        backgroundComponent = new Background("ropetestmap.png");
        background = backgroundComponent.getImage();
        alphaRaster = backgroundComponent.getAlphaRaster();
        initializePanel();
        initializePlayers();
        initializeWind();
        startTurn();

        explosions = new ArrayList<ExplosionDetails>();
    }
/*
    private void initializeBackground(){
        //background = KrumHelpers.readSprite("chameleon.png");
        background = KrumHelpers.readSprite("ropetestmap.png");
        alphaRaster = background.getAlphaRaster();
    }
*/
    private void initializePanel(){
        panel = new KrumPanel(this);
        panel.setPreferredSize(panel.getPreferredSize());
    }

    private void initializePlayers(){
        players = new KrumPlayer[2];
        players[0] = new KrumPlayer(235, 0, "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31, true, alphaRaster, 0, players);
        players[1] = new KrumPlayer(600, 0, "kangaroo_sprite/kangaroo_bazooka_0.png", 8, 31, false, alphaRaster, 1, players);
        players[0].joey.otherPlayer = players[1];
        players[1].joey.otherPlayer = players[0];
        playerTurn = 0;
        savedTurns = new KrumTurn[] {new KrumTurn(players, background, windX, windY, updateCount), new KrumTurn(players, background, windX, windY, updateCount)};       
        currentTurn = savedTurns[playerTurn];
    }

    private void initializeWind(){
        windY = 0;
        windX = -0.02;
        windString = "Wind: left 2.00";
    }
    /*
     * Represents the details of an explosion, for use in the draw loop
     */
    class ExplosionDetails {
        int x;
        int y;
        long endFrame;
        int radius;
        ExplosionDetails(int x,int y, long f, int r){
            this.x = x;
            this.y = y;
            this.endFrame = f;
            this.radius = r;
        }
    }

    /*
     * Called to start a new turn, shifting control to the other player
     */
    void startTurn() {
        System.out.println("Start turn");
        windX = (rand.nextDouble() - 0.5) / 10;
        windString = "Wind: ";
        windString += windX > 0 ? "right " : "left ";
        windString += Math.round(windX * 10000.0) / 100.0;
        turnEndFrame = updateCount + KrumC.TURN_TIME_LIMIT_FRAMES;
        savedTurns[playerTurn] = new KrumTurn(players, background, windX, windY, updateCount);
        currentTurn = savedTurns[playerTurn];
        recordingTurn = true;
        turnOver = false;
    }

    /*
     * Called when turn time has expired. Next turn won't be started until
     * projectiles have exploded or fallen off the bottom of the screen, and 
     * players are no longer airborne.
     */
    void endTurn() {
        for (KrumPlayer p : players)
            p.stop();
        recordingTurn = false;
        currentTurn.endState = new KrumGameState(players, background, windX, windY, updateCount);
        playerTurn = 1 - playerTurn;        
        turnOver = true;
        readyToStartTurn = false;
        turnOverFrame = updateCount;
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
        ExplosionDetails newex = new ExplosionDetails(x, y, updateCount + 10, p.explosionRadius);            
        explosions.add(newex);
    }

    /*
     * Loads state from a KrumGameState object
     */
    void setGameState(KrumGameState state) {
        alphaRaster.setDataElements(0,0,background.getWidth(),background.getHeight(),state.pixelMatrix);
        updateCount = state.startTick;
        turnEndFrame = state.endTick;
        windX = state.windX;
        windY = state.windY;
        for (int i = 0; i < Math.min(players.length, state.playerStates.size()); i++) {
            KrumPlayerState ps = state.playerStates.get(i);
            players[i].hp = ps.hp;
            players[i].xpos = ps.xpos;
            players[i].xvel = ps.xvel;
            players[i].yvel = ps.yvel;
            players[i].ypos = ps.ypos;
            players[i].leftKeyDown = ps.leftKeyDown;
            players[i].rightKeyDown = ps.rightKeyDown;
            players[i].upArrowKeyDown = ps.upArrowKeyDown;
            players[i].downArrowKeyDown = ps.downArrowKeyDown;
            players[i].firing = ps.firing;
            players[i].firingGrenade = ps.firingGrenade;
            players[i].fireStart = ps.fireStart;
            players[i].fireGrenadeStart = ps.fireGrenadeStart;
            players[i].grenadeSeconds = ps.grenadeSeconds;
            players[i].flashFramesLeft = ps.flashFramesLeft;
            players[i].airborne = ps.airborne;
            players[i].jumping = ps.jumping;
            players[i].jumpStart = ps.jumpStart;
            players[i].walking = ps.walking;
            players[i].walkedOffEdge = ps.walkedOffEdge;
            players[i].jumpType = ps.jumpType;
            players[i].firstJumpFrame = ps.firstJumpFrame;
            players[i].deferredLanding = ps.deferredLanding;
            players[i].lastShotTime = ps.lastShotTime;
            players[i].lastGrenadeShotTime = ps.lastGrenadeShotTime;
            players[i].shootingRope = ps.shootingRope;
            players[i].onRope = ps.onRope;
            players[i].ropeAttachmentPoints = new ArrayList<Point2D.Double>(ps.ropeAttachmentPoints);
            players[i].ropeLength = ps.ropeLength;
            players[i].ropeAngleRadians = ps.ropeAngleRadians;
            players[i].onRopeSpeed = ps.onRopeSpeed;
            players[i].wasOnRope = ps.wasOnRope;
            players[i].shotPower = ps.shotPower;
            players[i].shootNextFrame = ps.shootNextFrame;
            players[i].grenadePower = ps.grenadePower;
            players[i].grenadeNextFrame = ps.grenadeNextFrame;
            players[i].detachRopeNextFrame = ps.detachRopeNextFrame;
            players[i].shootRopeNextFrame = ps.shootRopeNextFrame;
            players[i].jumpPower = ps.jumpPower;
            players[i].jumpNextFrame = ps.jumpNextFrame;
            players[i].grenadeAimAngle = ps.grenadeAimAngle;
            players[i].shootAimAngle = ps.shootAimAngle;
            players[i].ropeAimAngle = ps.ropeAimAngle;
            players[i].ropeSegmentLengths = new ArrayList<Double>(ps.ropeSegmentLengths);
            players[i].setDirection(ps.facingRight, null);
        }
    }

    /**
     * Called once per frame to update game state
     */
    void update() {
        if (playBackTurn) {
            playingBackTurn = true;
            recordingTurn = false;
            playBackTurn = false;
            playBackFrame = 0;
            currentTurn = savedTurns[1 - playerTurn];
            setGameState(currentTurn.startState);
            System.out.println("playback " + currentTurn);
            playerTurn = 1 - playerTurn;
        } 
        else if (!playingBackTurn && updateCount > turnEndFrame && !turnOver) {
            endTurn();
        }       
        readyToStartTurn = true;
        for (KrumPlayer p : players) {    
            KrumInputFrame pf = null;
            KrumTurn rt = null;
            if (p.playerIndex == playerTurn && recordingTurn) {
                rt = currentTurn;
            }  
            if (playingBackTurn) {
                if (playBackFrame >= currentTurn.frames.size()) {
                    playingBackTurn = false;
                    System.out.println("done replaying");
                    endTurn();
                }
                else if (p == players[currentTurn.frames.get(playBackFrame).activePlayer]) {
                    pf = currentTurn.frames.get(playBackFrame);
                    playBackFrame++;
                }                
            }  
            p.update(windX, windY, alphaRaster, updateCount, rt, pf, turnOver);
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
                if (p.grenade.timerCheck(updateCount)) {
                    System.out.println("EX");
                    for (KrumPlayer pl : players) {
                        if (KrumHelpers.distanceBetween(p.grenade.x, p.grenade.y, pl.playerCentre().x, pl.playerCentre().y) <= p.grenade.explosionRadius) {
                            pl.hit();
                        }
                    }
                    explode((int)p.grenade.x, (int)p.grenade.y, p.grenade);
                    p.grenade = null;
                }
            }
            if (p.joey.active) {
                if (p.joey.timerCheck(updateCount)) {
                    for (KrumPlayer pl : players) {
                        if (KrumHelpers.distanceBetween(p.joey.xpos, p.joey.ypos, pl.playerCentre().x, pl.playerCentre().y) <= p.joey.explosionRadius) {
                            pl.hit();
                        }
                    }
                    explode((int)p.joey.xpos, (int)p.joey.ypos, p.joey);
                    p.joey.active = false;
                }
            }
            if (turnOver && readyToStartTurn) {
                if (p.projectile != null && p.projectile.y < KrumC.RES_Y)
                    readyToStartTurn = false;
                else if (p.grenade != null)
                    readyToStartTurn = false;
                else if (p.joey.active) 
                    readyToStartTurn = false;
                else if (p.airborne)
                    readyToStartTurn = false;
            }            
        }   
        updateCount++;
        if (turnOver) {
            if (readyToStartTurn || updateCount - turnOverFrame > MAX_TURN_GAP_FRAMES)
                startTurn();
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
            if (p.joey.active) p.joey.draw(g);
        }        
        g.setColor(Color.red);
        if (windX > 0) g.setColor(Color.blue);        
        g.setFont(new Font("Courier New", 1, 24));
        g.drawString(windString, 300, 25);
        g.setFont(new Font("Courier New", 1, 26));
        String timerString = "";
        if (turnEndFrame - updateCount > KrumC.TARGET_FRAMERATE * 3) {
            g.setColor(Color.green);
            timerString += Math.round((turnEndFrame - updateCount) / (double)KrumC.TARGET_FRAMERATE);
        }
        else if (turnEndFrame - updateCount > 0) {
            g.setColor(Color.orange);
            timerString += Math.round((turnEndFrame - updateCount) / (double)KrumC.TARGET_FRAMERATE * 10.0) / 10.0;
        }
        else {
            g.setColor(Color.red);
            timerString += "0";
        }
        g.drawString(timerString, 5, 20);
        if (playingBackTurn) {
            g.setColor(Color.gray);
            g.drawString("REPLAY", 325, 60);
        }
        g.setColor(Color.red);
        for (int i = 0; i < explosions.size(); i++) {
            ExplosionDetails e = explosions.get(i);
            int r = e.radius - (int)(e.endFrame - updateCount) / 2;
            g.fillOval(e.x -r, e.y - r, r * 2, r * 2);
            if (updateCount >= e.endFrame) {
                explosions.remove(e);
                i--;
            }                
        }
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
                players[playerTurn].shootRopeNextFrame = true;
            }
            if (players[playerTurn].onRope) {
                players[playerTurn].detachRopeNextFrame = true;
            }
            else {
                players[playerTurn].startJump(0);
            }            
        }   
        else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) { // backspace
            if (players[playerTurn].airborne) {
                players[playerTurn].shootRopeNextFrame = true;
            }
             if (players[playerTurn].onRope) {
                players[playerTurn].detachRopeNextFrame = true;
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
            players[playerTurn].enterKeyDownNextFrame = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            players[playerTurn].upArrowKeyDownNextFrame = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            players[playerTurn].downArrowKeyDownNextFrame = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_R) {
            playBackTurn = true;
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
            players[playerTurn].enterKeyDownNextFrame = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            players[playerTurn].upArrowKeyDownNextFrame = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            players[playerTurn].downArrowKeyDownNextFrame = false;
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
/*
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
*/
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
