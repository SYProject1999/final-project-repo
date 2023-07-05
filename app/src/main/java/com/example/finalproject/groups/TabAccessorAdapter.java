package com.example.finalproject.groups;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAccessorAdapter extends FragmentStateAdapter {
    public TabAccessorAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new ContactsFragment();
            case 2:
                return new RequestsFragment();
        }
        return new GroupsChatsFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
