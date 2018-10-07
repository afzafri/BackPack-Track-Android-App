package com.afifzafri.backpacktrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String itinerary_id;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String itinerary_id) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.itinerary_id = itinerary_id;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("itinerary_id", itinerary_id);

        switch (position) {
            case 0:
                ViewActivitiesFragment tab1 = new ViewActivitiesFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                ViewActivitiesFragment tab2 = new ViewActivitiesFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                ViewActivitiesFragment tab3 = new ViewActivitiesFragment();
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                ViewActivitiesFragment tab4 = new ViewActivitiesFragment();
                tab4.setArguments(bundle);
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}