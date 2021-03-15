package co.thanker.data;

public class CategoryValue {

    private String mName;
    private long mValue;

    public CategoryValue(String name, long value){
        mName = name;
        mValue = value;
    }

    public String getName(){
        return mName;
    }

    public long getValue(){
        return mValue;
    }

    public void setName(String s){
        mName = s;
    }

    public void setValue(long v){
        mValue = v;
    }
}
