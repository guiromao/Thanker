package co.thanker.data;

public class IdData {

    private String mId;
    private ThanksData mData;

    public IdData(String id, ThanksData data){
        mId = id;
        mData = data;
    }

    public String getId(){
        return mId;
    }

    public ThanksData getData(){
        return mData;
    }

    public void setId(String s){
        mId = s;
    }

    public void setValue(ThanksData t){
        mData = t;
    }

}
