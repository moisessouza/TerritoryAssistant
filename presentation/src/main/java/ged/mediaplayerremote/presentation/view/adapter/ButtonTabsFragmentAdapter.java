package ged.mediaplayerremote.presentation.view.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import ged.mediaplayerremote.presentation.view.fragment.ButtonGridFragment;

import java.util.Collections;
import java.util.List;


/**
 * Adapter that manages a list of {@link ButtonGridFragment}.
 */
public class ButtonTabsFragmentAdapter extends FragmentPagerAdapter {

    private List<String> pageTitles;

    public ButtonTabsFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.pageTitles = Collections.emptyList();
    }

    public void setPagesList(List<String> pagesList) {
        this.pageTitles = pagesList;
    }

    @Override
    public int getCount() {
        return pageTitles.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ButtonGridFragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}