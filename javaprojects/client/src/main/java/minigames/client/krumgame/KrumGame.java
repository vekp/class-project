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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.ArrayList;
import java.util.Collections;
import minigames.client.GameClient;
import minigames.client.MinigameNetworkClient;
import minigames.rendering.GameMetadata;
import minigames.rendering.NativeCommands.LoadClient;
import minigames.rendering.NativeCommands.QuitToMenu;
import minigames.rendering.NativeCommands.ShowMenuError;
import minigames.rendering.RenderingPackage;
import minigames.commands.CommandPackage;
import minigames.krumgame.KrumInputFrame;

import java.util.List;
import java.util.Optional;
import java.awt.geom.Point2D;

import minigames.krumgame.KrumInputFrame;

import minigames.client.krumgame.components.*;
/**
 * The state of each running game will be represented by an instance of KrumGame on the server and an instance on each participating client
 */
public class KrumGame implements GameClient {

    int myPlayerIndex;

    ArrayList<KrumInputFrame> receivedFrames;

    // Networking
    MinigameNetworkClient mnClient;

    // Game metadata and state
    GameMetadata gm;
    KrumPlayer players[];
    int playerTurn;
    boolean turnOver = false;
    boolean readyToStartTurn = false;
    boolean ending = false;
    int winner = -1;
    double waterLevel;

    // Player information
    String player;

    // Wind properties
    WindManager windManager;
    double windX;
    double windY;
    String windString;

    // Graphic and rendering
    Background backgroundComponent;
    BufferedImage background = null;
    WritableRaster alphaRaster;
    KrumPanel panel;
    long lastFrameTime;

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

    final int MIN_PLAYBACK_BUFFER = 1;

    long seed; // seed for Random() -- needs to be the same for all clients clients

    public KrumGame() {  
        receivedFrames = new ArrayList<KrumInputFrame>();
        //rand = new Random(); 
        firstRun = true;
        updateCount = 0;
        waterLevel = KrumC.RES_Y - 1;
        // Initializing the background image
        //backgroundComponent = new Background("chameleon.png");
        backgroundComponent = new Background("ropetestmap.png");
        background = backgroundComponent.getImage();
        alphaRaster = backgroundComponent.getAlphaRaster();

        initializePanel();
        initializePlayers();
        //initializeWind();     
        windString = "";

    }

    private void initializePanel(){
        panel = new KrumPanel(this);
        panel.setPreferredSize(panel.getPreferredSize());
    }

    private void initializePlayers(){
        players = new KrumPlayer[2];
        players[0] = new KrumPlayer(235, 0, "kangaroo_sprite/", 8, 31, true, alphaRaster, 0, players);
        players[1] = new KrumPlayer(600, 0, "kangaroo_sprite/", 8, 31, false, alphaRaster, 1, players);
        players[0].joey.otherPlayer = players[1];
        players[1].joey.otherPlayer = players[0];
        playerTurn = 0;
        savedTurns = new KrumTurn[] {new KrumTurn(players, background, windX, windY, updateCount, ending, running, winner, waterLevel), new KrumTurn(players, background, windX, windY, updateCount, ending, running, winner, waterLevel)};       
        currentTurn = savedTurns[playerTurn];
    }

    /** 
     * Called to start a new turn, shifting control to the other player
     */
    void startTurn() {
        System.out.println("Start turn");
        playerTurn *= -1;
        windString = windManager.updateWindString();
        windX = windManager.getWindX();
        turnEndFrame = updateCount + KrumC.TURN_TIME_LIMIT_FRAMES;
        savedTurns[playerTurn] = new KrumTurn(players, background, windX, windY, updateCount, ending, running, winner, waterLevel);
        currentTurn = savedTurns[playerTurn];
        recordingTurn = true;
        turnOver = false;
        players[playerTurn].jumping = false;
    }

    int numLivingPlayers() {
        int n = 0;
        for (KrumPlayer p : players) {
            if (!p.dead)
                n++;
        }
        return n;
    }

    /** 
     * Called when turn time has expired. Next turn won't be started until
     * projectiles have exploded or fallen off the bottom of the screen, and 
     * players are no longer airborne.
     */
    void endTurn() {
        receivedFrames.clear();
        playBackFrame = 0;
        for (KrumPlayer p : players)
            p.stop();
        recordingTurn = false;
        currentTurn.endState = new KrumGameState(players, background, windX, windY, updateCount, ending, running, winner, waterLevel);

        //sendFrames(currentTurn.frames);

        playerTurn = 1 - playerTurn; 
        playerTurn *= -1;       
        turnOver = true;
        readyToStartTurn = false;
        turnOverFrame = updateCount;
        int playersAlive = numLivingPlayers();        
        if (playersAlive < 2) {
            if (playersAlive == 0) {
                gameOver(-1);
            }
            else {
                for (KrumPlayer p : players) {
                    if (!p.dead) {
                        gameOver(p.playerIndex);
                    }
                }
            }
        }
    }

    void gameOver(int w) {
        winner = w;        
        running = false;
        ending = true;
    }

    void handlePlayerKnock(KrumProjectile proj){               
        for (int i = 0; i < players.length; i++) {
            if (proj != null) players[i].knockback(proj);  
            if (!players[i].onRope) {
                players[i].airborne = true;                
            }                       
        }
    }
    /** 
     * Loads state from a KrumGameState object
     */
    void setGameState(KrumGameState state) {
        alphaRaster.setDataElements(0,0,background.getWidth(),background.getHeight(),state.pixelMatrix);
        updateCount = state.startTick;
        turnEndFrame = state.endTick;
        windX = state.windX;
        windY = state.windY;
        ending = state.ending;
        running = state.running;
        winner = state.winner;
        waterLevel = state.waterLevel;
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
            players[i].joey.active = false;
            players[i].grenade = null;
            players[i].projectile = null;
            players[i].canShootRope = ps.canShootRope;
            players[i].dead = ps.dead;
            players[i].firstLanding = ps.firstLanding;
            players[i].spriteIndex = ps.spriteIndex;
        }
    }

    /**
     * Called once per frame to update game state
     */
    void update() {       
        if (myPlayerIndex == playerTurn) {
            recordingTurn = true;
            playingBackTurn = false;
        } 
        else if (!turnOver && myPlayerIndex != playerTurn && playerTurn >= 0) {
            if (receivedFrames.size() < KrumC.TURN_TIME_LIMIT_FRAMES) {
                requestSingleFrame();
            }
            if (receivedFrames.size() - playBackFrame <= MIN_PLAYBACK_BUFFER && playBackFrame < KrumC.TURN_TIME_LIMIT_FRAMES - MIN_PLAYBACK_BUFFER - 1) {
                return;
            }
            playingBackTurn = true;
            recordingTurn = false; 
        }
        else {
            playingBackTurn = false;
            recordingTurn = false;
        }
        // if (playBackTurn) {
        //     playingBackTurn = true;
        //     recordingTurn = false;
        //     playBackTurn = false;
        //     playBackFrame = 0;
        //     currentTurn = savedTurns[1 - playerTurn];
        //     setGameState(currentTurn.startState);
        //     System.out.println("playback " + currentTurn);
        //     playerTurn = 1 - playerTurn;
        // } 
        // else if (!playingBackTurn && updateCount > turnEndFrame && !turnOver) {
        if (!playingBackTurn && updateCount >= turnEndFrame && !turnOver) {
            endTurn();
        }       
        if (playingBackTurn && playBackFrame >= KrumC.TURN_TIME_LIMIT_FRAMES && !turnOver) {
            endTurn();
        }
        readyToStartTurn = true;
        KrumInputFrame rf = null;
        for (KrumPlayer p : players) {    
            KrumInputFrame pf = null;
            rf = null;
            //KrumTurn rt = null;
            // if (p.playerIndex == playerTurn && playerTurn == myPlayerIndex) {
            //     rt = currentTurn;
            // }  
            if (playingBackTurn && !turnOver) {
                if (playBackFrame >= KrumC.TURN_TIME_LIMIT_FRAMES) {
                    playingBackTurn = false;
                    //System.out.println("done replaying");
                    //endTurn();
                }
                else if (p == players[receivedFrames.get(playBackFrame).activePlayer]) {
                    pf = receivedFrames.get(playBackFrame);
                    playBackFrame++;
                }                
            }
            else if (myPlayerIndex == playerTurn && p.playerIndex == playerTurn && !turnOver) {
                rf = new KrumInputFrame();                  
            }
            p.update(windX, windY, alphaRaster, updateCount, rf, pf, turnOver);
            if (p.ypos > waterLevel) p.die();
            if (numLivingPlayers() < 2) {
                turnEndFrame = updateCount;
            }
            if (p.projectile != null) {
                if(p.projectile.collisionCheck()) {
                    ExplosionDetails.explode((int)p.projectile.x, (int)p.projectile.y, p.projectile.explosionRadius, 
                        KrumC.RES_X, KrumC.RES_Y, alphaRaster, updateCount);
                    handlePlayerKnock(p.projectile);                   
                    for (KrumPlayer pl : players) {
                        double distance = KrumHelpers.distanceBetween(p.projectile.centre()[0], p.projectile.centre()[1], pl.playerCentre().x, pl.playerCentre().y);
                        if (distance <= p.projectile.damageRadius) {
                            pl.hit(p.projectile.maxDamage, distance, p.projectile.damageRadius);
                        }
                    }
                    p.projectile = null;
                }
                if (p.projectile != null) {
                    int n = p.projectile.playerCollisionCheck(players);
                    if (n >= 0) {
                        ExplosionDetails.explode((int)p.projectile.x, (int)p.projectile.y, p.projectile.explosionRadius, 
                            KrumC.RES_X, KrumC.RES_Y, alphaRaster, updateCount);
                        handlePlayerKnock(p.projectile);
                        players[n].hit(p.projectile.maxDamage, 0, p.projectile.damageRadius);
                        for (int i = 0; i < players.length; i++) {
                            if (i == n) continue;
                            double distance = KrumHelpers.distanceBetween(p.projectile.centre()[0], p.projectile.centre()[1], players[i].playerCentre().x, players[i].playerCentre().y);
                            if (distance <= p.projectile.damageRadius) {
                                players[i].hit(p.projectile.maxDamage, distance, p.projectile.damageRadius);
                            }
                        }
                        p.projectile = null;                        
                    }
                }
            }
            if (p.grenade != null) {
                if (p.grenade.timerCheck(updateCount)) {
                    for (KrumPlayer pl : players) {
                        double distance = KrumHelpers.distanceBetween(p.grenade.centre()[0], p.grenade.centre()[1], pl.playerCentre().x, pl.playerCentre().y);
                        if (distance <= p.grenade.damageRadius) {
                            pl.hit(p.grenade.maxDamage, distance, p.grenade.damageRadius);
                        }
                    }
                    ExplosionDetails.explode((int)p.grenade.x, (int)p.grenade.y, p.grenade.explosionRadius, 
                        KrumC.RES_X, KrumC.RES_Y, alphaRaster, updateCount);
                    handlePlayerKnock(p.grenade);
                    p.grenade = null;
                }
            }
            if (p.joey.active) {
                if (p.joey.timerCheck(updateCount)) {
                    for (KrumPlayer pl : players) {
                        double distance = KrumHelpers.distanceBetween(p.joey.centre()[0], p.joey.centre()[1], pl.playerCentre().x, pl.playerCentre().y);
                        if (distance <= p.joey.damageRadius) {
                            pl.hit(p.joey.maxDamage, distance, p.joey.damageRadius);
                        }
                    }
                    ExplosionDetails.explode((int)p.joey.xpos, (int)p.joey.ypos, p.joey.explosionRadius, 
                        KrumC.RES_X, KrumC.RES_Y, alphaRaster, updateCount);
                    handlePlayerKnock(p.joey); 
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
                else if (p.flashFramesLeft > 0)
                    readyToStartTurn = false;
            }  
            if (rf != null)
                sendFrame(rf);        
        }   
        updateCount++;
        if (turnOver) {
            if (readyToStartTurn) //} || updateCount - turnOverFrame > MAX_TURN_GAP_FRAMES)
                startTurn();
        }
    }

    /**
     * Draw loop triggered indirectly by a call to repaint() 
     * @param g
     */
    void draw(Graphics2D g) {
        //draw background
        g.drawImage(background, null, 0, 0);

        //draw players and their weapons
        for (KrumPlayer p : players) {
            if (p.dead) continue;
            p.draw(g, playerTurn);
            if (p.projectile != null) p.projectile.draw(g);
            if (p.grenade != null) p.grenade.draw(g);
            if (p.joey.active) p.joey.draw(g);
        }        

        //draw wind info
        g.setColor(Color.red);
        if (windX > 0) g.setColor(Color.blue);        
        g.setFont(new Font("Courier New", 1, 24));
        g.drawString(windString, 300, 25);

        //draw timer
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

        //draw replay text
        g.drawString(timerString, 5, 20);
        if (playingBackTurn) {
            g.setColor(Color.gray);
            g.drawString("SPECTATING", 310, 60);
        }
        else if (!turnOver) {
            g.setColor(Color.green);
            g.drawString("YOUR TURN!", 310, 60);
        }

        //draw explosions
        g.setColor(Color.red);
        for (int i = 0; i < ExplosionDetails.explosions.size(); i++) {
            //System.out.println("Explosion " + ExplosionDetails.explosions.size());
            ExplosionDetails e = ExplosionDetails.explosions.get(i);
            int r = e.getRadius() - (int)(e.getEndFrame() - updateCount) / 2;
            //System.out.println("R: " + r);
            g.fillOval(e.getXCoords() - r, e.getYCoords() - r, r * 2, r * 2);
            if (updateCount >= e.getEndFrame()) {
                ExplosionDetails.explosions.remove(e);
                i--;
                //System.out.println("Explosion " + ExplosionDetails.explosions.size());
            }              
        }

        //draw water
        if (waterLevel < KrumC.RES_Y - 1) {
            g.setColor(new Color(0x64, 0x2c, 0xa9, 200));
            g.fillRect(0, (int)waterLevel, KrumC.RES_X - 1, KrumC.RES_Y - 1);
        }
        if (ending) {
            String resultString = "";
            int w = winner + 1;
            if (winner == -1) {
                resultString += "Game drawn!";
            }
            else {
                resultString += "Player " + w + " wins!";
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Courier New", 1, 42));
            g.drawString(resultString, KrumC.RES_X / 2 - 160, 120);
        }
    }

    /**
     * Main game loop
     */
    void startGame(){
        // Starting the Wind Manager
        windManager = new WindManager(seed);
        windX = windManager.getWindX();
        windY = windManager.getWindY();
        windString = windManager.getWindString();
        startTurn();
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
        while (ending) {

        }
    }

    // mouse and key Down/Up functions are triggered via the listeners in KrumPanel
    void mouseDown(MouseEvent e){
        if (myPlayerIndex != playerTurn) return;
        if (e.getButton() == MouseEvent.BUTTON1)
            players[playerTurn].startFire(e);
        else 
            players[playerTurn].startGrenadeFire(e);
    }
    void mouseUp(MouseEvent e) {
        if (myPlayerIndex != playerTurn) return;
        if (e.getButton() == MouseEvent.BUTTON1 && players[playerTurn].firing)
            players[playerTurn].endFire(e);
        else if (players[playerTurn].firingGrenade)
            players[playerTurn].endGrenadeFire(e);
    }
    void keyDown(KeyEvent e) {
        if (myPlayerIndex != playerTurn) return;
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
        if (myPlayerIndex != playerTurn) return;
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

    void addReceivedFrame(JsonObject frame) {
        //JsonObject frame = command.getJsonObject("frame");
        if (frame == null) return;
        receivedFrames.add(new KrumInputFrame(frame));
    }

    // void addReceivedFrames(JsonObject frames) {
    //     frames.fieldNames()
    // }

    /** 
     * Sends a command to the game at the server.
     */
    public void sendFrames(ArrayList<KrumInputFrame> frames) {
        List<JsonObject> jsonList = new ArrayList<JsonObject>();
        for (KrumInputFrame f : frames) {
            jsonList.add(f.getJson());
        }       
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, jsonList));
    }

    public void sendFrame(KrumInputFrame frame) {       
        // Collections.singletonList() is a quick way of getting a "list of one item"
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(frame.getJson())));
    }

    // public void requestFrames(int numFrames) {
    //     JsonObject j = new JsonObject().put("framesRequest", numFrames);
    //     mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    // }

    public void requestSingleFrame() {
        JsonObject j = new JsonObject().put("frameRequest", myPlayerIndex);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    }

    public void requestMyPlayerIndex() {
        JsonObject j = new JsonObject().put("indexRequest", 1);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    }

    public void setPlayerIndexAndBegin(int index) {
        this.myPlayerIndex = index;
        Thread gameThread = new Thread() {
            public void run(){
                startGame();                
            }
        };
        gameThread.start();
    }

    void disableInfoLogging(){
        // LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        // Configuration config = ctx.getConfiguration();
        // LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        // loggerConfig.setLevel(Level.WARN);
        // ctx.updateLoggers();
        Configurator.setLevel(LogManager.getLogger(MinigameNetworkClient.class), Level.WARN);
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
        requestMyPlayerIndex();
        disableInfoLogging();
    }

    @Override
    public void execute(GameMetadata game, JsonObject command) {
        this.gm = game;
        //System.out.println("command received" + command + "metadata: " + game);
        JsonObject frame = command.getJsonObject("frame");
        if (frame != null) {
            addReceivedFrame(frame);
            return;
        }
        try {
            int index = command.getInteger("playerIndexFromServer");
            this.seed = command.getLong("seed");
            setPlayerIndexAndBegin(index);
        } catch (Exception e) {
            System.out.println("unknown command received from server");
        }
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
