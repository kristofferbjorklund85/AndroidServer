import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {

    public static boolean writeCommentToDb(List<CommentModel> list) {
        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for(CommentModel cm : list) {
            try {
                stmt.executeUpdate( "INSERT INTO comments (id, campsiteid, date, username, commentbody) VALUES " +
                        "('" + cm.id + "', '" + cm.campsiteId + "', '" + cm.date + "', '" + cm.username + "', '" + cm.commentBody + "')");
            } catch (SQLException e) {
                rollbackSQL(stmt);
                System.out.println(e.getMessage());
                return false;
            }
            commitSQL(stmt);
        }
        return true;
    }

    //TODO ändra från List till CampsiteModel-object istället?
    public static boolean writeCampsiteToDb(List<CampsiteModel> list) {

        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for(CampsiteModel cm : list) {
            try {
                stmt.executeUpdate( "INSERT INTO campsites (id, location, name, lat, lng, type, fee, capacity, availability, " +
                                    "description, rating, views, username) VALUES ('" + cm.id + "', '" + cm.location + "', '" + cm.name + "', '" +
                                    cm.lat + "', '" + cm.lng + "', '" + cm.type + "', '" + cm.fee + "', '" + cm.capacity + "', '" + cm.availability +
                                    "', '" + cm.description + "', '" + cm.rating + "', " + cm.views + ", '" + cm.username + "')");
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

    public static boolean writeUserToDb(User user) {
        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
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
        }
        commitSQL(stmt);

        return true;
    }

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
                                                        rs.getInt(8),
                                                        rs.getString(9),
                                                        rs.getString(10),
                                                        rs.getDouble(11),
                                                        rs.getInt(12),
                                                        rs.getString(13));
                campList.add(cm);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeRS(rs);
        }
        return campList;
    }

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
                        rs.getString(5));
                commentList.add(cm);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeRS(rs);
        }

        return commentList;
    }

    public static User getUserFromDb(String username, String password) {
        User user = null;

        String query = "SELECT * FROM users where username=" + username + " AND password=" + password;

        ResultSet rs = createRS(query);

        try {
            user = new User(rs.getString(1),
                    rs.getString(2),
                    rs.getString(3));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeRS(rs);
        }

        return user;
    }

    public static boolean updateViews(String campsiteId) {
        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
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

    public static boolean deleteComment(String commentId) {
        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
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

    public static boolean deleteCampsite(String campsiteId) {
        Statement stmt = null;

        try {
            stmt = DBConMan.getConnection().createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        try {
            stmt.executeUpdate("DELETE FROM campsites WHERE id=" + campsiteId);
        } catch (SQLException e) {
            rollbackSQL(stmt);
            System.out.println(e.getMessage());
            return false;
        } finally {
            commitSQL(stmt);
        }
        return true;
    }

    public static ResultSet createRS(String query) {
        Connection c = DBConMan.getConnection();
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

    public static void closeRS(ResultSet rs) throws NullPointerException {
        try {
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void commitSQL(Statement stmt) {
        try {
            DBConMan.getConnection().commit();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            rollbackSQL(stmt);
        }
    }

    public static void rollbackSQL(Statement stmt) {
        try {
            DBConMan.getConnection().rollback();
            stmt.close();
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }






}



