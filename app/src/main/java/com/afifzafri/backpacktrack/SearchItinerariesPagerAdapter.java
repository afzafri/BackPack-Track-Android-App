package com.afifzafri.backpacktrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SearchItinerariesPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String query;

    public SearchItinerariesPagerAdapter(FragmentManager fm, int NumOfTabs, String query) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.query = query;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("query", query);

        switch (position) {
            case 0:
                ListSearchItinerariesFragment tab1 = new ListSearchItinerariesFragment();
                bundle.putInt("sort", 1); // Sort by NEW : 1
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                ListSearchItinerariesFragment tab2 = new ListSearchItinerariesFragment();
                bundle.putInt("sort", 2); // Sort by TOP : 2
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                ListSearchItinerariesFragment tab3 = new ListSearchItinerariesFragment();
                bundle.putInt("sort", 3); // Sort by TRENDING : 3
                tab3.setArguments(bundle);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}