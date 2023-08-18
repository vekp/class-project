package minigames.client.krumgame;

public class KrumHelpers {
    static double distanceBetween(double ax, double ay, double bx, double by) {
        return Math.sqrt((ax - bx)*(ax-bx)+(ay-by)*(ay-by));
    }

    static double angleBetween(double ax, double ay, double bx, double by) {
        return Math.atan2(ay - by, bx - ax);
    }
    
    static double[] addVectors(double dirA, double magA, double dirB, double magB) {        
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

}
