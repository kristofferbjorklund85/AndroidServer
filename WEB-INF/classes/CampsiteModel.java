public class CampsiteModel {

    String id;
    String location;
    String name;
    double lat;
    double lng;
    String type;
    String fee;
    int capacity;
    String availability;
    String description;
    int views;
    String userId;

    public CampsiteModel(String id, String location, String name, double lat, double lng, String type, String fee,
                         int capacity, String availability, String description, int views, String userId){
        this.id = id;
        this.location = location;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.fee = fee;
        this.capacity = capacity;
        this.availability = availability;
        this.description = description;
        this.views = views;
        this.userId = userId;
    }
}
