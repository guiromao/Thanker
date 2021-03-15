package co.thanker.data;

public class NameId {

    private String mName;
    private String mId;

    public NameId(String name, String id){
        mName = name;
        mId = id;
    }

    public String getName(){
        return mName;
    }

    public String getId(){
        return mId;
    }

}
