package co.thanker.data;

import java.io.Serializable;

public class FriendRequest implements Serializable {

    private String userId;
    private boolean seen;

    public FriendRequest(){

    }

    public FriendRequest(String id, boolean b){
        userId = id;
        seen = b;
    }

    public String getUserId(){
        return userId;
    }

    public boolean getSeen(){
        return seen;
    }

    public void setUserId(String s){
        userId = s;
    }

    public void setSeen(boolean b){
        seen = b;
    }
}
