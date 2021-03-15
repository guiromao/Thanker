package co.thanker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
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

import java.util.List;

import co.thanker.R;
import co.thanker.data.CategoryValue;
import co.thanker.data.User;
import co.thanker.fragments.TopFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

import static co.thanker.utils.DataUtils.categoryToString;

public class CategoryValueAdapter extends ArrayAdapter<CategoryValue> {

    private static final String TAG = "CategoryValueAdapter";
    private final String TOP_CATEGORY = "top-category";
    private final String TOP_CATEGORY_TRANSLATED = "top-category-translated";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";

    private Context mContext;
    private String mUserId;
    private String mCountry;

    public CategoryValueAdapter(@NonNull Context context, int resource, List<CategoryValue> listCategories, String id, String country) {
        super(context, resource, listCategories);
        mContext = context;
        mUserId = id;
        mCountry = country;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_fame, parent, false);
        }

        final CategoryValue currentCategory = getItem(position);
        final String catName = currentCategory.getName();
        final String catString = DataUtils.thanksCategoryToStringCategory(currentCategory.getName());

        TextView textCatName = (TextView) listItemView.findViewById(R.id.text_category);
        textCatName.setText(DataUtils.translateToOwnLanguage(mContext, catString));
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.category_picture);
        Drawable image = ImageUtils.getCategoryDrawable(mContext, catString);
        Log.v(TAG, "Firestore stuff. Category name: " + catName);

        if(image != null){
            imageView.setImageDrawable(image);
        }

        Log.v(TAG, "Fame from Search. Cat Name: " + catName + ". Translated: " + DataUtils.translateToOwnLanguage(mContext, catString) + ". ID: " + mUserId + ". Country: " + mCountry);

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment topFragment = new TopFragment();
                Bundle bundle = new Bundle();
                bundle.putString(TOP_CATEGORY, catName);
                bundle.putString(TOP_CATEGORY_TRANSLATED, DataUtils.translateToOwnLanguage(mContext, catString));
                bundle.putString(OUR_USER_ID, mUserId);
                bundle.putString(OUR_USER_COUNTRY, mCountry);
                topFragment.setArguments(bundle);

                if(mContext != null){
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, topFragment).addToBackStack(null).commit();
                }
            }
        });

        return listItemView;
    }


}
