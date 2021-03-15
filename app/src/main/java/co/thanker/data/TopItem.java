package co.thanker.data;

import co.thanker.utils.DataUtils;

public class TopItem {

    private String mUserId;
    private String mName;
    private long mValue;

    public TopItem(String userId, String name, long value){
        mUserId = userId;
        mName = DataUtils.capitalize(name);
        mValue = value;
    }

    public String getUserId(){
        return mUserId;
    }

    public String getName(){
        return mName;
    }

    public long getValue(){
        return mValue;
    }

    public void setUserId(String id){
        mUserId = id;
    }

    public void setName(String name){
        mName = name;
    }

    public void setValue(long value){
        mValue = value;
    }

}
