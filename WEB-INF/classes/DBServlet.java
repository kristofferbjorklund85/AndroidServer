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

        if(req.getParameterMap().containsKey("Campsite")) {
        resp.setContentType("application/json");

        List list = DBManager.getCampsiteFromDb();
        JSONArray jRay = JSONManager.campsiteToJSON(list);
        PrintWriter out = resp.getWriter();

        out.print(jRay);
        out.close();
        resp.setStatus(200);
        }

        else if(req.getParameterMap().containsKey("Comment")) {
        resp.setContentType("application/json");

        List list = DBManager.getComments();
        JSONArray jRay = JSONManager.CommentsToJSON(list);

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

        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        if(req.getParameterMap().containsKey("Campsite")) {
        CampsiteModel cm;
        List<CampsiteModel> campList = new ArrayList<>();
        try {
            cm = gson.fromJson(jb.toString(), CampsiteModel.class);
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        campList.add(cm);
        DBManager.writeToDb(campList);
        resp.setStatus(200);
        }

        else if(req.getParameterMap().containsKey("Comment")) {
        CommentModel comment;
        List<CommentModel> commentsList = new ArrayList<>();
        try {
            comment = gson.fromJson(jb.toString(), CommentModel.class);
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        commentsList.add(comment);
        DBManager.writeToDb(commentsList);
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
