import java.sql.*;

public class Seed {

    public static void init() {
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
                + "	id integer PRIMARY KEY,\n"
                + "	location text NOT NULL,\n"
                + "	coordinates text,\n"
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

            String[] sql = {    "INSERT INTO campsites VALUES('1','Lindholmen','44,34', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people')",
                                "INSERT INTO campsites VALUES('2','Lindholmen','44,34', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people')",
                                "INSERT INTO campsites VALUES('3','Lindholmen','44,34', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people')"};

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
