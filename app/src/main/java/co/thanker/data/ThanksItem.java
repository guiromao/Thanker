package co.thanker.data;

import co.thanker.utils.DataUtils;

public class ThanksItem {

    private String mName;
    private String mThanksType;
    private long mDate;
    private boolean mWasWelcomed;

    public ThanksItem(String name, String type, long date, boolean welcomed){
        mName = DataUtils.capitalize(name);
        mThanksType = type;
        mDate = date;
        mWasWelcomed = welcomed;
    }

    public String getName(){
        return mName;
    }

    public String getThanksType(){
        return mThanksType;
    }

    public long getDate(){
        return mDate;
    }

    public boolean getWasWelcomed(){
        return mWasWelcomed;
    }

    public void setName(String name){
        mName = DataUtils.capitalize(name);
    }

    public void setThanksType(String type){
        mThanksType = type;
    }

    public void setDate(long time){
        mDate = time;
    }

    public void setWasWelcomed(boolean welcomed){
        mWasWelcomed = welcomed;
    }

}
