import java.util.ArrayList;
import java.util.List;

import org.json.*;


public class JSONManager {

    //TODO se över denna metod, behöver vi den?
    public static List CampsitefromJSON(JSONArray array) {
        List<CampsiteModel> campList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObj = array.getJSONObject(i);
                CampsiteModel cm = new CampsiteModel(
                    jsonObj.getString("id"),
                    jsonObj.getString("location"),
                    jsonObj.getString("name"),
                    jsonObj.getDouble("lat"),
                    jsonObj.getDouble("lng"),
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

    public static JSONArray campsiteToJSON(List list) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            CampsiteModel cm = (CampsiteModel) list.get(i);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", cm.id);
            jsonObj.put("location", cm.location);
            jsonObj.put("name", cm.name);
            jsonObj.put("lat", cm.lat);
            jsonObj.put("lng", cm.lng);
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
