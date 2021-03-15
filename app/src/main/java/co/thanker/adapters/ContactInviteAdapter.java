package co.thanker.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.data.Contact;
import co.thanker.utils.ImageUtils;

public class ContactInviteAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "ContactInviteAdapter";

    private Context mContext;
    private List<Contact> mListContacts;
    private List<Contact> mListInvited;
    private List<Contact> mListExisting;
    private ColorStateList mGreen;
    private ColorStateList mWhite;
    private ColorStateList mGrey;

    public ContactInviteAdapter(@NonNull Context context, int resource, List<Contact> list, List<Contact> existingList) {
        super(context, resource, list);
        mContext = context;
        mListContacts = list;
        mListInvited = new ArrayList<>();
        mListExisting = existingList;
        mGreen = mContext.getResources().getColorStateList(R.color.colorPrimary);
        mWhite = mContext.getResources().getColorStateList(R.color.white);
        mGrey = mContext.getResources().getColorStateList(R.color.lightGrey);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_contact_invite, parent, false);
        }

        final Contact user = getItem(position);
        final TextView textName = listItemView.findViewById(R.id.text_name);
        final TextView textEmail = listItemView.findViewById(R.id.text_email);
        final CheckBox checkbox = listItemView.findViewById(R.id.check_invite);
        final ImageView imageThanks = listItemView.findViewById(R.id.image_thanks);
        final ImageView imageProfile = listItemView.findViewById(R.id.profile_picture);

        textName.setText(user.getName());
        textEmail.setText(user.getEmail());

        if(user.getPhotoUri() != null){
            imageProfile.setImageURI(user.getPhotoUri());
        }
        else {
            ImageUtils.loadImageInto(mContext, ImageUtils.DEFAULT_IMAGE, imageProfile);
        }

        checkbox.setClickable(false);

        if (existsInExisting(user)) {
            listItemView.setEnabled(false);
            checkbox.setClickable(false);

        } else {
            textEmail.setTypeface(null, Typeface.NORMAL);
            //checkbox.setVisibility(View.VISIBLE);
            if (existsInList(user)) {
                Log.v(TAG, "Updating contact invites. User exists in list");
                checkbox.setChecked(true);
                //checkbox.setVisibility(View.GONE);
                imageThanks.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thanks));
            } else {
                Log.v(TAG, "Updating contact invites. User does NOT exist in list");
                checkbox.setChecked(false);
                //checkbox.setVisibility(View.VISIBLE);
                imageThanks.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thanksoff));
            }
        }

        //setChecked(user, checkbox);

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkbox.isChecked();

                if (!isChecked) {
                    if (!existsInList(user)) {
                        mListInvited.add(user);
                    }
                    checkbox.setChecked(true);
                    //checkbox.setVisibility(View.GONE);
                    imageThanks.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thanks));
                    Log.v(TAG, "Inviting contacts. Size of List Invited: " + mListInvited.size());
                } else {
                    if (existsInList(user)) {
                        removeFromInvited(user);
                        Log.v(TAG, "Updating contact invites. Removing contact on listItem Click");
                    }
                    checkbox.setChecked(false);
                    //checkbox.setVisibility(View.VISIBLE);
                    imageThanks.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thanksoff));
                }
            }
        });

        /*checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCard.setBackgroundTintList(mGreen);
                    textName.setTextColor(mContext.getResources().getColor(R.color.white));
                    textEmail.setTextColor(mContext.getResources().getColor(R.color.white));
                    if(!existsInList(user))
                    {
                        mListInvited.add(user);
                    }
                    Log.v(TAG, "Inviting contacts. Size of List Invited: " + mListInvited.size());
                }
                else {
                    mCard.setBackgroundTintList(mWhite);
                    textName.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                    textEmail.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                    if(existsInList(user)){
                        removeFromInvited(user);
                        Log.v(TAG, "Updating contact invites. Removing contact on CheckedChange");
                    }
                }
            }
        });*/

        return listItemView;
    }

    public List<Contact> getListInvited() {
        return mListInvited;
    }

    public void selectAll(){
        for(Contact item: mListContacts){
            if(!existsInList(item)){
                mListInvited.add(item);
            }
        }
    }

    public void selectNone(){
        for(Contact item: mListContacts){
            if(existsInList(item)){
                mListInvited.remove(item);
            }
        }
    }

    public void removeFromInvited(Contact nameEmail) {
        for (int i = 0; i != mListInvited.size(); i++) {
            if (mListInvited.get(i).getEmail().trim().equalsIgnoreCase(nameEmail.getEmail().trim())) {
                mListInvited.remove(i);
                i--;
            }
        }
    }

    public boolean existsInList(Contact user) {
        for (Contact item : mListInvited) {
            if (item.getEmail().equalsIgnoreCase(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    public boolean existsInExisting(Contact user) {
        for (Contact item : mListExisting) {
            if (item.getEmail().trim().equalsIgnoreCase(user.getEmail().trim())) {
                return true;
            }
        }
        return false;
    }

    public void setChecked(Contact item, CheckBox box) {
        int count = 0;
        for (Contact user : mListInvited) {
            if (user.getEmail().equalsIgnoreCase(item.getEmail())) {
                count++;
            }
        }

        if (count > 0) {
            box.setChecked(true);
        } else {
            box.setChecked(false);
        }
    }

}
