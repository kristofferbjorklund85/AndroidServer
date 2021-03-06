import java.sql.*;
import java.util.UUID;

/**
 * Static seed-method. Creates tables for Campsites, Comments and Users. RatingModel is not included.
 */
public class Seed {

    /**
     * Method to call to seed database. Creates connection and calls deleteTables(), createNewTable(), seedDB().
     */
    public static void init() {
        DBSingleton.createConnection();
        deleteTables();
        createNewTable();
        seedDB();
    }

    /**
     * Drop tables from Database.
     */
    private static void deleteTables() {
        String sql1 = "DROP TABLE IF EXISTS campsites";
        String sql2 = "DROP TABLE IF EXISTS comments";
        String sql3 = "DROP TABLE IF EXISTS users";
        try {
            Statement stmt = DBSingleton.getConnection().createStatement();
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
            System.out.println("Dropped tables");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Create new tables in the database for Campsites, Comments and Users.
     */
    private static void createNewTable() {
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
                                + " userId text NOT NULL, \n"
                                + " username text NOT NULL,\n"
                                + " commentbody text NOT NULL,\n"
                                + "PRIMARY KEY (id, campsiteid));";

        String userTableSQL     = "CREATE TABLE IF NOT EXISTS users (\n"
                                + " id text NOT NULL, \n"
                                + " username TEXT NOT NULL UNIQUE, \n"
                                + " password TEXT NOT NULL, \n"
                                + "PRIMARY KEY (id));";

        try {
            Statement stmt = DBSingleton.getConnection().createStatement();
            // create a new table
            stmt.execute(campsiteTableSQL);
            stmt.execute(commentTableSQL);
            stmt.execute(userTableSQL);
            System.out.println("Tables created");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Insert data into the database. Generates unique IDs with {@link UUID#randomUUID().toString()}.
     * Creates Comments for the first campsite inserted.
     * Creates two users.
     * Commits all SQL by calling {@link DBManager#commitSQL(Statement)}.
     */
    private static void seedDB() {
        try {
            Statement stmt = DBSingleton.getConnection().createStatement();

            String id1 = UUID.randomUUID().toString();

            String[] campsiteSQL = {"INSERT INTO campsites VALUES('" + id1 + "','Lindholmen', 'Gothenburg', '57.850894', '12.033463', 'School', 'Free', '30', 'All year', 'Very nice place, lots of cool people', '0', '0')"};

            String[] commentSQL = { "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', '1', 'test', 'Test comment 1')",
                                    "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', '1', 'test', 'Test comment 2')",
                                    "INSERT INTO comments VALUES('" + UUID.randomUUID().toString() + "','" + id1 + "', '2017-05-24', '1', 'test', 'Test comment 3')"};

            String[] userSQL = {  "INSERT INTO users VALUES('1', 'test', 'test')",
                                "INSERT INTO users VALUES('0', 'admin666', 'admin666')" };

            for (int i = 0; i < campsiteSQL.length; i++) {
                stmt.executeUpdate(campsiteSQL[i]);
            }

            for (int i = 0; i < commentSQL.length; i++) {
                stmt.executeUpdate(commentSQL[i]);
            }

            for (int i = 0; i < userSQL.length; i++) {
                stmt.executeUpdate(userSQL[i]);
            }

            DBManager.commitSQL(stmt);
            System.out.println("Database seeded!");
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
