package co.thanker.data;

import android.net.Uri;

public class Contact {

    private String mName;
    private String mEmail;
    private Uri mPhotoUri;

    public Contact(String name, String email, Uri uri){
        mName = name;
        mEmail = email;
        mPhotoUri = uri;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public Uri getPhotoUri() {
        return mPhotoUri;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setPhotoUri(Uri mPhotoUri) {
        this.mPhotoUri = mPhotoUri;
    }
}
