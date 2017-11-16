

public class Main {

    public static void main(String[] args){
        System.out.println("Running db.Main...");
        if(DBServlet.hasConnection()){
            System.out.println("We have a connection");
        }else{
            System.out.println("There was a problem getting a connection.");
        }
        Seed.init();
    }
}

