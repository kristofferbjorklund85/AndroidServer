import java.sql.*;
import java.util.UUID;

public class Seed {

    public static void init() {
        DBConMan.createConnection();
        deleteTables();
        createNewTable();
        seedDB();
    }

    private static void deleteTables() {
        String sql = "DROP TABLE IF EXISTS campsites";
        try {
            Statement stmt = DBConMan.getConnection().createStatement();
            // create a new table
            stmt.execute(sql);
            System.out.println("Dropped table");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createNewTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS campsites (\n"
                + "	id text PRIMARY KEY,\n"
                + "	location text NOT NULL,\n"
                + "	lat REAL,\n"
                + "	lng REAL,\n"
                + "	type text,\n"
                + "	fee text,\n"
                + "	capacity text,\n"
                + "	availability text,\n"
                + "	description text\n"
                + ");";

        try {
            Statement stmt = DBConMan.getConnection().createStatement();
            // create a new table
            stmt.execute(sql);
            System.out.println("Table created");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void seedDB() {
        try {
            Statement stmt = DBConMan.getConnection().createStatement();

            String[] sql = {    "INSERT INTO campsites VALUES('" + UUID.randomUUID().toString() + "','Lindholmen', '-31.952854', '115.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people')",
                                "INSERT INTO campsites VALUES('" + UUID.randomUUID().toString() + "','Lindholmen', '-32.952854', '116.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people')",
                                "INSERT INTO campsites VALUES('" + UUID.randomUUID().toString() + "','Lindholmen', '-33.952854', '117.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people')"};

            for (int i = 0; i < sql.length; i++) {
                stmt.executeUpdate(sql[i]);
            }

            DBManager.commitSQL(stmt);
            System.out.println("Database seeded!");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
