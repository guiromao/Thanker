package co.thanker.data;

import java.io.Serializable;

public class FriendData implements Serializable {

    private FriendRank mFriendRank;
    private String mName;

    public FriendData(FriendRank friend, String name){
        mFriendRank = friend;
        mName = name;
    }

    public FriendRank getFriendRank(){
        return mFriendRank;
    }

    public String getName(){
        return mName;
    }

    public void setFriendRank(FriendRank friend){
        mFriendRank = friend;
    }

    public void setName(String name){
        mName = name;
    }

}
