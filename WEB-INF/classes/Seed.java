import java.sql.*;

public class Seed {

    private static Connection conn = null;

    public static void init() {
        conn = DBServlet.getConnection();
        deleteTables();
        createNewTable();
        seedDB();
    }

    private static void deleteTables() {
        if(DBServlet.hasConnection()) {
            String sql = "DROP TABLE IF EXISTS campsites";
            try{
                Statement stmt = conn.createStatement();
                // create a new table
                stmt.execute(sql);
                System.out.println("Dropped table");
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void createNewTable() {
        if(DBServlet.hasConnection()) {
            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS campsites (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + "	location text NOT NULL,\n"
                    + "	coordinates text,\n"
                    + "	type text,\n"
                    + "	fee text,\n"
                    + "	capacity text,\n"
                    + "	availability text,\n"
                    + "	description text\n"
                    + ");";

            try{
                Statement stmt = conn.createStatement();
                // create a new table
                stmt.execute(sql);
                System.out.println("Table created");
            } catch(SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void seedDB() {
        if(DBServlet.hasConnection()) {
            try{
                String sql = "INSERT INTO campsites VALUES('1','Lindholmen','44,34', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people')";
                Statement s = conn.createStatement();
                s.executeUpdate(sql);
                System.out.println("Database seeded!");
            }catch(SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
