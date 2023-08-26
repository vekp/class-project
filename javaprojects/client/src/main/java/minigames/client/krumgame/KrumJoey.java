package minigames.client.krumgame;

import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.Graphics2D;

/*
 * Joey bounces along, changing direction when it hits a wall. After a brief
 * delay, proximity to the enemy player makes the joey explode very soon.
 * Otherwise it explodes when its timer runs out.
 */

public class KrumJoey extends KrumProjectile {
    final int PROXIMITY_THRESHOLD = 60; // distance from oppoent (in pixels) at which proximity explosion is triggered
    final int PROXIMITY_DELAY = 32; // delay (in frames) between proximity explosion being triggered and actual explosion
    final int PROXIMITY_BEGIN = 90; // delay (in frames after launch) before proximity explosion can be triggered
    final int HITBOX_X_S = 5;
    final int HITBOX_X_F = 17;
    final int HITBOX_Y_S = 0;
    final int HITBOX_Y_F = 19;
    final double KNOCKBACK_DISTANCE = 75;
    final double KNOCKBACK_POWER = 8;
    final int EXPLOSION_RADIUS = 50;
    final int TIMER_SECONDS = 6;
    final int MAX_DAMAGE = 30;
    long explosionTick;
    long startTick;
    double jumpPower = 3;
    KrumPlayer otherPlayer;
    boolean facingRight;
    double xpos;
    double ypos;
    int empty[];
    int leftEdge[];
    int rightEdge[];
    int topEdge[];
    int bottomEdge[];
    int topEdgeFlipped[];
    int bottomEdgeFlipped[];
    int topEdgeLeft, topEdgeRight, bottomEdgeLeft, bottomEdgeRight, leftEdgeBottom, leftEdgeTop, rightEdgeBottom, rightEdgeTop;
    WritableRaster levelRaster;
    WritableRaster alphaRaster;    
    boolean firstJumpFrame;
    boolean deferredLanding;
    boolean active;    
    boolean flash;
    long currentTick;
    KrumJoey(int xpos, int ypos, double xvel, double yvel, int seconds, BufferedImage sprite, WritableRaster ground, long tick) {
        super(xpos, ypos, xvel, yvel, sprite, ground);
        maxDamage = MAX_DAMAGE;
        this.flash = false;
        this.facingRight = true;
        this.xpos = xpos;
        this.ypos = ypos;
        knockbackDistance = KNOCKBACK_DISTANCE;
        knockbackPower = KNOCKBACK_POWER;
        explosionRadius = EXPLOSION_RADIUS; 
        explosionTick = tick + KrumC.TARGET_FRAMERATE * TIMER_SECONDS;
        topEdgeLeft = -1;
        topEdgeRight = -1;
        bottomEdgeLeft = -1;
        bottomEdgeRight = -1;
        leftEdgeBottom = -1;
        leftEdgeTop = -1;
        rightEdgeBottom = -1;
        rightEdgeTop = -1;
        levelRaster = this.ground;
        this.alphaRaster = sprite.getAlphaRaster();
        leftEdge = new int[alphaRaster.getHeight()];
        rightEdge = new int[alphaRaster.getHeight()];
        topEdge = new int[alphaRaster.getWidth()];
        bottomEdge = new int[alphaRaster.getWidth()];
        topEdgeFlipped = new int[alphaRaster.getWidth()];
        bottomEdgeFlipped = new int[alphaRaster.getWidth()];     
        for (int y = HITBOX_Y_S; y <= HITBOX_Y_F; y++) {
            int x = HITBOX_X_S;
            while (x < HITBOX_X_F && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
                x++;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {
                if (leftEdgeTop < 0) {
                    leftEdgeTop = y;
                }
                leftEdgeBottom = y;
            }
            leftEdge[y] = x;
            x = HITBOX_X_F;
            while (x > HITBOX_X_S && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
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
        for (int x = HITBOX_X_S; x <= HITBOX_X_F; x++) {
            int y = HITBOX_Y_S;
            while (y < HITBOX_Y_F && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
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
            y = HITBOX_Y_F;
            while (y > HITBOX_Y_S && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
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
        leftEdge = new int[alphaRaster.getHeight()];
        rightEdge = new int[alphaRaster.getHeight()];
        topEdge = new int[alphaRaster.getWidth()];
        bottomEdge = new int[alphaRaster.getWidth()];
        topEdgeFlipped = new int[alphaRaster.getWidth()];
        bottomEdgeFlipped = new int[alphaRaster.getWidth()];     
        for (int y = HITBOX_Y_S; y <= HITBOX_Y_F; y++) {
            int x = HITBOX_X_S;
            while (x < HITBOX_X_F && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
                x++;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > KrumC.OPACITY_THRESHOLD) {
                if (leftEdgeTop < 0) {
                    leftEdgeTop = y;
                }
                leftEdgeBottom = y;
            }
            leftEdge[y] = x;
            x = HITBOX_X_F;
            while (x > HITBOX_X_S && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
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
        for (int x = HITBOX_X_S; x <= HITBOX_X_F; x++) {
            int y = HITBOX_Y_S;
            while (y < HITBOX_Y_F && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
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
            y = HITBOX_Y_F;
            while (y > HITBOX_Y_S && alphaRaster.getPixel(x,y,empty)[0] <= KrumC.OPACITY_THRESHOLD) {
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
    } 

    /**
     * 
     * @return  True if joey overlaps any level pixels
     */
    boolean nonDirectionalCollisionCheck() {
        for (int x = HITBOX_X_S; x <= HITBOX_X_F; x++) {
            if (x + xpos < 0) continue;
            if (x + xpos >= levelRaster.getWidth()) break;
            for (int y = HITBOX_Y_S; y <= HITBOX_Y_F; y++) {
                if (y + ypos < 0) continue;
                if (y + ypos >= levelRaster.getHeight()) break;
                if (alphaRaster.getPixel(x, y, empty)[0] > KrumC.OPACITY_THRESHOLD) {
                    if (levelRaster.getPixel((int)(x + xpos), (int)(y + ypos), empty)[0] > KrumC.OPACITY_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Called when player launches a joey
     * @param xpos
     * @param ypos
     * @param xvel
     * @param yvel
     * @param tick
     * @param facingRight
     */
    void spawn(double xpos, double ypos, double xvel, double yvel, long tick, boolean facingRight){
        this.xvel = xvel;
        this.yvel = yvel;
        exploding = false;   
        this.xpos = xpos;
        this.ypos = ypos;
        explosionTick = tick + KrumC.TARGET_FRAMERATE * TIMER_SECONDS;
        this.active = true;
        this.setDirection(facingRight, levelRaster);
        this.currentTick = tick;
        this.flash = false;
        startTick = tick;
    }
    @Override 
    void draw(Graphics2D g) {
        if (!flash || currentTick % 4 < 2) {
            g.drawImage(sprite, null, (int)xpos, (int)ypos);
        }        
    }

    double[] centre(){
        return new double[] {xpos + sprite.getWidth() / 2, ypos + sprite.getHeight() / 2};
    }

    void update(long tick) {
        this.currentTick = tick;
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
                 return;
            }
        }
        if (nonDirectionalCollisionCheck()){
            int inc = Math.abs(Math.max((int)xvel, (int)yvel));
            inc++;
            int i = inc;
            while(nonDirectionalCollisionCheck() && i > 0) {
                x -= xvel / inc;
                y -= yvel / inc;
                i--;
            }            
        }
        if ((l && xvel < 0) || (r && xvel > 0)) {                
            xvel = -xvel;
            setDirection(!facingRight, levelRaster);
        }         
        if (ud && (land || !deferredLanding)) {
            yvel = 0;
        }
        if (land) {
            yvel = 0;
            jump();
        }
        if (!flash && KrumHelpers.distanceBetween(xpos + sprite.getWidth() / 2, ypos + sprite.getHeight() / 2, otherPlayer.playerCentre().x, otherPlayer.playerCentre().y) < PROXIMITY_THRESHOLD) {
            if (currentTick - startTick > PROXIMITY_BEGIN) {
                explosionTick = Math.min(explosionTick, tick + PROXIMITY_DELAY);
                flash = true;
            }           
        }
    }
    void setDirection(boolean right, WritableRaster levelRaster) {
        if (right == facingRight) return;        
        facingRight = right;
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1); 
        tx.translate(-sprite.getWidth(null), 0);        
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);        
        sprite = op.filter(sprite, null);
        alphaRaster = sprite.getAlphaRaster();
        if (levelRaster == null) return;
        if (collisionCheck(levelRaster, (facingRight ? 1 : 0))) {
            facingRight = !facingRight;                  
            sprite = op.filter(sprite, null);
            alphaRaster = sprite.getAlphaRaster();
        }
    }
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
            for (int i = (facingRight ? HITBOX_X_S + 1 : sprite.getWidth() - HITBOX_X_F - 1); i < (facingRight ? HITBOX_X_F : sprite.getWidth() - HITBOX_X_S - 1); i++) {
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
    void jump() {
        double p = jumpPower;
        xvel += p * Math.cos(facingRight ? 3 * Math.PI / 8 : Math.PI - 3 * Math.PI / 8);
        yvel -= p * Math.sin(facingRight ? 3 * Math.PI / 8 : Math.PI - 3 * Math.PI / 8);
        firstJumpFrame = true;
        deferredLanding = false;
    }
    boolean timerCheck(long tick) {
        return tick >= explosionTick;
    }
}
