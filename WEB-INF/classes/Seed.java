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
        String sql1 = "DROP TABLE IF EXISTS campsites";
        String sql2 = "DROP TABLE IF EXISTS comments";
        String sql3 = "DROP TABLE IF EXISTS users";
        try {
            Statement stmt = DBConMan.getConnection().createStatement();
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
            System.out.println("Dropped tables");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createNewTable() {
        // SQL statement for creating a new table
        String campsiteTableSQL = "CREATE TABLE IF NOT EXISTS campsites (\n"
                                + "	id text PRIMARY KEY,\n"
                                + "	location text NOT NULL,\n"
                                + " name text,\n"
                                + "	lat REAL,\n"
                                + "	lng REAL,\n"
                                + "	type text,\n"
                                + "	fee text,\n"
                                + "	capacity text,\n"
                                + "	availability text,\n"
                                + "	description text,\n"
                                + " views int,\n"
                                + " userId text\n"
                                + ");";

        String commentTableSQL  = "CREATE TABLE IF NOT EXISTS comments (\n"
                                + "	id text NOT NULL,\n"
                                + " campsiteid text NOT NULL,\n"
                                + " date text NOT NULL,\n"
                                + " username text NOT NULL,\n"
                                + " userId text NOT NULL, \n"
                                + " commentbody text NOT NULL,\n"
                                + "PRIMARY KEY (id, campsiteid));";

        String userTableSQL     = "CREATE TABLE IF NOT EXISTS users (\n"
                                + " id text NOT NULL, \n"
                                + " username TEXT NOT NULL UNIQUE, \n"
                                + " password TEXT NOT NULL, \n"
                                + "PRIMARY KEY (id));";

        try {
            Statement stmt = DBConMan.getConnection().createStatement();
            // create a new table
            stmt.execute(campsiteTableSQL);
            stmt.execute(commentTableSQL);
            stmt.execute(userTableSQL);
            System.out.println("Tables created");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void seedDB() {
        try {
            Statement stmt = DBConMan.getConnection().createStatement();

            String id1 = UUID.randomUUID().toString();
            String id2 = UUID.randomUUID().toString();
            String id3 = UUID.randomUUID().toString();

            String[] campsiteSQL = {"INSERT INTO campsites VALUES('" + id1 + "','Lindholmen', 'Gothenburg', '-31.952854', '115.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people', '0', '1348564')",
                                    "INSERT INTO campsites VALUES('" + id2 + "','Lindholmen', 'Gothenburg', '-32.952854', '116.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people', '1', '1348564')",
                                    "INSERT INTO campsites VALUES('" + id3 + "','Lindholmen', 'Gothenburg', '-33.952854', '117.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people', '1', '1348564')"};

            String[] commentSQL = { "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', 'JanBanan', '1348564', 'Good stuff yo!')",
                                    "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', 'JanBanan', '1348564', 'Good stuff yo!')",
                                    "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', 'JanBanan', '1348564', 'Good stuff yo!')"};

            String userSQL = "INSERT INTO users VALUES('1', 't', 't')";

            for (int i = 0; i < campsiteSQL.length; i++) {
                stmt.executeUpdate(campsiteSQL[i]);
            }

            for (int i = 0; i < commentSQL.length; i++) {
                stmt.executeUpdate(commentSQL[i]);
            }

            stmt.executeUpdate(userSQL);

            DBManager.commitSQL(stmt);
            System.out.println("Database seeded!");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
