package co.thanker.data;

import java.util.Calendar;
import java.util.Date;

public class DateShow {

    private Date mDate;
    private boolean mShow;

    public DateShow(Date d, boolean s){
        mShow = s;
        mDate = d;
    }

    public boolean getShow(){
        return mShow;
    }

    public Date getDate(){
        return mDate;
    }

    public void setShow(boolean b){
        mShow = b;
    }

    public void setDate(Date d){
        mDate = d;
    }

}
