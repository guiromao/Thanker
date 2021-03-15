package co.thanker.data;

import java.io.Serializable;

public class UserResult implements Serializable {

    private String mUserId;
    private boolean mIsConcept;
    private String mImageUrl;
    private String mName;
    private String mEmail;
    private String mCategory;
    private long mDateAdded;

    public UserResult(String id, boolean concept, String url, String name, String email, String category){
        mUserId = id;
        mIsConcept = concept;
        mImageUrl = url;
        mName = name;
        mEmail = email;
        mCategory = category;
        mDateAdded = System.currentTimeMillis();
    }

    public String getUserId(){
        return mUserId;
    }

    public boolean getIsConcept(){
        return mIsConcept;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public String getName(){
        return mName;
    }

    public String getEmail(){
        return mEmail;
    }

    public String getCategory(){
        return mCategory;
    }

    public long getDateAdded(){
        return mDateAdded;
    }

    public void setUserId(String id){
        mUserId = id;
    }

    public void setIsConcept(boolean b){
        mIsConcept = b;
    }

    public void setImageUrl(String url){
        mImageUrl = url;
    }

    public void setName(String name){
        mName = name;
    }

    public void setEmail(String email){
        mEmail = email;
    }

    public void setCategory(String category){
        mCategory = category;
    }

    public void setDateAdded(long date){
        mDateAdded = date;
    }

}
