package co.thanker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.thanker.R;
import co.thanker.data.DateShow;
import co.thanker.data.Message;
import co.thanker.data.UserSnippet;
import co.thanker.fragments.MessageFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MessageAdapter extends ArrayAdapter<Message> {

    private final String TAG = "MessageAdapter";
    private static final String DB_REFERENCE = "users";
    private final String MESSAGES_REFERENCE = "messages-list";
    private final String MESSAGE_OBJECT = "message-object";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String OUR_USER_ID = "our-user-id";
    private final String USER_SNIPPET = "user-snippet";
    private final String USER_SNIPPET_VAR = "user-snippet-var";
    private final String PLATFORM_MESSAGE = "platform-message";
    private final String FROM_USER_NAME = "from-user-name";
    private final String USER_ID_STRING = "user-id-string";
    private final String INFO_HAS_PREMIUM = "info-has-premium";

    private Context mContext;
    private FirebaseFirestore mFirestore;
    private List<Message> mListMessages;
    private String mUserId;
    private String mCountry;
    private List<String> mMessagesKeys;
    private Calendar mLastCalendar;
    private List<DateShow> mListDateShows;
    private String mFromUsername;
    private UserSnippet mUserSnippet;
    private boolean mIsPremium;

    public MessageAdapter(@NonNull Context context, int resource, List<Message> listMessages, List<String> keys, String userId, String country, boolean isPremium) {
        super(context, resource, listMessages);
        mContext = context;
        mFirestore = FirebaseFirestore.getInstance();
        mListMessages = listMessages;
        mUserId = userId;
        mCountry = country;
        mListDateShows = new ArrayList<>();
        mMessagesKeys = keys;
        mIsPremium = isPremium;
        mLastCalendar = Calendar.getInstance();
        mLastCalendar.set(Calendar.YEAR, 1970);
        mLastCalendar.set(Calendar.MONTH, 1);
        mLastCalendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        Holder holder;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_message, parent, false);

            holder = new Holder();
            holder.badge = new QBadgeView(getContext()).bindTarget(listItemView.findViewById(R.id.cardview_message));
            listItemView.setTag(holder);
        }

        else {
            holder = (Holder) listItemView.getTag();
        }

        final Message currentMessage = getItem(position);
        final TextView nameView = (TextView) listItemView.findViewById(R.id.text_sender_name);
        TextView titleText = (TextView) listItemView.findViewById(R.id.text_message_title);
        final TextView dateText = (TextView) listItemView.findViewById(R.id.text_date);
        final ImageView imageView = (ImageView) listItemView.findViewById(R.id.friend_pic);
        //final CardView cardMessage = (CardView) listItemView.findViewById(R.id.cardview_message);

        long date = currentMessage.getDate();
        String dateString = DataUtils.getDateString(mContext, date);
        String title = currentMessage.getTitle();
        final boolean seen = currentMessage.getSeen();
        final String url = "https://firebasestorage.googleapis.com/v0/b/thanker-b301f.appspot.com/o/users-profile-pictures%2Fthankyou3.png?alt=media&token=edf26987-1b36-4a47-aaf0-a1d484681fee";
        boolean beenSeen = seen;
        //cardMessage.setBackground(mContext.getResources().getDrawable(R.drawable.circlenone));

        //dateText.setText(DataUtils.getDateString(mContext, currentMessage.getDate()));

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(new Date(date));
        dateText.setText(dateString);

        Date day = new Date(date);

        dateText.setVisibility(View.GONE);

        if(!isItSameCalendar(mLastCalendar, calendar)){
            if(!doesDayExistInList(mListDateShows, day)){
                mListDateShows.add(new DateShow(day, true));
            }
        }

        else {
            mListDateShows.add(new DateShow(day, false));
        }

        dateText.setVisibility(getVisibility(day));

        mLastCalendar = calendar;

        Log.v(TAG, "Reading messages. Current layout_title: " + title);
        //dateText.setText(date);
        titleText.setText(Html.fromHtml(title));

        Log.v(TAG, "Reading messages. Current value of seen: " + seen);

        if(!seen){
            Log.v(TAG, "Checking messages in Adapter. Message wasn't seen");
            if(currentMessage.getToUserId() != null){
                if(!currentMessage.getToUserId().equals("")){
                    holder.badge.setBadgeBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    holder.badge.setBadgeText("");
                    Log.v(TAG, "Checking messages in Adapter. Created not seen symbol");
                    Log.v(TAG, "Checking messages. Value of current message's key: " + currentMessage.getKey());
                }
            }
        }

        else {
            if(beenSeen){
                holder.badge.hide(true);
            }
        }

        Log.v(TAG, "Updating images in Messages. Before checking the user.");
        if(currentMessage.getFromUserId() != null){
            if(!currentMessage.getFromUserId().equals("") && !currentMessage.getFromUserId().equals("platform-message")){
                mFirestore.collection(USER_SNIPPET).document(currentMessage.getFromUserId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    mUserSnippet = documentSnapshot.toObject(UserSnippet.class);
                                    mFromUsername = DataUtils.capitalize(mUserSnippet.getName());
                                    String imageUrl = mUserSnippet.getImageUrl();
                                    Log.v(TAG, "MessageAdapter: Value of image Url: " + imageUrl);

                                    ImageUtils.loadImageIntoRound(mContext, imageUrl, imageView);
                                    nameView.setText(mFromUsername);

                                }

                                Log.v(TAG, "Reading from Firestore | " + TAG + " | Reading from Message Sender\'s UserSnippet");
                            }
                        });

            }
            else if(currentMessage.getFromUserId().equals("platform-message")){
                mUserSnippet = null;
                Log.v(TAG, "User name setter. We've reach the Platform name");;
                String message = mContext.getResources().getString(R.string.message);
                nameView.setText(message);
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thankyou3));
            }

        }

        else {
            ImageUtils.loadImageInto(mContext, url, imageView);
        }

        listItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mFirestore.collection(MESSAGES_REFERENCE).document(mMessagesKeys.get(position)).update("seen", true);
                Fragment msgFragment = new MessageFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(MESSAGE_OBJECT, currentMessage);
                bundle.putString(OUR_USER_ID, mUserId);
                bundle.putString(USER_ID_STRING, currentMessage.getFromUserId());
                bundle.putString(OUR_USER_COUNTRY, mCountry);
                bundle.putSerializable(USER_SNIPPET_VAR, mUserSnippet);
                bundle.putSerializable("messages", (Serializable) mListMessages);
                bundle.putBoolean(INFO_HAS_PREMIUM, mIsPremium);
                msgFragment.setArguments(bundle);

                if(mContext != null){
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, msgFragment).addToBackStack(null).commit();
                }
            }
        });

        return listItemView;
    }

    public boolean isItSameCalendar(Calendar one, Calendar two){
        return (one.get(Calendar.DAY_OF_MONTH) == two.get(Calendar.DAY_OF_MONTH)
                && one.get(Calendar.MONTH) == two.get(Calendar.MONTH)
                && one.get(Calendar.YEAR) == two.get(Calendar.YEAR));
    }

    public boolean doesDayExistInList(List<DateShow> list, Date day){
        for(DateShow item: list){
            if(item.getDate().getTime() == day.getTime()){
                return true;
            }
        }
        return false;
    }

    public int getVisibility(Date date){
        for(DateShow item: mListDateShows){
            if(item.getDate().getTime() == date.getTime()){
                if(item.getShow()){
                    return View.VISIBLE;
                }
                else {
                    return View.GONE;
                }
            }
        }
        return -1;
    }

    public boolean doesListContainCalendar(List<Calendar> list, Calendar calendar){
        for(Calendar item: list){
            if(item.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                    item.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    item.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {

                return true;
            }
        }
        return false;
    }

    class Holder {
        //CardView cardView;
        Badge badge;
    }

}
