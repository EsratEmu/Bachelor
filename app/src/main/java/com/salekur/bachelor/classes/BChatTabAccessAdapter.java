package com.salekur.bachelor.classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.salekur.bachelor.fragments.ChatsFragment;
import com.salekur.bachelor.fragments.ContactsFragment;
import com.salekur.bachelor.fragments.GroupsFragment;

public class BChatTabAccessAdapter extends FragmentPagerAdapter

{

    public BChatTabAccessAdapter(@NonNull FragmentManager fm) {

        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                ChatsFragment chatFragment = new ChatsFragment();
                return chatFragment;

            case 1:
                GroupsFragment groupFragment = new GroupsFragment();
                return groupFragment;

            case 2:
                ContactsFragment contactFragment = new ContactsFragment();
                return contactFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            default:
                return null;
        }

    }
}
