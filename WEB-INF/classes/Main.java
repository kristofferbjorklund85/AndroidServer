import org.json.JSONArray;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Running db.Main...");

        DBServlet.createConnection();
        Seed.init();
        List list = DBManager.readFromDb();
        JSONArray jRay = JSONManager.toJSON(list);
        List jList = JSONManager.fromJSON(jRay);

        System.out.println("Finished");
    }
}

