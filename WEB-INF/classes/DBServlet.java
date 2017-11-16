import java.sql.*;

public class DBServlet {

    private static Connection con;
    private final static String DB_CONN_STR="jdbc:sqlite:campsites.db";

    static{
        try{
            Class.forName("org.sqlite.JDBC");
        }catch(ClassNotFoundException cnfe){
            System.err.println("Could not load driver: "+cnfe.getMessage());
        }
        con = createConnection();
    }

    private static Connection createConnection(){
        Connection c = null;
        try{
            c = DriverManager.getConnection(DB_CONN_STR);
        }catch(Exception e){
            System.err.println("Error getting connection to " +
                    DB_CONN_STR);
        }
        return c;
    }

    public static boolean hasConnection(){
        return con != null;
    }

    public static Connection getConnection() {
        return con;
    }
}
