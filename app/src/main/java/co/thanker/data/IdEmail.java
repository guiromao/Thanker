package co.thanker.data;

public class IdEmail {

    private String mId;
    private String mEmail;

    public IdEmail(String id, String email){
        mId = id;
        mEmail = email;
    }

    public String getId(){
        return mId;
    }

    public String getEmail(){
        return mEmail;
    }
}
