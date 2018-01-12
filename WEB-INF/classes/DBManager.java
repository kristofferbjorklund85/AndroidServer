import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Database-manager provides static CRUD-methods for database. Inludes utility-methods to open and close ResultSet and
 * commit or rollback transactions to database.
 * {@link #writeCommentToDb(List)} and {@link #writeCampsiteToDb(List)} requires a List of objects, although there is
 * always just one campsite or model to write at any one time. This is to future-proof if the servlet were to
 * handle a batch of posted comments or campsites.
 */
public class DBManager {

    /**
     * Writes comments to database.
     * @param list a list of {@link CommentModel}-object being written to database.
     * @return TRUE if comment were successfully entered into the database, FALSE if something went
     * wrong.
     */
    public static boolean writeCommentToDb(List<CommentModel> list) {
        Statement stmt = null;

        try {
            stmt = DBSingleton.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for(CommentModel cm : list) {
            try {
                stmt.executeUpdate( "INSERT INTO comments (id, campsiteid, date, userId, username, commentbody) VALUES " +
                        "('" + cm.id + "', '" + cm.campsiteId + "', '" + cm.date + "', '" + cm.userId + "', '" + cm.username + "', '" + cm.commentBody + "')");
            } catch (SQLException e) {
                rollbackSQL(stmt);
                System.out.println(e.getMessage());
                return false;
            }
            commitSQL(stmt);
        }
        return true;
    }

    /**
     * Writes campsite to database.
     * @param list a list of {@link CampsiteModel}-object being written to database.
     * @return TRUE if campsite were successfully entered into the database, FALSE if something went
     * wrong.
     */
    public static boolean writeCampsiteToDb(List<CampsiteModel> list) {

        Statement stmt = null;

        try {
            stmt = DBSingleton.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for(CampsiteModel cm : list) {
            try {
                stmt.executeUpdate( "INSERT INTO campsites (id, location, name, lat, lng, type, fee, capacity, availability, " +
                                    "description, views, userId) VALUES ('" + cm.id + "', '" + cm.location + "', '" + cm.name + "', '" +
                                    cm.lat + "', '" + cm.lng + "', '" + cm.type + "', '" + cm.fee + "', '" + cm.capacity + "', '" + cm.availability +
                                    "', '" + cm.description + "', " + cm.views + ", '" + cm.userId + "')");
                System.out.println("Updated database with new Campsite object.");
            } catch (SQLException e) {
                rollbackSQL(stmt);
                System.out.println(e.getMessage());
                return false;
            }
            commitSQL(stmt);
        }
        return true;
    }

    /**
     * Writes user to database.
     * @param user {@link UserModel}-object being written to database.
     * @return TRUE if user were successfully entered into the database, FALSE if something went
     * wrong.
     */
    public static boolean writeUserToDb(UserModel user) {
        Statement stmt = null;

        try {
            stmt = DBSingleton.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            stmt.executeUpdate( "INSERT INTO users VALUES " +
                    "('" + user.id + "', '" + user.username + "', '" + user.password + "')");
        } catch (SQLException e) {
            rollbackSQL(stmt);
            System.out.println(e.getMessage());
            return false;
        } finally {
            commitSQL(stmt);
        }

        return true;
    }

    /**
     * Writes rating to database. If no rating has previously been entered, there will be no table for ratings of
     * that campsite, and a table will be created. Name of table will be the campsiteId. If user already rated a
     * campsite, the rating will be updated.
     * @param rating {@link RatingModel}-object being written to database.
     * @param campsiteId the id of the campsite being rated.
     * @return TRUE if rating were successfully entered into the database, FALSE if something went
     * wrong.
     */
    public static boolean writeRatingToDb(RatingModel rating, String campsiteId) {
        Statement stmt = null;

        try {
            stmt = DBSingleton.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        String ratingTable = "CREATE TABLE IF NOT EXISTS " + campsiteId + " ( userid text NOT NULL PRIMARY KEY, rating int NOT NULL);";
        String ratingInput = "INSERT INTO " + campsiteId + " VALUES('" + rating.userId + "', " + rating.rating + ");";

        try {
            stmt.executeUpdate(ratingTable);
            stmt.executeUpdate(ratingInput);
        } catch (SQLException e) {
            try {
                stmt.executeUpdate("UPDATE " + campsiteId + " SET rating = " + rating.rating + " WHERE userid='" + rating.userId + "'");
            } catch (SQLException e2) {
                rollbackSQL(stmt);
                System.out.println(e2.getMessage());
                return false;
            }
        } finally {
            commitSQL(stmt);
        }

        return true;
    }

    /**
     * Gets all campsites from the database.
     * @return a list of {@link CampsiteModel}-objects.
     */
    public static List getCampsitesFromDb() {
        List<CampsiteModel> campList = new ArrayList<>();

        String query =  "SELECT * " +
                        "FROM campsites ";

        ResultSet rs = createRS(query);

        try {
            while(rs.next()) {
                CampsiteModel cm = new CampsiteModel(   rs.getString(1),
                                                        rs.getString(2),
                                                        rs.getString(3),
                                                        rs.getDouble(4),
                                                        rs.getDouble(5),
                                                        rs.getString(6),
                                                        rs.getString(7),
                                                        rs.getString(8),
                                                        rs.getString(9),
                                                        rs.getString(10),
                                                        rs.getInt(11),
                                                        rs.getString(12));
                campList.add(cm);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeRS(rs);
        }
        return campList;
    }

    /**
     * Gets all comments from a given campsite.
     * @param campsiteId determines which campsite to find comments for.
     * @return list of {@link CommentModel}-objects.
     */
    public static List getCommentsFromDb(String campsiteId) {
        List<CommentModel> commentList = new ArrayList<>();

        String query = "SELECT * FROM comments where campsiteid=" + campsiteId;

        ResultSet rs = createRS(query);

        try {
            while(rs.next()) {
                CommentModel cm = new CommentModel(rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6));
                commentList.add(cm);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeRS(rs);
        }

        return commentList;
    }

    /**
     * At the moment the only way of checking login-credentials. It also fetches user-data. Checks if username and password exists in the database, and if
     * it does, returns the user. Password is not encrypted.
     * @param username a string with username.
     * @param password a string with password.
     * @return {@link UserModel}-object.
     */
    public static UserModel getUserFromDb(String username, String password) {
        UserModel user = null;

        String query = "SELECT * FROM users where username=" + username + " AND password=" + password;

        ResultSet rs = createRS(query);

        try {
            user = new UserModel(rs.getString(1),
                    rs.getString(2),
                    rs.getString(3));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeRS(rs);
        }

        return user;
    }

    /**
     * Gets rating from database and returns a list with all ratings for a given campsite.
     * @param campsiteId determines which campsite to find ratings for.
     * @return a list of {@link RatingModel}-objects.
     */
    public static List getRatingFromDb(String campsiteId) {
        List<RatingModel> ratingList = new ArrayList<>();

        String query = "SELECT * FROM " + campsiteId;

        ResultSet rs = createRS(query);

        try {
            while(rs.next()) {
                RatingModel rat = new RatingModel(rs.getString(1),
                                        rs.getInt(2));
                ratingList.add(rat);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeRS(rs);
        }

        return ratingList;
    }

    /**
     * Updates viewcount on a given campsite by 1.
     * @param campsiteId which campsite to update views for.
     * @return TRUE if views were successfully updated, FALSE if something went wrong.
     */
    public static boolean updateViews(String campsiteId) {
        Statement stmt = null;

        try {
            stmt = DBSingleton.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        try {
            stmt.executeUpdate("UPDATE campsites SET views = views + 1 WHERE id='" + campsiteId + "'");
        } catch (SQLException e) {
            rollbackSQL(stmt);
            System.out.println("SQL Exception when UPDATING: " + e.getMessage());
            return false;
        } finally {
            commitSQL(stmt);
        }
        return true;
    }

    /**
     * Delete comment from database.
     * @param commentId which comment to delete.
     * @return TRUE if comment were successfully deleted, FALSE if something went wrong.
     */
    public static boolean deleteComment(String commentId) {
        Statement stmt = null;

        try {
            stmt = DBSingleton.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        try {
            stmt.executeUpdate("DELETE FROM comments WHERE id='" + commentId + "'");
        } catch (SQLException e) {
            rollbackSQL(stmt);
            System.out.println("SQL Exception when DELETING: " + e.getMessage());
            return false;
        } finally {
            commitSQL(stmt);
        }
        return true;
    }

    /**
     * Delete campsite from database.
     * @param campsiteId which campsite to delete.
     * @return TRUE if campsite was successfully deleted, FALSE if something went wrong.
     */
    public static boolean deleteCampsite(String campsiteId) {
        Statement stmt = null;

        try {
            stmt = DBSingleton.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        try {
            stmt.executeUpdate("DELETE FROM campsites WHERE id='" + campsiteId + "'");
            //Delete Associated comments to campsite
            stmt.executeUpdate("DELETE FROM comments WHERE campsiteid='" + campsiteId + "'");
        } catch (SQLException e) {
            rollbackSQL(stmt);
            System.out.println(e.getMessage());
            System.out.println("campsiteId: " + campsiteId);
            return false;
        } finally {
            commitSQL(stmt);
        }
        return true;
    }

    /**
     * Utility method for creating a resultset.
     * @param query the sql-query.
     * @return a {@link ResultSet} with the data from the database.
     */
    public static ResultSet createRS(String query) {
        Connection c = DBSingleton.getConnection();
        Statement s;
        ResultSet rs = null;

        try {
            s = c.createStatement();
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return rs;
    }

    /**
     * Utility method for closing a {@link ResultSet}.
     * @param rs the ResultSet to close.
     * @throws NullPointerException because this method is run in the finally-block, if there was a problem with
     * creating the ResultSet, trying to close the ResultSet will throw a NullPointException. No action required.
     */
    public static void closeRS(ResultSet rs) throws NullPointerException {
        try {
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Implements {@link Connection#commit()}.
     * @param stmt pass the statement for closing after commit is performed.
     */
    public static void commitSQL(Statement stmt) {
        try {
            DBSingleton.getConnection().commit();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            rollbackSQL(stmt);
        }
    }

    /**
     * Implements {@link Connection#rollback()}.
     * @param stmt pass the statement for closing after rollback is performed.
     */
    public static void rollbackSQL(Statement stmt) {
        try {
            DBSingleton.getConnection().rollback();
            stmt.close();
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }






}



