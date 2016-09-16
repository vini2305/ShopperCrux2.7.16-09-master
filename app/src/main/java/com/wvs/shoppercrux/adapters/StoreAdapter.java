package com.wvs.shoppercrux.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wvs.shoppercrux.fragments.StoreOffers;
import com.wvs.shoppercrux.fragments.StoreProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 22/8/16.
 */
public class StoreAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentNames = new ArrayList<>();
    int total;

    public StoreAdapter(FragmentManager manager,int total) {
        super(manager);
        this.total = total;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                StoreProfile profile = new StoreProfile();
                return profile;
            case 1:
                StoreOffers offers = new StoreOffers();
                return offers;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return total;
    }

    public void addFragment(Fragment fragment,String title){
        fragments.add(fragment);
        fragmentNames.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNames.get(position);
    }
}
