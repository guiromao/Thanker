package co.thanker.data;

import java.io.Serializable;

public class UserValue implements Serializable {

    private String userId;
    private long valueThanks;

    public UserValue(){

    }

    public UserValue(String uId, long value){
        userId = uId;
        valueThanks = value;
    }

    public String getUserId(){
        return userId;
    }

    public long getValueThanks(){
        return valueThanks;
    }

    public void setUserId(String uId){
        userId = uId;
    }

    public void setValueThanks(long l){
        valueThanks = l;
    }
}
