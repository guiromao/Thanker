package co.thanker.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class Message implements Serializable {

    private String key;
    private String title;
    private String text;
    private String fromUserId;
    private String toUserId;
    private long date;
    private boolean seen;
    private int type;

    public Message(){

    }

    public Message(String title, String text, String fromUserId, String toUserId, int type){
        key = "";
        this.title = title;
        this.text = text;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        seen = false;
        date = System.currentTimeMillis();
        this.type = type;
    }

    public String getKey(){
        return key;
    }

    public String getTitle(){
        return title;
    }

    public String getText(){
        return text;
    }

    public String getFromUserId(){
        return fromUserId;
    }

    public String getToUserId(){
        return toUserId;
    }

    public long getDate(){
        return date;
    }

    public boolean getSeen(){
        return seen;
    }

    public int getType() {
        return type;
    }

    public void setKey(String s){
        key = s;
    }

    public void setTitle(String s){
        title = s;
    }

    public void setText(String s){
        text = s;
    }

    public void setFromUserId(String s){
        fromUserId = s;
    }

    public void setToUserId(String s){
        toUserId = s;
    }

    public void setDate(long l){
        date = l;
    }

    public void setSeen(boolean b){
        seen = b;
    }

    public void setType(int type) {
        this.type = type;
    }
}
