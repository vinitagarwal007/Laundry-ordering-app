package com.vinitagarwal.DemoLaundryAdmin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class vpadapter extends FragmentPagerAdapter {
    public vpadapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }
    ArrayList<Fragment> fragments = new ArrayList<>();
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
    public void addfragments(Fragment fragment){
        fragments.add(fragment);
    }
}
