package com.example.whatsappclone.helper;

import android.app.DownloadManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatsappclone.Fragments.ChatFragment;
import com.example.whatsappclone.Fragments.RequestFragment;
import com.example.whatsappclone.Fragments.StatusFragment;

public class TabAccesorAdapter extends FragmentPagerAdapter {

    public TabAccesorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                ChatFragment chatFragment= new ChatFragment();
                return chatFragment;
            case 1:
                StatusFragment statusFragment = new StatusFragment();
                return statusFragment;
            case 2:
                RequestFragment requestFragment= new RequestFragment();
                return requestFragment;
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
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Status";
            case 2:
                return "Requests";
            default:
                return null;
        }
    }
}
