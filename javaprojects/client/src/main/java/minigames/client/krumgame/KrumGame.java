package minigames.client.krumgame;

import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import org.checkerframework.checker.units.qual.C;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.ArrayList;
import java.util.Arrays;
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

//Imports for sound effects
import java.net.URL;
import javax.swing.*;
import javax.sound.sampled.*;

import minigames.client.krumgame.components.*;
/**
 * The state of each running game will be represented by an instance of KrumGame on the server and an instance on each participating client
 */
public class KrumGame {

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
    boolean choosingLevel;

    int currentLevelIndex = 0;
    boolean ready;
    KrumLevel currentLevel;

    boolean initialized;
    boolean playersInitialized;

    
    // Player information
    String player;
    Color primaryColor = new Color(50, 122, 87);
    Color secondaryColor = new Color(255, 224, 119);
    Color gunColor = new Color(237, 28, 36);


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


    ArrayList<KrumLevel> levels;

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

    final int MIN_PLAYBACK_BUFFER = 0;

    ComponentAdapter offsetListener;



    long seed; // seed for Random() -- needs to be the same for all clients

    public KrumGame() {  
        initialized = false;
        playersInitialized = false;
        ready = false;
        choosingLevel = true;
        receivedFrames = new ArrayList<KrumInputFrame>();
        firstRun = true;
        updateCount = 0;
        waterLevel = KrumC.RES_Y - 1;
        initializePanel();
        initializeLevels();
        setActiveLevel(0);                   
        windString = "";        
        initialized = true;
        System.out.println("KrumGame instance created on client");
    }

    private void initializeLevels() {
        levels = new ArrayList<KrumLevel>();
        KrumLevel chameleon = new KrumLevel("chameleon.png", null, 235, 0, 600, 0);
        KrumLevel ropetest = new KrumLevel("ropetestmap.png", null, 235, 0, 600, 0);
        KrumLevel cavetest = new KrumLevel("cave_test_2.png", "cave_test_2_mask.png", 300, 500, 680, 110);
        levels.add(chameleon);
        levels.add(ropetest);
        levels.add(cavetest);    
        setActiveLevel(0);           
    }

    private void setActiveLevel(int index) {
        currentLevel = levels.get(index);
        backgroundComponent = currentLevel.background;
        background = backgroundComponent.getImage();
        alphaRaster = backgroundComponent.getAlphaRaster();        
        currentLevelIndex = index;        
    }

    private void setReady() {
        ready = true;
        sendReady();
    }

    private void initializePanel(){
        panel = new KrumPanel(this);
        panel.setPreferredSize(panel.getPreferredSize());
    }

    private void initializePlayers(KrumLevel level){
        players = new KrumPlayer[2];
        players[0] = new KrumPlayer(level.p1x, level.p1y, "kangaroo_sprite/", 8, 31, true, alphaRaster, 0, players, primaryColor, secondaryColor, gunColor);
        players[1] = new KrumPlayer(level.p2x, level.p2y, "kangaroo_sprite/", 8, 31, false, alphaRaster, 1, players, primaryColor, secondaryColor, gunColor);
        players[0].joey.otherPlayer = players[1];
        players[1].joey.otherPlayer = players[0];
        playerTurn = 0;
        savedTurns = new KrumTurn[] {new KrumTurn(players, background, windX, windY, updateCount, ending, running, winner, waterLevel), new KrumTurn(players, background, windX, windY, updateCount, ending, running, winner, waterLevel)};       
        currentTurn = savedTurns[playerTurn];
        playersInitialized = true;
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
        //KrumSound.playSound("transition");
        for (int i = 0; i < KrumPlayer.shotsPerTurn.length; i++) {
            players[playerTurn].firedThisTurn[i] = 0;
        }
        players[playerTurn].punchedThisTurn = false;
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
        KrumSound.playSound("transition");
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
            if (receivedFrames.size() - playBackFrame <= MIN_PLAYBACK_BUFFER) {//}) && playBackFrame < KrumC.TURN_TIME_LIMIT_FRAMES - MIN_PLAYBACK_BUFFER - 1) {
                return;
            }
            playingBackTurn = true;
            recordingTurn = false; 
        }
        else {
            playingBackTurn = false;
            recordingTurn = false;
        }
        readyToStartTurn = true;
        KrumInputFrame rf = null;
        boolean incud = false;
        for (KrumPlayer p : players) {    
            KrumInputFrame pf = null;
            rf = null;
            if (playingBackTurn && !turnOver) {
                if (playBackFrame >= KrumC.TURN_TIME_LIMIT_FRAMES) {
                    playingBackTurn = false;
                }
                else if (receivedFrames.size() > playBackFrame && p == players[receivedFrames.get(playBackFrame).activePlayer]) {
                    pf = receivedFrames.get(playBackFrame);
                    playBackFrame++;
                }                
            }
            else if (myPlayerIndex == playerTurn && p.playerIndex == playerTurn && !turnOver) {
                rf = new KrumInputFrame();
            }
            p.update(windX, windY, alphaRaster, updateCount, rf, pf, turnOver);
            if (p.ypos > waterLevel) p.die();
            if (p.projectile != null) {
                if(p.projectile.collisionCheck()) {
                    KrumSound.playSound("explode2");
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
                    int n = p.projectile.playerCollisionCheck(players, p.playerIndex);
                    if (n >= 0) {
                        KrumSound.playSound("explode2");
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
                    KrumSound.playSound("explode2");
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
                else if (p.blowtorchActive)
                    readyToStartTurn = false;
            }  
            if (rf != null)
                sendFrame(rf); 
            if (pf != null || rf != null) {
                incud = true;
            }       
        }        
        if (incud || turnOver) 
            updateCount++;
        if (turnOver) {
            if (readyToStartTurn) //
                startTurn();
        }
        if (!playingBackTurn && updateCount >= turnEndFrame && !turnOver) {
            endTurn();
        }       
        if (playingBackTurn && playBackFrame >= KrumC.TURN_TIME_LIMIT_FRAMES && !turnOver) {
            endTurn();
        }
        if (numLivingPlayers() < 2) {
            endTurn();
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
        if (playersInitialized) {
            for (KrumPlayer p : players) {
                if (p.dead) continue;
                p.draw(g, playerTurn);
                if (p.projectile != null) p.projectile.draw(g);
                if (p.grenade != null) p.grenade.draw(g);
                if (p.joey.active) p.joey.draw(g);
            }
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
        g.drawString(timerString, 5, 20);

        //draw replay text        
        if (playingBackTurn) {
            g.setColor(Color.blue);
            g.drawString("SPECTATING", 310, 60);
        }
        else if (myPlayerIndex == playerTurn) {
            g.setColor(Color.green);
            g.drawString("YOUR TURN!", 310, 60);
        }
        else {
            g.setColor(Color.GRAY);
            g.drawString("WAITING...", 310, 60);
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
            if (winner == -1) {
                resultString += "Game drawn!";
            }
            else {
                if (winner == myPlayerIndex){
                    resultString += "You win!";
                    KrumSound.playSound("applause");
                }else{ 
                    KrumSound.playSound("wahwah");
                    resultString += "You lose!";
                }
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Courier New", 1, 42));
            g.drawString(resultString, KrumC.RES_X / 2 - 160, 120);
        }

        // draw ammo
        if (playersInitialized) {
            int tx = KrumC.RES_X - 52;
            int ty = 16;
            g.setFont(new Font("Courier New", 1, 18));
            g.setColor(Color.black);
            g.drawString("Ammo", tx, ty);        
            tx += 34;
            ty += 20;
            drawAmmo(KrumPlayer.ZOOK, tx, ty, 0xED1C24, g);
            ty += 18;
            drawAmmo(KrumPlayer.NADE, tx, ty, 0x267F00, g);
            ty += 18;
            drawAmmo(KrumPlayer.JOEY, tx, ty, 0xB97A57, g);
            ty += 18;
            drawAmmo(KrumPlayer.ROPE, tx, ty, Color.ORANGE.getRGB(), g);
            ty += 18;
            drawAmmo(KrumPlayer.BLOW, tx, ty, 0x8afff7, g);
        }

        // draw level selection / ready text
        if (choosingLevel) {
            g.setColor(Color.black);
            g.setFont(new Font("Courier New", 1, 22));
            if (myPlayerIndex == 0) {
                if (!ready)
                    g.drawString("Use number keys to change level, enter to start game", 10, 200);
                else 
                    g.drawString("Ready! Waiting for opponent", 200, 200);
            }
            else {
                if (!ready) {
                    g.drawString("Press enter key when ready", 200, 200);
                }
                else {
                    g.drawString("Ready! Waiting for opponent to choose level", 50, 200);
                }
            }                
        }

        // draw sound status
        if (KrumSound.muted) {
            g.setFont(new Font("Courier New", 1, 14));
            g.setColor(Color.BLACK);
            g.drawString("muted - M to unmute", 70, 15);
        }            


    }

    void drawAmmo(int w, int x, int y, int c, Graphics2D g) {
        if (playerTurn < 0) return;
        int a = players[playerTurn].ammo[w];
        // if (players[playerTurn].firedThisTurn[w] >= KrumPlayer.shotsPerTurn[w]) {
        //     c = c << 16;
        //     c += 128;
        // }
        g.setColor(new Color(c));
        String s = "" + a;
        int o = 0;        
        if (a >= 10) o += 11;
        if (a >= 100) o += 11;
        g.drawString(s, x - o, y);
        if (players[playerTurn].firedThisTurn[w] >= KrumPlayer.shotsPerTurn[w]) {
            g.setColor(new Color(0x80808080));
            g.drawString(s, x - o, y);
        }
    }

    /**
     * Main game loop
     */
    void startGame(){
        panel.gameActive = true;
        lastFrameTime = System.nanoTime();
        while (choosingLevel || !initialized) {
            if (System.nanoTime() - lastFrameTime >= KrumC.TARGET_FRAMETIME) {
                if (!initialized) continue;
                lastFrameTime = System.nanoTime();
                if (myPlayerIndex != 0)
                    requestLevelIndex();
                checkReady(); 
                panel.repaint();   
            }              
        }  
        initializePlayers(currentLevel); 
        KrumSound.playSound("intro2");
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
        if (!playersInitialized) return;
        if (e.getButton() == MouseEvent.BUTTON1)
            players[playerTurn].startFire(e);
        else 
            players[playerTurn].startGrenadeFire(e);
    }
    void mouseUp(MouseEvent e) {
        if (myPlayerIndex != playerTurn) return;
        if (!playersInitialized) return;
        if (e.getButton() == MouseEvent.BUTTON1 && players[playerTurn].firing)
            players[playerTurn].endFire(e);
        else if (players[playerTurn].firingGrenade)
            players[playerTurn].endGrenadeFire(e);
    }
    void keyDown(KeyEvent e) {
        if (!panel.gameActive) return;
        // keys that should take effect even if it's not your turn
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (choosingLevel) {
                setReady();
                return;
            }
            else if (myPlayerIndex == playerTurn) {
                players[playerTurn].enterKeyPressed();
            }
        }
        else if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_M){
            KrumSound.toggleMuted();
        }
        else if(e.getKeyCode() == KeyEvent.VK_Q || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            sendQuitCommand();
        }

        if (myPlayerIndex != playerTurn) return;

        //keys that should only take effect if it's your turn
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
        else if (e.getKeyCode() == KeyEvent.VK_UP) {
            players[playerTurn].upArrowKeyDownNextFrame = true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            players[playerTurn].downArrowKeyDownNextFrame = true;
        }
        // else if (e.getKeyCode() == KeyEvent.VK_R) {
        //     playBackTurn = true;
        // }
        else if (e.getKeyCode() == KeyEvent.VK_L) {
            players[playerTurn].fireTorchNextFrame();
        }
        else if (e.getKeyCode() == KeyEvent.VK_P) {
            players[playerTurn].punchNextFrame();
        }
        else if (choosingLevel && Character.isDigit(e.getKeyChar())) {
            int n = Character.getNumericValue(e.getKeyChar());
            if (n == 0) 
                n = 10;
            else 
                n--;
            if (n >= levels.size()) {
                return;
            }
            if (myPlayerIndex == 0) {
                sendLevelIndex(n);
                setActiveLevel(n);                
            }            
        }
    }
    void keyUp(KeyEvent e) {
        if (myPlayerIndex != playerTurn) return;
        if (!playersInitialized) return;
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
        KrumSound.setClientIndex(index);
        Thread gameThread = new Thread() {
            public void run(){
                startGame();                
            }
        };
        gameThread.start();
    }


    public void sendLevelIndex(int index) {
        JsonObject j = new JsonObject().put("levelIndexSend", index);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    }

    public void sendLevelIndex() {
        sendLevelIndex(currentLevelIndex);
    }

    public void requestLevelIndex() {
        JsonObject j = new JsonObject().put("levelCheck", 0);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    }

    public void sendReady() {
        System.out.println("ready");
        JsonObject j = new JsonObject().put("readySend", myPlayerIndex).put("level", currentLevelIndex);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    }

    public void checkReady() {
        JsonObject j = new JsonObject().put("readyCheck", 0);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    }

    public void sendQuitCommand() {        
        JsonObject j = new JsonObject().put("quit", myPlayerIndex);
        mnClient.send(new CommandPackage(gm.gameServer(), gm.name(), player, Collections.singletonList(j)));
    }

    void disableInfoLogging(){
        Configurator.setLevel(LogManager.getLogger(MinigameNetworkClient.class), Level.WARN);
    }

    void enableInfoLogging() {
        Configurator.setLevel(LogManager.getLogger(MinigameNetworkClient.class), Level.INFO);
    }

    /**
     * Called when our client is loaded into the main screen
     */ 
    public void load(MinigameNetworkClient mnClient, GameMetadata game, String player) {
        this.mnClient = mnClient;
        this.gm = game;
        this.player = player;
        offsetListener = new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                if (playersInitialized) {
                    for (KrumPlayer p : players) {
                        p.setMouseOffsets(panel.getLocationOnScreen().x, panel.getLocationOnScreen().y);
                    }
                }

            }
        };
        mnClient.getMainWindow().getFrame().addComponentListener(offsetListener);       
        mnClient.getMainWindow().getFrame().addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we) {
                sendQuitCommand();
            }
        });
        mnClient.getMainWindow().clearAll();
        mnClient.getMainWindow().addCenter(panel);
        mnClient.getMainWindow().pack();
        requestMyPlayerIndex();
        disableInfoLogging();
    }

    public void execute(GameMetadata game, JsonObject command) {
        if (!this.gm.name().equals(game.name())) {
            System.out.println("ignoring command from previous server");
            return;
        }
        //System.out.println("command received" + command + "metadata: " + game);
        JsonObject frame = command.getJsonObject("frame");
        if (frame != null) {
            addReceivedFrame(frame);
            return;
        }
        if (command.containsKey("levelIndex")){
            if (choosingLevel) {
                int i = command.getInteger("levelIndex");
                if (i != currentLevelIndex) {
                    setActiveLevel(i);
                    if (ready) {
                        sendReady();
                    }
                }

            }
            return;
        }
        if (command.containsKey("p1ready") && command.containsKey("p2ready")){
            int[] readyStatus = new int[]{-1,-1};
            readyStatus[0] = command.getInteger("p1ready");
            readyStatus[1] = command.getInteger("p2ready");
            if (readyStatus[0] == readyStatus[1] && readyStatus[0] != -1) {
                System.out.println("both players ready");
                choosingLevel = false;
            }
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

    public void closeGame() {
        panel.gameActive = false;        
        // todo: make sure we don't leave any mess
        mnClient.getMainWindow().getFrame().removeComponentListener(offsetListener);
        enableInfoLogging();
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
