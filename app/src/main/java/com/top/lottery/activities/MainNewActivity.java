package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.fragments.MainFragment;
import com.top.lottery.fragments.MeFragment;
import com.top.lottery.fragments.OpenLotteryFragment;
import com.top.lottery.utils.AppManager;

/**
 * Author: 邓言诚  Create at : 2018/12/15  22:28
 * Email: yanchengdeng@gmail.com
 * Describle: 首页2.0 改版
 */
public class MainNewActivity extends BaseActivity {

    private ViewPager viewPager;
    private SmartTabLayout smartTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        viewPager = findViewById(R.id.viewpager);
        smartTabLayout = findViewById(R.id.viewpagertab);
        hideTittle();
        initFragments();
    }

    private void initFragments() {
        final FragmentPagerItems pages = new FragmentPagerItems(this);

        pages.add(FragmentPagerItem.of(getString(R.string.tab_main), MainFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.tab_open), OpenLotteryFragment.class));
//        pages.add(FragmentPagerItem.of(getString(R.string.tab_apply), MeFragment.class));
        pages.add(FragmentPagerItem.of(getString(R.string.tab_me), MeFragment.class));


        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);


        final LayoutInflater inflater = LayoutInflater.from(MainNewActivity.this);
        smartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View tabView = inflater.inflate(R.layout.custom_tab_icon, container,
                        false);
                ImageView imageView = (ImageView) tabView.findViewById(R.id.custom_tab_icon);
                TextView tvName = (TextView) tabView.findViewById(R.id.custom_tab_text);
                switch (position) {
                    case 0:
                        imageView.setImageResource(R.mipmap.tab_kind);
                        tvName.setText(getString(R.string.tab_main));
                        break;
                    case 1:
                        imageView.setImageResource(R.mipmap.tab_open);
                        tvName.setText(getString(R.string.tab_open));
                        break;
                    case 2:
                        imageView.setImageResource(R.mipmap.tab_center);
                        tvName.setText(getString(R.string.tab_me));
                        break;
//                    case 3:
//                        imageView.setImageResource(R.drawable.ic_tab_me);
//                        tvName.setText(getString(R.string.tab_me));
//                        break;
                }

                return tabView;
            }
        });
        viewPager.setOffscreenPageLimit(pages.size());
        viewPager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(pages.size());
    }

    @Override
    public void onBackPressed() {
        doubleClickExist();
    }

    private long mExitTime;

    /****
     * 连续两次点击退出
     */
    private boolean doubleClickExist() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            ToastUtils.showShort(R.string.double_click_exit);
            mExitTime = System.currentTimeMillis();
            return true;
        } else {
            Constants.HAS_VESRSION_TIPS = false;
            AppManager.getAppManager().AppExit(this);
            finish();
        }
        return false;
    }
}
