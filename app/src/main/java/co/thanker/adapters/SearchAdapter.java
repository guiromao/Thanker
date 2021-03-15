package co.thanker.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.thanker.R;
import co.thanker.data.UserResult;
import co.thanker.data.UserSnippet;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class SearchAdapter extends RecyclerView.Adapter {

    private static final String TAG = "SearchAdapter";
    private final String DB_REFERENCE = "users";
    private final int TYPE_RESULT = 1;
    private final int TYPE_MORE = 2;

    private List<UserSnippet> mListProfiles;
    private Context mContext;
    private SearchAdapterClickListener mListener;

    public SearchAdapter(Context c, List<UserSnippet> profiles, SearchAdapterClickListener listener){
        mContext = c;
        mListProfiles = profiles;
        mListener = listener;
    }

    public interface SearchAdapterClickListener {

        public void onSearchItemClick(String userId, String email);
    }

    @Override
    public int getItemViewType(int position) {

        if(position != mListProfiles.size() - 1){
            return TYPE_RESULT;
        }

        else {
            return TYPE_MORE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_query, parent, false);
        SearchViewHolder searchHolder = new SearchViewHolder(view, mListener);

        return searchHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchViewHolder searchViewHolder = (SearchViewHolder) holder;
        UserSnippet result = mListProfiles.get(position);

        searchViewHolder.setDetails(mContext, result);
    }

    @Override
    public int getItemCount() {
        if(mListProfiles.size() > 10){
            return 10;
        }

        else {
            return mListProfiles.size();
        }
    }

    public void addAll(List<UserSnippet> list){
        for(UserSnippet item: list){
            mListProfiles.add(item);
        }
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameOrEmail;
        TextView mCategoryOrInvited;
        ImageView mImage;
        CardView mCardView;

        public SearchViewHolder(@NonNull View itemView, SearchAdapterClickListener listener){
            super(itemView);
            mNameOrEmail = (TextView) itemView.findViewById(R.id.search_profile_name);
            mCategoryOrInvited = (TextView) itemView.findViewById(R.id.search_primary_category);
            //mCardView = (CardView) itemView.findViewById(R.id.cardview_search_item);
            mImage = (ImageView) itemView.findViewById(R.id.search_profile_picture);
            mListener = listener;
            itemView.setOnClickListener(this);
        }


        public void setDetails(Context context, final UserSnippet user){

            mImage.setVisibility(View.VISIBLE);

            if(user.getUserId() != null){

                Log.v(TAG, "This is Search Adapter. And this is a real user: " + user.getName());
                String name = DataUtils.capitalize(user.getName());

                mNameOrEmail.setText(name);

                mCategoryOrInvited.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor2));
                mCategoryOrInvited.setTypeface(null, Typeface.NORMAL);
                mCategoryOrInvited.setText(DataUtils.decapitalize(DataUtils.translateToOwnLanguage(mContext, user.getPrimaryCategory())));

                ImageUtils.loadImageIntoRound(mContext, user.getImageUrl(), mImage);
            }

            else {
                Log.v(TAG, "This is Search Adapter. And this is an email");
                mNameOrEmail.setText(user.getEmail());
                mCategoryOrInvited.setTextColor(mContext.getResources().getColor(R.color.defaultTextColor));
                mCategoryOrInvited.setTypeface(null, Typeface.BOLD);
                mCategoryOrInvited.setText(mContext.getString(R.string.click_to_invite));
                mImage.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View v) {
            if(getAdapterPosition() > -1) {
                int pos = getAdapterPosition();
                mListener.onSearchItemClick(mListProfiles.get(pos).getUserId(), mListProfiles.get(pos).getEmail());
            }

            /*String userId = mListProfiles.get(pos).getUserId();
            Bundle userInfoBundle = new Bundle();
            userInfoBundle.putString(USER_ID_STRING, userId);
            Fragment fragment = new MyProfileFragment();
            fragment.setArguments(userInfoBundle);
            FragmentManager fManager = ((MainActivity)mContext).getSupportFragmentManager();
            fManager.beginTransaction().replace(R.id.fragment_container, fragment).commit(); */
        }
    }
}
