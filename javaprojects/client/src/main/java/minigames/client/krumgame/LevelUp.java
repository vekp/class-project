package minigames.client.krumgame;

public class LevelUp {

    public static int xp;
    public static int level;

    //TO DO - Make global variable of Username, crosschecked with the MySQL database also 
    // and at the same time, saving the XP to a local variable 'xp' 

    //TO DO - Write reverse code that adds more XP to MySQL database at the end of every game when xp
    // has been earned. If playing two players, this will have to be done for each username.


    // The below function will call the MySQLConnector class to connect to the database and source
    // the appropriate data (xp) from the MySQL database
    

    public static int loadXp() {
        //MySQLConnector class
        //read file info for xp from database
        //below is a temp test value
        xp = 10;
        return xp;
    }

    //This function figures out what level a character is

    public static int calculateLevel() {
        //Calculate level based on total XP
        if (xp > 500) {
            level = 5;
        }else {
            level = xp/100;
        }
        return level;
    }


    public static void main(String[] args) {
        
        //read username file info from database
        loadXp();
        calculateLevel();

        System.out.println("Your XP is currently: " + xp);
        System.out.println("Your level is currently: " + level);


       //Must also return the Level and XP for use in the game
    }
}
