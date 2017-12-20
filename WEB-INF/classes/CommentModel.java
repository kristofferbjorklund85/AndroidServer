public class CommentModel {
    String id;
    String campsiteId;
    String date;
    String userId;
    String username;
    String commentBody;

    public CommentModel(String id, String campsiteId, String date, String userId, String username, String commentBody){
        this.id = id;
        this.campsiteId = campsiteId;
        this.date = date;
        this.userId = userId;
        this.username = username;
        this.commentBody = commentBody;
    }
}
