package com.parse.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragment.DiscussionFragment;
import fragment.MembresFragment;

/**
 * Created by severin_mbekou on 4/8/15.
 */
public class TontinePagerAdapter extends FragmentStatePagerAdapter {
    private String sessionId;
    public TontinePagerAdapter(FragmentManager fm, String sessionId) {
        super(fm);
        this.sessionId = sessionId;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {

            case 0:
                // a la une fragment activity
                return new DiscussionFragment(sessionId);
            case 1:
                // mes journaux fragment activity
                return new MembresFragment(sessionId);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
