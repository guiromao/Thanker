package co.thanker.data;

public class BirthdayTimes {

    private long mBirthday;
    private int mTimes;

    public BirthdayTimes(long birthday, int times){
        mBirthday = birthday;
        mTimes = times;
    }

    public long getBirthday(){
        return mBirthday;
    }

    public int getTimes(){
        return mTimes;
    }

    public void setBirthday(long birthday){
        mBirthday = birthday;
    }

    public void setTimes(int times){
        mTimes = times;
    }

}
