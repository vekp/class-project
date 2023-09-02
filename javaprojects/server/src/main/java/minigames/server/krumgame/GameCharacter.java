// TODO: Change the name after deciding game name
package minigames.server.krumgame;

import minigames.server.GameServer;

// TODO: Change the class name if desired
public class GameCharacter{
    // Class fields
    public int xPosition;
    public int yPosition;
    public int health;
    public String type;
    public String name;
    public String playerType;
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

    //Achievement constructor

// Achievement newAchievement = new Achievement(name, description, xpValue, mediafile, hidden)
// handler.registerAchievement(Achievement newAchievement);


// Achievements Code from Intelligits Wiki, edited for our use - also should link to
// the Enum created in KrumAchievements.java 
    // AchievementHandler achievementHandler;    // Nathan's Handler for implementing achievement features


    //     achievementHandler = new AchievementHandler(BattleshipServer.class);
    //     // Create the achievements and give them to the handler
    //     achievementHandler.registerAchievement(new Achievement(achievements.SUICIDE_5.toString(),
    //             "Congratulations. That's the 5th time you've killed yourself.", 25, "", false));
    //     achievementHandler.registerAchievement(new Achievement(achievements.BAZOOKA_5.toString(),
    //             "5th kill with standard weapon. We should call you Bazooka Joe.", 25, "", false));
    //     achievementHandler.registerAchievement(new Achievement(achievements.TARZAN_DEATH.toString(),
    //             "Rope Kill! Sharp Shooting, Tarzan...", 25, "", false));
    //     achievementHandler.registerAchievement(new Achievement(achievements.LASER_DEATH.toString(),
    //             "Death by laser. Pew-Pew.", 25, "", false));
    //     achievementHandler.registerAchievement(new Achievement(achievements.MAP_SLAP.toString(),
    //             "Nice interior decorating.", 25, "", false));
    //     achievementHandler.registerAchievement(new Achievement(achievements.JOEY_SUICIDE.toString(),
    //             "Well, that backfired - Killed by your son.", 25, "", false));
    //     achievementHandler.registerAchievement(new Achievement(achievements.DIRECT_HIT.toString(),
    //             "Direct hit! Grenade to the face!", 25, "", false));
    //     achievementHandler.registerAchievement(new Achievement(achievements.LONG_RANGE.toString(),
    //             "Destroy a carrier.", 25, "", false));

    // }



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

}