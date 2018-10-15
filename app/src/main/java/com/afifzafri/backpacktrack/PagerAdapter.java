package com.afifzafri.backpacktrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String itinerary_id;
    String itinerary_user_id;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, String itinerary_id, String itinerary_user_id) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.itinerary_id = itinerary_id;
        this.itinerary_user_id = itinerary_user_id;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("itinerary_id", itinerary_id);
        bundle.putString("itinerary_user_id", itinerary_user_id);

        switch (position) {
            case 0:
                ViewActivitiesFragment tab1 = new ViewActivitiesFragment();
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                MapFragment tab2 = new MapFragment();
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                BudgetFragment tab3 = new BudgetFragment();
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                CommentsFragment tab4 = new CommentsFragment();
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