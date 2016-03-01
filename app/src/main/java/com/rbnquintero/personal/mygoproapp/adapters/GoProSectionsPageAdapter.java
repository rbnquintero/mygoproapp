package com.rbnquintero.personal.mygoproapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rbnquintero.personal.mygoproapp.fragments.GoProControlCameraFragment;
import com.rbnquintero.personal.mygoproapp.fragments.GoProControlMediaFragment;

/**
 * Created by rbnquintero on 2/26/16.
 */
public class GoProSectionsPageAdapter extends FragmentPagerAdapter {
    private String TAG = this.getClass().getSimpleName();

    public GoProSectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return GoProControlCameraFragment.newInstance(position + 1);
        } else if (position == 1) {
            return GoProControlMediaFragment.newInstance(position + 1);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CAMERA";
            case 1:
                return "MEDIA";
        }
        return null;
    }
}
