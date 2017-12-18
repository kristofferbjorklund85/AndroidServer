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
        try {
            Statement stmt = DBConMan.getConnection().createStatement();
            // create a new table
            stmt.execute(sql1);
            stmt.execute(sql2);
            System.out.println("Dropped tables");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createNewTable() {
        // SQL statement for creating a new table
        String campsiteTablesql = "CREATE TABLE IF NOT EXISTS campsites (\n"
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
                + " rating REAL,\n"
                + " views int,\n"
                + " username text\n"
                + ");";

        String commentTablesql = "CREATE TABLE IF NOT EXISTS comments (\n"
                + "	id text NOT NULL,\n"
                + " campsiteid text NOT NULL,\n"
                + " date text NOT NULL,\n"
                + " username text NOT NULL,\n"
                + " commentbody text NOT NULL,\n"
                + "PRIMARY KEY (id, campsiteid));";

        try {
            Statement stmt = DBConMan.getConnection().createStatement();
            // create a new table
            stmt.execute(campsiteTablesql);
            stmt.execute(commentTablesql);
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

            String[] campsitesql = {"INSERT INTO campsites VALUES('" + id1 + "','Lindholmen', 'Gothenburg', '-31.952854', '115.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people', '3.5', '312', 'JanBanan')",
                                    "INSERT INTO campsites VALUES('" + id2 + "','Lindholmen', 'Gothenburg', '-32.952854', '116.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people', '3.5', '4535', 'JanBanan')",
                                    "INSERT INTO campsites VALUES('" + id3 + "','Lindholmen', 'Gothenburg', '-33.952854', '117.857342', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people', '3.5', '312342', 'JanBanan')"};

            String[] commentsql = { "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', 'JanBanan','Good stuff yo!')",
                                    "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', 'JanBanan','Good stuff yo!')",
                                    "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', 'JanBanan','Good stuff yo!')"};

            for (int i = 0; i < campsitesql.length; i++) {
                stmt.executeUpdate(campsitesql[i]);
            }

            for (int i = 0; i < commentsql.length; i++) {
                stmt.executeUpdate(commentsql[i]);
            }

            DBManager.commitSQL(stmt);
            System.out.println("Database seeded!");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
