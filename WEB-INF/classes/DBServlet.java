import com.google.common.base.Splitter;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

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

        if(req.getParameter("type").equals("campsite")) {
        resp.setContentType("application/json");

        List list = DBManager.getCampsitesFromDb();
        JSONArray jRay = JSONManager.campsitesToJSON(list);
        PrintWriter out = resp.getWriter();

        out.print(jRay);
        out.close();
        System.out.println("Returning campsites");
        resp.setStatus(200);
        }

        else if(req.getParameter("type").equals("comment")) {
        resp.setContentType("application/json");

        List list = DBManager.getCommentsFromDb(req.getParameter("campsiteid"));
        JSONArray jRay = JSONManager.commentsToJSON(list);

        PrintWriter out = resp.getWriter();

        out.print(jRay);
        out.close();
        System.out.println("Returning Comments");
        resp.setStatus(200);
        }

        else {
            resp.setStatus(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);

        //TODO tar endast ett object i nuläget
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
            // crash and burn
            System.out.println(e.getMessage());
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
            // crash and burn
            System.out.println(e.getMessage());
            resp.setStatus(400);
            throw new IOException("Error parsing JSON request string");
        }
        //Write List of CommentModels to DB
        commentsList.add(comment);
        DBManager.writeCommentToDb(commentsList);
        System.out.println("Added comment to DB");
        resp.setStatus(200);
        } else {
            System.out.println("Error in POST: " + jb.toString());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPut(req, resp);

        String parameters = req.getQueryString();

        Map<String, String> dataMap =   Splitter.on('&')
                .trimResults()
                .withKeyValueSeparator(
                        Splitter.on('=')
                                .limit(2)
                                .trimResults())
                .split(parameters);

        if(dataMap.get("type").equals("views")) {
            if (dataMap.get("campsiteId") == null || dataMap.get("campsiteId").equals("")) {
                System.out.println("campsiteId is null or empty");
                resp.setStatus(400);
            } else {
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
        System.out.println("params: " + req.getQueryString());

        Map<String, String> dataMap =   Splitter.on('&')
                                        .trimResults()
                                        .withKeyValueSeparator(
                                            Splitter.on('=')
                                            .limit(2)
                                            .trimResults())
                                        .split(parameters);

        if(dataMap.get("type").equals("comment")) {
            if (dataMap.get("commentId") == null || dataMap.get("commentId").equals("")) {
                System.out.println("CommentId is null or empty");
                resp.setStatus(400);
            } else {
                if(DBManager.deleteComment(dataMap.get("commentId"))) {
                    System.out.println("Comment deleted.");
                    resp.setStatus(200);
                } else {
                    resp.setStatus(400);
                }
            }
        } else if(dataMap.get("type").equals("campsite")) {
            if (dataMap.get("campsiteId") == null || dataMap.get("campsiteId").equals("")) {
                System.out.println("campsiteId is null or empty");
                resp.setStatus(400);
            } else {
                if(DBManager.deleteCampsite(dataMap.get("campsiteId"))) {
                    System.out.println("Campsite deleted.");
                    resp.setStatus(200);
                } else {
                    resp.setStatus(400);
                }
            }
        } else {
            System.out.println("Error in DELETE-params: " + req.getQueryString());
            resp.setStatus(400);
        }
    }
}
