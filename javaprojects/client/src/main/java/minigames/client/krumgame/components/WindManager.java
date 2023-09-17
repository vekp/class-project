package minigames.client.krumgame.components;

import java.util.Random;

public class WindManager{
    private double windX;
    private double windY;
    private String windString;
    private Random rand;
    private boolean onMoon;


    public WindManager(long seed, boolean onMoon){
        rand = new Random(seed);
        this.onMoon = onMoon;
        updateWind();  
    }

    public void updateWind(){
        windY = 0;
        if (onMoon) {
            windX = 0;
        }
        else {
            windX = (rand.nextDouble() - 0.5) / 10;
        }
        updateWindString();
    }

    public void updateWindString(){
        if (windX == 0) {
            windString = "Wind: still";
        }
        else {
            windString = "Wind: ";
            windString += windX > 0 ? "Right " : "Left ";        
            windString += Math.round(windX * 10000.0) / 100.0;
        }        
    }

    public double getWindX(){
        return windX;
    }

    public double getWindY(){
        return windY;
    }  

    public String getWindString(){
        return windString;
    } 
    public void setOnMoon(boolean onMoon) {
        this.onMoon = onMoon;
    }
}