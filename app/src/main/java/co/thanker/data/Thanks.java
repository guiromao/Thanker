package co.thanker.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import co.thanker.utils.DataUtils;

public class Thanks implements Serializable {

    //private String thanksId;
    private String thanksId;
    private String fromUserId;
    private String toUserId;
    private String description;
    private long date;
    private String primaryCategory;
    private String secondaryCategory;
    private String year;
    private String month;
    private String day;
    private boolean showThanksDescription;
    //private String mCity;
    private String country;
    private String thanksType;
    private boolean wasWelcomed;


    public Thanks(){

    }

    public Thanks(/*String id, */String fromUser, String toUser, String desc, long d, String pCat, String sCat, String year, String month, String day,
            /*String city, */String country, String type){
        //thanksId = id;
        fromUserId = fromUser;
        toUserId = toUser;
        description = desc;
        date = d;
        primaryCategory = pCat;
        secondaryCategory = sCat;
        this.year= year;
        this.month = month;
        this.day = day;
        showThanksDescription = true;
        //mCity = city;
        this.country = country;
        thanksType = type;
        wasWelcomed = false;
        thanksId = fromUserId + "" +
                "" +
                "" + toUserId + "_" + year + "_" + month + "_" + day;
    }

    public Thanks(/*String id, */String fromUser, String toUser, String desc, long d, String pCat, String sCat, String year, String month, String day,
            /*String city, */String country, String type, boolean showDescription){
        //thanksId = id;
        fromUserId = fromUser;
        toUserId = toUser;
        description = desc;
        date = d;
        primaryCategory = pCat;
        secondaryCategory = sCat;
        this.year= year;
        this.month = month;
        this.day = day;
        showThanksDescription = showDescription;
        //mCity = city;
        this.country = country;
        thanksType = type;
        wasWelcomed = false;
        thanksId = fromUserId + "" +
                "" +
                "" + toUserId + "_" + year + "_" + month + "_" + day;
    }

    public Thanks(User user, SmsInvite invite){
        fromUserId = invite.getFromId();
        toUserId = user.getUserId();
        description = invite.getText();

        if(description == null){
            description = "";
        }

        date = invite.getDate();
        primaryCategory = user.getPrimaryCategory();
        secondaryCategory = user.getSecondaryCategory();

        Date inviteDate = new Date(invite.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inviteDate);

        year = DataUtils.generateYear(calendar);
        month = DataUtils.generateMonth(calendar);
        day = DataUtils.generateDay(calendar);
        country = invite.getCountry();
        thanksType = invite.getType();
        wasWelcomed = false;
        thanksId = fromUserId + "_" + toUserId + "_" + year + "_" + month + "_" + day;
    }

    public Thanks(User user, ThanksInvite invite){
        fromUserId = invite.getFromUserId();
        toUserId = user.getUserId();
        description = invite.getDescription();

        if(description == null){
            description = "";
        }

        date = invite.getDate();
        primaryCategory = user.getPrimaryCategory();
        secondaryCategory = user.getSecondaryCategory();

        Date inviteDate = new Date(invite.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inviteDate);

        year = DataUtils.generateYear(calendar);
        month = DataUtils.generateMonth(calendar);
        day = DataUtils.generateDay(calendar);
        country = invite.getCountry();
        thanksType = invite.getThanksType();
        wasWelcomed = false;
        thanksId = fromUserId + "_" + toUserId + "_" + year + "_" + month + "_" + day;
    }

    /*public String getThanksId(){
        return thanksId;
    }*/

    public String printThanks(){
        String result =
                "From: " + fromUserId
                + ". To: " + toUserId
                +" . Date: " + date
                + ". Type: " + String.valueOf(thanksType);

        return result;
    }

    public String getThanksId(){
        return thanksId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public String getDescription(){
        return description;
    }

    public long getDate() {
        return date;
    }

    public String getDay(){
        return day;
    }

    public String getPrimaryCategory(){
        return primaryCategory;
    }

    public String getSecondaryCategory(){
        return secondaryCategory;
    }

    public boolean getShowThanksDescription(){
        return showThanksDescription;
    }

   /*public String getCity() {
        return mCity;
    } */

    public String getCountry() {
        return country;
    }

    public String getYear(){
        return year;
    }

    public String getMonth(){
        return month;
    }

    public String getThanksType() {
        return thanksType;
    }

    public boolean getWasWelcomed() {
        return wasWelcomed;
    }

    /*public void setThanksId(String id){
        thanksId = id;
    }*/

    public void setThanksId(String string){
        thanksId = string;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public void setDescription(String s){
        description = s;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setPrimaryCategory(String s){
        primaryCategory = s;
    }

    public void setSecondaryCategory(String s){
        secondaryCategory = s;
    }

    /*public void setCity(String city) {
        this.mCity = city;
    }*/

    public void setCountry(String country) {
        this.country = country;
    }

    public void setShowThanksDescription(boolean b){
        showThanksDescription = b;
    }

    public void setYear(String s){
        year = s;
    }

    public void setMonth(String s){
        month = s;
    }

    public void setDay(String day){
        this.day = day;
    }

    public void setThanksType(String thanksType) {
        this.thanksType = thanksType;
    }

    public void setWasWelcomed(boolean wasWelcomed) {
        this.wasWelcomed = wasWelcomed;
    }

    public enum ThanksType {
        NORMAL("NORMAL", 0),
        SUPER("SUPER", 1),
        MEGA("MEGA", 2),
        POWER("SUPER", 3);

        private String thanksTypeString;
        private int thanksTypeInt;

        private ThanksType(String string, int i){
            thanksTypeString = string;
            thanksTypeInt = i;
        }

        @Override
        public String toString(){
            return thanksTypeString;
        }
    };

}
