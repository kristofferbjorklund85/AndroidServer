import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DBServlet extends HttpServlet {

    public void init() throws ServletException  {
        DBConMan.createConnection();
        //Seed.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);

        JSONArray jRay = null;

        //Create and return User
        if(req.getParameter("type").equals("user")) {
            resp.setContentType("application/json");

            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if(username.isEmpty() || password.isEmpty()) {
                System.out.println("username or password is empty; Querystring: " + req.getQueryString());
                resp.setStatus(404);
                return;
            }

            User user = DBManager.getUserFromDb(username, password);
            if(user == null) {
                System.out.println("Could not find user; username: " + username + ", password: " + password);
                resp.setStatus(404);
                return;
            }
            //Create JSON from User-object
            String userJson = JSONManager.userToJSON(user);

            //Return User-object as JSON to the user
            PrintWriter out = resp.getWriter();
            out.print(userJson);
            out.close();
            System.out.println("Found User id:" + user.id + ". Returning.");
            resp.setStatus(200);
            return;
        }

        //Create list of Campsites
        else if(req.getParameter("type").equals("campsite")) {
            resp.setContentType("application/json");

            List list = DBManager.getCampsitesFromDb();
            if(list.isEmpty()) {
                System.out.println("Found no campsites");
                resp.setStatus(404);
                return;
            }


            jRay = JSONManager.campsitesToJSON(list);

        }

        //Create List of Comments
        else if(req.getParameter("type").equals("comment")) {
            resp.setContentType("application/json");

            List list = DBManager.getCommentsFromDb(req.getParameter("campsiteid"));
            if(list.isEmpty()) {
                System.out.println("Found no comments");
                resp.setStatus(404);
                return;
            }

            jRay = JSONManager.commentsToJSON(list);
        }

        //Create List of Ratings
        else if(req.getParameter("type").equals("rating")) {
            resp.setContentType("application/json");

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
            resp.setStatus(404);
            return;
        }

        //Return list of comments or campsites
        PrintWriter out = resp.getWriter();
        out.print(jRay);
        out.close();
        System.out.println("Returning");
        resp.setStatus(200);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);

        //Tar endast ett object i nuläget


        Gson gson = new Gson();
        StringBuffer jb = new StringBuffer();
        String line = null;
        String type = req.getParameter("type");

        try {
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
            DBManager.writeCampsiteToDb(campList);
                System.out.println("Added campsite to DB");
            resp.setStatus(200);
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
            DBManager.writeCommentToDb(commentsList);
            System.out.println("Added comment to DB");
            resp.setStatus(200);
        }

        //Create new User in DB
        else if(type.equals("user")) {
            User user;
            try {
                user = gson.fromJson(jb.toString(), User.class);
            } catch (JSONException e) {
                System.out.println("Error creating new user" + e.getMessage());
                resp.setStatus(400);
                throw new IOException("Error parsing JSON request string");
            }

            DBManager.writeUserToDb(user);
            System.out.println("Added User to DB");
            resp.setStatus(200);
        }

        //Create or update Rating
        else if(type.equals("rating")) {
            Rating rating;
            System.out.println(jb.toString());
            try {
                rating = gson.fromJson(jb.toString(), Rating.class);
            } catch (JSONException e) {
                System.out.println("Error inputting rating" + e.getMessage());
                resp.setStatus(400);
                throw new IOException("Error parsing JSON request string");
            }

            DBManager.writeRatingToDb(rating, req.getParameter("campsiteId"));
            System.out.println("Added rating to campsite");
            resp.setStatus(200);
        }


        else {
            System.out.println("Error in POST: " + jb.toString());
            resp.setStatus(400);
        }
    }

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
