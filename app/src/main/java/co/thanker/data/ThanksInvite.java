package co.thanker.data;

public class ThanksInvite {

    private String fromUserId;
    private String toEmail;
    private String description;
    private String thanksType;
    private String country;
    private long date;
    private String inviteCode;

    public ThanksInvite(){

    }

    public ThanksInvite(String fromId, String email, long date, String type, String nation){
        fromUserId = fromId;
        toEmail = email.toLowerCase();
        description = "";
        thanksType = type;
        country = nation;
        this.date = date;
        inviteCode = fromUserId + "_" + toEmail;
    }

    public String getFromUserId(){
        return fromUserId;
    }

    public String getToEmail(){
        return toEmail;
    }

    public String getDescription(){
        return description;
    }

    public String getThanksType(){
        return  thanksType;
    }

    public String getCountry(){
        return country;
    }

    public long getDate(){
        return date;
    }

    public String getInviteCode(){
        return inviteCode;
    }

    public void setFromUserId(String s){
        fromUserId = s;
    }

    public void setToEmail(String email){
        toEmail = email;
    }

    public void setDescription(String reason){
        description = reason;
    }

    public void setThanksType(String type){
        thanksType = type;
    }

    public void setCountry(String nation){
        country = nation;
    }

    public void setDate(long value){
        date = value;
    }

    public void setInviteCode(String code){
        inviteCode = code;
    }

}
