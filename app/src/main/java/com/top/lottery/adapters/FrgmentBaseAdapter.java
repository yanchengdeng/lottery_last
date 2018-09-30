package com.top.lottery.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import java.util.ArrayList;

/**
*
* Author: 邓言诚  Create at : 2018/7/9  17:44
* Email: yanchengdeng@gmail.com
* Describle: Fragment 适配器
*/
public class FrgmentBaseAdapter extends FragmentPagerAdapter {

    ArrayList<Pair<String,Fragment>> items;

    public FrgmentBaseAdapter(FragmentManager supportFragmentManager, ArrayList<Pair<String,Fragment>> items) {
        super(supportFragmentManager);
        this.items = items;
    }

    @Override
        public Fragment getItem(int position) {
            return items.get(position).second;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items.get(position).first;
        }
    }