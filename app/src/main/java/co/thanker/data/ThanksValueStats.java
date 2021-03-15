package co.thanker.data;

import android.os.Parcelable;

import java.io.Serializable;

public class ThanksValueStats implements Serializable {

    private String mThanksType;
    private int mGraphicLength;
    private long mThanksValue;

    public ThanksValueStats(String type, int length, long value){
        mThanksType = type;
        mGraphicLength = length;
        mThanksValue = value;
    }

    public String getThanksType(){
        return mThanksType;
    }

    public int getGraphicLength(){
        return mGraphicLength;
    }

    public long getThanksValue(){
        return mThanksValue;
    }

    public void setGraphicLength(int number){
        mGraphicLength = number;
    }
}
