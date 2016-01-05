package com.parse.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragment.MesTontinesFragment;
import fragment.TontinesFragment;

/**
 * Created by severin_mbekou on 4/8/15.
 */
public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {

            case 0:
                return new MesTontinesFragment();
            case 1:
                return new TontinesFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
