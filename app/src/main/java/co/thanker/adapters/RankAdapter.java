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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.data.ThanksValue;
import co.thanker.data.User;
import co.thanker.fragments.MyProfileFragment;
import co.thanker.fragments.OtherProfileFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class RankAdapter extends ArrayAdapter<User> {

    private static final String TAG = "RankAdapter";
    private final String USERS_DATABASE = "users";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";
    private final String USER_ID_STRING = "user-id-string";
    private final String OTHER_USER_OBJECT = "other-user-object";

    private Context mContext;
    private String mCountry;
    private String mUserId;
    private String mCategory;

    public RankAdapter(@NonNull Context context, int resource, List<User> listUsers, String category, String country, String id) {
        super(context, resource, listUsers);

        mContext = context;
        mCountry = country;
        mUserId = id;
        mCategory = category;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_rank, parent, false);
        }

        final User currentUser = getItem(position);

        final TextView textRank = (TextView) listItemView.findViewById(R.id.text_rank_number);
        final TextView textName  = (TextView) listItemView.findViewById(R.id.text_name);
        final TextView textThanksGiven = (TextView) listItemView.findViewById(R.id.text_thanks);
        final TextView textCategories = (TextView) listItemView.findViewById(R.id.text_categories);
        final ImageView picture = (ImageView) listItemView.findViewById(R.id.profile_pic);
        final LinearLayout linearInfo = (LinearLayout) listItemView.findViewById(R.id.linear_info);
        final ImageView catPicOne = (ImageView) listItemView.findViewById(R.id.image_first_category);
        final ImageView catPicTwo = (ImageView) listItemView.findViewById(R.id.image_second_category);
        final ImageView catPicThree = (ImageView) listItemView.findViewById(R.id.image_third_category);
        final TextView catTextOne = (TextView) listItemView.findViewById(R.id.text_first_category);
        final TextView catTextTwo = (TextView) listItemView.findViewById(R.id.text_second_category);
        final TextView catTextThree = (TextView) listItemView.findViewById(R.id.text_third_category);
        final TextView categoryViewOne = (TextView) listItemView.findViewById(R.id.text_category_1);
        final TextView categoryViewTwo = (TextView) listItemView.findViewById(R.id.text_category_2);
        final TextView categoryViewThree = (TextView) listItemView.findViewById(R.id.text_category_3);
        final LinearLayout linearThanks = (LinearLayout) listItemView.findViewById(R.id.linear_thanks);
        final LinearLayout linearThanksSubOne = (LinearLayout) listItemView.findViewById(R.id.linear_thanks_1);
        final LinearLayout linearThanksSubTwo = (LinearLayout) listItemView.findViewById(R.id.linear_thanks_2);
        final LinearLayout linearThanksSubThree = (LinearLayout) listItemView.findViewById(R.id.linear_thanks_3);

        textRank.setText(String.valueOf(position + 1) + "º");

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visit(currentUser);
            }
        });

        String categoryString = DataUtils.thanksCategoryToStringCategory(mCategory);
        Log.v(TAG, "Creating Rank Adapter. categoryString: " + categoryString + ". The value of it is: " + currentUser.getRightCategoryValue(categoryString));
        textName.setText(DataUtils.capitalize(currentUser.getName()));
        textThanksGiven.setText(Html.fromHtml("<b>" + String.format("%,d", currentUser.getRightCategoryValue(categoryString)) + " " +
                mContext.getString(R.string.thanks) + "</b>"));

        ImageUtils.loadImageIntoRound(mContext, currentUser.getImageUrl(), picture);

        String categories = DataUtils.translateAndFormat(mContext, currentUser.getPrimaryCategory());

        if(currentUser.getSecondaryCategory() != null){
            if(!currentUser.getSecondaryCategory().equals("")){
                categories += " | " + DataUtils.translateAndFormat(mContext, currentUser.getSecondaryCategory());
            }
        }

        textCategories.setText(categories);

        List<ThanksValue> userThanks = new ArrayList<>();

        userThanks.add(new ThanksValue("personThanks", currentUser.getPersonThanks()));
        userThanks.add(new ThanksValue("brandThanks", currentUser.getBrandThanks()));
        userThanks.add(new ThanksValue("businessThanks", currentUser.getBusinessThanks()));
        userThanks.add(new ThanksValue("healthThanks", currentUser.getHealthThanks()));
        userThanks.add(new ThanksValue("foodThanks", currentUser.getFoodThanks()));
        userThanks.add(new ThanksValue("associationThanks", currentUser.getAssociationThanks()));
        userThanks.add(new ThanksValue("homeThanks", currentUser.getHomeThanks()));
        userThanks.add(new ThanksValue("scienceThanks", currentUser.getScienceThanks()));
        userThanks.add(new ThanksValue("religionThanks", currentUser.getReligionThanks()));
        userThanks.add(new ThanksValue("sportsThanks", currentUser.getSportsThanks()));
        userThanks.add(new ThanksValue("lifestyleThanks", currentUser.getLifestyleThanks()));
        userThanks.add(new ThanksValue("techThanks", currentUser.getTechThanks()));
        userThanks.add(new ThanksValue("fashionThanks", currentUser.getFashionThanks()));
        userThanks.add(new ThanksValue("educationThanks", currentUser.getEducationThanks()));
        userThanks.add(new ThanksValue("gamesThanks", currentUser.getGamesThanks()));
        userThanks.add(new ThanksValue("govThanks", currentUser.getGovThanks()));
        userThanks.add(new ThanksValue("beautyThanks", currentUser.getBeautyThanks()));
        userThanks.add(new ThanksValue("financeThanks", currentUser.getFinanceThanks()));
        userThanks.add(new ThanksValue("cultureThanks", currentUser.getCultureThanks()));
        userThanks.add(new ThanksValue("natureThanks", currentUser.getNatureThanks()));
        userThanks.add(new ThanksValue("travelThanks", currentUser.getTravelThanks()));

        DataUtils.sortThanksValues(userThanks);

        List<ImageView> categoryImageList = new ArrayList<>();
        List<TextView> categoryTextList = new ArrayList<>();
        List<TextView> categoryTextThanked = new ArrayList<>();

        categoryImageList.add(catPicOne);
        categoryImageList.add(catPicTwo);
        categoryImageList.add(catPicThree);

        categoryTextList.add(catTextOne);
        categoryTextList.add(catTextTwo);
        categoryTextList.add(catTextThree);

        categoryTextThanked.add(categoryViewOne);
        categoryTextThanked.add(categoryViewTwo);
        categoryTextThanked.add(categoryViewThree);

        List<LinearLayout> listLinears = new ArrayList<>();
        listLinears.add(linearThanksSubOne);
        listLinears.add(linearThanksSubTwo);
        listLinears.add(linearThanksSubThree);

        int maxIndex = userThanks.size() > 3 ? 3 : userThanks.size();

        if(maxIndex > 0){
            linearThanks.setVisibility(View.VISIBLE);
        }

        for(int i = 0; i < maxIndex; i++){
            String category = DataUtils.thanksCategoryToStringCategory(userThanks.get(i).getKey());
            categoryImageList.get(i).setImageDrawable(ImageUtils.getIconImageGrey(mContext, category));
            categoryTextList.get(i).setText(String.format("%,d", userThanks.get(i).getValue()));
            categoryTextThanked.get(i).setText(DataUtils.translateAndFormat(mContext, category));

            Log.v(TAG, "Category name: " + DataUtils.translateAndFormat(mContext, category));

            listLinears.get(i).setVisibility(View.VISIBLE);
        }

        return listItemView;
    }

    public void visit(User otherUser){
        Fragment profileFrag;

        if(!mUserId.equalsIgnoreCase(otherUser.getUserId())){
            profileFrag = new OtherProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString(OUR_USER_ID, mUserId);
            bundle.putString(OUR_USER_COUNTRY, mCountry);
            bundle.putSerializable(OTHER_USER_OBJECT, otherUser);
            profileFrag.setArguments(bundle);
        }

        else {
            profileFrag = new MyProfileFragment();
        }

        if(mContext != null){
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, profileFrag).addToBackStack(null).commit();
        }
    }

}
