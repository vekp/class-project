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

public class KrumPlayer {
    int hp;
    double x;
    double y;
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
    KrumPlayer(int xpos, int ypos, String spriteFileName, int panelX, int panelY) {
        this.x = xpos;
        this.y = ypos;
        this.active = false;
        this.aimAngleRadians = 0;
        this.hp = 100;
        File spriteFile = new File(spriteFileName);
        //System.out.println(spriteFile.canRead());
        try {
            sprite = ImageIO.read(spriteFile);
        }
        catch (IOException e) {
            System.out.println("error reading sprite image");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        firing = false;
        this.xoff = panelX;
        this.yoff = panelY;
        alphaRaster = sprite.getAlphaRaster();
    }
    void draw(Graphics2D g){
        if (flashFramesLeft <= 0 || flashFramesLeft % 4 == 0) {
            g.drawImage(sprite, null, (int)x, (int)y);
        }        
        if (firing) {
            int mx = MouseInfo.getPointerInfo().getLocation().x - xoff;
            int my = MouseInfo.getPointerInfo().getLocation().y - yoff;
            aimAngleRadians = Math.atan2(y - my, mx - x);
            long power = System.nanoTime() - fireStart;
            power /= 10000000;
            g.drawLine((int)(x + 15 - Math.cos(aimAngleRadians) * 10), (int)(y + 5 - Math.sin(aimAngleRadians) * 10 * -1), (int)(x + 15 + Math.cos(aimAngleRadians) * power), (int)(y + 5 + Math.sin(aimAngleRadians) * power * -1));
        }
    }
    void update(double windX, double windY){
        //System.out.println(MouseInfo.getPointerInfo().getLocation());
        if (projectile != null) {
            projectile.update(windX, windY);
        }
        if (flashFramesLeft > 0) flashFramesLeft--;
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
        //System.out.println(power);
        power /= 100000000;
        int mx = MouseInfo.getPointerInfo().getLocation().x - xoff;
        int my = MouseInfo.getPointerInfo().getLocation().y - yoff;
        aimAngleRadians = Math.atan2(y - my, mx - x);
        //System.out.println(mx);
        //System.out.println(my);
        //System.out.println(aimAngleRadians);
        projectile = new KrumProjectile((int)x + 50, (int)y + 18, Math.cos(aimAngleRadians) * power, Math.sin(aimAngleRadians) * power * -1);
    }
    public void setMouseOffsets(int x, int y) {
        this.xoff = x;
        this.yoff = y;
    }
    public void hit() {
        flashFramesLeft += 30;
    }
}
