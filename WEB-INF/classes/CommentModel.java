public class CommentModel {
    String id;
    String campsiteId;
    String date;
    String username;
    String commentBody;

    public CommentModel(String id, String campsiteId, String date, String username, String commentBody){
        this.id = id;
        this.campsiteId = campsiteId;
        this.date = date;
        this.username = username;
        this.commentBody = commentBody;
    }
}
