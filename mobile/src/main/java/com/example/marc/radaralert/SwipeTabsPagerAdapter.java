package com.example.marc.radaralert;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Observable;

/**
* Created by Marc on 01/03/2015.
*/
public class SwipeTabsPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    Observable observable;
    public SwipeTabsPagerAdapter(FragmentManager supportFragmentManager, Observable listener) {
        super(supportFragmentManager);
        this.observable = listener;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Mapa";
            case 1:
                return "Les meves alertes";

        }
        return "Default";

    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MapFrag();
            case 1:
                return new AlertsListFrag();
        }
        return DefaultPageFragment.create(position + 1);
    }
}
