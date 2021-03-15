package co.thanker.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import co.nedim.maildroidx.MaildroidX;
import co.nedim.maildroidx.MaildroidXType;
import co.thanker.R;
import co.thanker.adapters.ContactInviteAdapter;
import co.thanker.data.Contact;
import co.thanker.data.ThanksInvite;
import co.thanker.data.User;
import co.thanker.utils.DataUtils;
import co.thanker.utils.TextUtils;
import co.thanker.utils.Utils;

public class ContactsInviteFragment extends Fragment {

    private static final String TAG = "ContactsInviteFragment";
    private static final String DB_REFERENCE = "users";
    private final String USER_OBJECT = "user-object";
    private final String CONTACTS_INVITED = "contacts-invited";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String INVITE_REF = "invites";
    private final int REQUEST_CONTACTS_PERMISSION = 6;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private ListView mListView;
    private ContactInviteAdapter mAdapter;
    private FrameLayout mFrameButton;
    private ImageView mImageFriends;
    private ImageView mImageFriendsLittle;
    private Button mButtonImport;
    private Button mButtonInvite;
    private TextView mTextInvited;
    private TextView mTextSelect;
    private TextView mEmptyView;
    private ImageView mImageLogo;
    private LinearLayout mLinearInvited;
    private LinearLayout mLinearHeader;
    private ProgressBar mProgressBar;
    private String mCountry;
    private User mUser;
    private long mCountInvites;
    private long mCountSuccessInvites;
    private boolean mStartedImporting;
    private boolean mAllSelected;

    private List<Contact> mListContacts;
    private List<Contact> mListToInvite;
    private List<Contact> mListAlreadyInvited;
    private boolean mPermission;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public ContactsInviteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts_invite, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mSharedPreferences = getActivity().getSharedPreferences(CONTACTS_INVITED + mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mAllSelected = false;

        mListView = view.findViewById(R.id.list_contacts);
        mImageFriends = view.findViewById(R.id.image_friends);
        mImageFriendsLittle = view.findViewById(R.id.image_friends_two);
        mFrameButton = view.findViewById(R.id.frame_button);
        mButtonImport = (Button) view.findViewById(R.id.button_import_contacts);
        mButtonInvite = view.findViewById(R.id.button_invite);
        mEmptyView = view.findViewById(R.id.text_empty_view_contacts);
        mTextInvited = view.findViewById(R.id.text_invited);
        mTextSelect = view.findViewById(R.id.text_select);
        mImageLogo = view.findViewById(R.id.image_logo);
        mLinearInvited = view.findViewById(R.id.linear_invited);
        mLinearHeader = view.findViewById(R.id.linear_header);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mCountry = "";
        mStartedImporting = false;

        mTextSelect.setText(TextUtils.underlineText(getActivity().getString(R.string.select_all)));

        Drawable friendsDraw = mImageFriends.getDrawable();
        friendsDraw.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        Drawable friendsLittleDraw = mImageFriendsLittle.getDrawable();
        friendsLittleDraw.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);

        if (getArguments() != null) {
            mCountry = getArguments().getString(OUR_USER_COUNTRY);
            mUser = (User) getArguments().getSerializable(USER_OBJECT);
        }

        if(savedInstanceState != null){
            mStartedImporting = savedInstanceState.getBoolean("started-importing");
            mListContacts = (List<Contact>) savedInstanceState.getSerializable("list-invited");
            mAllSelected = savedInstanceState.getBoolean("all-selected");

            if(mAllSelected){
                mTextSelect.setText(TextUtils.underlineText(getActivity().getString(R.string.select_all)));
            }
            else {
                mTextSelect.setText(TextUtils.underlineText(getActivity().getString(R.string.deselect_all)));
            }
        }

        requestContactPermissions();

        if(!mStartedImporting){
            Log.v(TAG, "Import contacts. Activating onClick for importing");
            mButtonImport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Import contacts. Clicked");
                    mStartedImporting = true;
                    startList();
                }
            });
        }
        else {
            getList();
            startList();
        }

        mTextSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAllSelected){
                    mAdapter.selectNone();
                    mAllSelected = false;
                    mTextSelect.setText(TextUtils.underlineText(getActivity().getString(R.string.select_all)));
                }
                else {
                    mAdapter.selectAll();
                    mAllSelected = true;
                    mTextSelect.setText(TextUtils.underlineText(getActivity().getString(R.string.deselect_all)));

                }
                mAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }

    public void makeList() {
        if (getActivity() != null) {
            if (mListContacts.size() > 0) {
                mAdapter = new ContactInviteAdapter(getActivity(), 0, mListContacts, mListAlreadyInvited);
                mListView.setAdapter(mAdapter);

                mButtonInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListToInvite = mAdapter.getListInvited();
                        if(mListToInvite.size() > 0){
                            mCountInvites = 0;
                            mCountSuccessInvites = 0;
                            mProgressBar.setVisibility(View.VISIBLE);
                            for (final Contact invite : mListToInvite) {
                                Query inviteQuery = mFirestore.collection(INVITE_REF).whereEqualTo("fromUserId", mAuth.getCurrentUser().getUid())
                                        .whereEqualTo("toEmail", invite.getEmail()).limit(1);
                                inviteQuery.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if(queryDocumentSnapshots.size() == 0){
                                                    Query userQuery = mFirestore.collection(DB_REFERENCE).whereEqualTo("email", invite.getEmail()).limit(1);
                                                    userQuery.get()
                                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    if (queryDocumentSnapshots.size() == 0) {
                                                                        String email = invite.getEmail().replace('.', ',');
                                                                        ThanksInvite thanks = new ThanksInvite(mAuth.getCurrentUser().getUid(), email, System.currentTimeMillis(), "NORMAL", mCountry);
                                                                        mFirestore.collection(INVITE_REF).add(thanks);
                                                                        sendMaildroid(invite);
                                                                        mCountSuccessInvites++;
                                                                    }

                                                                    if(!existsInAlreadyInvited(invite)){
                                                                        mListAlreadyInvited.add(invite);
                                                                    }

                                                                    mCountInvites++;
                                                                    if (mCountInvites == mListToInvite.size() && mCountSuccessInvites > 0) {
                                                                        setList(mListAlreadyInvited);
                                                                        mButtonInvite.setVisibility(View.GONE);
                                                                        mListView.setVisibility(View.GONE);
                                                                        mProgressBar.setVisibility(View.GONE);
                                                                        mFrameButton.setVisibility(View.GONE);
                                                                        mLinearInvited.setVisibility(View.VISIBLE);
                                                                    }
                                                                    else if(mCountInvites == mListToInvite.size() && mCountSuccessInvites == 0){
                                                                        mListView.setVisibility(View.GONE);
                                                                        mButtonImport.setVisibility(View.GONE);
                                                                        mImageFriends.setVisibility(View.GONE);
                                                                        mProgressBar.setVisibility(View.GONE);
                                                                        mImageLogo.setVisibility(View.GONE);
                                                                        mFrameButton.setVisibility(View.GONE);
                                                                        mTextInvited.setText(getActivity().getString(R.string.users_exist_or_invited));
                                                                        mLinearInvited.setVisibility(View.VISIBLE);
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.select_contacts), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                mListView.setEmptyView(mEmptyView);
            }
        }
    }

    public void getContactDetails() {
        mListContacts = new ArrayList<>();
        ArrayList<String> emlRecs = new ArrayList<String>();
        HashSet<String> emlRecsHS = new HashSet<String>();
        Context context = getActivity();
        ContentResolver cr = context.getContentResolver();
        String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                Log.v(TAG, "Contact Importing. Value of Photo ID: " + cur.getString(2));
                // names comes in hand sometimes
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);
                Uri photoUri;
                if(cur.getString(2) != null)
                {
                    photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, Long.parseLong(cur.getString(2)));
                }
                else {
                    photoUri = null;
                }

                // keep unique only
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    emlRecs.add(emlAddr);
                }

                if(!doesListContainContact(emlAddr))
                {
                    mListContacts.add(new Contact(name, emlAddr, photoUri));
                }

            } while (cur.moveToNext());
        }

        cur.close();
        //return emlRecs;
    }

    public void startList(){
        mProgressBar.setVisibility(View.VISIBLE);
        mButtonImport.setVisibility(View.GONE);
        mImageFriends.setVisibility(View.GONE);
        mButtonInvite.setVisibility(View.VISIBLE);
        mFrameButton.setVisibility(View.VISIBLE);
        mLinearHeader.setVisibility(View.VISIBLE);
        mTextSelect.setVisibility(View.VISIBLE);
        getContactsAndExecute();
        makeList();
        mProgressBar.setVisibility(View.GONE);
    }

    public void printContacts() {
        for (Contact item : mListContacts) {
            Log.v(TAG, "Importing contacts. Name: " + item.getName() + ". Email: " + item.getEmail());
        }
    }

    public boolean doesListContainContact(String email){
        for(Contact item: mListAlreadyInvited){
            if(item.getEmail().equalsIgnoreCase(email.trim())){
                return true;
            }
        }
        return false;
    }

    public void printListInvited() {
        for (Contact item : mListToInvite) {
            Log.v(TAG, "Importing contacts. These are to invite. Name: " + item.getName() + ". Email: " + item.getEmail());
        }
    }

    public boolean existsInAlreadyInvited(Contact user){
        for(Contact item: mListAlreadyInvited){
            if(item.getEmail().trim().equalsIgnoreCase(user.getEmail().trim())){
                return true;
            }
        }
        return false;
    }

    public void getContactsAndExecute() {
        getContactDetails();
        printContacts();
    }

    public void requestContactPermissions() {
        if (getActivity() != null) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                mPermission = true;
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    mPermission = true;
                }

                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_PERMISSION);
            }
        }
    }

    private void sendMaildroid(Contact user) {

        String title = "";
        String body = "";
        String typeThanks = getActivity().getString(R.string.thanked_email);
        String typeThanksHtml = getActivity().getString(R.string.thanked_html);



        String logo = "<img src=\"https://i.ibb.co/LCGHW7K/Thanker-HUG-Signature-Solid-Green-2x.png\" alt=\"Thanker-HUG-Signature-Solid-Green-2x\" width=200 height=200 border=\"0\">";
        title += getString(R.string.title_invite_thanker, DataUtils.capitalize(mUser.getName()), typeThanks);
        body += "<center><b><font size = 20>" + DataUtils.capitalize(mUser.getName()) + " " + typeThanksHtml + " " + getString(R.string.on) + "</font></b> " +
                "<br>" + logo + "</center>" + getString(R.string.emailinvite);

        new MaildroidX.Builder()
                .smtp("smtpout.secureserver.net")//.smtp("smtp.gmail.com")
                .smtpUsername("thankyou@thanker.co")
                //.smtpPassword("thankerinviteshello2020!")
                .smtpPassword("thisisthemakers2020!")
                //.smtpAuthentication(true)
                .port("465")
                .type(MaildroidXType.HTML)
                .to(user.getEmail().trim())
                .from("ThankYou@thanker.co")//("thanker.invites@gmail.com")
                .subject(title)
                .body(body)
                .mail();
    }

    public <T> void setList(List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(json);
    }

    public void set(String value) {
        mEditor.putString(CONTACTS_INVITED + mAuth.getCurrentUser().getUid(), value);
        mEditor.commit();
    }

    public void getList() {
        String serializedObject = mSharedPreferences.getString(CONTACTS_INVITED + mAuth.getCurrentUser().getUid(), null);

        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Contact>>() {
            }.getType();
            mListAlreadyInvited = gson.fromJson(serializedObject, type);
        } else {
            mListAlreadyInvited = new ArrayList<>();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CONTACTS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermission = true;

            } else {
                //Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("started-importing", mStartedImporting);
        outState.putSerializable("list-invited", (Serializable) mListContacts);
        outState.putBoolean("all-selected", mAllSelected);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setSubtitle(null);

        if (getActivity() != null) {
            Utils.changeBarTitle(getActivity(), actionBar, getActivity().getString(R.string.contacts));
        }

        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!mStartedImporting)
        {
            getList();
        }
    }


}
