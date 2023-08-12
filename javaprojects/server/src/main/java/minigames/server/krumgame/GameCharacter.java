// TODO: Change the name after deciding game name
package minigames.server.krumgame; 

// TODO: Change the class name if desired
public class GameCharacter{
    // Class fields
    private int xPosition;
    private int yPosition;
    private int health;
    private String type;
    // TODO: Projectiles this game character has, probably need a separate class
    // TODO: Terrain class 
    // TODO: Achievements that A player has

    // Constructor
    public GameCharacter(String name, String type, int x, int y, int health){
        this.name = name;
        this.type = type;
        this.xPosition = x;
        this.yPosition = y;
        this.health = health;
    }

    // Getters

    int getXPosition(){
        return this.xPosition;
    }

    int getYPosition(){
        return this.yPosition;
    }

    int getHealth(){
        return this.health;
    }

    String getType(){
        return this.type;
    }

    String getName(){
        return this.name;
    }

    // Setters

    void setXPosition(int xPosition){
        this.xPosition = xPosition;
    }

    void setYPosition(int yPosition){
        this.yPosition = yPosition;
    }

    void setHealth(int health){
        this.health = health;
    }

    
    // Move function only handling straight line movement
    // Using package private access modifier
    // TODO: move the object based on terrain
    void move(String direction){
        switch(direction.tolowercase()){
            case "east":
                xPosition += 1;
                break;
            case "west":
                xPosition -= 1;
                break;
            case "north":
                yPosition -= 1;
                break;
            case "south":
                yPosition += 1;
                break;
            default:
                break;
        }
    }

    // Using package private access modifier
    void shoot(int angle, int power){
        // TODO: implement the projectile trajectory
    }

}