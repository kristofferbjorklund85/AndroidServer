import java.sql.*;

public class DBConMan {

    private static Connection con;
    private final static String DB_CONN_STR="jdbc:sqlite:campsites.db";

    static{
        try{
            Class.forName("org.sqlite.JDBC");
        } catch(ClassNotFoundException cnfe){
            System.err.println("Could not load driver: "+cnfe.getMessage());
        }
    }

    public static void createConnection(){
        try{
            con = DriverManager.getConnection(DB_CONN_STR);
            con.setAutoCommit(false);
            System.out.println("We have a connection");
        } catch(Exception e){
            System.err.println("Error getting connection to " +
                    DB_CONN_STR);
        }
    }

/*    public static boolean hasConnection(){
        return con != null;
    }*/

    public static Connection getConnection() {
        return con;
    }
}
