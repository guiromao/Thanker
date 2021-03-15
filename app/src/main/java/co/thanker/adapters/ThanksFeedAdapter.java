package co.thanker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import co.thanker.R;
import co.thanker.data.DateShow;
import co.thanker.data.Thanks;
import co.thanker.data.User;
import co.thanker.data.UserSnippet;
import co.thanker.fragments.MyProfileFragment;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import co.thanker.utils.TextUtils;

public class ThanksFeedAdapter extends ArrayAdapter<Thanks> {

    private final String TAG = ThanksFeedAdapter.class.getSimpleName();
    private static final String USERS_DATABASE = "users";
    private final String USER_SNIPPET = "user-snippet";
    private final String DYNAMIC_GIVER = "dynamic-giver";
    private final String DYNAMIC_RECEIVER = "dynamic-receiver";
    private final String TYPE_ADAPTER_NORMAL = "type-adapter-normal";
    private final String TYPE_ADAPTER_FEED = "type-adapter-feed";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";

    private FirebaseFirestore mFirestore;
    private Context mContext;
    private List<Thanks> mListThanks;
    private List<Calendar> mListCalendars;
    private List<DateShow> mListDateShows;
    private Calendar mLastCalendar;
    private User mUser;
    private String mCountry;
    private CardView mCardGiver;
    private CardView mCardReceiver;

    public ThanksFeedAdapter(@NonNull Context context, int resource, List<Thanks> list, User user, String country) {
        super(context, resource, list);

        mContext = context;
        mFirestore = FirebaseFirestore.getInstance();
        mListThanks = list;
        mListCalendars = new ArrayList<>();
        mListDateShows = new ArrayList<>();
        mLastCalendar = Calendar.getInstance();
        mLastCalendar.setTime(new Date(System.currentTimeMillis() - 345600000));
        mUser = user;
        mCountry = country;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_thanks, parent, false);
        }

        final Thanks currentThanks = getItem(position);

        final CardView cardLayout = (CardView) listItemView.findViewById(R.id.cardview_thanks_table);
        final ImageView imageGiver = (ImageView) listItemView.findViewById(R.id.thanks_giver_profile_picture);
        final ImageView imageReceiver = (ImageView) listItemView.findViewById(R.id.thanks_receiver_profile_picture);
        final TextView thanksTitleText = (TextView) listItemView.findViewById(R.id.text_thanks_title);
        final TextView thanksDescriptionView = (TextView) listItemView.findViewById(R.id.text_thanks_description);
        final TextView dateText = (TextView) listItemView.findViewById(R.id.thanks_item_date);
        final ImageView thanksImage = (ImageView) listItemView.findViewById(R.id.image_thanks_type);
        final LinearLayout linearWelcome = (LinearLayout) listItemView.findViewById(R.id.linear_welcome);
        final ImageView welcomeWink = (ImageView) listItemView.findViewById(R.id.welcome_wink_confirmed);
        final TextView textWelcome = (TextView) listItemView.findViewById(R.id.text_welcome);
        //final LinearLayout linearColor = (LinearLayout) listItemView.findViewById(R.id.linear_coloured);
        mCardGiver = (CardView) listItemView.findViewById(R.id.cardview_picture);
        mCardReceiver = (CardView) listItemView.findViewById(R.id.cardview_thanks_item_receiver);

        mCardGiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    visit(currentThanks.getFromUserId());
                }
            }
        });

        mCardReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountry != null) {
                    visit(currentThanks.getToUserId());
                }
            }
        });

        cardLayout.setBackground(thanksTypeDrawable(currentThanks.getThanksType()));
        thanksImage.setImageDrawable(ImageUtils.getThanksDraw(mContext, currentThanks.getThanksType()));
        //linearColor.setBackgroundColor(ImageUtils.getBackgroundThanksColor(mContext, currentThanks.getThanksType()));
        
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(new Date(currentThanks.getDate()));
        Date day = new Date(currentThanks.getDate());
        int visibility;

        if(position == 0){
            visibility = View.VISIBLE;
        }

        else {
            Calendar one = Calendar.getInstance();
            one.setTime(new Date(mListThanks.get((int)position - 1).getDate()));
            if(isItSameCalendar(one, currentCalendar)){
                visibility = View.GONE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int)position - 1).getDate()) + " are considered the same");
            }
            else {
                visibility = View.VISIBLE;
                Log.v(TAG, "Checking date visibility. Day " + day + ", and day " + new Date(mListThanks.get((int)position - 1).getDate()) + " are NOT considered the same");
            }
        }

        dateText.setVisibility(visibility);
        String dateString = DataUtils.getDateString(mContext, mListThanks.get((int)position).getDate());
        dateText.setText(dateString);
        Log.v(TAG, "New Date formula. Written date: " + day + ", in position: " + position + ", dateString: " + dateString);

        mFirestore.collection(USER_SNIPPET).document(currentThanks.getFromUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserSnippet userSnippet = documentSnapshot.toObject(UserSnippet.class);
                            final String giverName = DataUtils.capitalize(userSnippet.getName());
                            ImageUtils.loadImageInto(mContext, userSnippet.getImageUrl(), imageGiver);

                            mFirestore.collection(USER_SNIPPET).document(currentThanks.getToUserId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                UserSnippet receiver = documentSnapshot.toObject(UserSnippet.class);
                                                final String receiverName = DataUtils.capitalize(receiver.getName());
                                                ImageUtils.loadImageInto(mContext, receiver.getImageUrl(), imageReceiver);
                                                String thanksTitle = mContext.getString(R.string.thanks_feed_string, giverName, DataUtils.retrieveThanksType(mContext, currentThanks), receiverName);
                                                String description = currentThanks.getDescription();
                                                long date = currentThanks.getDate();
                                                Calendar calendar = Calendar.getInstance();
                                                String endPhrase = "";

                                                if (description != null && !description.equals("") && currentThanks.getShowThanksDescription()) {
                                                    description = "\"" + TextUtils.firstLetterCapital(description) + "\"";
                                                    //endPhrase = ", " + mContext.getString(R.string.for_reason);
                                                    endPhrase = ":";
                                                } else {
                                                    thanksDescriptionView.setVisibility(View.GONE);
                                                    endPhrase = ".";
                                                }

                                                if (description.length() > 0) {
                                                    if (!DataUtils.isPunctuation(description.charAt(description.length() - 2))) {
                                                        description += ".";
                                                    }
                                                }

                                                if (currentThanks.getWasWelcomed()) {
                                                    cardLayout.setBackground(getBackgroundWithWelcome(currentThanks));
                                                    int color = Color.parseColor(pickColorWink(currentThanks)); //The color u want
                                                    welcomeWink.setColorFilter(color);
                                                    welcomeWink.setVisibility(View.VISIBLE);
                                                    linearWelcome.setBackground(setWelcomeBackground(currentThanks));
                                                    textWelcome.setText(mContext.getString(R.string.user_says_welcome, receiverName));
                                                    linearWelcome.setVisibility(View.VISIBLE);
                                                } else {
                                                    cardLayout.setBackground(getNormalBackground(currentThanks));
                                                    welcomeWink.setVisibility(View.GONE);
                                                    linearWelcome.setVisibility(View.GONE);
                                                }

                                                if(currentThanks.getShowThanksDescription() && description.length() > 0){
                                                    thanksDescriptionView.setVisibility(View.VISIBLE);
                                                }
                                                else {
                                                    thanksDescriptionView.setVisibility(View.GONE);
                                                }

                                                thanksTitleText.setText(Html.fromHtml(thanksTitle + endPhrase));
                                                thanksDescriptionView.setText(description);
                                                dateText.setText(dateString);
                                            }

                                            Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Current Friend\'s UserSnippet");
                                        }
                                    });
                        }
                    }
                });

        return listItemView;
    }

    public void sortDates(){
        Collections.sort(mListDateShows, new Comparator<DateShow>() {
            @Override
            public int compare(DateShow o1, DateShow o2) {
                return Long.compare(o2.getDate().getTime(), o1.getDate().getTime());
            }
        });
    }

    public boolean existsInCalendar(Calendar two){
        for(DateShow item: mListDateShows){
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

    public int getVisibility(DateShow date){
        for(DateShow item: mListDateShows){
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

    public DateShow retrieveDateShow(Date date){
        for(DateShow item: mListDateShows){
            if(item.getDate().getTime() == date.getTime()){
                return item;
            }
        }

        DateShow firstDate = new DateShow(date, true);
        mListDateShows.add(firstDate);

        return firstDate;
    }

    public boolean doesDayExistInList(List<DateShow> list, Date day) {
        for (DateShow item : list) {
            if (item.getDate().getTime() == day.getTime()) {
                return true;
            }
        }
        return false;
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

    public void visit(String otherId) {
        Fragment profileFrag;

        if (!mUser.getUserId().equalsIgnoreCase(otherId)) {
            profileFrag = new OtherProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString(OUR_USER_ID, mUser.getUserId());
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

    public int getVisibility(Date date) {
        for (DateShow item : mListDateShows) {
            if (item.getDate().getTime() == date.getTime()) {
                if (item.getShow()) {
                    return View.VISIBLE;
                } else {
                    return View.GONE;
                }
            }
        }
        return -1;
    }

    public boolean isItSameCalendar(Calendar one, Calendar two) {
        return (one.get(Calendar.DAY_OF_MONTH) == two.get(Calendar.DAY_OF_MONTH)
                && one.get(Calendar.MONTH) == two.get(Calendar.MONTH)
                && one.get(Calendar.YEAR) == two.get(Calendar.YEAR));
    }

    public boolean doesListContainCalendar(List<Calendar> list, Calendar calendar) {
        for (Calendar item : list) {
            if (item.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                    item.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    item.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {

                return true;
            }
        }
        return false;
    }

    public boolean doesListContainDate(List<Date> listDates, long date) {
        for (Date item : listDates) {
            if (item.getTime() == date) {
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
            notifyDataSetChanged();
        }
    }


    public long getNumberItems() {
        return mListThanks.size();
    }
}
