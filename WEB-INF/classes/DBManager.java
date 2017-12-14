import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    public static void writeCommentToDb(List<CommentModel> list) {
        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }

        for(CommentModel cm : list) {
            try {
                stmt.executeUpdate( "INSERT INTO comments (id, campsiteid, date, username, commentbody) VALUES " +
                        "('" + cm.id + "', '" + cm.campsiteId + "', '" + cm.date + "', '" + cm.username + "', '" + cm.commentBody + "')");
                System.out.println("Updated database with new Comment object.");
            } catch (SQLException e) {
                rollbackSQL(stmt);
                System.out.println(e);
            }
            commitSQL(stmt);
        }


    }

    //TODO ändra från List till CampsiteModel-object istället?
    public static void writeCampsiteToDb(List<CampsiteModel> list) {

        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e);
        }

        for(CampsiteModel cm : list) {
            try {
                stmt.executeUpdate( "INSERT INTO campsites (id, location, lat, lng, type, fee, capacity, availability, " +
                                    "description) VALUES ('" + cm.id + "', '" + cm.location + "', '" + cm.lat + "', '" + cm.lng +
                                    "', '" + cm.type + "', '" + cm.fee + "', " + cm.capacity + ", '" + cm.availability +
                                    "', '" + cm.description + "')");
                System.out.println("Updated database with new Campsite object.");
            } catch (SQLException e) {
                rollbackSQL(stmt);
                System.out.println(e);
            }
            commitSQL(stmt);
        }

    }

    public static List getCommentsFromDb(String campsiteId) {
        List<CommentModel> commentList = new ArrayList<>();

        String query = "SELECT * FROM comments where campsiteid=" + campsiteId;

        ResultSet rs = createRS(query);
        System.out.println("Comments retrieved");

        try {
            while(rs.next()) {
                CommentModel cm = new CommentModel(rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5));
                commentList.add(cm);
                System.out.println("Added comment to commentList");
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            closeRS(rs);
        }

        System.out.println("Returning commentList");
        return commentList;
    }

    public static List getCampsitesFromDb() {
        List<CampsiteModel> campList = new ArrayList<>();

        String query = "SELECT * FROM campsites";

        ResultSet rs = createRS(query);
        System.out.println("Campsites retrieved");

        try {
            while(rs.next()) {
                CampsiteModel cm = new CampsiteModel(   rs.getString(1),
                                                        rs.getString(2),
                                                        rs.getString(3),
                                                        rs.getDouble(4),
                                                        rs.getDouble(5),
                                                        rs.getString(6),
                                                        rs.getString(7),
                                                        rs.getInt(8),
                                                        rs.getString(9),
                                                        rs.getString(10),
                                                        rs.getDouble(11),
                                                        rs.getInt(12));
                campList.add(cm);
                System.out.println("Added campsite to campList");
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            closeRS(rs);
        }

        System.out.println("Returning campList");
        return campList;
    }

    public static ResultSet createRS(String query) {
        Connection c = DBConMan.getConnection();
        Statement s;
        ResultSet rs = null;

        try {
            s = c.createStatement();
            rs = s.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e);
        }

        return rs;
    }

    public static void closeRS(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void commitSQL(Statement stmt) {
        try {
            DBConMan.getConnection().commit();
            stmt.close();
        } catch (SQLException e) {
            rollbackSQL(stmt);
        }
    }

    public static void rollbackSQL(Statement stmt) {
        try {
            DBConMan.getConnection().rollback();
            stmt.close();
        }
        catch(SQLException e) {
            System.out.println(e);
        }
    }

}
