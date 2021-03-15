package co.thanker.data;

public class IdEmailText {

    private String mId;
    private String mEmail;
    private String mText;

    public IdEmailText(String id, String email, String text){
        mId = id;
        mEmail = email;
        mText = text;
    }

    public IdEmailText(IdEmail idEmail, String text){
        mId = idEmail.getId();
        mEmail = idEmail.getEmail();
        mText = text;
    }

    public String getId(){
        return mId;
    }

    public String getEmail(){
        return mEmail;
    }

    public String getText(){
        return mText;
    }

}
