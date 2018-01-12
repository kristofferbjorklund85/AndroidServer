import java.sql.*;

/**
 * Singleton for setting up and getting connection to database. Used by DBManager.
 */
public class DBSingleton {

    private static Connection con;
    private final static String DB_CONN_STR="jdbc:sqlite:campsites.db";

    /**
     * Load SQLite-driver
     */
    static{
        try{
            Class.forName("org.sqlite.JDBC");
        } catch(ClassNotFoundException cnfe){
            System.err.println("Could not load driver: "+cnfe.getMessage());
        }
    }

    /**
     * Gets connection to database, and sets AutoCommit to false to allow manual commit and rollback.
     * Called by {@link DBServlet#init()}.
     * Commit is done in {@link DBManager#commitSQL(Statement)}.
     */
    public static void createConnection(){
        try{
            con = DriverManager.getConnection(DB_CONN_STR);
            con.setAutoCommit(false);
        } catch(Exception e){
            System.err.println("Error getting connection to " +
                    DB_CONN_STR);
        }
    }

    /**
     * Used by {@link DBManager}.
     * @return connection to database.
     */
    public static Connection getConnection() {
        return con;
    }
}
