package com.example.hikerdatabase.HikesEdit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HikeEditTabsAdapter extends FragmentStateAdapter {

    public HikeEditTabsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HikeEditFragment(); // DETAILS tab
        } else {
            return new ObservationEditFragment(); // OBSERVATIONS tab
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
