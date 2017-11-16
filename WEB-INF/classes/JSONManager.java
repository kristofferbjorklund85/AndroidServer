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
            } catch (JSONException e) {
                //TODO create a standardised method for logging
            }
        }

        return campList;
    }

    public static JSONArray toJSON(List list) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObj = new JSONObject();

            jsonArray.put(jsonObj);
        }

        return jsonArray;
    }

}
