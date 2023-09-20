package minigames.server.krumgame.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TableManager{

    private Connection conn;
    private PlayerService playerService; 
    private AchievementsService achievementsService;
    private ColorsService colorsService;
      

    public TableManager(Connection conn){
        this.conn = conn;

        // Creating tables
        initializeTables();

        // Creating services
        initializeServices();
    }

    private void initializeTables(){
        PlayerTable playerTable = new PlayerTable(conn);
        playerTable.create();

        AchievementsTable achievementsTable = new AchievementsTable(conn);
        achievementsTable.create();

        ColorsTable colorsTable = new ColorsTable(conn);
        colorsTable.create();
    }

    private void initializeServices(){
        playerService = new PlayerService(conn);

        achievementsService = new AchievementsService(conn, playerService);

        colorsService = new ColorsService(conn, playerService);
    }

    // Adding a getter for player service
    public PlayerService getPlayerService(){
        return playerService;
    }

    public AchievementsService getAchievementsService(){
        return achievementsService;
    }

    public ColorsService getColorsService(){
        return colorsService;
    }
}

