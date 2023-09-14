package minigames.server.krumgame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import minigames.server.krumgame.database.TableManager;

public class KrumDatabase{

    private Connection conn = null;
    private TableManager tableManager;

    public KrumDatabase(){
        // to close database when program exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                closeDatabase();
            }
        });
    }

    public void startDatabase(){
        // Load the Derby driver
        //System.out.println(System.getProperty("java.class.path"));

        try {
            //Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            // Connect to the database, if does not exist will be created
            conn = DriverManager.getConnection("jdbc:derby:KrumDatabase;create=true");

            tableManager = new TableManager(conn);
            // Print to console when database is connected
            System.out.println("Database connected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Adding a getter for table manager
    public TableManager getTableManager(){
        return this.tableManager;
    }

    public void closeDatabase(){
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}