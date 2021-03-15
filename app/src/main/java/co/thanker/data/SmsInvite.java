package co.thanker.data;

public class SmsInvite {

    private String phone;
    private String fromId;
    private String country;
    private String type;
    private String text;
    private long date;

    public SmsInvite(){

    }

    public SmsInvite(String number, String id, String c, String t, String body, long d){
        phone = number;
        fromId = id;
        country = c;
        type = t;
        text = body;
        date = d;
    }

    public String getPhone(){
        return phone;
    }

    public String getFromId(){
        return fromId;
    }

    public String getCountry(){
        return country;
    }

    public String getType(){
        return type;
    }

    public String getText(){
        return text;
    }

    public long getDate(){
        return date;
    }

    public void setPhone(String number){
        phone = number;
    }

    public void setFromId(String id){
        fromId = id;
    }

    public void setCountry(String nation){
        country = nation;
    }

    public void setType(String t){
        type = t;
    }

    public void setText(String b){
        text = b;
    }

    public void setDate(long newDate){
        date = newDate;
    }

}
