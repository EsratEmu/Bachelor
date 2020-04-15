package com.salekur.bachelor.classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.salekur.bachelor.fragments.BMealComplainFragment;
import com.salekur.bachelor.fragments.BMealFragment;
import com.salekur.bachelor.fragments.BMealMembersFragment;
import com.salekur.bachelor.fragments.BMealMenuFragment;
import com.salekur.bachelor.fragments.BMealNoticeFragment;

public class TabAccessorAdapter extends FragmentPagerAdapter {

    public TabAccessorAdapter(@NonNull FragmentManager fm) {

        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                BMealFragment bMealFragment = new BMealFragment();
                return bMealFragment;

            case 1:
                BMealMembersFragment bMealMembersFragment = new BMealMembersFragment();
                return bMealMembersFragment;

            case 2:
                BMealMenuFragment bMealMenuFragment = new BMealMenuFragment();
                return bMealMenuFragment;

            case 3:
                BMealComplainFragment bMealComplainFragment = new BMealComplainFragment();
                return bMealComplainFragment;

            case 4:
                BMealNoticeFragment bMealNoticeFragment = new BMealNoticeFragment();
                return bMealNoticeFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Home";

            case 1:
                return "Members";

            case 2:
                return "Food Menu";

            case 3:
                return "Complains";

            case 4:
                return "Notice";

            default:
                return null;
        }

    }
}