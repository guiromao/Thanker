package co.thanker.data;

public class UserFriend {

    private User mUser;
    private FriendRank mFriendRank;

    public UserFriend(User user, FriendRank friend){
        mUser = user;
        mFriendRank = friend;
    }

    public User getUser(){
        return mUser;
    }

    public FriendRank getFriendRank(){
        return mFriendRank;
    }

    public void setUser(User u){
        mUser = u;
    }

    public void setFriendRank(FriendRank friend){
        mFriendRank = friend;
    }

}
