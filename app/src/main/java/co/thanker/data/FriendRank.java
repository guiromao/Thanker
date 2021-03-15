package co.thanker.data;

import java.io.Serializable;

public class FriendRank implements Serializable {

    private String userId;
    private String name;
    private long thanksGivenTo;
    private long thanksReceivedFrom;
    private double rankFactor;
    private long dateAdded;

    public FriendRank(){

    }

    public FriendRank(String userId, String name, long thanksIGave, long thanksIReceived){
        this.userId = userId;
        this.name = name;
        this.thanksGivenTo = thanksIGave;
        this.thanksReceivedFrom = thanksIReceived;
        rankFactor = (thanksIGave) + (thanksIReceived * 0.5);
        dateAdded = System.currentTimeMillis();
    }

    public FriendRank(String userId, String name, long give, long received, double rankFactor){
        this.userId = userId;
        this.name = name;
        thanksGivenTo = give;
        thanksReceivedFrom = received;
        this.rankFactor = rankFactor;
        dateAdded = System.currentTimeMillis();
    }

    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
    }

    public long getThanksGivenTo(){
        return thanksGivenTo;
    }

    public long getThanksReceivedFrom(){
        return thanksReceivedFrom;
    }

    public double getRankFactor(){
        return rankFactor;
    }

    public long getDateAdded(){
        return dateAdded;
    }

    public void setUserId(String uId){
        userId = uId;
    }

    public void setName(String s){
        name = s;
    }

    public void setThanksGivenTo(long t){
        thanksGivenTo = t;
    }

    public void setThanksReceivedFrom(long t){
        thanksReceivedFrom = t;
    }

    public void setRankFactor(double d){
        rankFactor = d;
    }

    public void setDateAdded(long date){
        dateAdded = date;
    }

}
