package com.example.marc.radaralert;



import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
    Context context;
    public SwipeTabsPagerAdapter(FragmentManager supportFragmentManager, Context context, Observable listener) {
        super(supportFragmentManager);
        this.observable = listener;
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return context.getResources().getString(R.string.map);
            case 1:
                return context.getResources().getString(R.string.my_alerts);

        }
        return context.getResources().getString(R.string.default_text);

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
