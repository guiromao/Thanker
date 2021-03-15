package co.thanker.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import co.thanker.R;
import co.thanker.adapters.CategoryAdapter;
import co.thanker.data.User;
import co.thanker.utils.Utils;


public class FameFragment extends Fragment {

    private final String OUR_USER_ID = "our-user-id";
    private final String OUR_USER_COUNTRY = "our-user-country";

    private GridView mGridView;
    private CategoryAdapter mAdapter;
    private List<User.Category> mListCategories;
    private MenuItem mFameItem;
    private String mUserId;
    private String mCountry;

    public FameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fame, container, false);

        //setHasOptionsMenu(true);

        mGridView = (GridView) view.findViewById(R.id.gridview_hall_of_fame);

        if(getArguments() != null){
            mUserId = getArguments().getString(OUR_USER_ID);
            mCountry = getArguments().getString(OUR_USER_COUNTRY);
        }

        mListCategories = new ArrayList<>();
        for(User.Category c: User.Category.values()){
            mListCategories.add(c);
        }

        mAdapter = new CategoryAdapter(getContext(), 0, mListCategories, mCountry, mUserId);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mFameItem != null){
                    Drawable fameDraw = mFameItem.getIcon();
                    fameDraw.setColorFilter( getResources().getColor(R.color.grey), PorterDuff.Mode.MULTIPLY );
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
        mFameItem = menu.findItem(R.id.item_fame);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_fame: break;

            default:
                break;
        }

        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setSubtitle(null);

        if(getActivity() != null){
            /*if(mFromUsername != null && !mOtherUserId.equalsIgnoreCase(PLATFORM_MESSAGE)){
                Utils.changeBarSubTitle(getActivity(), mActionBar, getActivity().getString(R.string.message_from, mFromUsername));
            }
            else {*/
            Utils.changeBarTitle(getActivity(), actionBar, getActivity().getString(R.string.hall_of_fame_literal));
            // }
        }

        actionBar.setDisplayHomeAsUpEnabled(false);
        /*final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultTextColor), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);*/
    }

}
