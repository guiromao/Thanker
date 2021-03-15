package co.thanker.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FriendsFragmentPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> pages = new ArrayList<Fragment>();
    ArrayList<String> pageTitles = new ArrayList<String>();

    public FriendsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment frag, String title){
        pages.add(frag);
        pageTitles.add(title);
    }

    public void removeFragment(int index){
        pages.remove(index);
        pageTitles.remove(index);
    }

    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate layout_title based on item position
        return pageTitles.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}
