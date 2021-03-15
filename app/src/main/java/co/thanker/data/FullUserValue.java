package co.thanker.data;

public class FullUserValue {

    private User mUser;
    private UserValue mUserValue;

    public FullUserValue(User user, UserValue value){
        mUser = user;
        mUserValue = value;
    }

    public User getUser(){
        return mUser;
    }

    public UserValue getUserValue(){
        return mUserValue;
    }

    public void setUser(User u){
        mUser = u;
    }

    public void setUserValue(UserValue v){
        mUserValue = v;
    }
}
