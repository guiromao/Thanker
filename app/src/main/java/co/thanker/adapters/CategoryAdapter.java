package co.thanker.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import co.thanker.data.User;
import co.thanker.fragments.TopFragment;
import co.thanker.utils.DataUtils;
import co.thanker.utils.ImageUtils;

public class CategoryAdapter extends ArrayAdapter<User.Category> {

    private final String TAG = CategoryAdapter.class.getSimpleName();
    private final String TOP_CATEGORY = "top-category";
    private final String TOP_CATEGORY_TRANSLATED = "top-category-translated";
    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";

    private Context mContext;
    private String mCountry;
    private String mUserId;

    public CategoryAdapter(@NonNull Context context, int resource, List<User.Category> listCategories, String country, String userId) {
        super(context, resource, listCategories);
        mContext = context;
        mCountry = country;
        mUserId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_fame, parent, false);
        }

        final User.Category currentCategory = getItem(position);

        Drawable categoryImage;
        TextView textCatName = (TextView) listItemView.findViewById(R.id.text_category);
        ImageView imageCat = (ImageView) listItemView.findViewById(R.id.category_picture);
        textCatName.setText(currentCategory.toString());

        String cat;

        if(currentCategory == User.Category.BLANK){
            cat = "Overall";
            textCatName.setText(mContext.getString(R.string.overall));
        }

        else {
            String newWord = translatedCategory(currentCategory.toString());
            cat = newWord;
            textCatName.setText(cat);
        }

        String category;
        if(cat.equalsIgnoreCase("Overall")){
            category = "Overall";
        }

        else {
            category = DataUtils.categoryToString(currentCategory.toString());
        }

        final String catToString = category;

        categoryImage = ImageUtils.getCategoryDrawable(mContext, currentCategory.toString());

        if(categoryImage != null){
            imageCat.setImageDrawable(categoryImage);
        }


        listItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Fragment topFragment = new TopFragment();
                Bundle bundle = new Bundle();
                bundle.putString(TOP_CATEGORY, catToString);
                bundle.putString(TOP_CATEGORY_TRANSLATED, translatedCategory(currentCategory.toString()));
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

    private String translatedCategory(String category){
        List<String> englishCategories = DataUtils.getTranslatableStrings(mContext);
        String [] arrayTranslatedCategories = mContext.getResources().getStringArray(R.array.translatable_pages);

        if(category.equalsIgnoreCase("")){
            return mContext.getString(R.string.overall);
        }

        else {

            for(int i = 0; i != englishCategories.size(); i++){
                String word = englishCategories.get(i);

                if(category.toUpperCase().equals("PERSON")){
                    category = "PEOPLE";
                }

                else if(category.toUpperCase().equalsIgnoreCase("Brand")){
                    category = "Brands";
                }

                else if(category.equalsIgnoreCase("Association")){
                    category = "Associations";
                }

                Log.v(TAG, "Category: " + category +". English category word: " + word + ". From the Array: " + arrayTranslatedCategories[i]);

                if(word.equalsIgnoreCase(category)){
                    Log.v(TAG, "Category: Found. Sending the word: " + arrayTranslatedCategories[i]);
                    return arrayTranslatedCategories[i];
                }
            }
        }

        return "";
    }
}
