package com.afifzafri.backpacktrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CountryItinerariesPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    String country_id;

    public CountryItinerariesPagerAdapter(FragmentManager fm, int NumOfTabs, String country_id) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.country_id = country_id;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putString("country_id", country_id);

        switch (position) {
            case 0:
                TopCountryItinerariesFragment tab1 = new TopCountryItinerariesFragment();
                bundle.putInt("sort", 1); // Sort by NEW : 1
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                TopCountryItinerariesFragment tab2 = new TopCountryItinerariesFragment();
                bundle.putInt("sort", 2); // Sort by TOP : 2
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                TopCountryItinerariesFragment tab3 = new TopCountryItinerariesFragment();
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