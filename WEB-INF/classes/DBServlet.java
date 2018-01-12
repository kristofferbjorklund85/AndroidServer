import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that initiates the servlet and handles the incoming requests.
 */
public class DBServlet extends HttpServlet {

    /**
     * Used by winstone-script to start the Servlet.
     * 
     * @throws ServletException no action required.
     */
    public void init() throws ServletException  {
        DBSingleton.createConnection();
    }

    /**
     * GET-method for the servlet. Gets data from the database and sends back to client as JSON. Checks the
     * request-parameter for type. Types can be: user, campsite, comment, rating. If request is bad,
     * the servlet will return http status code 400.
     *
     * If {@code req.getParameter("type")} is {@code "user"}, pass username and password to database to get userdata and
     * check login-credentials. If a user matching username and password is found in the database, responds to the
     * client with a JSON of the users data. If no user is found matching username and password, the serlvet returns
     * http status code 404.
     *
     * If {@code req.getParameter("type")} is {@code "campsite"}, all campsites with be fetched from the database and
     * sent back to the client as JSON. If no campsites were found in the database, the serlvet returns http
     * status code 404.
     *
     * If {@code req.getParameter("type")} is {@code "campsite"}, checks second parameter for campsiteId and gets all
     * comments from the database for the supplied campsiteId and returns as JSON to client. If no comments were found,
     * the servlet will return http status code 404.
     *
     * If {@code req.getParameter("type")} is {@code "rating"}, checks second parameter for campsiteId and gets all
     * rating from the database for the supplied campsiteId and returns as JSON to client. If no rating were found,
     * the servlet will return http status code 404.
     *
     * @param req the request coming to the servlet.
     * @param resp the response to send back to client.
     * @throws ServletException no action required.
     * @throws IOException no action required.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        JSONArray jRay = null;
        String type = req.getParameter("type");
        resp.setContentType("application/json");

        //Create and return UserModel
        if(type.equals("user")) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if(username.isEmpty() || password.isEmpty()) {
                System.out.println("username or password is empty; Querystring: " + req.getQueryString());
                resp.setStatus(404);
                return;
            }

            UserModel user = DBManager.getUserFromDb(username, password);
            if(user == null) {
                System.out.println("Could not find user; username: " + username + ", password: " + password);
                resp.setStatus(404);
                return;
            }
            //Create JSON from UserModel-object
            String userJson = JSONManager.userToJSON(user);

            //Return UserModel-object as JSON to the user
            PrintWriter out = resp.getWriter();
            out.print(userJson);
            out.close();
            System.out.println("Found UserModel id:" + user.id + ". Returning.");
            resp.setStatus(200);
            return;
        }

        //Create list of Campsites
        else if(type.equals("campsite")) {
            List list = DBManager.getCampsitesFromDb();
            if(list.isEmpty()) {
                System.out.println("Found no campsites");
                resp.setStatus(404);
                return;
            }


            jRay = JSONManager.campsitesToJSON(list);

        }

        //Create List of Comments
        else if(type.equals("comment")) {
            List list = DBManager.getCommentsFromDb(req.getParameter("campsiteid"));
            if(list.isEmpty()) {
                System.out.println("Found no comments");
                resp.setStatus(404);
                return;
            }

            jRay = JSONManager.commentsToJSON(list);
        }

        //Create List of Ratings
        else if(type.equals("rating")) {
            List list = DBManager.getRatingFromDb(req.getParameter("campsiteId"));
            if(list.isEmpty()) {
                System.out.println("Found no rating");
                resp.setStatus(404);
                return;
            }

            jRay = JSONManager.ratingToJSON(list);
        }

        //In case of invalid request
        else {
            System.out.println("Invalid request; QueryString: " + req.getQueryString());
            resp.setStatus(400);
            return;
        }

        //Return list of comments or campsites
        PrintWriter out = resp.getWriter();
        out.print(jRay);
        out.close();
        System.out.println("Returning");
        resp.setStatus(200);

    }

    /**
     * POST-method for the serlvet. Handles requests posted by the client. Parses the request to a string.
     *
     * If {@code req.getParameter("type")} is {@code "campsite"}, convert the string to {@link CampsiteModel}-object
     * using {@link Gson} and write campsite to database. If writing to database succeeded, responds to client with
     * http status code 200. If writing to DB fails, responds to client with http status code 403.
     *
     * If {@code req.getParameter("type")} is {@code "comment"}, convert the string to {@link CommentModel}-object
     * using {@link Gson} and write comment to database. If writing to database succeeded, responds to client with
     * http status code 200. If writing to DB fails, responds to client with http status code 403.
     *
     * If {@code req.getParameter("type")} is {@code "user"}, convert the string to {@link UserModel}-object
     * using {@link Gson} and write user to database. If writing to database succeeded, responds to client with
     * http status code 200. If writing to DB fails, responds to client with http status code 403.
     *
     * If {@code req.getParameter("type")} is {@code "rating"}, convert the string to {@link RatingModel}-object
     * using {@link Gson} and write rating to database. If writing to database succeeded, responds to client with
     * http status code 200. If writing to DB fails, responds to client with http status code 403.
     *
     * If request type is bad, responds with http code 400.
     *
     * @param req the request coming to the servlet.
     * @param resp the response to send back to client.
     * @throws ServletException no action required.
     * @throws IOException no action required.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);

        Gson gson = new Gson();
        StringBuffer jb = new StringBuffer();
        String type = req.getParameter("type");

        try {
            String line = null;
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            resp.setStatus(400);
            return;
        }

        //Create List of campsiteModels from REQ
        if(type.equals("campsite")) {
            CampsiteModel cm;
            List<CampsiteModel> campList = new ArrayList<>();
            try {
                cm = gson.fromJson(jb.toString(), CampsiteModel.class);
            } catch (JSONException e) {
                System.out.println("Error creating new campsite" + e.getMessage());
                resp.setStatus(400);
                throw new IOException("Error parsing JSON request string");
            }

            //Write List of CampsiteModels to DB
            campList.add(cm);
            if(DBManager.writeCampsiteToDb(campList)) {
                System.out.println("Added campsite to DB");
                resp.setStatus(200);
            } else {
                System.out.println("Could not add campsite to DB, return 403");
                resp.setStatus(403);
            }

        }

        //Create List of commentModels from REQ
        else if(type.equals("comment")) {
            CommentModel comment;
            List<CommentModel> commentsList = new ArrayList<>();
            try {
                comment = gson.fromJson(jb.toString(), CommentModel.class);
            } catch (JSONException e) {
                System.out.println("Error creating new comment" + e.getMessage());
                resp.setStatus(400);
                throw new IOException("Error parsing JSON request string");
            }
            //Write List of CommentModels to DB
            commentsList.add(comment);
            if(DBManager.writeCommentToDb(commentsList)) {
                System.out.println("Added comment to DB");
                resp.setStatus(200);
            } else {
                System.out.println("Could not add comment to DB, return 403");
                resp.setStatus(403);
            }

        }

        //Create new UserModel in DB
        else if(type.equals("user")) {
            UserModel user;
            try {
                user = gson.fromJson(jb.toString(), UserModel.class);
            } catch (JSONException e) {
                System.out.println("Error creating new user" + e.getMessage());
                resp.setStatus(400);
                throw new IOException("Error parsing JSON request string");
            }

            if(DBManager.writeUserToDb(user)) {
                System.out.println("Added UserModel to DB");
                resp.setStatus(200);
            } else {
                System.out.println("Could not add user to DB, return 403");
                resp.setStatus(403);
            }

        }

        //Create or update RatingModel
        else if(type.equals("rating")) {
            RatingModel rating;
            try {
                rating = gson.fromJson(jb.toString(), RatingModel.class);
            } catch (JSONException e) {
                System.out.println("Error inputting rating" + e.getMessage());
                resp.setStatus(400);
                throw new IOException("Error parsing JSON request string");
            }

            if(DBManager.writeRatingToDb(rating, req.getParameter("campsiteId"))) {
                System.out.println("Added/Updated rating to campsite");
                resp.setStatus(200);
            } else {
                System.out.println("Could not add/update rating to DB, return 403");
                resp.setStatus(403);
            }

        }

        else {
            System.out.println("Error in POST: " + jb.toString());
            resp.setStatus(400);
        }
    }

    /** Update-method for the servlet. Handles requests posted by the client. Parses the request to a Map
     * using {@link Splitter} from Google's <a href="https://github.com/google/guava">Guava</a> library.
     *
     * Only handles updates for views. Updates views on the supplied campsite, if database was successfully updated,
     * the servlet responds with http code 200. If request was bad or the servlet was unsuccessful in updating views,
     * the servlet responds with http code 400 to client.
     *
     * @param req the request coming to the servlet.
     * @param resp the response to send back to client.
     * @throws ServletException no action required.
     * @throws IOException no action required.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPut(req, resp);

        String parameters = req.getQueryString();

        //Split parameter into key-value pairs.
        Map<String, String> dataMap =   Splitter.on('&')
                .trimResults()
                .withKeyValueSeparator(
                        Splitter.on('=')
                                .limit(2)
                                .trimResults())
                .split(parameters);

        //Check if update is of type views
        if(dataMap.get("type").equals("views")) {
            if (dataMap.get("campsiteId") == null || dataMap.get("campsiteId").equals("")) {
                System.out.println("campsiteId is null or empty");
                resp.setStatus(400);
            } else {
                //Update views for campsite. Return 200 if OK.
                if(DBManager.updateViews(dataMap.get("campsiteId"))) {
                    System.out.println("Views updated for campsiteId: " + dataMap.get("campsiteId"));
                    resp.setStatus(200);
                } else {
                    resp.setStatus(400);
                }
            }
        } else {
            System.out.println("Error in PUT-params: " + req.getQueryString());
            resp.setStatus(400);
        }
    }

    /** Delete-method for the servlet. Handles requests posted by the client. Parses the request to a Map
     * using {@link Splitter} from Google's <a href="https://github.com/google/guava">Guava</a> library.
     *
     * If {@code dataMap.get("type")} is {@code "campsite"}, tries to delete the campsite from the database. If
     * successful, responds with http code 200. If unsuccessful, responds with http status code 400.
     *
     * If {@code dataMap.get("type")} is {@code "comment"}, tries to delete the comment from the database. If
     * successful, responds with http code 200. If unsuccessful, responds with http status code 400.
     *
     * If the request is bad, responds with http status code 400.
     *
     * @param req the request coming to the servlet.
     * @param resp the response to send back to client.
     * @throws ServletException no action required.
     * @throws IOException no action required.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doDelete(req, resp);

        String parameters = req.getQueryString();
        //System.out.println("params: " + req.getQueryString());

        //Split parameter into key-value pairs.
        Map<String, String> dataMap =   Splitter.on('&')
                                        .trimResults()
                                        .withKeyValueSeparator(
                                            Splitter.on('=')
                                            .limit(2)
                                            .trimResults())
                                        .split(parameters);

        //Check if Delete is of type Comment
        if(dataMap.get("type").equals("comment")) {
            if (dataMap.get("commentId") == null || dataMap.get("commentId").equals("")) {
                System.out.println("CommentId is null or empty");
                resp.setStatus(400);
            } else {
                //Try to delete comment. Return 200 if OK.
                if(DBManager.deleteComment(dataMap.get("commentId"))) {
                    System.out.println("Comment deleted.");
                    resp.setStatus(200);
                } else {
                    System.out.println("Comment could not be deleted.");
                    resp.setStatus(400);
                }
            }
        }

        //Check if Delete is of type Campsite
        else if(dataMap.get("type").equals("campsite")) {
            if (dataMap.get("campsiteId") == null || dataMap.get("campsiteId").equals("")) {
                System.out.println("campsiteId is null or empty");
                resp.setStatus(400);
            } else {
                //Try to delete campsite. Return 200 if OK.
                if(DBManager.deleteCampsite(dataMap.get("campsiteId"))) {
                    System.out.println("Campsite deleted.");
                    resp.setStatus(200);
                } else {
                    System.out.println("Campsite could not be deleted.");
                    resp.setStatus(400);
                }
            }
        }

        //Invalid request, return status 400
        else {
            System.out.println("Error in DELETE-params: " + req.getQueryString());
            resp.setStatus(400);
        }
    }
}
