package com.afifzafri.backpacktrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ItinerariesPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String type;
    String input;

    public ItinerariesPagerAdapter(FragmentManager fm, int NumOfTabs, String type, String input) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.type = type;
        this.input = input;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("input", input);

        switch (position) {
            case 0:
                ListItinerariesFragment tab1 = new ListItinerariesFragment();
                bundle.putInt("sort", 1); // Sort by NEW : 1
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                ListItinerariesFragment tab2 = new ListItinerariesFragment();
                bundle.putInt("sort", 2); // Sort by TOP : 2
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                ListItinerariesFragment tab3 = new ListItinerariesFragment();
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