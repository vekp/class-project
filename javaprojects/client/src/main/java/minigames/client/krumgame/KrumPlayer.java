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
    boolean firing;
    long fireStart;
    int xoff;
    int yoff;
    KrumProjectile projectile = null;
    WritableRaster alphaRaster;
    int flashFramesLeft = 0;
    boolean airborne;
    boolean jumping;
    long jumpStart;
    boolean facingRight;
    static final double GRAVITY = 0.05;
    static final double AIR_RES_FACTOR = 0.99;
    static final double JUMP_ANGLE = Math.PI/3.0;
    static final double JUMP_ANGLE_TWO = Math.PI/2.5;
    static final int HITBOX_X_S = 11;
    static final int HITBOX_X_F = 33;
    static final int HITBOX_Y_S = 1;
    static final int HITBOX_Y_F = 37;
    final double OPACITY_THRESHOLD = 0.4;
    AffineTransform originalAffineTransform;
    WritableRaster levelRaster;
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
    KrumPlayer(int xpos, int ypos, String spriteFileName, int panelX, int panelY, boolean direction, WritableRaster level) {
        topEdgeLeft = -1;
        topEdgeRight = -1;
        bottomEdgeLeft = -1;
        bottomEdgeRight = -1;
        leftEdgeBottom = -1;
        leftEdgeTop = -1;
        rightEdgeBottom = -1;
        rightEdgeTop = -1;
        ////System.out.println("hxs " + HITBOX_X_S);
        this.xpos = xpos;
        this.ypos = ypos;
        this.active = false;
        this.aimAngleRadians = 0;
        this.hp = 100;
        File spriteFile = new File(spriteFileName);
        //////System.out.println(spriteFile.canRead());
        try {
            sprite = ImageIO.read(spriteFile);
        }
        catch (IOException e) {
            ////System.out.println("error reading sprite image");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        firing = false;
        this.xoff = panelX;
        this.yoff = panelY;
        alphaRaster = sprite.getAlphaRaster();
        airborne = true;
        jumping = false;
        jumpStart = 0;
        
        int empty[] = null;
        leftEdge = new int[alphaRaster.getHeight()];
        rightEdge = new int[alphaRaster.getHeight()];
        topEdge = new int[alphaRaster.getWidth()];
        bottomEdge = new int[alphaRaster.getWidth()];
        topEdgeFlipped = new int[alphaRaster.getWidth()];
        bottomEdgeFlipped = new int[alphaRaster.getWidth()];
        facingRight = true;
        deferredLanding = false;
        ////System.out.println("let s " + leftEdgeTop);
        for (int y = HITBOX_Y_S; y <= HITBOX_Y_F; y++) {
            ////System.out.println("Y " + y);
            int x = HITBOX_X_S;
            while (x < HITBOX_X_F && alphaRaster.getPixel(x,y,empty)[0] <= OPACITY_THRESHOLD) {
                x++;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > OPACITY_THRESHOLD) {
                if (leftEdgeTop < 0) {
                    leftEdgeTop = y;
                    ////System.out.println("let " + leftEdgeTop);
                }
                leftEdgeBottom = y;
            }
            leftEdge[y] = x;
            x = HITBOX_X_F;
            while (x > HITBOX_X_S && alphaRaster.getPixel(x,y,empty)[0] <= OPACITY_THRESHOLD) {
                x--;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > OPACITY_THRESHOLD) {
                if (rightEdgeTop < 0) {
                    rightEdgeTop = y;
                }
                rightEdgeBottom = y;
            }
            rightEdge[y] = x;
        }
        for (int x = HITBOX_X_S; x <= HITBOX_X_F; x++) {
            int y = HITBOX_Y_S;
            while (y < HITBOX_Y_F && alphaRaster.getPixel(x,y,empty)[0] <= OPACITY_THRESHOLD) {
                y++;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > OPACITY_THRESHOLD) {
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
                //topEdge[x] = y;
            }            
            y = HITBOX_Y_F;
            while (y > HITBOX_Y_S && alphaRaster.getPixel(x,y,empty)[0] <= OPACITY_THRESHOLD) {
                y--;
            }
            if (alphaRaster.getPixel(x,y,empty)[0] > OPACITY_THRESHOLD) {
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
                //topEdge[x] = y;
            } 
        }
        for (int i = 0; i < topEdge.length; i++) {
            topEdgeFlipped[i] = topEdge[topEdge.length - 1 - i];
            bottomEdgeFlipped[i] = bottomEdge[bottomEdge.length - 1 - i];
            ////System.out.println(bottomEdge[i]);
        }
        ////System.out.println("s");
        ////System.out.println(leftEdgeTop);
        ////System.out.println(leftEdgeBottom);
        ////System.out.println(rightEdgeTop);
        ////System.out.println(rightEdgeBottom);
        ////System.out.println(topEdgeLeft);
        ////System.out.println(topEdgeRight);
        ////System.out.println(bottomEdgeLeft);
        ////System.out.println(bottomEdgeRight);
        ////System.out.println("e");
        setDirection(direction, level);
    }
    void draw(Graphics2D g){
        if (flashFramesLeft <= 0 || flashFramesLeft % 4 == 0) {
            g.drawImage(sprite, null, (int)xpos, (int)ypos);
        }        
        if (firing) {
            int mx = MouseInfo.getPointerInfo().getLocation().x - xoff;
            int my = MouseInfo.getPointerInfo().getLocation().y - yoff;
            aimAngleRadians = Math.atan2(ypos - my, mx - xpos);
            long power = System.nanoTime() - fireStart;
            power /= 10000000;
            g.drawLine((int)(xpos + 45 - Math.cos(aimAngleRadians) * 10), (int)(ypos + 15 - Math.sin(aimAngleRadians) * 10 * -1), (int)(xpos + 45 + Math.cos(aimAngleRadians) * power), (int)(ypos + 15 + Math.sin(aimAngleRadians) * power * -1));
        }
    }
    void update(double windX, double windY, WritableRaster levelRaster){
        //////System.out.println(MouseInfo.getPointerInfo().getLocation());
        if (projectile != null) {
            projectile.update(windX, windY);
        }
        if (flashFramesLeft > 0) flashFramesLeft--;
        if (airborne) {
            double oldx = xpos;
            double oldy = ypos;
            yvel += GRAVITY;
            yvel *= AIR_RES_FACTOR;
            xvel *= AIR_RES_FACTOR;
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
            if (collision){
                int inc = (xvel > 3 || yvel > 3) ? 50 : 20;
                while (collisionCheck(levelRaster, -1)) { // any direction
                    ypos -= yvel / inc;
                    xpos -= xvel / inc;
                    //System.out.println("collision loop");
                }
            }
            // if ((l || r) && !deferredLanding && land) {
            //     deferredLanding = true;
            //     land = false;
            // }
            if ((l && xvel < 0) || (r && xvel > 0)) {
                double mag = Math.max(Math.abs(xvel) * -0.1, 0.2);
                //xvel *= (jumpType == 1 ? -1 : -0.1);
                xvel = xvel > 0 ? -mag : mag;
                //xpos += xvel * 7;
                if (land && !deferredLanding) {
                    land = false;   
                    deferredLanding = true;
                    //System.out.println("def");
                }               
                //System.out.println("xvel " + xvel + " def " + deferredLanding);
            }
            if (ud && (land || !deferredLanding)) {
                yvel = 0;
            }
            if (land) {
                yvel = 0;
                xvel = 0;
                airborne = false;
            }
            ////System.out.println(xvel + ", " + yvel);
        }
    }
    boolean collisionCheck(WritableRaster levelRaster, int direction){ 
        int empty[] = null;
        if (direction == 0 || (direction == -1 && xvel < 0) || direction == -2) { // left
            for (int i = (facingRight ? leftEdgeTop : rightEdgeTop) + 1; i < (facingRight ? leftEdgeBottom : rightEdgeBottom) - (yvel < 0 || direction == -2 ? 20 : 1); i++) {
                if (ypos + i < 0) continue;
                if (ypos + i >= levelRaster.getHeight()) break;
                if ((int)xpos >= 0 && (int)xpos < levelRaster.getWidth()) {
                    int x = facingRight ? leftEdge[i] : alphaRaster.getWidth() - 1 - rightEdge[i];
                    //////System.out.println("x: " + x);
                    if (alphaRaster.getPixel(x, i, empty)[0] > OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + x, (int)ypos + i, empty)[0] > OPACITY_THRESHOLD) {
                        //System.out.println("left");
                        return true;
                    }
                }
            }
        }
        else if (direction == 1 || (direction == -1 && xvel > 0) || direction == -2) { // right
            for (int i = (facingRight ? rightEdgeTop : leftEdgeTop) + 1; i < (facingRight ? rightEdgeBottom : leftEdgeBottom) - (yvel < 0 || direction == -2 ? 20 : 1); i++) {
                if (ypos + i < 0) continue;
                if (ypos + i >= levelRaster.getHeight()) break;
                if ((int)xpos + sprite.getWidth() - 2 >= 0 && (int)xpos + sprite.getWidth() - 2 < levelRaster.getWidth()) {
                    int x = facingRight ? rightEdge[i] : alphaRaster.getWidth() - 1 - leftEdge[i];
                    //////System.out.println("xr: " + x);
                    if (alphaRaster.getPixel(x, i, empty)[0] > OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + x, (int)ypos + i, empty)[0] > OPACITY_THRESHOLD) {
                        //System.out.println("right " + x + ", " + i);
                        return true;
                    }
                }
            }
        }
        else if (direction == 2 || (direction == -1 && yvel <= 0) || direction == -2) { // up
            for (int i = (facingRight ? topEdgeLeft + 1 : sprite.getWidth() - topEdgeRight); i < (facingRight ? topEdgeRight - 1 : sprite.getWidth() - topEdgeLeft - 2); i++) {
                if (xpos + i < 0) continue;
                if (xpos + i >= levelRaster.getWidth()) break;
                if ((int)ypos >= 0 && (int)ypos < levelRaster.getHeight()) {
                    int y = facingRight ? topEdge[i] : topEdgeFlipped[i];
                    if (alphaRaster.getPixel(i, y, empty)[0] > OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + i, (int)ypos + y, empty)[0] > OPACITY_THRESHOLD) {
                        //System.out.println("up " + i + ", " +y);
                        return true;
                    }
                }
            }
        }
        else if (direction == 3 || (direction == -1 && yvel >= 0) || direction == -2) { // down
            int hits = 0;
            for (int i = (facingRight ? HITBOX_X_S + 1 : sprite.getWidth() - HITBOX_X_F - 1); i < (facingRight ? HITBOX_X_F : sprite.getWidth() - HITBOX_X_S - 1); i++) {
                if (xpos + i < 0) continue;
                if (xpos + i >= levelRaster.getWidth()) break;
                if ((int)ypos >= 0 && (int)ypos + sprite.getHeight() < levelRaster.getHeight()) {
                    int y = facingRight ? bottomEdge[i] : bottomEdgeFlipped[i];
                    ////System.out.println("d " + i + ", " + y);
                    if (alphaRaster.getPixel(i, y, empty)[0] > OPACITY_THRESHOLD && levelRaster.getPixel((int)xpos + i, (int)ypos + y, empty)[0] > OPACITY_THRESHOLD) {
                        //System.out.println("down");
                        //System.out.println("dc " + i + ", " + y);
                        hits++;
                        if (hits > 1) return true;
                        //return true;
                    }
                }
            }
        }
        //System.out.println("none");
        return false;
    }
    void setDirection(boolean right, WritableRaster levelRaster) {
        ////System.out.println(right + ", " + facingRight);
        if (right == facingRight) return;
        facingRight = right;
        //AffineTransform tx = (right ? AffineTransform.getScaleInstance(1, 1) : AffineTransform.getScaleInstance(-1, 1)); 
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1); 
        tx.translate(-sprite.getWidth(null), 0);        
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);        
        sprite = op.filter(sprite, null);
        alphaRaster = sprite.getAlphaRaster();
        if (collisionCheck(levelRaster, -2)) {
            ////System.out.println("X");
            facingRight = !facingRight;                  
            sprite = op.filter(sprite, null);
            alphaRaster = sprite.getAlphaRaster();
        }
    }
    void startJump(int type) {
        if (airborne || jumping) return;
        jumpType = type;
        jumping = true;
        jumpStart = System.nanoTime();
    }
    void endJump(int type) {
        if (airborne || type != jumpType) return;
        jumping = false;
        jump(System.nanoTime() - jumpStart);
    }
    void jump(long power) {
        //////System.out.println(power);
        power /= 100000000;
        //////System.out.println(power);
        double p = (double)power;
        p /= 2.5;
        //////System.out.println(p);
        if (jumpType == 0) {
            xvel += p * Math.cos(facingRight ? JUMP_ANGLE : Math.PI - JUMP_ANGLE);
            yvel -= p * Math.sin(facingRight ? JUMP_ANGLE : Math.PI - JUMP_ANGLE);
        }
        else if (jumpType == 1) {
            xvel += p * Math.cos(facingRight ? Math.PI - JUMP_ANGLE_TWO : JUMP_ANGLE_TWO);
            yvel -= p * Math.sin(facingRight ? Math.PI - JUMP_ANGLE_TWO : JUMP_ANGLE_TWO);
        }

        //////System.out.println(xvel + ", " + yvel);
        airborne = true;
        firstJumpFrame = true;
        deferredLanding = false;
    }
    void startFire(MouseEvent e) {
        firing = true;
        fireStart = System.nanoTime();
    }
    void endFire(MouseEvent e) {
        firing = false;
        shoot(System.nanoTime() - fireStart);
    }
    void shoot(long power) {
        //////System.out.println(power);
        ////System.out.println(power);
        power /= 100000000;
        int mx = MouseInfo.getPointerInfo().getLocation().x - xoff;
        int my = MouseInfo.getPointerInfo().getLocation().y - yoff;
        aimAngleRadians = Math.atan2(ypos - my, mx - xpos);
        //////System.out.println(mx);
        //////System.out.println(my);
        //////System.out.println(aimAngleRadians);
        projectile = new KrumProjectile((int)xpos + 50, (int)ypos + 18, Math.cos(aimAngleRadians) * power + xvel, Math.sin(aimAngleRadians) * power * -1 + yvel);
    }
    public void setMouseOffsets(int x, int y) {
        this.xoff = x;
        this.yoff = y;
    }
    public void hit() {
        flashFramesLeft += 30;
    }
}
