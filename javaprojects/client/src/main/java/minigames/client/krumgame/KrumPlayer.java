package minigames.client.krumgame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.lang.Math;
import java.util.ArrayList;
import java.awt.MouseInfo;

import java.awt.event.MouseEvent;
import java.awt.image.WritableRaster;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import java.awt.geom.Point2D;

import java.awt.Color;

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

    ArrayList<Double> ropeSegmentLengths;

    int empty[];
    
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
        lastShotTime = System.nanoTime();
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
    void draw(Graphics2D g){
        if (flashFramesLeft <= 0 || flashFramesLeft % 4 == 0) {
            g.drawImage(sprite, null, (int)xpos, (int)ypos);
        }        
        if (firing) {
            aimAngleRadians = calcAimAngle(); 
            long power = System.nanoTime() - fireStart;
            power /= 10000000;
            g.setColor(Color.black);
            g.drawLine((int)shotOrigin().x, (int)shotOrigin().y, (int)(shotOrigin().x + Math.cos(aimAngleRadians) * power), (int)(shotOrigin().y - Math.sin(aimAngleRadians) * power));
        }
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
    }

    /**
     * called by KrumGame.update() every frame      
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
                xpos += xv * 35;
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
                shootingRope = false;
                wasOnRope = false;
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
        if (shootingRope) {
            ropeLength += KrumC.ROPE_SPEED;
            Point2D.Double collisionPoint = ropeCollisionTest(0);
            if (collisionPoint != null) {
                shootingRope = false;
                onRope = true;
                airborne = false;
                ropeAttachmentPoints.add(collisionPoint);
                //onRopeSpeed = Math.abs(Math.cos(ropeAngleRadians - Math.atan2(yvel, xvel)) * Math.sqrt(xvel*xvel + yvel*yvel));
                wasOnRope = true;
                ropeLength = Math.sqrt((collisionPoint.x - ropeOrigin().x) * (collisionPoint.x - ropeOrigin().x) + (collisionPoint.y - ropeOrigin().y) * (collisionPoint.y - ropeOrigin().y));
                //if (xvel < 0) onRopeSpeed *= -1;
                //System.out.println(onRopeSpeed);
            }
        }
        else if (onRope) {  
            
            
            ropeAngleRadians = Math.atan2(ypos + sprite.getHeight() / 2 - ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).y,  ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).x - xpos - sprite.getWidth() / 2);
            boolean cancelRopeLengthChange = false;
            if (upArrowKeyDown) {
                ropeLength -= KrumC.ROPE_LENGTH_SPEED;
                xpos += Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                ypos -= Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                if (nonDirectionalCollisionCheck()) {
                    ropeLength += KrumC.ROPE_LENGTH_SPEED;
                    xpos -= Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    ypos += Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    cancelRopeLengthChange = true;
                }
            }
            if (downArrowKeyDown) {
                ropeLength += KrumC.ROPE_LENGTH_SPEED;
                xpos -= Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                ypos += Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                if (nonDirectionalCollisionCheck()) {
                    ropeLength -= KrumC.ROPE_LENGTH_SPEED;
                    xpos += Math.cos(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    ypos -= Math.sin(ropeAngleRadians) * KrumC.ROPE_LENGTH_SPEED;
                    cancelRopeLengthChange = true;
                }
            }
            //System.out.println("RAR: " + ropeAngleRadians + ", " + xpos + ", " + ypos + ", " + ropeAttachmentPoints.get(ropeAttachmentPoints.size() -1).x + ", " + ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).y);
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
            //System.out.println(ropeAngleRadians + ", " + velDir + ", " + d);
            if ((d > 0 && d < Math.PI) || (d < 0 && d < -Math.PI)) {
                clockwise = false;
                //System.out.println("anti");
            }
            if (clockwise) {
                ropeVelDir = ropeAngleRadians + Math.PI / 2;                
            }
            else {
                ropeVelDir = ropeAngleRadians - Math.PI / 2;                
            }
            double racc = KrumC.ROPE_KEY_ACCEL;
            if (!cancelRopeLengthChange) {
                // if (upArrowKeyDown) racc *= KrumC.ROPE_LENGTH_ACCEL_FACTOR;
                // if (downArrowKeyDown) racc /= KrumC.ROPE_LENGTH_ACCEL_FACTOR;
            }
            if (leftKeyDown) {                          
                double dir = ropeVelDir;
                if (Math.cos(ropeVelDir) > 0) dir += Math.PI;
                //System.out.println(ropeVelDir + ", " + dir);
                double[] res = addVectors(ropeVelDir, ropeVelMag, dir, racc);
                ropeVelDir = res[0];
                ropeVelMag = res[1];
            } 
            if (rightKeyDown) {
                double dir = ropeVelDir;
                if (Math.cos(ropeVelDir) < 0) dir += Math.PI;
                //System.out.println(ropeVelDir + ", " + dir);
                double[] res = addVectors(ropeVelDir, ropeVelMag, dir, KrumC.ROPE_KEY_ACCEL);
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
            //System.out.println(velMag + ", " + velDir + ", " + ropeVelMag + ", " + clockwise + ", " + ropeVelDir + ", " + ropeAngleRadians);
            ropeAngleRadians += (clockwise ? -ropeVelMag / ropeLength : ropeVelMag / ropeLength);
            xpos = ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).x + ropeLength * Math.cos(Math.PI + ropeAngleRadians) - sprite.getWidth()/2;
            ypos = ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 1).y - ropeLength * Math.sin(Math.PI + ropeAngleRadians) - sprite.getHeight()/2;
            //System.out.println(ropeAngleRadians + ", " + xpos + ", " + ypos);
            if (nonDirectionalCollisionCheck()) {
                xpos = oldx;
                ypos = oldy;
                ropeAngleRadians -= (clockwise ? ropeVelMag / ropeLength : -ropeVelMag / ropeLength);
                clockwise = !clockwise;
                //System.out.println("ndcc: " + ", " + xpos + ", " + ypos + ", " + ropeAngleRadians + ", " + ropeVelDir);
            }            
            if (clockwise) {
                ropeVelDir = ropeAngleRadians + Math.PI / 2;
            }
            else {
                ropeVelDir = ropeAngleRadians - Math.PI / 2;
            }        
            //System.out.println(ropeVelDir);   
            xvel = Math.cos(ropeVelDir) * ropeVelMag;
            yvel = Math.sin(ropeVelDir) * ropeVelMag * -1;
            //System.out.println(xvel + ", " + yvel + ", " + ropeVelDir + ", " + ropeVelMag);
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
                    double len = distanceBetween(ropeAttachmentPoints.get(ropeAttachmentPoints.size()-1).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size()-1).y, ropeAttachmentPoints.get(ropeAttachmentPoints.size()-2).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size()-2).y);
                    ropeAttachmentPoints.remove(ropeAttachmentPoints.size()-1);                    
                    ropeLength += ropeSegmentLengths.get(ropeSegmentLengths.size()-1);
                    ropeSegmentLengths.remove(ropeSegmentLengths.size()-1);
                }

            }
            return;
        }
    }

    double[] addVectors(double dirA, double magA, double dirB, double magB) {        
        double xa = Math.cos(dirA) * magA;
        double ya = Math.sin(dirA) * magA;
        double xb = Math.cos(dirB) * magB;
        double yb = Math.sin(dirB) * magB;
        xa += xb;
        ya += yb;
        double dirR = Math.atan2(ya, xa);
        double magR = Math.sqrt(xa*xa + ya*ya);
        double result[] = {dirR, magR};
        return result;
    }

    boolean nonDirectionalCollisionCheck() {
        for (int x = KrumC.HITBOX_X_S; x <= KrumC.HITBOX_X_F; x++) {
            if (x + xpos < 0) continue;
            if (x + xpos >= levelRaster.getWidth()) break;
            for (int y = KrumC.HITBOX_Y_S; y <= KrumC.HITBOX_Y_F; y++) {
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

    double distanceBetween(double ax, double ay, double bx, double by) {
        return Math.sqrt((ax - bx)*(ax-bx)+(ay-by)*(ay-by));
    }

    double angleBetween(double ax, double ay, double bx, double by) {
        return Math.atan2(ay - by, bx - ax);
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
            len = distanceBetween(ropeOrigin().x, ropeOrigin().y, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).y) - 1;
            if (len <= 0) return null;
            ang = angleBetween(ropeOrigin().x, ropeOrigin().y, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).x, ropeAttachmentPoints.get(ropeAttachmentPoints.size() - 2).y);
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

    void shootRope() {
        shootingRope = true;
        if (wasOnRope) {
            double x = Math.cos(ropeAngleRadians) * -1;
            double y = Math.abs(Math.sin(ropeAngleRadians));
            ropeAngleRadians = Math.atan2(y, x);
        }
        else {
            //ropeAngleRadians = calcAimAngle();
            ropeAngleRadians = facingRight ? Math.PI / 4 : Math.PI * 3.0 / 4;
        }
        
        ropeAttachmentPoints.clear();
        walking = false;
        ropeLength = 0;
    }

    void detachRope() {
        onRope = false;
        airborne = true;
        //System.out.println("DETACH " + xvel + ", " + yvel);
    }

    void fireRope() {
        if (onRope) {
            detachRope();
        }
        else if (!shootingRope) {
            shootRope();
        }
    }

    void enterKeyPressed() {
        if (onRope) {
            projectile = new KrumProjectile((int)(xpos + sprite.getWidth()/2 + Math.cos(aimAngleRadians) * KrumC.psd), (int)(ypos + sprite.getHeight() / 2 - Math.sin(aimAngleRadians) * KrumC.psd), xvel, yvel);
            lastShotTime = System.nanoTime();
        }
    }

    void enterKeyReleased() {

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
        aimAngleRadians = calcAimAngle();    
        projectile = new KrumProjectile((int)(xpos + sprite.getWidth()/2 + Math.cos(aimAngleRadians) * KrumC.psd), (int)(ypos + sprite.getHeight() / 2 - Math.sin(aimAngleRadians) * KrumC.psd), Math.cos(aimAngleRadians) * power + xvel, Math.sin(aimAngleRadians) * power * -1 + yvel);
        lastShotTime = System.nanoTime();
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

    /**
     * called when this player is hit by a projectile
     */
    public void hit() {
        flashFramesLeft += 30; // currently just showing that the hit was registered by making the sprite flash
    }
}
