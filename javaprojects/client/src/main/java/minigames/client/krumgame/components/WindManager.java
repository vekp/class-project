package minigames.client.krumgame.components;

import java.util.Random;

public class WindManager{
    private double windX;
    private double windY;
    private String windString;
    private Random rand;

    public WindManager(){
        rand = new Random();
        initializeWind();
    }

    private void initializeWind(){
        windY = 0;
        windX = -0.02;
        windString = "Wind: left 2.00";
    }

    public double updateWindX(){
        return (rand.nextDouble() - 0.5) / 10;
        
    }

    public String updateWindString(){
        windX = updateWindX();
        windString = "Wind: ";
        windString += windX > 0 ? "Right " : "Left ";
        windString += Math.round(windX * 10000.0) / 100.0;
        return windString;
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
}