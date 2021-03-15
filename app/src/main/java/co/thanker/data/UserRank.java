package co.thanker.data;

public class UserRank {

    private String username;
    private String imageUrl;
    private long valueOfThanks;

    public UserRank(){

    }

    public UserRank(String name, String image, long value){
        username = name;
        imageUrl = image;
        valueOfThanks = value;
    }

    public String getUsername(){
        return username;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public long getValueOfThanks(){
        return valueOfThanks;
    }

    public void setUsername(String s){
        username = s;
    }

    public void setImageUrl(String img){
        imageUrl = img;
    }

    public void setValueOfThanks(long l){
        valueOfThanks = l;
    }
}
