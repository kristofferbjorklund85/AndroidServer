import java.util.ArrayList;
import java.util.List;

import org.json.*;


public class JSONManager {

    public static List fromJSON(JSONArray array) {
        List<CampsiteModel> campList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                CampsiteModel cm = new CampsiteModel(
                    jsonObj.getInt("id"),
                    jsonObj.getString("location"),
                    jsonObj.getString("coordinates"),
                    jsonObj.getString("type"),
                    jsonObj.getString("fee"),
                    jsonObj.getInt("capacity"),
                    jsonObj.getString("availability"),
                    jsonObj.getString("description"));
                campList.add(cm);
                System.out.println("created JSON Object from JsonArray.");
            } catch (JSONException e) {
                //TODO create a standardised method for logging
            }
        }
        System.out.println("Return List with Jsonobjects.");
        return campList;
    }

    public static JSONArray toJSON(List list) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            CampsiteModel cm = (CampsiteModel) list.get(i);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", cm.id);
            jsonObj.put("location", cm.location);
            jsonObj.put("coordinates", cm.coordinates);
            jsonObj.put("type", cm.type);
            jsonObj.put("fee", cm.fee);
            jsonObj.put("capacity", cm.capacity);
            jsonObj.put("availability", cm.availability);
            jsonObj.put("description", cm.description);

            jsonArray.put(jsonObj);
            System.out.println("Added JSON Object to array.");
        }
        System.out.println("Return Array with campsiteobjects.");
        return jsonArray;
    }

}
