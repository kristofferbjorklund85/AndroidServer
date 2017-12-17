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
        } catch (Exception e) { /*report an error*/ }

        System.out.println("\n" + jb.toString());

        //Create List of campsiteModels from REQ
        if(type.equals("campsite")) {
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
        else if(type.equals("comment")) {
        CommentModel comment;
        List<CommentModel> commentsList = new ArrayList<>();
        try {
            comment = gson.fromJson(jb.toString(), CommentModel.class);
        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

        System.out.println("id: " + comment.id);
        System.out.println("csid: " + comment.campsiteId);
        System.out.println("date: " + comment.date);
        System.out.println("user: " + comment.username);
        System.out.println("body: " + comment.commentBody);

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
        //super.doDelete(req, resp);

        Map<String, String> params = getParameterMap(req);

        System.out.println("params: " + params.get("type"));
        if(params.get("type").equals("comment")) {
            System.out.println("params: " + params.get("commentId"));

            if (params.get("commentId") == null) {
                System.out.println("CommentId is null");
            } else {
                System.out.println("CommentId: " + params.get("commentId"));
                DBManager.deleteComment(params.get("commentId"));
                resp.setStatus(200);
            }
        }
        else if(params.get("type").equals("campsite")) {
            System.out.println("params: " + params.get("campsiteId"));

            if (true) {
                System.out.println("CampsiteId is null");
            } else {
                System.out.println("CampsiteId: " + params.get("campsiteId"));
                DBManager.deleteCampsite(params.get("campsiteId"));
                resp.setStatus(200);
            }
        }
}

    public static Map<String, String> getParameterMap(HttpServletRequest request) {

        BufferedReader br = null;
        Map<String, String> dataMap = null;

        try {

            InputStreamReader reader = new InputStreamReader(
                    request.getInputStream());
            br = new BufferedReader(reader);

            String data = br.readLine();

            dataMap = Splitter.on('&')
                    .trimResults()
                    .withKeyValueSeparator(
                            Splitter.on('=')
                                    .limit(2)
                                    .trimResults())
                    .split(data);

            return dataMap;
        } catch (IOException ex) {


        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {

                }
            }
        }

        return dataMap;
    }

}
