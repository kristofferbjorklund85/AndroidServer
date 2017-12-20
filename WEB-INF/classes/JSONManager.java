import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.json.*;


public class JSONManager {

    //TODO se över denna metod, behöver vi den?
    /*public static List CampsitefromJSON(JSONArray array) {
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
                    jsonObj.getString("description"),
                    jsonObj.getDouble("rating"),
                    jsonObj.getInt("views"),
                    jsonObj.getString("username"));

                campList.add(cm);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }
        return campList;
    }*/

    public static JSONArray campsitesToJSON(List list) {
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
            jsonObj.put("views", cm.views);
            jsonObj.put("userId", cm.userId);

            jsonArray.put(jsonObj);
        }
        return jsonArray;
    }

    public static JSONArray commentsToJSON(List list) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            CommentModel c = (CommentModel) list.get(i);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("id", c.id);
            jsonObj.put("campsiteid", c.campsiteId);
            jsonObj.put("date", c.date);
            jsonObj.put("userId", c.userId);
            jsonObj.put("username", c.username);
            jsonObj.put("commentbody", c.commentBody);

            jsonArray.put(jsonObj);
        }
        return jsonArray;
    }

    public static String userToJSON(User user) {
        Gson gson = new Gson();
        String userJSON = gson.toJson(user);

        return userJSON;
    }

    public static JSONArray ratingToJSON(List list) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            Rating rat = (Rating) list.get(i);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userId", rat.userId);
            jsonObj.put("rating", rat.rating);

            jsonArray.put(jsonObj);
        }
        return jsonArray;

    }

}
