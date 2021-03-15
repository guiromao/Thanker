package co.thanker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import co.thanker.R;
import co.thanker.data.ButtonState;
import co.thanker.data.DateShow;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.fragments.MyProfileFragment;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.TextUtils;

public class ThanksListAdapter extends ArrayAdapter<Thanks> {

    private final String TAG = "ThanksListAdapter";
    private final String THANKS_DB = "thanks-db";
    private static final String DB_REFERENCE = "users";
    private static final String USERS_DATABASE = "users";
    private final String USER_SNIPPET = "user-snippet";
    private final String DYNAMIC_GIVER = "dynamic-giver";
    private final String DYNAMIC_RECEIVER = "dynamic-receiver";
    private final String TYPE_ADAPTER_NORMAL = "type-adapter-normal";
    private final String TYPE_ADAPTER_FEED = "type-adapter-feed";
    private final String THANKS_GIVEN = "thanks-given";
    private final String THANKS_RECEIVED = "thanks-received";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Context mContext;
    private List<Thanks> mListThanks;
    private List<String> mListIds;
    private User mUser;
    private String mThankerDynamic;
    private String mTypeItemLayout;
    private String mUserName;
    private String mCountry;
    private boolean mIsPremium;
    private List<Calendar> mListCalendars;
    private List<ButtonState> mButtonStates;
    private List<Boolean> mListPublic;
    private List<DateShow> mListDates;
    private Calendar mLastCalendar;
    private CardView mCardGiver;
    private CardView mCardReceiver;
    private int mControl;

    public ThanksListAdapter(@NonNull Context context, int resource, List<Thanks> list, List<String> thanksIds, User user, String country, String dynamic, String type, boolean hasWelcomes) {
        super(context, resource, list);

        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mListThanks = list;
        mListIds = thanksIds;
        mUser = user;
        mCountry = country;
        mThankerDynamic = dynamic;
        mTypeItemLayout = type;
        mIsPremium = hasWelcomes;
        mUserName = DataUtils.capitalize(user.getName());
        mFirestore = FirebaseFirestore.getInstance();
        mListCalendars = new ArrayList<>();
        mListDates = tagDates();
        mListPublic = new ArrayList<>();
        mButtonStates = new ArrayList<>();
        mLastCalendar = Calendar.getInstance();
        mLastCalendar.set(Calendar.YEAR, 1970);

        if (user.getIsConceptual()) {
            mUserName = DataUtils.translateAndFormat(mContext, mUserName);
        }

        Log.v(TAG, "Checking Country in Thanks List: " + mCountry);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            if (mTypeItemLayout.equals(TYPE_ADAPTER_NORMAL)) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_thanks, parent, false);
            }
        }

        final Thanks currentThanks = getItem(position);

        final ImageView thanksImage = (ImageView) listItemView.findViewById(R.id.image_thanks_type);
        CardView cardLayout = (CardView) listItemView.findViewById(R.id.cardview_thanks_table);
        mCardGiver = (CardView) listItemView.findViewById(R.id.cardview_picture);
        mCardReceiver = (CardView) listItemView.findViewById(R.id.cardview_thanks_item_receiver);
        LinearLayout linearThanks = (LinearLayout) listItemView.findViewById(R.id.linear_thanks);

        Drawable thanksTypeDraw = ImageUtils.getThanksDraw(mContext, currentThanks.getThanksType());
        thanksImage.setImageDrawable(thanksTypeDraw);

        //linearThanks.setBackgroundColor(ImageUtils.getBackgroundThanksColor(mContext, currentThanks.getThanksType()));

        cardLayout.setBackground(thanksTypeDrawable(currentThanks.getThanksType()));

        if (mThankerDynamic.equals(DYNAMIC_GIVER)) {
            fillGiverAdapter(listItemView, currentThanks, position);
        } else {
            fillReceiverAdapter(listItemView, currentThanks, position);
        }

        /*if((position + 1) == mListThanks.size()){
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) listItemView.getLayoutParams();
            params.bottomMargin = 100;
        }*/

        return listItemView;
    }

    public void fillGiverAdapter(final View view, final Thanks thanks, final long pos) {
        final DocumentReference userRef = mFirestore.collection(USER_SNIPPET).document(thanks.getToUserId());
        mControl = 0;

        mCardGiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    Log.v(TAG, "Checking Country, entered OnClickListener");
                    visit(mUser.getUserId());
                }
            }
        });

        if(thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
            mCardGiver.setVisibility(View.GONE);
        }

        mCardReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    Log.v(TAG, "Checking Country, entered OnClickListener");
                    visit(thanks.getToUserId());
                }
            }
        });

        TextView dateText = (TextView) view.findViewById(R.id.thanks_item_date);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date(thanks.getDate()));

        mListPublic.add(thanks.getShowThanksDescription());

        currentCalendar.setTime(new Date(thanks.getDate()));
        Date day = new Date(thanks.getDate());
        DateShow currentDateShow;
        int visibility;

        sortDates();

        if(pos == 0){
            currentDateShow = new DateShow(day, true);
            mListDates.add(currentDateShow);
            visibility = View.VISIBLE;
        }

        else {
            Calendar one = Calendar.getInstance();
            one.setTime(new Date(mListThanks.get((int)pos - 1).getDate()));
            if(isItSameCalendar(one, currentCalendar)){
                visibility = View.GONE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int)pos - 1).getDate()) + " are considered the same");
            }
            else {
                visibility = View.VISIBLE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int)pos - 1).getDate()) + " are NOT considered the same");
            }
        }

        dateText.setVisibility(visibility);
        String dateString = DataUtils.getDateString(mContext, mListThanks.get((int)pos).getDate());
        dateText.setText(dateString);
        Log.v(TAG, "New Date formula. Written date: " + day + ", in position: " + pos + ", dateString: " + dateString);

        userRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserSnippet user = documentSnapshot.toObject(UserSnippet.class);

                            final String userNameReceiver = DataUtils.capitalize(user.getName());
                            TextView thanksTitleText = (TextView) view.findViewById(R.id.text_thanks_title);
                            TextView thanksDescriptionText = (TextView) view.findViewById(R.id.text_thanks_description);
                            //Button welcomeButton = (Button) view.findViewById(R.id.button_welcome);
                            final CardView cardThanks = view.findViewById(R.id.cardview_thanks_table);
                            LinearLayout linearSwitch = view.findViewById(R.id.linear_switch);
                            Switch descriptionSwitch = (Switch) view.findViewById(R.id.switch_show_description);
                            ImageView toggle = (ImageView) view.findViewById(R.id.image_toggle);
                            TextView descriptionText = view.findViewById(R.id.text_show_description);
                            Button saveSwitch = view.findViewById(R.id.button_switch);
                            String endPhrase = "";
                            boolean state = true;
                            boolean isPublic = thanks.getShowThanksDescription();

                            String description = thanks.getDescription();

                            mLastCalendar = currentCalendar;

                            String name = DataUtils.capitalize(userNameReceiver);
                            Log.v(TAG, "Formating Thanks List Adapter. Name of Receiver: " + name);

                            String thanksTitleString;

                            if(!thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                              thanksTitleString  = (mUserName + " " + retrieveThanksType(thanks)).trim();
                            }

                            else {
                                thanksTitleString = (chooseTypeThanksGiver(thanks.getThanksType())).trim();
                            }

                            thanksTitleString += " " + name.trim();

                            if (description != null && !description.trim().equals("")) {
                                if(thanks.getShowThanksDescription() || thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid()) || thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid()))
                                description = "\"" + TextUtils.firstLetterCapital(description) + "\"";
                                thanksDescriptionText.setVisibility(View.VISIBLE);
                                endPhrase = ":";
                                //endPhrase = ", " + mContext.getString(R.string.for_reason);
                            } else {
                                thanksDescriptionText.setVisibility(View.GONE);
                                endPhrase = ".";
                            }

                            if (description.length() > 0) {
                                if (!DataUtils.isPunctuation(description.charAt(description.length() - 2))) {
                                    description += ".";
                                }
                            }

                            thanksTitleText.setText(Html.fromHtml(thanksTitleString + endPhrase));

                            if (thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                                linearSwitch.setVisibility(View.VISIBLE);
                                thanksDescriptionText.setText(description);
                                if(thanks.getShowThanksDescription()){
                                    toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleon));
                                    Drawable toggleDraw = toggle.getDrawable();
                                    toggleDraw.setColorFilter(mContext.getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                                }
                                else {
                                    toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleoff));
                                    Drawable toggleDraw = toggle.getDrawable();
                                    toggleDraw.setColorFilter(mContext.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                                }
                            } else if (thanks.getShowThanksDescription() || thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                                thanksDescriptionText.setText(description);
                                linearSwitch.setVisibility(View.GONE);
                            }

                            else {
                                thanksDescriptionText.setVisibility(View.GONE);
                                linearSwitch.setVisibility(View.GONE);
                            }

                            descriptionSwitch.setThumbTintList(ImageUtils.getThanksColor(mContext, thanks));
                            //saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, thanks.getThanksType()));

                            saveSwitch.setVisibility(View.GONE);

                            toggle.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if(mListPublic.get((int)pos) == thanks.getShowThanksDescription()){
                                        saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, thanks.getThanksType()));
                                        saveSwitch.setText(mContext.getString(R.string.save));
                                        saveSwitch.setEnabled(true);
                                        saveSwitch.setVisibility(View.VISIBLE);
                                    }

                                    else {
                                        saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, "shazaam"));
                                        saveSwitch.setText(mContext.getString(R.string.save));
                                        saveSwitch.setEnabled(false);
                                        saveSwitch.setVisibility(View.GONE);
                                    }
                                    Log.v(TAG, "Switch in Thanks. It's checked: " + descriptionSwitch.isChecked());

                                    if (!mListPublic.get((int)pos)) {
                                        thanksDescriptionText.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                                        descriptionText.setText(mContext.getString(R.string.description_public));
                                        toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleon));
                                        Drawable toggleDraw = toggle.getDrawable();
                                        toggleDraw.setColorFilter(mContext.getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                                        mListPublic.set((int)pos, true);
                                    } else {
                                        thanksDescriptionText.setTextColor(mContext.getResources().getColor(R.color.grey));
                                        descriptionText.setText(mContext.getString(R.string.description_private, userNameReceiver));
                                        toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleoff));
                                        Drawable toggleDraw = toggle.getDrawable();
                                        toggleDraw.setColorFilter(mContext.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                                        mListPublic.set((int)pos, false);
                                    }

                                    ButtonState state = new ButtonState(pos, true);
                                    update(state);

                                    return false;
                                }
                            });

                            descriptionSwitch.setChecked(thanks.getShowThanksDescription());

                            if(thanks.getShowThanksDescription() || thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                thanksDescriptionText.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                            }
                            else {
                                thanksDescriptionText.setTextColor(mContext.getResources().getColor(R.color.grey));
                            }

                            if(thanks.getShowThanksDescription() && thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                descriptionText.setText(mContext.getString(R.string.description_public));
                            }
                            else if(thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                descriptionText.setText(mContext.getString(R.string.description_private, userNameReceiver));
                            }

                            if(description.equals("")){
                                linearSwitch.setVisibility(View.GONE);
                            }

                            saveSwitch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, "shazaam"));
                                    saveSwitch.setEnabled(false);
                                    saveSwitch.setText(mContext.getString(R.string.saved));
                                    mListThanks.get((int)pos).setShowThanksDescription(mListPublic.get((int)pos));
                                    mFirestore.collection(THANKS_DB).document(mListIds.get((int) pos)).update("showThanksDescription", mListPublic.get((int)pos));
                                    ButtonState state = new ButtonState(pos, false);
                                    update(state);
                                }
                            });

                            //setButtonAccordingly(saveSwitch, pos, thanks);

                            Log.v(TAG, "Renewing Thanks List Adapter. Found imageUrl on To User");
                            String receiverImageUrl = user.getImageUrl();
                            ImageView giverImage = (ImageView) view.findViewById(R.id.thanks_giver_profile_picture);
                            final ImageView receiverImage = (ImageView) view.findViewById(R.id.thanks_receiver_profile_picture);
                            CardView cardViewGiver = (CardView) view.findViewById(R.id.cardview_picture);
                            //CardView cardViewWelcome = (CardView) view.findViewById(R.id.cardview_welcome);
                            ImageView welcomeWink = (ImageView) view.findViewById(R.id.welcome_wink_confirmed);
                            ImageView welcomeImage = (ImageView) view.findViewById(R.id.welcome_wink);
                            final TextView textWelcome = (TextView) view.findViewById(R.id.text_welcome);
                            LinearLayout linearWelcome = (LinearLayout) view.findViewById(R.id.linear_welcome);

                            welcomeImage.setVisibility(View.GONE);

                            ImageUtils.loadImageInto(mContext, mUser.getImageUrl(), giverImage);

                            RequestOptions options = new RequestOptions();
                            Glide.with(mContext)
                                    .load(receiverImageUrl)
                                    .apply(options.circleCropTransform())
                                    .into(receiverImage);


                            if (thanks.getWasWelcomed()) {
                                int color = Color.parseColor(pickColorWink(thanks)); //The color u want
                                welcomeWink.setColorFilter(color);
                                welcomeWink.setVisibility(View.VISIBLE);
                                cardThanks.setBackground(getBackgroundWithWelcome(thanks));
                                linearWelcome.setBackground(setWelcomeBackground(thanks));
                                linearWelcome.setVisibility(View.VISIBLE);

                                textWelcome.setText(Html.fromHtml("<b>" + mContext.getString(R.string.user_says_welcome, userNameReceiver) + "</b>"));

                            } else {
                                welcomeWink.setVisibility(View.GONE);
                                cardThanks.setBackground(getNormalBackground(thanks));
                                linearWelcome.setVisibility(View.GONE);
                            }

                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Current Thanks Receiver\'s UserSnippet");
                    }
                });
    }

    public void sortDates(){
        Collections.sort(mListDates, new Comparator<DateShow>() {
            @Override
            public int compare(DateShow o1, DateShow o2) {
                return Long.compare(o2.getDate().getTime(), o1.getDate().getTime());
            }
        });
    }

    public List<DateShow> tagDates(){
        List<DateShow> result = new ArrayList<>();

        //sortDates();

        if(mListThanks.size() > 0){
            result.add(new DateShow(new Date(mListThanks.get(0).getDate()), true));
        }

        if(mListThanks.size() > 1){
            for(int i = 1; i != mListThanks.size(); i++){
                Thanks thanks = mListThanks.get(i);
                Date date = new Date(thanks.getDate());
                boolean visible = false;
                Calendar one = Calendar.getInstance();
                Calendar two = Calendar.getInstance();
                one.setTime(new Date(mListThanks.get(i - 1).getDate()));
                two.setTime(date);
                if(!isItSameCalendar(one, two)){
                    visible = true;
                }
                DateShow dateShow = new DateShow(date, visible);
                result.add(dateShow);
            }
        }

        return result;
    }

    public boolean existsState(ButtonState state){
        for(ButtonState button: mButtonStates){
            if(button.getPosition() == state.getPosition()){
                return true;
            }
        }
        return false;
    }

    public boolean existsInListCalendar(Date date){
        DateShow dateShow = new DateShow(date, false);
        for(DateShow item: mListDates){
            if(item.getDate().getTime() == dateShow.getDate().getTime()){
                return true;
            }
        }
        return false;
    }

    public DateShow retrieveDateShow(Date date){
        for(DateShow item: mListDates){
            if(item.getDate().getTime() == date.getTime()){
                return item;
            }
        }

        DateShow firstdate = new DateShow(date, true);
        mListDates.add(firstdate);

        return firstdate;
    }

    public void update(ButtonState state){
        if(existsState(state)){
            for(int i = 0; i != mButtonStates.size(); i++){
                if(mButtonStates.get(i).getPosition() == state.getPosition()){
                    mButtonStates.get(i).setState(state.getState());
                }
            }
        }
        else {
            mButtonStates.add(state);
        }
    }

    public void setButtonAccordingly(Button button, long pos, Thanks thanks){
        if(existsButtonState((int)pos)){
            button.setEnabled(mButtonStates.get((int)pos).getState());
            button.setBackground(ImageUtils.getThanksButtonBackground(mContext, "shazaam"));
            button.setText(mContext.getString(R.string.saved));
        }
        else {
            button.setEnabled(true);
            button.setBackground(ImageUtils.getThanksButtonBackground(mContext, thanks.getThanksType()));
            button.setText(mContext.getString(R.string.save));
        }
    }

    public boolean existsButtonState(int pos){
        for(ButtonState button: mButtonStates){
            if(button.getPosition() == pos){
                return true;
            }
        }
        return false;
    }

    public void fillReceiverAdapter(final View view, final Thanks thanks, final long pos) {
        DocumentReference userRef = mFirestore.collection(USER_SNIPPET).document(thanks.getFromUserId());

        mCardGiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    Log.v(TAG, "Checking Country, entered OnClickListener");
                    visit(thanks.getFromUserId());
                }
            }
        });

        mCardReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    Log.v(TAG, "Checking Country, entered OnClickListener");
                    visit(mUser.getUserId());
                }
            }
        });

        if(thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
            mCardReceiver.setVisibility(View.GONE);
        }

        mListPublic.add(thanks.getShowThanksDescription());

        TextView dateText = (TextView) view.findViewById(R.id.thanks_item_date);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date(thanks.getDate()));
        Date day = new Date(thanks.getDate());
        DateShow currentDateShow;
        int visibility;

        if(pos == 0){
            currentDateShow = new DateShow(day, true);
            mListDates.add(currentDateShow);
            visibility = View.VISIBLE;
        }

        else {
            Calendar one = Calendar.getInstance();
            one.setTime(new Date(mListThanks.get((int)pos - 1).getDate()));
            if(isItSameCalendar(one, currentCalendar)){
                visibility = View.GONE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int)pos - 1).getDate()) + " are considered the same");
            }
            else {
                visibility = View.VISIBLE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int)pos - 1).getDate()) + " are NOT considered the same");
            }
        }

        dateText.setVisibility(visibility);
        String dateString = DataUtils.getDateString(mContext, mListThanks.get((int)pos).getDate());
        dateText.setText(dateString);
        Log.v(TAG, "New Date formula. Written date: " + day + ", in position: " + pos + ", dateString: " + dateString);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            final UserSnippet user = documentSnapshot.toObject(UserSnippet.class);
                            final String userNameGiver = DataUtils.capitalize(user.getName());
                            TextView thanksTitleText = (TextView) view.findViewById(R.id.text_thanks_title);
                            TextView thanksDescriptionText = (TextView) view.findViewById(R.id.text_thanks_description);
                            LinearLayout linearSwitch = view.findViewById(R.id.linear_switch);
                            ImageView toggle = view.findViewById(R.id.image_toggle);
                            Switch descriptionSwitch = (Switch) view.findViewById(R.id.switch_show_description);
                            TextView descriptionText = view.findViewById(R.id.text_show_description);
                            Button saveSwitch = view.findViewById(R.id.button_switch);
                            String endPhrase = "";

                            String thanksTitleString = "";
                            String thanksDescription = thanks.getDescription();

                            if (userNameGiver != null) {
                                String name = DataUtils.capitalize(userNameGiver);
                                Log.v(TAG, "Formating Thanks List Adapter. Name of Giver: " + name);
                                thanksTitleString = name + " ";
                                if(!thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                    thanksTitleString += retrieveThanksType(thanks) + " "
                                            + mUserName;
                                }
                                else {
                                    thanksTitleString += chooseTypeThanksReceiver(thanks.getThanksType());
                                }
                            }

                            if (thanksDescription != null) {
                                if (!thanksDescription.trim().equals("")) {
                                    if(thanks.getShowThanksDescription() ||  thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid()) || thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                        //endPhrase = ", " + mContext.getString(R.string.for_reason);
                                        endPhrase = ":";
                                        thanksDescription = "\"" + TextUtils.firstLetterCapital(thanksDescription) + "\"";
                                        thanksDescriptionText.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        thanksDescriptionText.setVisibility(View.GONE);
                                        endPhrase = ".";
                                    }

                                } else {
                                    thanksDescriptionText.setVisibility(View.GONE);
                                    endPhrase = ".";
                                }
                            } else {
                                endPhrase = ".";
                                thanksDescriptionText.setVisibility(View.GONE);
                            }

                            if (thanksDescription.length() > 0) {
                                if (!DataUtils.isPunctuation(thanksDescription.charAt(thanksDescription.length() - 2))) {
                                    thanksDescription += ".";
                                }
                            }

                            thanksTitleText.setText(Html.fromHtml(thanksTitleString + endPhrase));

                            if(!thanks.getShowThanksDescription() && !thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                thanksDescriptionText.setVisibility(View.GONE);
                            }

                            else if(thanksDescription.length() > 0){
                                thanksDescriptionText.setVisibility(View.VISIBLE);
                            }

                            if ((thanks.getShowThanksDescription() && thanksDescription != null) || thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())
                                || thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                                if(!thanksDescription.equals("")){
                                    thanksDescriptionText.setText(thanksDescription);
                                    thanksDescriptionText.setVisibility(View.VISIBLE);
                                    linearSwitch.setVisibility(View.GONE);
                                }
                            }

                            if(thanks.getToUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid()) || thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                thanksDescriptionText.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                                thanksDescriptionText.setVisibility(View.VISIBLE);
                                if(thanksDescription != null){
                                    if(thanksDescription.length() == 0){
                                        thanksDescriptionText.setVisibility(View.GONE);
                                    }
                                }
                                if(!thanks.getFromUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid()) || thanksDescription.equals(""))
                                {
                                    linearSwitch.setVisibility(View.GONE);
                                }
                                else {
                                    linearSwitch.setVisibility(View.VISIBLE);
                                    descriptionText.setVisibility(View.VISIBLE);
                                    if(thanks.getShowThanksDescription()){
                                        descriptionText.setText(mContext.getString(R.string.description_public));
                                        toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleon));
                                        Drawable toggleDraw = toggle.getDrawable();
                                        toggleDraw.setColorFilter(mContext.getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                                    }
                                    else {
                                        descriptionText.setText(mContext.getString(R.string.description_private, DataUtils.capitalize(mUser.getName())));
                                        toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleoff));
                                        Drawable toggleDraw = toggle.getDrawable();
                                        toggleDraw.setColorFilter(mContext.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                                    }
                                }
                            }

                            //saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, thanks.getThanksType()));

                            Log.v(TAG, "Renewing Thanks List Adapter. Found Image on From User");
                            String imageUrl = user.getImageUrl();
                            ImageView giverImage = (ImageView) view.findViewById(R.id.thanks_giver_profile_picture);
                            ImageView receiverImage = (ImageView) view.findViewById(R.id.thanks_receiver_profile_picture);
                            CardView cardViewReceiver = (CardView) view.findViewById(R.id.cardview_thanks_item_receiver);
                            final ImageView welcomeImage = (ImageView) view.findViewById(R.id.welcome_wink);
                            final TextView textWelcome = (TextView) view.findViewById(R.id.text_welcome);
                            final ImageView wink = (ImageView) view.findViewById(R.id.welcome_wink_confirmed);
                            final LinearLayout welcomeLinear = (LinearLayout) view.findViewById(R.id.linear_welcome);
                            final CardView cardBackground = view.findViewById(R.id.cardview_thanks_table);

                            ImageUtils.loadImageInto(mContext, imageUrl, giverImage);

                            ImageUtils.loadImageInto(mContext, mUser.getImageUrl(), receiverImage);

                            saveSwitch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, "shazaam"));
                                    saveSwitch.setEnabled(false);
                                    saveSwitch.setText(mContext.getString(R.string.saved));
                                    mListThanks.get((int)pos).setShowThanksDescription(mListPublic.get((int)pos));
                                    mFirestore.collection(THANKS_DB).document(mListIds.get((int) pos)).update("showThanksDescription", mListPublic.get((int)pos));
                                    ButtonState state = new ButtonState(pos, false);
                                    update(state);
                                }
                            });

                            descriptionSwitch.setThumbTintList(ImageUtils.getThanksColor(mContext, thanks));

                            saveSwitch.setVisibility(View.GONE);

                            toggle.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if(mListPublic.get((int)pos) == thanks.getShowThanksDescription()){
                                        saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, thanks.getThanksType()));
                                        saveSwitch.setText(mContext.getString(R.string.save));
                                        saveSwitch.setEnabled(true);
                                        saveSwitch.setVisibility(View.VISIBLE);
                                    }

                                    else {
                                        saveSwitch.setBackground(ImageUtils.getThanksButtonBackground(mContext, "shazaam"));
                                        saveSwitch.setText(mContext.getString(R.string.save));
                                        saveSwitch.setEnabled(false);
                                        saveSwitch.setVisibility(View.GONE);
                                    }
                                    Log.v(TAG, "Switch in Thanks. It's checked: " + descriptionSwitch.isChecked());

                                    if (!mListPublic.get((int)pos)) {
                                        thanksDescriptionText.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                                        descriptionText.setText(mContext.getString(R.string.description_public));
                                        toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleon));
                                        Drawable toggleDraw = toggle.getDrawable();
                                        toggleDraw.setColorFilter(mContext.getColor(R.color.defaultTextColor2), PorterDuff.Mode.SRC_ATOP);
                                        mListPublic.set((int)pos, true);
                                    } else {
                                        thanksDescriptionText.setTextColor(mContext.getResources().getColor(R.color.grey));
                                        descriptionText.setText(mContext.getString(R.string.description_private, DataUtils.capitalize(mUser.getName())));
                                        toggle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.toggleoff));
                                        Drawable toggleDraw = toggle.getDrawable();
                                        toggleDraw.setColorFilter(mContext.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
                                        mListPublic.set((int)pos, false);
                                    }

                                    ButtonState state = new ButtonState(pos, true);
                                    update(state);

                                    return false;
                                }
                            });

                            descriptionSwitch.setChecked(thanks.getShowThanksDescription());

                            boolean wasWelcomed = thanks.getWasWelcomed();

                            if (wasWelcomed) {
                                //cardViewWelcome.setVisibility(View.VISIBLE);

                                textWelcome.setTextColor(mContext.getResources().getColor(R.color.white));
                                textWelcome.setText(mContext.getString(R.string.you_re_welcome));
                                textWelcome.setTypeface(textWelcome.getTypeface(), Typeface.BOLD);

                                int color = Color.parseColor(pickColorWink(thanks)); //The color u want
                                wink.setColorFilter(color);
                                wink.setVisibility(View.VISIBLE);
                                welcomeImage.setVisibility(View.GONE);
                                welcomeLinear.setVisibility(View.VISIBLE);
                                cardBackground.setBackground(getBackgroundWithWelcome(thanks));
                                welcomeLinear.setBackground(setWelcomeBackground(thanks));


                                textWelcome.setText(Html.fromHtml("<b>" + mContext.getString(R.string.user_says_welcome, mUserName) + "</b>"));


                            } else if (mIsPremium) {

                                textWelcome.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                                welcomeLinear.setBackground(mContext.getResources().getDrawable(R.drawable.button_rounded_to_welcomed));
                                welcomeLinear.setVisibility(View.VISIBLE);
                                cardBackground.setBackground(getBackgroundWithWelcome(thanks));
                                int color = Color.parseColor("#808080"); //The color u want
                                welcomeImage.setColorFilter(color);
                                welcomeImage.setVisibility(View.VISIBLE);
                                wink.setVisibility(View.GONE);
                                textWelcome.setText(mContext.getString(R.string.give_welcomes));
                                textWelcome.setTypeface(null, Typeface.NORMAL);
                                setupButton(welcomeLinear, thanks, thanks.getFromUserId(), mUser.getUserId(), welcomeImage, cardBackground, wink, textWelcome, mUserName, pos);


                            } else {

                                cardBackground.setBackground(getNormalBackground(thanks));
                                welcomeLinear.setVisibility(View.GONE);
                                wink.setVisibility(View.GONE);
                                welcomeImage.setVisibility(View.GONE);
                                //textWelcome.setText(mContext.getString(R.string.give_welcomes));
                                textWelcome.setTypeface(null, Typeface.NORMAL);

                            }

                            if (!thanks.getWasWelcomed()) {
                                //welcomeImage.setVisibility(View.GONE);
                            }
                        }

                        Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Current Thanks\' Sender\'s UserSnippet");
                    }

                });


    }

    public boolean doesDayExistInList(List<DateShow> list, Date day) {
        for (DateShow item : list) {
            if (item.getDate().getTime() == day.getTime()) {
                return true;
            }
        }
        return false;
    }

    public int getVisibility(int pos){
        return (mListDates.get(pos).getShow() ? View.VISIBLE : View.GONE);
    }

    public int getVisibility(Date date) {
        for (DateShow item : mListDates) {
            if (item.getDate().getTime() == date.getTime()) {
                if (item.getShow()) {
                    return View.VISIBLE;
                }
                else {
                    return View.GONE;
                }
            }
        }
        return View.GONE;
    }

    public int getVisibility(DateShow date){
        for(DateShow item: mListDates){
            if(item.getDate().getTime() == date.getDate().getTime()){
                if(date.getShow()){
                    return View.VISIBLE;
                }
                else {
                    return View.GONE;
                }
            }
        }
        return View.GONE;
    }

    public boolean isItSameCalendar(Calendar one, Calendar two) {
        return (one.get(Calendar.DAY_OF_MONTH) == two.get(Calendar.DAY_OF_MONTH)
                && one.get(Calendar.MONTH) == two.get(Calendar.MONTH)
                && one.get(Calendar.YEAR) == two.get(Calendar.YEAR));
    }

    public boolean existsInCalendar(Calendar two){
        for(DateShow item: mListDates){
            Calendar one = Calendar.getInstance();
            Date date = new Date(item.getDate().getTime());
            one.setTime(date);

            if(one.get(Calendar.DAY_OF_MONTH) == two.get(Calendar.DAY_OF_MONTH)
                    && one.get(Calendar.MONTH) == two.get(Calendar.MONTH)
                    && one.get(Calendar.YEAR) == two.get(Calendar.YEAR)){
                return true;
            }
        }
        return false;
    }

    private Drawable thanksTypeDrawable(String thanksType) {

        Drawable result = mContext.getResources().getDrawable(R.drawable.cardview_thanks_shape);

        switch (thanksType) {
            case "SUPER":
                result = mContext.getResources().getDrawable(R.drawable.cardview_super_thanks_shape);
                break;
            case "MEGA":
                result = mContext.getResources().getDrawable(R.drawable.cardview_mega_thanks_shape);
                break;
            case "POWER":
                result = mContext.getResources().getDrawable(R.drawable.cardview_power_thanks_shape);
                break;
            case "ULTRA":
                result = mContext.getResources().getDrawable(R.drawable.cardview_ultra_thanks_shape);
                break;
            default:
                break;
        }

        return result;
    }

    public void addAll(List<Thanks> listThanks) {
        for (Thanks thanks : listThanks) {
            mListThanks.add(thanks);
        }
    }

    public String chooseTypeThanksGiver(String type){
        String result = "";

        switch(type.toLowerCase()){
            case "normal":
                result = mContext.getString(R.string.thanked_adapter);
                break;

            case "super":
                result = mContext.getString(R.string.super_thanked_adapter);
                break;

            case "mega":
                result = mContext.getString(R.string.mega_thanked_adapter);
                break;

            case "power":
                result = mContext.getString(R.string.power_thanked_adapter);
                break;

            case "ultra":
                result = mContext.getString(R.string.ultra_thanked_adapter);
                break;
        }

        return result;
    }

    public String chooseTypeThanksReceiver(String type){
        String result = "";

        switch(type.toLowerCase()){
            case "normal":
                result = mContext.getString(R.string.thanked_adapter_received);
                break;

            case "super":
                result = mContext.getString(R.string.super_thanked_adapter_received);
                break;

            case "mega":
                result = mContext.getString(R.string.mega_thanked_adapter_received);
                break;

            case "power":
                result = mContext.getString(R.string.power_thanked_adapter_received);
                break;

            case "ultra":
                result = mContext.getString(R.string.ultra_thanked_adapter_received);
                break;
        }

        return result;
    }

    public Drawable setWelcomeBackground(Thanks thanks) {
        Drawable result = null;

        switch (thanks.getThanksType().toLowerCase()) {
            case "normal":
                result = mContext.getResources().getDrawable(R.drawable.button_rounded_welcome_thanks);
                break;

            case "super":
                result = mContext.getResources().getDrawable(R.drawable.button_rounded_welcome_super);
                break;

            case "mega":
                result = mContext.getResources().getDrawable(R.drawable.button_rounded_welcome_mega);
                break;

            case "power":
                result = mContext.getResources().getDrawable(R.drawable.button_rounded_welcome_power);
                break;

            case "ultra":
                result = mContext.getResources().getDrawable(R.drawable.button_rounded_welcome_ultra);
                break;
        }

        return result;
    }

    public Drawable getBackgroundWithWelcome(Thanks thanks) {
        Drawable result = null;

        switch (thanks.getThanksType().toLowerCase()) {
            case "normal":
                result = mContext.getResources().getDrawable(R.drawable.cardview_thanks_shape_with_welcome);
                break;

            case "super":
                result = mContext.getResources().getDrawable(R.drawable.cardview_super_thanks_shape_with_welcome);
                break;

            case "mega":
                result = mContext.getResources().getDrawable(R.drawable.cardview_mega_thanks_shape_with_welcome);
                break;

            case "power":
                result = mContext.getResources().getDrawable(R.drawable.cardview_power_thanks_shape_with_welcome);
                break;

            case "ultra":
                result = mContext.getResources().getDrawable(R.drawable.cardview_ultra_thanks_shape_with_welcome);
                break;
        }

        return result;
    }

    public Drawable getNormalBackground(Thanks thanks) {
        Drawable result = null;

        switch (thanks.getThanksType().toLowerCase()) {
            case "normal":
                result = mContext.getResources().getDrawable(R.drawable.cardview_thanks_shape);
                break;

            case "super":
                result = mContext.getResources().getDrawable(R.drawable.cardview_super_thanks_shape);
                break;

            case "mega":
                result = mContext.getResources().getDrawable(R.drawable.cardview_mega_thanks_shape);
                break;

            case "power":
                result = mContext.getResources().getDrawable(R.drawable.cardview_power_thanks_shape);
                break;

            case "ultra":
                result = mContext.getResources().getDrawable(R.drawable.cardview_ultra_thanks_shape);
                break;
        }

        return result;
    }

    private void setupButton(final LinearLayout buttonLinearLayout, final Thanks thanks, final String giverId, final String receiverId, final ImageView imageWelcome, final CardView cardBackground,
                             final ImageView winkConfirmed, final TextView textWelcome, final String name, long pos) {

        final String thanksId = giverId + "_" + receiverId + "_" + thanks.getYear() + "_" + thanks.getMonth() + "_" + thanks.getDay();

        Log.v(TAG, "Updating welcomes. Setting button_rounded_green for: " + thanksId);
        Log.v(TAG, "Updating welcomes. Giver ID:" + giverId);

        if (thanks.getThanksId() != null) {
            if (!thanks.getThanksId().equals("")) {

                buttonLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(TAG, "Checking welcomes. Entered onclicklistener");

                        getItem((int)pos).setWasWelcomed(true);
                        buttonLinearLayout.setBackground(setWelcomeBackground(thanks));
                        //imageWelcome.setVisibility(View.VISIBLE);
                        imageWelcome.setVisibility(View.GONE);
                        //cardBackground.setBackground(getNormalBackground(thanks));

                        int color = Color.parseColor(pickColorWink(thanks)); //The color u want
                        winkConfirmed.setColorFilter(color);
                        winkConfirmed.setVisibility(View.VISIBLE);

                        textWelcome.setTextColor(mContext.getResources().getColor(R.color.white));
                        textWelcome.setTypeface(textWelcome.getTypeface(), Typeface.BOLD);
                        textWelcome.setText(Html.fromHtml("<b>" + mContext.getString(R.string.user_says_welcome, name) + "</b>"));

                        mFirestore.collection(THANKS_DB).document(mListIds.get((int)pos)).update("wasWelcomed", true);

                        String title = mContext.getString(R.string.you_were_welcomed,
                                DataUtils.capitalize(mUser.getName()));
                        String text = mContext.getString(R.string.thanks_welcomed_text,
                                DataUtils.capitalize(mUser.getName()));
                        DataUtils.createMessage(giverId, title, text, mUser.getUserId(), DataUtils.MSG_SEE_PREMIUM);
                    }

                });
            }
        }
    }

    public void visit(String otherId) {
        Fragment profileFrag;

        if (!mAuth.getCurrentUser().getUid().equalsIgnoreCase(otherId)) {
            profileFrag = new OtherProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString(OUR_USER_ID, mAuth.getCurrentUser().getUid());
            bundle.putString(OUR_USER_COUNTRY, mCountry);
            bundle.putString(USER_ID_STRING, otherId);
            profileFrag.setArguments(bundle);
        } else {
            profileFrag = new MyProfileFragment();
        }

        if (mContext != null) {
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFrag).addToBackStack(null).commit();
        }
    }

    private String retrieveThanksType(Thanks thanks) {
        String type = "";
        if (thanks.getThanksType() != null) {
            if (!thanks.getThanksType().equals("")) {
                switch (thanks.getThanksType()) {
                    case "NORMAL":
                        type = mContext.getString(R.string.thanked_string);
                        break;
                    case "SUPER":
                        type = mContext.getString(R.string.super_thanked_string);
                        break;
                    case "MEGA":
                        type = mContext.getString(R.string.mega_thanked_string);
                        break;
                    case "POWER":
                        type = mContext.getString(R.string.power_thanked_string);
                        break;

                    case "ULTRA":
                        type = mContext.getString(R.string.ultra_thanked_string);
                        break;
                }
            }
        }
        return type;
    }

    private String pickColorWink(Thanks thanks) {
        String result = "";

        switch (thanks.getThanksType().toUpperCase()) {
            case "NORMAL":
                result = "#4CAF50";
                break;
            case "SUPER":
                result = "#28a9e0";
                break;
            case "MEGA":
                result = "#1d7ba3";
                break;
            case "POWER":
                result = "#165e7d";
                break;
            case "ULTRA":
                result = "#4B0082";
                break;
        }

        Log.v(TAG, "Escolhendo cor do Welcomed: " + result);
        return result;
    }

    public long getNumberItems() {
        return mListThanks.size();
    }
}
