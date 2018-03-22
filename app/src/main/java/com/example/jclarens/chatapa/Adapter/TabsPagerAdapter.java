package com.example.jclarens.chatapa.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jclarens.chatapa.Fragment.ChatsFragment;
import com.example.jclarens.chatapa.Fragment.FriendsFragment;
import com.example.jclarens.chatapa.Fragment.RequestsFragments;

/**
 * Created by jclarens on 06/12/17.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {
    private Context thiscontext;


    public TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        thiscontext=context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new RequestsFragments();
            case 1:
                return new ChatsFragment();
            case 2:
                return new FriendsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}

