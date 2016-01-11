package com.parse.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragment.DiscussionFragment;
import fragment.MembresFragment;
import fragment.SessionsFragment;

/**
 * Created by severin_mbekou on 4/8/15.
 */
public class TabsTontinePagerAdapter extends FragmentStatePagerAdapter {
    private String tontineId;
    private String date;
    private String nom;
    public TabsTontinePagerAdapter(FragmentManager fm, String nom, String tontineId, String date) {

        super(fm);
        this.tontineId = tontineId;
        this.date = date;
        this.nom = nom;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {

            case 0:
                return new DiscussionFragment(tontineId);
            case 1:
                return new SessionsFragment(tontineId, date);
            case 2:
                return new MembresFragment(tontineId,nom);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
