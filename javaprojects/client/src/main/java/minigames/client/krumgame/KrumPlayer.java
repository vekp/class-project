package minigames.client.krumgame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.lang.Math;
import java.awt.MouseInfo;

import java.awt.event.MouseEvent;
import java.awt.image.WritableRaster;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class KrumPlayer {
    int hp;
    double xpos;
    double ypos;
    double xvel;
    double yvel;

    boolean active;
    double aimAngleRadians;

    BufferedImage sprite;
    WritableRaster alphaRaster; // alpha values (opacities) of player sprite pixels
    WritableRaster levelRaster; // alpha values of level pixels

    boolean firing;
    long fireStart;
    int xoff; // accounts for position of window on user's screen. reset when window is moved
    int yoff; // accounts for position of window on user's screen. reset when window is moved

    KrumProjectile projectile = null;
    
    int flashFramesLeft = 0;

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

    long lastShotTime;
    
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
    KrumPlayer(int xpos, int ypos, String spriteFileName, int panelX, int panelY, boolean direction, WritableRaster level) {
        topEdgeLeft = -1;
        topEdgeRight = -1;
        bottomEdgeLeft = -1;
        bottomEdgeRight = -1;
        leftEdgeBottom = -1;
        leftEdgeTop = -1;
        rightEdgeBottom = -1;
        rightEdgeTop = -1;
        walkedOffEdge = false;
        this.xpos = xpos;
        this.ypos = ypos;
        this.active = false;
        this.aimAngleRadians = 0;
        this.hp = 100;

        File spriteFile = new File(KrumC.imgDir + spriteFileName);
        try {
            sprite = ImageIO.read(spriteFile);
        }
        catch (IOException e) {
            System.out.println("can't load sprite");          
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        alphaRaster = sprite.getAlphaRaster();

        firing = false;
        this.xoff = panelX;
        this.yoff = panelY;
        
        airborne = true;
        jumping = false;
        jumpStart = 0;        
        int empty[] = null; // dummy array for use when calling getPixel

        facingRight = true;
        deferredLanding = false;
        walking = false;
        
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
        
        lastShotTime = System.nanoTime();
    }

    /**
     * called by KrumGame.draw() every time a frame is painted
     * @param g
     */
    void draw(Graphics2D g){
        int mx = MouseInfo.getPointerInfo().getLocation().x - xoff;
        int my = MouseInfo.getPointerInfo().getLocation().y - yoff;
        if (flashFramesLeft <= 0 || flashFramesLeft % 4 == 0) {
            g.drawImage(sprite, null, (int)xpos, (int)ypos);
        }        
        if (firing) {
            aimAngleRadians = Math.atan2(ypos - my, mx - xpos);
            long power = System.nanoTime() - fireStart;
            power /= 10000000;
            g.drawLine((int)(xpos + sprite.getWidth()/2 + Math.cos(aimAngleRadians) * KrumC.psd), (int)(ypos + sprite.getWidth()/2 - Math.sin(aimAngleRadians) * KrumC.psd), (int)(xpos + sprite.getWidth()/2 + Math.cos(aimAngleRadians) * (power + KrumC.psd)), (int)(ypos + sprite.getWidth()/2 - Math.sin(aimAngleRadians) * (KrumC.psd + power)));
        }
    }

    /**
     * called by KrumGame.update() every frame     * 
     * @param windX
     * @param windY
     * @param levelRaster
     */
    void update(double windX, double windY, WritableRaster levelRaster){
        if (projectile != null) {
            projectile.update(windX, windY);
        }
        if (flashFramesLeft > 0) flashFramesLeft--;
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
            if ((l && xvel < 0) || (r && xvel > 0)) {                
                double mag = Math.max(Math.abs(xvel) * -0.1, 0.2);
                double xv = xvel > 0 ? -mag : mag;
                xpos += xv * 25;
            }
            if (collision){
                int inc = (xvel > 3 || yvel > 3) ? 50 : 20;
                if (collisionCheck(levelRaster, 3)) {
                    for (int i = 0; i < inc; i ++) {
                        ypos -= KrumC.WALK_CLIMB / inc;
                        if(!collisionCheck(levelRaster, 3)) break;
                    }
                }                
                while (collisionCheck(levelRaster, -3)) {
                    ypos -= yvel / inc;
                    xpos -= xvel / inc;
                }                
            }
            if ((l && xvel < 0) || (r && xvel > 0)) {
                double mag = Math.max(Math.abs(xvel) * -0.1, 0.2);
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
                yvel = 0;
                xvel = 0;
                airborne = false;
                walkedOffEdge = false;
            }
        }
        if (walking && (!airborne || walkedOffEdge)) {
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
    }

    /**
     * tests for collision between player and ground (todo: also test for collision with other player)
     * direction determines which side(s) of the sprite we test (see details below)
     * @param levelRaster
     * @param direction 0 -> left, 1 -> right, 2 -> up, 3 -> down. -1 -> only the directions we're moving toward. -2 -> every direction. -3 -> all directions except down.
     * @return true if collision detected
     */
    boolean collisionCheck(WritableRaster levelRaster, int direction){ 
        int empty[] = null;
        if (direction == 0 || (direction == -1 && xvel < 0) || direction == -2 || direction == -3) { // left
            for (int i = (facingRight ? leftEdgeTop : rightEdgeTop) + 1; i < (facingRight ? leftEdgeBottom : rightEdgeBottom) - (yvel < 0 || direction == -2 ? 20 : 1); i++) {
                if (ypos + i < 0) continue;
                if (ypos + i >= levelRaster.getHeight()) break;
                if ((int)xpos >= 0 && (int)xpos + sprite.getWidth() < levelRaster.getWidth()) {
                    int x = facingRight ? leftEdge[i] : alphaRaster.getWidth() - 1 - rightEdge[i];
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
                    if (alphaRaster.getPixel(i, y, empty)[0] > KrumC.OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + i, (int)ypos + y, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                        hits++;
                        if (hits > 1) return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * change the direction we're facing, if we can do so without colliding with the level
     * @param right true -> face right. false -> face left
     * @param levelRaster
     */
    void setDirection(boolean right, WritableRaster levelRaster) {
        if (right == facingRight) return;
        facingRight = right;
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1); 
        tx.translate(-sprite.getWidth(null), 0);        
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);        
        sprite = op.filter(sprite, null);
        alphaRaster = sprite.getAlphaRaster();
        if (collisionCheck(levelRaster, (facingRight ? 1 : 0))) {
            facingRight = !facingRight;                  
            sprite = op.filter(sprite, null);
            alphaRaster = sprite.getAlphaRaster();
            walking = false;
        }
    }

    /**
     * called when jump key is pressed
     * @param type currently unused -- was for distinguishing between forward jump and backflip
     */
    void startJump(int type) {
        if (airborne || jumping) return;
        jumpStart = System.nanoTime();
        jumpType = type;
        jumping = true;        
    }

    /**
     * called when jump key is released
     * @param type currently unused -- was for distinguishing between forward jump and backflip
     */
    void endJump(int type) {
        if (!jumping || airborne || type != jumpType) {
            jumpStart = System.nanoTime();
            return;
        }
        jumping = false;
        jump(System.nanoTime() - jumpStart);
    }

    /**
     * called by endJump to actually make the player jump
     * @param power number of nanoseconds the jump key was held for
     */
    void jump(long power) {
        power /= 100000000;
        double p = (double)power;
        p /= 2.5;
        if (jumpType == 0) {
            xvel += p * Math.cos(facingRight ? KrumC.JUMP_ANGLE : Math.PI - KrumC.JUMP_ANGLE);
            yvel -= p * Math.sin(facingRight ? KrumC.JUMP_ANGLE : Math.PI - KrumC.JUMP_ANGLE);
        }
        else if (jumpType == 1) {
            xvel += p * Math.cos(facingRight ? Math.PI - KrumC.JUMP_ANGLE_TWO : KrumC.JUMP_ANGLE_TWO);
            yvel -= p * Math.sin(facingRight ? Math.PI - KrumC.JUMP_ANGLE_TWO : KrumC.JUMP_ANGLE_TWO);
        }
        airborne = true;
        firstJumpFrame = true;
        deferredLanding = false;
    }

    /**
     * called when the shoot button is pressed
     * @param e
     */
    void startFire(MouseEvent e) {
        firing = true;
        fireStart = System.nanoTime();
    }

    /**
     * called when the shoot button is released
     * @param e
     */
    void endFire(MouseEvent e) {
        firing = false;
        shoot(System.nanoTime() - fireStart);
    }

    /**
     * called by endFire to actually launch a projectile
     * @param power
     */
    void shoot(long power) {
        power /= 100000000;
        int mx = MouseInfo.getPointerInfo().getLocation().x - xoff;
        int my = MouseInfo.getPointerInfo().getLocation().y - yoff;
        aimAngleRadians = Math.atan2(ypos - my, mx - xpos);    
        projectile = new KrumProjectile((int)(xpos + sprite.getWidth()/2 + Math.cos(aimAngleRadians) * KrumC.psd), (int)(ypos + sprite.getHeight() / 2 - Math.sin(aimAngleRadians) * KrumC.psd), Math.cos(aimAngleRadians) * power + xvel, Math.sin(aimAngleRadians) * power * -1 + yvel);
        lastShotTime = System.nanoTime();
    }

    /**
     * called when window is moved, to ensure mouse locations are accurately reported relative to our drawable area
     * @param x
     * @param y
     */
    public void setMouseOffsets(int x, int y) {
        this.xoff = x;
        this.yoff = y;
    }

    /**
     * called when this player is hit by a projectile
     */
    public void hit() {
        flashFramesLeft += 30; // currently just showing that the hit was registered by making the sprite flash
    }
}
