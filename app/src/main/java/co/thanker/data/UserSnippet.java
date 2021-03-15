package co.thanker.data;

import java.io.Serializable;

public class UserSnippet implements Serializable {

    private String name;
    private String userId;
    private String email;
    private String imageUrl;
    private String primaryCategory;
    private String secondaryCategory;
    private String livingCountry;
    /*private long givenThanksValue;
    private long receivedThanksValue;*/

    public UserSnippet(){

    }

    public UserSnippet(String name, String userId, String email, String imageUrl, String primaryCategory, String secondaryCategory, String country/*, long givenValue, long receivedValue*/){
        this.name = name;
        this.userId = userId;
        this.email = email;
        this.imageUrl = imageUrl;
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        livingCountry = country;
        /*givenThanksValue = givenValue;
        receivedThanksValue = receivedValue;*/
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail(){
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public String getSecondaryCategory() {
        return secondaryCategory;
    }

    public String getLivingCountry(){
        return livingCountry;
    }

    /*public long getGivenThanksValue(){
        return givenThanksValue;
    }

    public long getReceivedThanksValue(){
        return receivedThanksValue;
    }*/

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String mail){
        email = mail;
    }

    public void setImageUrl(String s){
        imageUrl = s;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public void setSecondaryCategory(String secondaryCategory) {
        this.secondaryCategory = secondaryCategory;
    }

    public void setLivingCountry(String country){
        livingCountry = country;
    }

    /*public void setGivenThanksValue(long value){
        givenThanksValue = value;
    }

    public void setReceivedThanksValue(long value){
        receivedThanksValue = value;
    }*/
}
