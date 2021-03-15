package co.thanker.data;

import java.io.Serializable;

public class ThanksValue implements Serializable {

    private String mKey;
    private long mValue;

    public ThanksValue(){

    }

    public ThanksValue(String key, long value){
        mKey = key;
        mValue = value;
    }

    public String getKey(){
        return mKey;
    }

    public long getValue(){
        return mValue;
    }

    public void setKey(String s){
        mKey = s;
    }

    public void setValue(long value){
        mValue = value;
    }

}
