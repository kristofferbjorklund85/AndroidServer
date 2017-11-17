import com.google.gson.Gson;
import org.json.JSONArray;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DBServlet extends HttpServlet {

    public void init() throws ServletException  {
        DBConMan.createConnection();
        //Seed.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        resp.setContentType("application/json");

        List list = DBManager.readFromDb();
        JSONArray jRay = JSONManager.toJSON(list);

        PrintWriter out = resp.getWriter();

        out.print(jRay);
        out.close();
        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);

        BufferedReader reader = req.getReader();

        Gson gson = new Gson();

        CampsiteModel cm = gson.fromJson(reader, CampsiteModel.class);
        System.out.println("asdasd");
        //System.out.println(cm.location);
        resp.setStatus(200);
        //List jList = JSONManager.fromJSON();

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
