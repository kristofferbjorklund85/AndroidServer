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
        resp.setStatus(200);
        }

        else if(req.getParameter("type").equals("comment")) {
        resp.setContentType("application/json");

        List list = DBManager.getCommentsFromDb(req.getParameter("campsiteid"));
        JSONArray jRay = JSONManager.commentsToJSON(list);

        PrintWriter out = resp.getWriter();

        out.print(jRay);
        out.close();
        resp.setStatus(200);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);

        //TODO tar endast ett object i nuläget
        Gson gson = new Gson();
        StringBuffer jb = new StringBuffer();
        String line = null;


        System.out.println(req.getAttribute("id"));

        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                System.out.println(line);
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        System.out.println(jb.toString());
        //Create List of campsiteModels from REQ
        if(req.getParameter("type").equals("campsite")) {
        CampsiteModel cm;
        List<CampsiteModel> campList = new ArrayList<>();
        try {
            cm = gson.fromJson(jb.toString(), CampsiteModel.class);
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        //Write List of CampsiteModels to DB
        campList.add(cm);
        DBManager.writeCampsiteToDb(campList);
        resp.setStatus(200);
        }

        //Create List of commentModels from REQ
        else if(req.getParameter("type").equals("comment")) {
        CommentModel comment;
        List<CommentModel> commentsList = new ArrayList<>();
        try {
            comment = gson.fromJson(jb.toString(), CommentModel.class);
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        //Write List of CommentModels to DB
        commentsList.add(comment);
        DBManager.writeCommentToDb(commentsList);
        resp.setStatus(200);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
