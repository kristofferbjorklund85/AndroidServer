import java.util.List;
import com.google.gson.Gson;
import org.json.*;

/**
 * Util class to convert List of objects to JSON. Uses Google's {@link Gson} library.
 */
public class JSONManager {

    /**
     * Converts list of CampsiteModel-objects to JSONArray.
     * @param list of CampsiteModel-objects.
     * @return JSONArray of CampsiteModel-objects.
     */
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

    /**
     * Converts list of CommentModel-objects to JSONArray.
     * @param list of CommentModel-objects.
     * @return JSONArray of CommentModel-objects.
     */
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

    /**
     * Converts UserModel-objects to JSON using {@link Gson}.
     * @param user
     * @return JSON object as a string.
     */
    public static String userToJSON(UserModel user) {
        Gson gson = new Gson();
        String userJSON = gson.toJson(user);

        return userJSON;
    }

    /**
     * Converts list of RatingModel-objects to JSONArray.
     * @param list of RatingModel-objects.
     * @return JSONArray of RatingModel-objects.
     */
    public static JSONArray ratingToJSON(List list) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < list.size(); i++) {
            RatingModel rat = (RatingModel) list.get(i);
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("userId", rat.userId);
            jsonObj.put("rating", rat.rating);

            jsonArray.put(jsonObj);
        }
        return jsonArray;

    }

}
