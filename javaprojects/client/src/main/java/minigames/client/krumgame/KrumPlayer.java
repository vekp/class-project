package minigames.client.krumgame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.lang.Math;
import java.util.ArrayList;

import minigames.krumgame.KrumInputFrame;

import java.awt.MouseInfo;

import java.awt.event.MouseEvent;
import java.awt.image.WritableRaster;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import java.util.HashMap;

/*
 * Class representing a player-controlled character
 */

public class KrumPlayer {
    final double FALL_DAMAGE_VELOCITY_THRESHOLD = 3.0;
    final int FALL_DAMAGE_MAX = 25;

    double hp;
    double xpos;
    double ypos;
    double xvel;
    double yvel;


    
    final static int ZOOK = 0;
    final static int NADE = 1;
    final static int JOEY = 2;
    final static int ROPE = 3;
    final static int BLOW = 4;
    final static int[] startingAmmo = {99, 99, 3, 4, 1};
    final static int[] shotsPerTurn = {1, 1, 1, 2, 1};
    int[] ammo;
    int[] firedThisTurn;
    
    


    boolean canShootRope;

    boolean firstLanding;

    boolean dead = false;;

    boolean active;
    double aimAngleRadians;

    String spriteDir;
    BufferedImage sprite;
    BufferedImage spriteGun;
    Color primaryColor;
    Color secondaryColor;
    Color gunColor;
    WritableRaster alphaRaster; // alpha values (opacities) of player sprite pixels
    WritableRaster levelRaster; // alpha values of level pixels

    BufferedImage[] sprites;

    boolean firing;
    boolean firingGrenade;
    long fireStart;
    long fireGrenadeStart;
    int xoff; // accounts for position of window on user's screen. reset when window is moved
    int yoff; // accounts for position of window on user's screen. reset when window is moved

    KrumProjectile projectile = null;
    KrumGrenade grenade = null;

    int grenadeSeconds;
    
    int flashFramesLeft = 0;

    int stuckFrames = 0;

    boolean airborne;
    boolean jumping;
    long jumpStart;
    boolean facingRight;
    boolean walking;
    boolean walkedOffEdge;

    AffineTransform originalAffineTransform;
    
    int leftEdge[];
    int rightEdge[];
    int topEdge[];
    int bottomEdge[];
    int topEdgeFlipped[];
    int bottomEdgeFlipped[];
    int topEdgeLeft, topEdgeRight, bottomEdgeLeft, bottomEdgeRight, leftEdgeBottom, leftEdgeTop, rightEdgeBottom, rightEdgeTop;
    
    int jumpType;
    boolean firstJumpFrame = false;
    boolean deferredLanding;

    boolean shootingRope;
    boolean onRope;
    ArrayList<Point2D.Double> ropeAttachmentPoints;
    double ropeLength;
    double ropeAngleRadians;
    double onRopeSpeed;
    boolean wasOnRope;
    boolean leftKeyDown = false;
    boolean rightKeyDown = false;
    boolean upArrowKeyDown = false;
    boolean downArrowKeyDown = false;
    boolean leftKeyDownNextFrame = false;
    boolean rightKeyDownNextFrame = false;

    boolean enterKeyDownNextFrame = false;

    boolean stuck = false;

    boolean upArrowKeyDownNextFrame;
    boolean downArrowKeyDownNextFrame;
    boolean upArrowKeyUpNextFrame;
    boolean downArrowKeyUpNextFrame;

    long tick = 0;
    long shotPower;
    boolean shootNextFrame;

    long grenadePower;
    boolean grenadeNextFrame;

    int spriteIndex = 0;

    boolean detachRopeNextFrame = false;
    boolean shootRopeNextFrame = false;

    boolean fireBlowtorchNextFrame = false;
    boolean blowtorchActive = false;
    double blowtorchAimAngle;
    int blowtorchFrameCount;
    final static int BLOWTORCH_MAX_LENGTH = 200;
    final static int BLOWTORCH_MIN_WIDTH = 1;
    final static int BLOWTORCH_MAX_WIDTH = 45;
    final static int BLOWTORCH_FRAMES = 120;
    final static double BLOWTORCH_INC = (double)BLOWTORCH_MAX_LENGTH / BLOWTORCH_FRAMES;
    double blowtorchLength;
    double blowtorchStartX;
    double blowtorchStartY;
    boolean blowtorchLengthening = false;
    boolean blowtorchWidening = true;
    boolean blowtorchWide = false;

    long jumpPower;
    boolean jumpNextFrame;

    double grenadeAimAngle;
    double shootAimAngle;
    double ropeAimAngle;

    boolean punchNextFrame = false;
    boolean punching = false;
    boolean punchedThisTurn = false;
    int punchFrame;
    BufferedImage[] punchSprites;
    BufferedImage punchSprite;
    WritableRaster punchRaster;

    boolean punchHit = false;


    double lastAimAngle;
    double lastMouseX;
    double lastMouseY;
    
    BufferedImage projectileSprite;
    BufferedImage grenadeSprite;

    ArrayList<Double> ropeSegmentLengths;
 
    int playerIndex;

    int spriteIndexNextFrame;

    double empty[];

    KrumJoey joey;

    KrumPlayer[] players;

    
    /**
     * 
     * @param xpos          starting position x
     * @param ypos          starting position y (will fall down to next piece of solid ground)
     * @param spriteFileName
     * @param panelX        used to set xoff
     * @param panelY        used to set yoff
     * @param direction     true = pleyer faces right at beginning of fame
     * @param level         alpha raster of level
     */
    KrumPlayer(int xpos, int ypos, String spriteFileName, int panelX, int panelY, boolean direction, WritableRaster level, int index, KrumPlayer[] players, Color primaryColor, Color secondaryColor, Color gunColor) {
        this.levelRaster = level;
        topEdgeLeft = -1;
        topEdgeRight = -1;
        bottomEdgeLeft = -1;
        bottomEdgeRight = -1;
        leftEdgeBottom = -1;
        leftEdgeTop = -1;
        rightEdgeBottom = -1;
        rightEdgeTop = -1;
        walkedOffEdge = false;
        wasOnRope = false;
        this.xpos = xpos;
        this.ypos = ypos;
        this.active = false;
        this.aimAngleRadians = 0;
        this.hp = 250;
        this.spriteDir = spriteFileName;
        firstLanding = true;
        canShootRope = true;

        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.gunColor = gunColor;

        BufferedImage joeySprite = KrumHelpers.readSprite("joey.png");

        joey = new KrumJoey(0,0,0,0,8,joeySprite,level,0);
        
        // Reading sprites
        projectileSprite = KrumHelpers.readSprite("carrot_s.png");
        grenadeSprite = KrumHelpers.readSprite("grenade.png");
        

        sprites = new BufferedImage[]{ 
            KrumHelpers.readSprite(spriteDir + "0.png"),
            KrumHelpers.readSprite(spriteDir + "45_UP.png"),
            KrumHelpers.readSprite(spriteDir + "45_DOWN.png"),
            KrumHelpers.readSprite(spriteDir + "90_UP.png"),
            KrumHelpers.readSprite(spriteDir + "90_DOWN.png"),
            KrumHelpers.readSprite(spriteDir + "0_L.png"),
            KrumHelpers.readSprite(spriteDir + "45_UP_L.png"),
            KrumHelpers.readSprite(spriteDir + "45_DOWN_L.png"),
            KrumHelpers.readSprite(spriteDir + "90_UP_L.png"),
            KrumHelpers.readSprite(spriteDir + "90_DOWN_L.png")
        };

        for (BufferedImage i : sprites) {
            i = applyCustomColors(i);
        }

        sprite = sprites[0];
        
        ammo = new int[startingAmmo.length];
        firedThisTurn = new int[ammo.length];
        for (int i = 0; i < ammo.length; i++) {
            ammo[i] = startingAmmo[i];
            firedThisTurn[i] = 0;
        }
        
        
        
        alphaRaster = sprite.getAlphaRaster();

        firing = false;
        this.xoff = panelX;
        this.yoff = panelY;
        
        airborne = true;
        jumping = false;
        jumpStart = 0;        
        double empty[] = null; // dummy array for use when calling getPixel

        facingRight = true;
        deferredLanding = false;
        walking = false;

        ropeAttachmentPoints = new ArrayList<Point2D.Double>();
        ropeSegmentLengths = new ArrayList<Double>();
        
        //  determine outline of player for use in directional collision detection
        leftEdge = new int[alphaRaster.getHeight()];
        rightEdge = new int[alphaRaster.getHeight()];
        topEdge = new int[alphaRaster.getWidth()];
        bottomEdge = new int[alphaRaster.getWidth()];
        topEdgeFlipped = new int[alphaRaster.getWidth()];
        bottomEdgeFlipped = new int[alphaRaster.getWidth()];     
        for (int y = KrumC.HITBOX_Y_S; y <= KrumC.HITBOX_Y_F; y++) {
            int x = KrumC.HITBOX_X_S;
            while (x < KrumC.HITBOX_X_F && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
                x++;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {
                if (leftEdgeTop < 0) {
                    leftEdgeTop = y;
                }
                leftEdgeBottom = y;
            }
            leftEdge[y] = x;
            x = KrumC.HITBOX_X_F;
            while (x > KrumC.HITBOX_X_S && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
                x--;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {
                if (rightEdgeTop < 0) {
                    rightEdgeTop = y;
                }
                rightEdgeBottom = y;
            }
            rightEdge[y] = x;
        }
        for (int x = KrumC.HITBOX_X_S; x <= KrumC.HITBOX_X_F; x++) {
            int y = KrumC.HITBOX_Y_S;
            while (y < KrumC.HITBOX_Y_F && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
                y++;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {
                if (topEdgeLeft < 0) {
                    topEdgeLeft = x;
                }
                topEdgeRight = x;
            }
            if (y < alphaRaster.getHeight() / 2) {
                topEdge[x] = y;
            }
            else {
                topEdge[x] = 0;
            }            
            y = KrumC.HITBOX_Y_F;
            while (y > KrumC.HITBOX_Y_S && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
                y--;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {
                if (bottomEdgeLeft < 0) {
                    bottomEdgeLeft = x;
                }
                bottomEdgeRight = x;
            }
            if (y > alphaRaster.getHeight() / 2) {
                bottomEdge[x] = y;
            }
            else {
                bottomEdge[x] = 0;
            } 
        }
        for (int i = 0; i < topEdge.length; i++) {
            topEdgeFlipped[i] = topEdge[topEdge.length - 1 - i];
            bottomEdgeFlipped[i] = bottomEdge[bottomEdge.length - 1 - i];
        }

        setDirection(direction, level); // this could modify alphaRaster, so keep it below the outline-determining code, which assumes it's acting on a right-facing sprite
        lastAimAngle = facingRight ? 0 : Math.PI;
        spriteGun();
        grenadeSeconds = 3;
        shootNextFrame = false;
        grenadeNextFrame = false;
        jumpNextFrame = false;
        tick = 0;
        playerIndex = index;
        this.spriteGun = KrumHelpers.readSprite(spriteDir + "bazooka.png");
        this.spriteGun = applyCustomGunColor(this.spriteGun);
        spriteIndexNextFrame = spriteIndex;
        this.players = players;

        punchSprites = new BufferedImage[6];
        punchSprites[0] = KrumHelpers.readSprite("kangaroo_sprite/punch1.png");
        punchSprites[1] = KrumHelpers.readSprite("kangaroo_sprite/punch2.png");
        punchSprites[2] = KrumHelpers.readSprite("kangaroo_sprite/punch3.png");
        punchSprites[3] = KrumHelpers.readSprite("kangaroo_sprite/punch1L.png");
        punchSprites[4] = KrumHelpers.readSprite("kangaroo_sprite/punch2L.png");
        punchSprites[5] = KrumHelpers.readSprite("kangaroo_sprite/punch3L.png");
    }

    void stop() {
        detachRope();
        leftKeyDownNextFrame = false;
        rightKeyDownNextFrame = false;
        upArrowKeyDownNextFrame = false;
        downArrowKeyDownNextFrame = false;
        shootNextFrame = false;
        shootRopeNextFrame = false;
        grenadeNextFrame = false;
        detachRopeNextFrame = false;
        enterKeyDownNextFrame = false;
        firing = false;
        firingGrenade = false;
        shootingRope = false;
    }

    Point2D.Double playerCentre() {
        return new Point2D.Double((int)(xpos + sprite.getWidth()/2), (int)(ypos + sprite.getHeight()/2));
    }

    Point2D.Double shotOrigin() {
        Point2D.Double p = playerCentre();
        aimAngleRadians = calcAimAngle();
        p.x += Math.cos(aimAngleRadians) * KrumC.psd;
        p.y -= Math.sin(aimAngleRadians) * KrumC.psd;
        return p;
    }

    Point2D.Double ropeOrigin() {
        return playerCentre();
    }

    Point2D.Double addVectorToCoords(double x, double y, double angle, double mag) {
        double newx = x + Math.cos(angle) * mag;
        double newy = y - Math.sin(angle) * mag;
        return new Point2D.Double(newx, newy);
    }

    /**
     * called by KrumGame.draw() every time a frame is painted
     * @param g
     */
    void draw(Graphics2D g, int playerTurn){
        //draw blowtorch
        g.setColor(new Color(0x8afff7));
        int x = (int)blowtorchStartX;
        int y = (int)blowtorchStartY;
        int maxDrawWidth = BLOWTORCH_MAX_WIDTH / 2;
        if (blowtorchActive) {
            if (blowtorchWidening || blowtorchWide) {                
                g.setStroke(new BasicStroke(maxDrawWidth));
                x += Math.cos(blowtorchAimAngle) * maxDrawWidth;
                y -= Math.sin(blowtorchAimAngle) * maxDrawWidth;
                Ellipse2D.Double endCircle = new Ellipse2D.Double((int)(blowtorchStartX + Math.cos(blowtorchAimAngle) * (blowtorchLength)) - maxDrawWidth / 2, (int)(blowtorchStartY - Math.sin(blowtorchAimAngle) * (blowtorchLength)) - maxDrawWidth / 2, maxDrawWidth, maxDrawWidth);
                g.fill(endCircle);
                Ellipse2D.Double startCircle = new Ellipse2D.Double((int)(blowtorchStartX + Math.cos(blowtorchAimAngle) * (maxDrawWidth / 2)) - maxDrawWidth / 2, (int)(blowtorchStartY - Math.sin(blowtorchAimAngle) * (maxDrawWidth / 2)) - maxDrawWidth / 2, maxDrawWidth, maxDrawWidth);
                g.fill(startCircle);
            }
            else {
                g.setStroke(new BasicStroke(BLOWTORCH_MIN_WIDTH));
            }
            g.setColor(new Color(0x8afff7));
            g.drawLine(x, y, (int)(blowtorchStartX + Math.cos(blowtorchAimAngle) * (blowtorchLength - maxDrawWidth / 2)), (int)(blowtorchStartY - Math.sin(blowtorchAimAngle) * (blowtorchLength - maxDrawWidth / 2)));
            g.setStroke(new BasicStroke(1));
        }

        //Sprite Drawing   
        
        if (flashFramesLeft <= 0 || flashFramesLeft % 4 == 0) {
            g.drawImage(sprite, null, (int)xpos, (int)ypos);   
            if (!punching) {
                spriteGun();
                if (!facingRight) {
                g.drawImage(spriteGun, null, (int)xpos + 7, (int)ypos + 6);
                } else {
                    g.drawImage(spriteGun, null, (int)xpos + 10, (int)ypos + 6);
                }
            }                      
        }  
        
        //draw shot power bar
        if (firing) {
            aimAngleRadians = calcAimAngle(); 
            long power = System.nanoTime() - fireStart;
            power /= 10000000;
            g.setColor(Color.black);
            g.drawLine((int)shotOrigin().x, (int)shotOrigin().y, (int)(shotOrigin().x + Math.cos(aimAngleRadians) * power), (int)(shotOrigin().y - Math.sin(aimAngleRadians) * power));
        }
        if (firingGrenade) {
            aimAngleRadians = calcAimAngle(); 
            long power = System.nanoTime() - fireGrenadeStart;
            power /= 10000000;
            g.setColor(Color.green);
            g.drawLine((int)shotOrigin().x, (int)shotOrigin().y, (int)(shotOrigin().x + Math.cos(aimAngleRadians) * power), (int)(shotOrigin().y - Math.sin(aimAngleRadians) * power));
        }
        
        //draw rope
        g.setColor(Color.orange);
        if (shootingRope) {            
            g.drawLine((int)ropeOrigin().x, (int)ropeOrigin().y, (int)(ropeOrigin().x + Math.cos(ropeAngleRadians) * ropeLength), (int)(ropeOrigin().y - Math.sin(ropeAngleRadians) * ropeLength));
        }
        if (onRope) {
            Point2D.Double o = playerCentre();
            for (int i = 0; i < ropeAttachmentPoints.size(); i++) {
                Point2D.Double p = ropeAttachmentPoints.get(ropeAttachmentPoints.size()-i-1);
                g.drawLine((int)o.x, (int)o.y, (int)p.x, (int)p.y);
                o = p;
            }
        }


        // draw punch
        if (punching) {
            g.drawImage(punchSprite, null, (int)xpos, (int)ypos);
        }

        //draw hp
        g.setFont(new Font("Courier New", 1, 12));
        g.setColor(new Color(64, 192, 64));
        String hpString = "";
        hpString += (int)Math.ceil(this.hp);
        g.drawString(hpString, (int)this.xpos + (int)sprite.getWidth() / 4, (int)this.ypos - 12);

    
    }

    /**
     * called by KrumGame.update() every frame      
     * @param windX
     * @param windY
     * @param levelRaster
     */
    // void update(double windX, double windY, WritableRaster levelRaster, long tick, KrumTurn recordingTurn, KrumInputFrame playbackFrame, boolean turnOver){
    void update(double windX, double windY, WritableRaster levelRaster, long tick, KrumInputFrame recordingFrame, KrumInputFrame playbackFrame, boolean turnOver){
        //System.out.println(tick + " start: x " + xpos + ", y " + ypos + " fr " + facingRight + ", si " + spriteIndex + ", xvel " + xvel + ", yvel " + yvel + ". airborne " + airborne);
        if (dead) return;
        // KrumInputFrame recordingFrame = new KrumInputFrame();
        if (recordingFrame != null) {
            recordingFrame.activePlayer = playerIndex;
            recordingFrame.frameCount = tick;
            if (!walking && !onRope && !airborne && !blowtorchActive)
                spriteLook();
            recordingFrame.spriteIndex = spriteIndex;
            recordingFrame.lastAimAngle = lastAimAngle;
            recordingFrame.facingRight = facingRight;
        }        
        this.tick = tick;
        if (playbackFrame != null && playbackFrame.activePlayer == playerIndex) {            
            shootNextFrame = playbackFrame.shoot;
            shotPower = playbackFrame.shotPower;
            shootAimAngle = playbackFrame.shootAimAngle;
            grenadeNextFrame = playbackFrame.shootGrenade;
            grenadePower = playbackFrame.grenadePower;
            grenadeAimAngle = playbackFrame.grenadeAimAngle;
            shootRopeNextFrame = playbackFrame.shootRope;
            ropeAimAngle = playbackFrame.ropeAimAngle;
            leftKeyDownNextFrame = playbackFrame.leftKeyDown;
            rightKeyDownNextFrame = playbackFrame.rightKeyDown;
            detachRopeNextFrame = playbackFrame.detachRope;
            jumpNextFrame = playbackFrame.jump;
            jumpPower = playbackFrame.jumpPower;
            jumpType = playbackFrame.jumpType;
            enterKeyDownNextFrame = playbackFrame.enterKeyDown;
            upArrowKeyDownNextFrame = playbackFrame.upArrowKeyDown;
            downArrowKeyDownNextFrame = playbackFrame.downArrowKeyDown;
            fireBlowtorchNextFrame = playbackFrame.fireBlowtorch;
            blowtorchAimAngle = playbackFrame.blowtorchAimAngle;
            lastAimAngle = playbackFrame.lastAimAngle;
            punchNextFrame = playbackFrame.punch;
            if (facingRight != playbackFrame.facingRight) {
                facingRight = playbackFrame.facingRight;
                spriteGun();
            }              
            if (spriteIndex != playbackFrame.spriteIndex) {
                spriteIndex = playbackFrame.spriteIndex;
                sprite = sprites[spriteIndex];
                alphaRaster = sprite.getAlphaRaster();
            } 
            
            
        }
        if (turnOver) {      
            shootNextFrame = false;
            grenadeNextFrame = false;
            shootRopeNextFrame = false;
            leftKeyDownNextFrame = false;
            rightKeyDownNextFrame = false;
            jumpNextFrame = false;
            enterKeyDownNextFrame = false;
            upArrowKeyDownNextFrame = false;
            downArrowKeyDownNextFrame = false;
            punchNextFrame = false;
        }
        if (shootNextFrame) {
            shoot(shotPower);
            //System.out.println("shot: from " + xpos + ", " + ypos + "; power " + shotPower + "; aingle " + shootAimAngle + ". tick " + tick);
            shootNextFrame = false;
            if (recordingFrame != null) {
                recordingFrame.shoot = true;
                recordingFrame.shotPower = shotPower;
                recordingFrame.shootAimAngle = shootAimAngle;
            }
        }
        if (grenadeNextFrame) {
            shootGrenade(grenadePower);
            grenadeNextFrame = false;
            if (recordingFrame != null) {
                recordingFrame.shootGrenade = true;
                recordingFrame.grenadePower = grenadePower;
                recordingFrame.grenadeAimAngle = grenadeAimAngle;
            }
        }
        if (shootRopeNextFrame) {
            fireRope();
            shootRopeNextFrame = false;
            if (recordingFrame != null) {
                recordingFrame.shootRope = true;
                recordingFrame.ropeAimAngle = ropeAimAngle;
            }
        }
        if (detachRopeNextFrame) {
            detachRope();
            detachRopeNextFrame = false;
            if (recordingFrame != null) {
                recordingFrame.detachRope = true;
            }
        }
        if (jumpNextFrame) {
            jump(jumpPower, jumpType);
            jumpNextFrame = false;
            if (recordingFrame != null) {
                recordingFrame.jump = true;
                recordingFrame.jumpPower = jumpPower;
                recordingFrame.jumpType = jumpType;
            }
        }
        if (enterKeyDownNextFrame) {
            fireJoey();
            enterKeyDownNextFrame = false;
            if (recordingFrame != null) {
                recordingFrame.enterKeyDown = true;
            }
        }
        if (fireBlowtorchNextFrame) {
            fireBlowtorch();
            fireBlowtorchNextFrame = false;
            if (recordingFrame != null) {
                recordingFrame.fireBlowtorch = true;                
            }
        }
        upArrowKeyDown = upArrowKeyDownNextFrame;
        downArrowKeyDown = downArrowKeyDownNextFrame;
        if (recordingFrame != null) {
            recordingFrame.upArrowKeyDown = upArrowKeyDownNextFrame;
            recordingFrame.downArrowKeyDown = downArrowKeyDownNextFrame;
            recordingFrame.leftKeyDown = leftKeyDownNextFrame;
            recordingFrame.rightKeyDown = rightKeyDownNextFrame; 
            recordingFrame.blowtorchAimAngle = blowtorchAimAngle;
            // recordingTurn.frames.add(recordingFrame);
        }
        if (projectile != null) {
            projectile.update(windX, windY);
        }
        if (grenade != null) {
            grenade.update(windX, windY);
        }       
        if (joey.active) {
            joey.update(tick);
        } 
        if (leftKeyDownNextFrame && !leftKeyDown) {
            if ((!airborne && !onRope) || stuck) {
                walking = true;                
            }
            setDirection(false, levelRaster);
            leftKeyDown = true;
        }
        if (rightKeyDownNextFrame && !rightKeyDown) {
            if ((!airborne && !onRope) || stuck) {
                walking = true;                
            }
            setDirection(true, levelRaster);
            rightKeyDown = true;
        }
        if (!leftKeyDownNextFrame && leftKeyDown) {
            walking = false;
            leftKeyDown = false;
        }
        if (!rightKeyDownNextFrame && rightKeyDown) {
            walking = false;
            rightKeyDown = false;
        }
        if (flashFramesLeft > 0) flashFramesLeft--;
        if (blowtorchActive) {
            updateBlowtorch();
        }
        if (punchNextFrame) {
            if (recordingFrame != null) {
                recordingFrame.punch = true;
            }
            punchNextFrame = false;
            punch();
        }
        if (punching) {
            updatePunch();
        }

        //fire weapons if at max power
        if (recordingFrame != null) {
            if (firingGrenade && System.nanoTime() - fireGrenadeStart >= KrumGrenade.maxPower)
                endGrenadeFire(null);
            if (firing && System.nanoTime() - fireStart >= KrumProjectile.maxPower)
                endFire(null);
        }

        if (airborne) {
            double oldx = xpos;
            double oldy = ypos;
            yvel += KrumC.GRAVITY;
            yvel *= KrumC.AIR_RES_FACTOR;
            xvel *= KrumC.AIR_RES_FACTOR;
            xpos += xvel;
            ypos += yvel; 
            boolean land = false;
            boolean l = false;
            boolean r = false;
            boolean ud = false;
            boolean collision = false;            
            if (collisionCheck(levelRaster, 0) && xvel <= 0) { // left
                l = true;
                collision = true;
            } 
            if (collisionCheck(levelRaster, 1) && xvel >= 0) { // right
                r = true;
                collision = true;
            } 
            if (collisionCheck(levelRaster, 2) && yvel <= 0) { // up
                ud = true;
                collision = true;
            } 
            if (collisionCheck(levelRaster, 3) && yvel >= 0) { // down
                collision = true;
                land = true;
            }
            if (firstJumpFrame) {
                firstJumpFrame = false;
                if (collision) {
                    xpos = oldx;
                    ypos = oldy;
                    xvel = 0;
                    yvel = 0;
                    airborne = false;
                    return;
                }
            }
            if (nonDirectionalCollisionCheck(new int[] {1,1,1,1})) {
                if(stuckFrames <= 10){
                    //System.out.println("player " + playerIndex + " may be stuck");
                    collision = true;
                    stuckFrames++;
                    if (stuckFrames > 10) {
                        xvel *= -1;                    
                        System.out.println("player " + playerIndex + " IS STUCK");
                        stuck = true;
                    }
                    int inc = Math.abs(Math.max((int)xvel, (int)yvel));
                    inc++;
                    int i = inc;
                    while(nonDirectionalCollisionCheck(new int[] {0,0,0,1}) && i > 0) {
                        xpos -= xvel / inc;
                        ypos -= yvel / inc;
                        i--;
                    }
                    xvel *= 0.9;
                    yvel *= 0.9;           
                }
                else {
                    stuckFrames = 0;
                }
            } 
            else {
                stuckFrames = 0;
                stuck = false;
            }
            if ((l && xvel < 0) || (r && xvel > 0)) {
                double mag = Math.max(Math.abs(xvel) * -0.5, 0.2);
                xvel = xvel > 0 ? -mag : mag;
                if (land && !deferredLanding) {
                    land = false;   
                    deferredLanding = true;
                }               
            }            
            if (ud && (land || !deferredLanding)) {
                yvel = 0;
            }
            if (land) {
                if (firstLanding) {
                    firstLanding = false;
                }
                else if (yvel > FALL_DAMAGE_VELOCITY_THRESHOLD) {
                    fallDamage(yvel);
                }
                yvel = 0;
                xvel = 0;
                airborne = false;
                walkedOffEdge = false;
                shootingRope = false;
                wasOnRope = false;     
                canShootRope = true;    
                stuck = false;       
            }
            if (collision && wasOnRope) {
                canShootRope = false;
            }
        }
        if (walking && (!airborne || walkedOffEdge || stuck) && !blowtorchActive) {
            double origX = xpos;
            double origY = ypos;
            xpos += KrumC.WALK_SPEED * (facingRight ? 1 : -1);
            if (collisionCheck(levelRaster, (facingRight ? 1 : 0))) {
                for (int i = 0; i < KrumC.WALK_CLIMB; i++) {
                    ypos--;
                    if (!collisionCheck(levelRaster, -3)) {
                        break;
                    }
                }            
                if (collisionCheck(levelRaster, -3)) {
                    ypos = origY;
                    for (int i = 0; i < KrumC.WALK_CLIMB; i++) {
                        ypos++;
                        if (!collisionCheck(levelRaster, -3)) {
                            break;
                        }
                    }  
                }   
                if (collisionCheck(levelRaster, -3)) {
                    xpos = origX;
                    ypos = origY;
                } 
            }            
            if (!collisionCheck(levelRaster, 3)) {
                for (int i = 0; i < KrumC.WALK_CLIMB; i++) {
                    ypos++;
                    if (collisionCheck(levelRaster, 3)) break;
                }
                if (!collisionCheck(levelRaster, 3)) {
                    ypos -= KrumC.WALK_CLIMB;
                    airborne = true;
                    walkedOffEdge = true;
                }                
            }
        }
        if (shootingRope) {
            ropeLength += KrumC.ROPE_SPEED;
            Point2D.Double collisionPoint = ropeCollisionTest(0);
            if (collisionPoint != null) {
                shootingRope = false;
                onRope = true; 
                airborne = false;
                ropeAttachmentPoints.add(collisionPoint);
                if (!wasOnRope) {
                    ammo[ROPE]--;
                    firedThisTurn[ROPE]++;
                }
                wasOnRope = true;
                ropeLength = Math.sqrt((collisionPoint.x - ropeOrigin().x) * (collisionPoint.x - ropeOrigin().x) + (collisionPoint.y - ropeOrigin().y) * (collisionPoint.y - ropeOrigin().y));
            }
        }
        else if (onRope) {            
            ropeAngleRadians = Math.atan2(ypos + sprite.getHeight() / 2 - ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).y,  ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).x - xpos - sprite.getWidth() / 2);
            boolean cancelRopeLengthChange = false;
            if (upArrowKeyDown) {
                ropeLength -= KrumC.ROPE_LENGTH_SPEED;
                xpos += Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                ypos -= Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                if (nonDirectionalCollisionCheck(null)) {
                    ropeLength += KrumC.ROPE_LENGTH_SPEED;
                    xpos -= Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    ypos += Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    cancelRopeLengthChange = true;
                }
                else {
                    xvel *= KrumC.ROPE_KEY_ACCEL_FACTOR;
                    yvel *= KrumC.ROPE_KEY_ACCEL_FACTOR;
                }
            }
            if (downArrowKeyDown) {
                ropeLength += KrumC.ROPE_LENGTH_SPEED;
                xpos -= Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                ypos += Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                if (nonDirectionalCollisionCheck(null)) {
                    ropeLength -= KrumC.ROPE_LENGTH_SPEED;
                    xpos += Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    ypos -= Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    cancelRopeLengthChange = true;
                }
                else {
                    xvel /= KrumC.ROPE_KEY_ACCEL_FACTOR;
                    yvel /= KrumC.ROPE_KEY_ACCEL_FACTOR;
                }
            }
            double oldx = xpos;
            double oldy = ypos;
            yvel += KrumC.GRAVITY;
            yvel *= KrumC.AIR_RES_FACTOR;       
            xvel *= KrumC.AIR_RES_FACTOR;            
            double velMag = Math.sqrt(xvel*xvel + yvel*yvel);
            double velDir = Math.atan2(-yvel, xvel);
            double ropeVelMag = Math.abs(Math.sin(ropeAngleRadians - velDir)) * velMag;
            boolean clockwise = true;
            double ropeVelDir;            
            while (ropeAngleRadians >= 2*Math.PI) ropeAngleRadians -= 2*Math.PI;
            while (ropeAngleRadians < 0) ropeAngleRadians += 2*Math.PI;
            while (velDir >= 2*Math.PI) velDir -= 2*Math.PI;
            while (velDir < 0) velDir += 2*Math.PI;
            double d = ropeAngleRadians - velDir;
            if ((d > 0 && d < Math.PI) || (d < 0 && d < -Math.PI)) {
                clockwise = false;
            }
            if (clockwise) {
                ropeVelDir = ropeAngleRadians + Math.PI / 2;                
            }
            else {
                ropeVelDir = ropeAngleRadians - Math.PI / 2;                
            }
            double racc = KrumC.ROPE_KEY_ACCEL;
            if (leftKeyDown) {                          
                double dir = ropeVelDir;
                if (Math.cos(ropeVelDir) > 0) dir += Math.PI;
                double[] res = KrumHelpers.addVectors(ropeVelDir, ropeVelMag, dir, racc);
                ropeVelDir = res[0];
                ropeVelMag = res[1];
            } 
            if (rightKeyDown) {
                double dir = ropeVelDir;
                if (Math.cos(ropeVelDir) < 0) dir += Math.PI;
                double[] res = KrumHelpers.addVectors(ropeVelDir, ropeVelMag, dir, KrumC.ROPE_KEY_ACCEL);
                ropeVelDir = res[0];
                ropeVelMag = res[1];
            }
            clockwise = true;
            d = ropeAngleRadians - ropeVelDir;
            while (d >= 2 * Math.PI) d -= 2 * Math.PI;
            while (d < 0) d += 2 * Math.PI;
            if ((d > 0 && d < Math.PI) || (d < 0 && d < -Math.PI)) {
                clockwise = false;
            }
            ropeAngleRadians += (clockwise ? -ropeVelMag / ropeLength : ropeVelMag / ropeLength);
            xpos = ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).x + ropeLength * Math.cos(Math.PI + ropeAngleRadians) - sprite.getWidth()/2;
            ypos = ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).y - ropeLength * Math.sin(Math.PI + ropeAngleRadians) - sprite.getHeight()/2;
            //System.out.println("onrope2 " + xpos + " " + ypos + " " + xvel + " " + yvel + " " + ropeLength + " " + ropeAngleRadians + " " + spriteIndex + " " + facingRight);
            if (nonDirectionalCollisionCheck(null)) {
                //System.out.println("coll");
                xpos = oldx;
                ypos = oldy;
                ropeAngleRadians -= (clockwise ? ropeVelMag / ropeLength : -ropeVelMag / ropeLength);
                clockwise = !clockwise;
            }            
            if (clockwise) {
                ropeVelDir = ropeAngleRadians + Math.PI / 2;
            }
            else {
                ropeVelDir = ropeAngleRadians - Math.PI / 2;
            }        
            xvel = Math.cos(ropeVelDir) * ropeVelMag;
            yvel = Math.sin(ropeVelDir) * ropeVelMag * -1;
            velDir = Math.atan2(-yvel, xvel);
            Point2D.Double newAttachPoint = ropeCollisionTest(1);
            if (newAttachPoint != null) {
                double ls = ropeLength;
                ropeAttachmentPoints.add(newAttachPoint);
                ropeLength = Math.sqrt((newAttachPoint.x - ropeOrigin().x) * (newAttachPoint.x - ropeOrigin().x) + (newAttachPoint.y - ropeOrigin().y) * (newAttachPoint.y - ropeOrigin().y));
                ropeSegmentLengths.add(ls - ropeLength);
            }
            if (ropeAttachmentPoints.size() > 1) {
                while (ropeAttachmentPoints.size() > 1 && ropeCollisionTest(2) == null) {
                    double len = KrumHelpers.distanceBetween(ropeAttachmentPoints.get(ropeAttachmentPoints.size()-1).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size()-1).y, ropeAttachmentPoints.get(ropeAttachmentPoints.size()-2).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size()-2).y);
                    ropeAttachmentPoints.remove(ropeAttachmentPoints.size()-1);                    
                    ropeLength += ropeSegmentLengths.get(ropeSegmentLengths.size()-1);
                    ropeSegmentLengths.remove(ropeSegmentLengths.size()-1);
                }

            }
            //System.out.println("x " + xpos + ", y " + ypos + " fr " + facingRight + ", si " + spriteIndex + ", xvel " + xvel + ", yvel " + yvel + ". airborne " + airborne + ". end tick " + tick);
            return;
        }
    }

    void knockback(KrumProjectile p) {
        System.out.println("kbs: " + p.x + ", " + p.y + ", " + playerCentre().x + ", " + playerCentre().y + ", " + p.knockbackDistance + ", " + p.knockbackPower);
        double distance = KrumHelpers.distanceBetween(p.x, p.y, playerCentre().x, playerCentre().y);
        double angle = KrumHelpers.angleBetween(p.x, p.y, playerCentre().x, playerCentre().y);
        if (distance > p.knockbackDistance) return;
        double power = p.knockbackPower - (p.knockbackPower * distance / p.knockbackDistance);
        xvel += power * Math.cos(angle);
        double yp = (onRope || airborne) ? power * Math.sin(angle) : power * Math.max(Math.sin(angle), 0.3);
        yvel -= yp;
        System.out.println("kb: " + distance + ", " + power + ", " + angle + "; " + xvel + ", " + yvel + " " + yp);
    }


    boolean nonDirectionalCollisionCheck(int[] leeway) {
        int l, r, u, d;
        l = 0;
        r = 0;
        u = 0;
        d = 0;
        if (leeway != null) {
            if (leeway.length == 4) {
                l = leeway[0];
                r = leeway[1];
                u = leeway[2];
                d = leeway[3];
            }
        }
        for (int x = l + (facingRight ? KrumC.HITBOX_X_S : sprite.getWidth() - 1 - KrumC.HITBOX_X_F); x <= (facingRight ? KrumC.HITBOX_X_F : sprite.getWidth() - KrumC.HITBOX_X_S) - r; x++) {
            if (x + xpos < 0) continue;
            if (x + xpos >= levelRaster.getWidth()) break;
            for (int y = KrumC.HITBOX_Y_S + u; y <= KrumC.HITBOX_Y_F - d; y++) {
                if (y + ypos < 0) continue;
                if (y + ypos >= levelRaster.getHeight()) break;
                if (alphaRaster.getPixel(x, y, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                    if (levelRaster.getPixel((int)(x + xpos), (int)(y + ypos), empty)[0] > KrumC.OPACITY_THRESHOLD) {
                        //System.out.println(x + ", " + y + "; " + (int)(x + xpos) + ", " + (int)(y + ypos));
                        return true;
                    }
                }
            }
        }
        return false;
    }


    Point2D.Double ropeCollisionTest(int t) {
        Point2D.Double p = ropeOrigin();
        double len = ropeLength;
        double ang = ropeAngleRadians;
        if (t == 1) {
            len -= 2;
            if (len <= 0) return null;
        }
        if (t == 2) {
            if (ropeAttachmentPoints.size() < 2) return null;
            len = KrumHelpers.distanceBetween(ropeOrigin().x, ropeOrigin().y, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).y) - 1;
            if (len <= 0) return null;
            ang = KrumHelpers.angleBetween(ropeOrigin().x, ropeOrigin().y, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).y);
        }
        double l = len;
        for (int i = 0; i < len - 1; i++) {
            l -= 1;
            p = addVectorToCoords(p.x, p.y, ang, 1);
            if ((int)p.x < 0 || (int)p.x >= levelRaster.getWidth() || (int)p.y < 0 || (int)p.y >= levelRaster.getHeight()) {
                shootingRope = false;
                return null;
            }
            if (levelRaster.getPixel((int)p.x, (int)p.y, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                return p;
            }
        }
        p = addVectorToCoords(p.x, p.y, ang, l);
        if ((int)p.x < 0 || (int)p.x >= levelRaster.getWidth() || (int)p.y < 0 || (int)p.y >= levelRaster.getHeight()) {
            shootingRope = false;
            return null;
        }
        if (levelRaster.getPixel((int)p.x, (int)p.y, empty)[0] > KrumC.OPACITY_THRESHOLD) {
            return p;
        }
        return null;
    }

    /**
     * tests for collision between player and ground (todo: also test for collision with other player)
     * direction determines which side(s) of the sprite we test (see details below)
     * @param levelRaster
     * @param direction 0 -> left, 1 -> right, 2 -> up, 3 -> down. -1 -> only the directions we're moving toward. -2 -> every direction. -3 -> all directions except down.
     * @return true if collision detected
     */
    boolean collisionCheck(WritableRaster levelRaster, int direction){ 
        double empty[] = null;
        if (direction == 0 || (direction == -1 && xvel < 0) || direction == -2 || direction == -3) { // left
            for (int i = (facingRight ? leftEdgeTop : rightEdgeTop) + 1; i < (facingRight ? leftEdgeBottom : rightEdgeBottom) - (yvel < 0 || direction == -2 ? 20 : 1); i++) {
                if (ypos + i < 0) continue;
                if (ypos + i >= levelRaster.getHeight()) break;
                if ((int)xpos >= 0 && (int)xpos + sprite.getWidth() < levelRaster.getWidth()) {
                    int x = facingRight ? leftEdge[i] : alphaRaster.getWidth() - 1 - rightEdge[i];
                    if ((int)xpos + x >= levelRaster.getWidth()) break;
                    if ((int) xpos + x < 0) continue;
                    if (alphaRaster.getPixel(x, i, empty)[0] > KrumC.OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + x, (int)ypos + i, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        if (direction == 1 || (direction == -1 && xvel > 0) || direction == -2 || direction == -3) { // right
            for (int i = (facingRight ? rightEdgeTop : leftEdgeTop) + 1; i < (facingRight ? rightEdgeBottom : leftEdgeBottom) - (yvel < 0 || direction == -2 ? 20 : 1); i++) {
                if (ypos + i < 0) continue;
                if (ypos + i >= levelRaster.getHeight()) break;
                if ((int)xpos >= 0 && (int)xpos + sprite.getWidth() < levelRaster.getWidth()) {
                    int x = facingRight ? rightEdge[i] : alphaRaster.getWidth() - 1 - leftEdge[i];
                    if ((int)xpos + x >= levelRaster.getWidth()) break;
                    if ((int) xpos + x < 0) continue;
                    if (alphaRaster.getPixel(x, i, empty)[0] > KrumC.OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + x, (int)ypos + i, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        if (direction == 2 || (direction == -1 && yvel <= 0) || direction == -2 || direction == -3) { // up
            for (int i = (facingRight ? topEdgeLeft + 1 : sprite.getWidth() - topEdgeRight); i < (facingRight ? topEdgeRight - 1 : sprite.getWidth() - topEdgeLeft - 2); i++) {
                if (xpos + i < 0) continue;
                if (xpos + i >= levelRaster.getWidth()) break;
                if ((int)ypos >= 0 && (int)ypos < levelRaster.getHeight()) {
                    int y = facingRight ? topEdge[i] : topEdgeFlipped[i];
                    if ((int)ypos + y >= levelRaster.getHeight()) break;
                    if ((int)ypos + y < 0) continue;
                    if (alphaRaster.getPixel(i, y, empty)[0] > KrumC.OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + i, (int)ypos + y, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        if (direction == 3 || (direction == -1 && yvel >= 0) || direction == -2) { // down
            int hits = 0;
            for (int i = (facingRight ? KrumC.HITBOX_X_S + 1 : sprite.getWidth() - KrumC.HITBOX_X_F - 1); i < (facingRight ? KrumC.HITBOX_X_F : sprite.getWidth() - KrumC.HITBOX_X_S - 1); i++) {
                if (xpos + i < 0) continue;
                if (xpos + i >= levelRaster.getWidth()) break;
                if ((int)ypos >= 0 && (int)ypos + sprite.getHeight() < levelRaster.getHeight()) {
                    int y = facingRight ? bottomEdge[i] : bottomEdgeFlipped[i];
                    if ((int)ypos + y >= levelRaster.getHeight()) break;
                    if ((int)ypos + y < 0) continue;
                    if (alphaRaster.getPixel(i, y, empty)[0] > KrumC.OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + i, (int)ypos + y, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                        hits++;
                        if (hits > 1) return true;
                    }
                }
            }
        }
        return false;
    }

    void spriteLook() {    
        if (walking) {
            int oldSpriteIndex = spriteIndex;
            lastAimAngle = facingRight ? 0 : Math.PI;
            spriteIndex = facingRight ? 0 : sprites.length / 2;
            sprite = sprites[spriteIndex];
            if (spriteIndex != oldSpriteIndex)
                alphaRaster = sprite.getAlphaRaster();
            return;
        }
        //if (lastMouseX == MouseInfo.getPointerInfo().getLocation().x && lastMouseY == MouseInfo.getPointerInfo().getLocation().y)
        //    return;
        lastAimAngle = calcAimAngle();     
        //lastMouseX = MouseInfo.getPointerInfo().getLocation().x;
        //lastMouseY = MouseInfo.getPointerInfo().getLocation().y;
        if (lastAimAngle <= 3.146 && lastAimAngle >= 2.749 ) {
            spriteIndex = 5;
        } else if (lastAimAngle <= 2.749 && lastAimAngle >= 1.963 ) {
            spriteIndex = 6;
        } else if (lastAimAngle <= 1.963 && lastAimAngle >= 1.571) { 
            spriteIndex = 8;
        } else if (lastAimAngle <= 1.571 && lastAimAngle >= 1.178) {
            spriteIndex = 3;
        } else if (lastAimAngle <= 1.178 && lastAimAngle >= 0.392   ) {
            spriteIndex = 1;
        } else if (lastAimAngle <= 0.392 && lastAimAngle >= -0.392 ) {
            spriteIndex = 0;
        } else if (lastAimAngle <= -0.392 && lastAimAngle >= -1.178 ) {
            spriteIndex = 2;
        } else if (lastAimAngle <= -1.178 && lastAimAngle >= -1.571 ) {
            spriteIndex = 4;
        } else if (lastAimAngle <= -1.571 && lastAimAngle >= -1.963 ) {
            spriteIndex = 9;
        } else if (lastAimAngle <= -1.963 && lastAimAngle >= -2.749 ) {
            spriteIndex = 7;
        } else if (lastAimAngle <= -2.749 && lastAimAngle >= -3.146 ) {
            spriteIndex = 5;
        } 
        sprite = sprites[spriteIndex];
        if (Math.cos(lastAimAngle) > 0 != facingRight)
            setDirection(Math.cos(lastAimAngle) > 0, levelRaster);
        else
            alphaRaster = sprite.getAlphaRaster();
    }

    void spriteGun() {
        this.spriteGun = KrumHelpers.readSprite(spriteDir + "bazooka.png");

        int width = this.spriteGun.getWidth();
        int height = this.spriteGun.getHeight();

        BufferedImage rotatedImage = new BufferedImage(this.spriteGun.getWidth(), this.spriteGun.getHeight(),this.spriteGun.getType());
        Graphics2D g = rotatedImage.createGraphics();
        g.rotate(-lastAimAngle, width/2, height/2);
        g.drawImage(this.spriteGun, null, 0, 0);
        g.dispose();

        this.spriteGun = rotatedImage;


    };

    BufferedImage applyCustomColors(BufferedImage i) {
        int pixel;
        int red;
        int blue;
        int green;

        for (int x = 0; x < i.getWidth(); x++) {
            for (int y = 0; y < i.getHeight(); y++) {
                pixel = i.getRGB(x, y);
                red = (pixel & 0x00ff0000) >> 16;
                green = (pixel & 0x0000ff00) >> 8;
                blue = pixel & 0x000000ff;

                if (red == 255 && green == 224 && blue == 119) {
                    i.setRGB(x, y, secondaryColor.getRGB());
                } else if (red == 185 && green == 122 && blue == 87) {
                    i.setRGB(x, y, primaryColor.getRGB());
                }
            }
        }
        return i;
    }

     BufferedImage applyCustomGunColor (BufferedImage i) {
        int pixel;
        int red;
        int blue;
        int green;

        for (int x = 0; x < i.getWidth(); x++) {
            for (int y = 0; y < i.getHeight(); y++) {
                pixel = i.getRGB(x, y);
                red = (pixel & 0x00ff0000) >> 16;
                green = (pixel & 0x0000ff00) >> 8;
                blue = pixel & 0x000000ff;

                if (red == 237 && green == 28 && blue == 36) {
                    i.setRGB(x, y, gunColor.getRGB());
                } else if (red == 185 && green == 122 && blue == 87) {
                    i.setRGB(x, y, primaryColor.getRGB());
                }
            }
        }
        return i;
     }

    double calcAimAngle() {
        int mx = MouseInfo.getPointerInfo().getLocation().x - xoff;
        int my = MouseInfo.getPointerInfo().getLocation().y - yoff;
        return Math.atan2(ypos + (sprite.getHeight() / 2) - my, mx - (xpos + sprite.getWidth() / 2));
    }

    /**
     * change the direction we're facing, if we can do so without colliding with the level
     * @param right true -> face right. false -> face left
     * @param levelRaster
     */
    void setDirection(boolean right, WritableRaster levelRaster) {
        facingRight = right;
        if (right != (spriteIndex < sprites.length / 2)) {
            spriteIndex = (spriteIndex + sprites.length / 2) % sprites.length;
            sprite = sprites[spriteIndex];
        }
        alphaRaster = sprite.getAlphaRaster();
        if (levelRaster == null) return;
        if (collisionCheck(levelRaster, (facingRight ? 1 : 0))) {
            for (int i = 0; i < KrumC.WALK_CLIMB; i++) {
                ypos--;
                if (!collisionCheck(levelRaster, (facingRight ? 1 : 0))) {
                    break;
                }
            }
            if (collisionCheck(levelRaster, (facingRight ? 1 : 0))) {  
                ypos += KrumC.WALK_CLIMB;   
                spriteIndex = (spriteIndex + sprites.length / 2) % sprites.length;
                sprite = sprites[spriteIndex];
                alphaRaster = sprite.getAlphaRaster();
                facingRight = !facingRight;
                walking = false;
            }            
        }
        if (facingRight != Math.cos(lastAimAngle) > 0) {
            lastAimAngle = (Math.PI - lastAimAngle) % (Math.PI * 2);
            spriteGun();
        }
    }

    /**
     * called when jump key is pressed
     * @param type 0 = forward jump; 1 = upward jump
     */
    void startJump(int type) {
        if (airborne || jumping || onRope) return;
        jumpStart = System.nanoTime();
        jumpType = type;
        jumping = true;        
    }

    /**
     * called when jump key is released
     * @param type 0 = forward jump; 1 = upward jump
     */
    void endJump(int type) {
        if (!jumping || airborne || type != jumpType || onRope) {
            jumpStart = System.nanoTime();
            return;
        }
        jumping = false;
        jumpNextFrame = true;
        jumpType = type;
        jumpPower = System.nanoTime() - jumpStart;
      }

    /**
     * called by endJump to actually make the player jump
     * @param power number of nanoseconds the jump key was held for
     */
    void jump(long power, int type) {
        //System.out.println(power);
        power += KrumC.JUMP_MIN_POWER;
        //System.out.println(power);
        power = Math.min(power, KrumC.JUMP_MAX_POWER);
        //System.out.println(power);
        power /= 100000000;
        //System.out.println(power);
        double p = (double)power;
        p /= 2.5;
        if (type == 0) {
            xvel += p * Math.cos(facingRight ? KrumC.JUMP_ANGLE : Math.PI - KrumC.JUMP_ANGLE);
            yvel -= p * Math.sin(facingRight ? KrumC.JUMP_ANGLE : Math.PI - KrumC.JUMP_ANGLE);
        }
        else if (type == 1) {
            xvel += p * Math.cos(facingRight ? Math.PI - KrumC.JUMP_ANGLE_TWO : KrumC.JUMP_ANGLE_TWO);
            yvel -= p * Math.sin(facingRight ? Math.PI - KrumC.JUMP_ANGLE_TWO : KrumC.JUMP_ANGLE_TWO);
        }
        airborne = true;
        firstJumpFrame = true;
        deferredLanding = false;
    }

    void shootRope() {
        if (!canShootRope) return;
        if (!wasOnRope) {
            if (ammo[ROPE] < 1 || firedThisTurn[ROPE] >= shotsPerTurn[ROPE]) {
                return;
            }
        }
        shootingRope = true;        
        ropeAttachmentPoints.clear();
        walking = false;
        ropeLength = 0;
        ropeAngleRadians = ropeAimAngle;
    }

    void detachRope() {
        onRope = false;
        airborne = true;
    }

    void fireRope() {
        if (onRope) {
            detachRopeNextFrame = true;
        }
        else if (!shootingRope) {            
            if (wasOnRope) {
                double x = Math.cos(ropeAngleRadians) * -1;
                double y = Math.abs(Math.sin(ropeAngleRadians));
                ropeAimAngle = Math.atan2(y, x);                
            }
            else {
                ropeAimAngle = facingRight ? Math.PI / 4 : Math.PI * 3.0 / 4;
            }
            shootRope();
        }
    }

    void enterKeyPressed() {
        if (ammo[JOEY] > 0 && firedThisTurn[JOEY] < shotsPerTurn[JOEY]) {
            enterKeyDownNextFrame = true;
        }
    }

    void enterKeyReleased() {
        enterKeyDownNextFrame = false;
    }

    void fireJoey() {
        ammo[JOEY]--;
        firedThisTurn[JOEY]++;
        joey.spawn(playerCentre().x - joey.sprite.getWidth() / 2,playerCentre().y - joey.sprite.getHeight() / 2,xvel + 1 * (facingRight ? 1 : -1), yvel - 1, tick, facingRight);
    }

    void startGrenadeFire(MouseEvent e) {
        if (ammo[NADE] < 1 || firedThisTurn[NADE] >= shotsPerTurn[NADE]) {
            //todo: play 'click' sound effect
            return;
        }
        firingGrenade = true;
        fireGrenadeStart = System.nanoTime();
    }

    void endGrenadeFire(MouseEvent e) {
        firingGrenade = false;
        grenadePower = System.nanoTime() - fireGrenadeStart;
        grenadePower = Math.min(grenadePower, KrumGrenade.maxPower);
        grenadeNextFrame = true;
        grenadeAimAngle = calcAimAngle();
    }

    void shootGrenade(long power) {
        if (ammo[NADE] < 1) {
            return;
        }
        ammo[NADE]--;
        firedThisTurn[NADE]++;
        power /= 100000000; 
        grenade = new KrumGrenade((int)(xpos + sprite.getWidth()/2 + Math.cos(grenadeAimAngle) * KrumC.psd), (int)(ypos + sprite.getHeight() / 2 - Math.sin(grenadeAimAngle) * KrumC.psd), Math.cos(grenadeAimAngle) * power + xvel, Math.sin(grenadeAimAngle) * power * -1 + yvel, grenadeSeconds, grenadeSprite, levelRaster, tick);
    }

    /**
     * called when the shoot button is pressed
     * @param e
     */
    void startFire(MouseEvent e) {
        if (ammo[ZOOK] < 1 || firedThisTurn[ZOOK] >= shotsPerTurn[ZOOK]) {
            //todo: play 'click' sound effect
            return;
        }
        firing = true;
        fireStart = System.nanoTime();
    }

    /**
     * called when the shoot button is released
     * @param e
     */
    void endFire(MouseEvent e) {
        firing = false;
        shotPower = System.nanoTime() - fireStart;
        shotPower = Math.min(shotPower, KrumProjectile.maxPower);
        shootNextFrame = true;
        shootAimAngle = calcAimAngle();
    }

    /**
     * called in the update loop to actually launch a projectile
     * @param power
     */
    void shoot(long power) {
        if (ammo[ZOOK] < 1) {
            return;
        }
        firedThisTurn[ZOOK]++;
        ammo[ZOOK]--;
        power /= 100000000;   
        projectile = new KrumProjectile((int)(xpos + sprite.getWidth()/2 + Math.cos(shootAimAngle) * KrumC.psd), (int)(ypos + sprite.getHeight() / 2 - Math.sin(shootAimAngle) * KrumC.psd), Math.cos(shootAimAngle) * power + xvel, Math.sin(shootAimAngle) * power * -1 + yvel, projectileSprite, levelRaster);
    }

    void updateBlowtorch(){
        if (!blowtorchActive) return;
        if (blowtorchLengthening && blowtorchLength < BLOWTORCH_MAX_LENGTH) {
            blowtorchLength += BLOWTORCH_INC;
        }
        else if (blowtorchLengthening) {
            blowtorchWidening = true;
            blowtorchLengthening = false;
            blowtorchStartX = playerCentre().x;
            blowtorchStartY = playerCentre().y;
        }
        else if (blowtorchWidening || blowtorchWide) {
            KrumPlayer otherPlayer = players[1 - playerIndex];
            // destory level and hit opponent            
            // double y = (int)blowtorchStartY;
            // double x = (int)blowtorchStartX;
            int maxDrawWidth = BLOWTORCH_MAX_WIDTH / 2;
            double ex = (int)(blowtorchStartX + Math.cos(blowtorchAimAngle) * (blowtorchLength));
            double ey = (int)(blowtorchStartY - Math.sin(blowtorchAimAngle) * (blowtorchLength));
            double x = (int)(blowtorchStartX + Math.cos(blowtorchAimAngle) * (maxDrawWidth / 2));
            double y = (int)(blowtorchStartY - Math.sin(blowtorchAimAngle) * (maxDrawWidth / 2));
            double xdist = ex - x;
            double ydist = ey - y;
            if (xdist == 0) xdist = 0.000001;
            if (ydist == 0) ydist = 0.000001;
            double larger = Math.max(Math.abs(xdist), Math.abs(ydist));
            double smaller = Math.min(Math.abs(xdist), Math.abs(ydist));
            boolean xl = Math.abs(xdist) > Math.abs(ydist);
            double xinc = (xl ? 1 : Math.abs(xdist) / Math.abs(ydist));
            if (xdist < 0) xinc *= -1;
            double yinc = (xl ? Math.abs(ydist) / Math.abs(xdist) : 1);
            if (ydist < 0) yinc *= -1;
            double z[] = {0};
            System.out.println(larger);
            boolean hitopp = false;
            double[] empty = null;
            for (int i = 0; i < larger; i++) {
                //System.out.println("x " + x + ", y " + y);
                for(int xt = (int)(x - BLOWTORCH_MAX_WIDTH / 2); xt <= (int)(x + BLOWTORCH_MAX_WIDTH / 2); xt++) {
                    if (xt < 0) continue;
                    if (xt >= KrumC.RES_X) break;
                    for(int yt = (int)(y - BLOWTORCH_MAX_WIDTH / 2); yt <= (int)(y + BLOWTORCH_MAX_WIDTH / 2); yt++) {
                        if (yt < 0) continue;
                        if (yt >= KrumC.RES_Y) break;
                        if (KrumHelpers.distanceBetween(x, y, xt, yt) <= BLOWTORCH_MAX_WIDTH / 2) { // if within radius
                            if (blowtorchWidening) { 
                                    // destroy level
                                    if (levelRaster.getPixel(xt, yt, empty)[0] != KrumC.INDESTRUCTIBLE_OPACITY)
                                        levelRaster.setPixel(xt, yt, z);
                                }
                            else if (!hitopp) {
                                // hit opponent
                                if  (   xt >= otherPlayer.xpos && xt < otherPlayer.xpos + otherPlayer.alphaRaster.getWidth() 
                                    &&  yt >= otherPlayer.ypos && yt < otherPlayer.ypos + otherPlayer.alphaRaster.getHeight()
                                ) { // possible hit
                                    if (otherPlayer.alphaRaster.getPixel((int)(xt - otherPlayer.xpos), (int)(yt - otherPlayer.ypos), empty)[0] > KrumC.OPACITY_THRESHOLD) {
                                        hitopp = true;
                                    }
                                } 
                            }
                        }
                    }
                }
                x += xinc;
                y += yinc;                
            }
            if (hitopp) {
                otherPlayer.damage(KrumC.BLOWTORCH_DAMAGE);
                //double kba = KrumHelpers.angleBetween(playerCentre().x, playerCentre().y, otherPlayer.playerCentre().x, otherPlayer.playerCentre().y);
                double kba = blowtorchAimAngle;
                double kbm = KrumC.BLOWTORCH_KNOCKBACK;
                if (Math.sin(kba) < 0.3 && Math.sin(kba) > -0.707) {
                    if (Math.cos(kba) > 0) {
                        kba = Math.asin(0.3);
                    }
                    else {
                        kba = Math.PI - Math.asin(0.3);
                    }
                }
                otherPlayer.xvel += Math.cos(kba) * kbm;
                otherPlayer.yvel -= Math.sin(kba) * kbm;
                otherPlayer.airborne = true;
            }
            
            blowtorchWide = true;
            blowtorchWidening = false;

            double kba = (Math.PI + blowtorchAimAngle) % (2 * Math.PI);
            if (Math.sin(kba) < 0.3 && Math.sin(kba) > -0.707) {
                if (Math.cos(kba) > 0) {
                    kba = Math.asin(0.3);
                }
                else {
                    kba = Math.PI - Math.asin(0.3);
                }
            }
            double kbm = KrumC.BLOWTORCH_KNOCKBACK / 1.8;
            xvel += Math.cos(kba) * kbm;
            yvel -= Math.sin(kba) * kbm;
            airborne = true;
        }        
        blowtorchFrameCount++;
        if (blowtorchFrameCount > BLOWTORCH_FRAMES + 6) {
            airborne = true;
            blowtorchActive = false;
            blowtorchWide = false;
        }
            
    }

    void fireBlowtorch() {
        if (ammo[BLOW] < 1 || firedThisTurn[BLOW] >= shotsPerTurn[BLOW] || blowtorchActive) {
            return;
        }
        ammo[BLOW]--;
        firedThisTurn[BLOW]++;
        blowtorchFrameCount = 0;
        blowtorchActive = true;
        blowtorchLength = 0;
        blowtorchWidening = false;
        blowtorchLengthening = true;
        blowtorchStartX = playerCentre().x;
        blowtorchStartY = playerCentre().y;
    }

    void fireTorchNextFrame() {
        fireBlowtorchNextFrame = true;
        blowtorchAimAngle = calcAimAngle();
    }

    void updatePunch() {        
        if (punchFrame >= 15) {
            punching = false;
            return;
        }
        int frame = 0;
        if (punchFrame < 4)
            frame = 0;
        else if (punchFrame < 8)
            frame = 1;
        else if (punchFrame < 15)
            frame = 2;
        else if (punchFrame < 20)
            frame = 1;
        else if (punchFrame < 25)
            frame = 0;
        if (!facingRight) 
            frame += 3;
        punchSprite = punchSprites[frame];
        punchFrame++;

        if (punchFrame <= 16 && !punchHit) {
            punchRaster = punchSprite.getAlphaRaster();
            KrumPlayer otherPlayer = players[1 - playerIndex];
            for (int x = 0; x < punchSprite.getWidth(); x++) {
                for (int y = 0; y < punchSprite.getHeight(); y++) {
                    int rx = (int)xpos - (int)otherPlayer.xpos + x;
                    int ry = (int)ypos - (int)otherPlayer.ypos + y;
                    if (rx < 0 || rx >= otherPlayer.sprite.getWidth()) continue;
                    if (ry < 0 || ry >= otherPlayer.sprite.getHeight()) continue;
                    if (punchRaster.getPixel(x, y, empty)[0] > KrumC.OPACITY_THRESHOLD
                    && otherPlayer.alphaRaster.getPixel(rx, ry, empty)[0] < KrumC.OPACITY_THRESHOLD) {
                        punchHit = true;
                        double punchMag = KrumC.PUNCH_FORCE;
                        double punchAng = KrumC.PUNCH_ANGLE;
                        if (!facingRight)
                            punchAng = Math.PI - punchAng;
                        otherPlayer.xvel += punchMag * Math.cos(punchAng);
                        otherPlayer.yvel -= punchMag * Math.sin(punchAng);
                        otherPlayer.airborne = true;
                        otherPlayer.damage(KrumC.PUNCH_DAMAGE);
                        return;
                    }
                }
            }
        }
        
    }

    void punch() {
        if (punchedThisTurn) return;
        punchedThisTurn = true;
        punching = true;
        punchFrame = 0;
        punchHit = false;
        punchNextFrame = false;
    }



    void punchNextFrame() {
        if (!punching)
            punchNextFrame = true;
    }

    /**
     * called at start and when window is moved, to ensure mouse locations are accurately reported relative to our drawable area
     * @param x
     * @param y
     */
    public void setMouseOffsets(int x, int y) {
        this.xoff = x;
        this.yoff = y;
    }

    public void die() {
        if (dead) return;
        System.out.println("Player " + playerIndex + " died");
        dead = true;
    }

    public void damage(double damage) {
        hp -=  damage;
        if (hp <= 0)
            die(); 
        else
            flashFramesLeft += damage * 1.5;    
    }

    /**
     * called when this player is hit by a projectile
     */
    public void hit(int maxDamage, double distance, double radius) {    
        int damage = maxDamage;
        if (distance > 0) {
            damage *= (1 - distance/radius);
        }
        damage(damage);
    }


    void fallDamage(double vel) {
        damage(Math.min((int)((vel - FALL_DAMAGE_VELOCITY_THRESHOLD) * 10), FALL_DAMAGE_MAX));
    }
}
