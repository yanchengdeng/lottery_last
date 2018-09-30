package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.top.lottery.R;
import com.top.lottery.adapters.FrgmentBaseAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ManageMemberInfo;
import com.top.lottery.beans.MemberTabs;
import com.top.lottery.fragments.ManageFragment;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/9/9  11:07
 * Email: yanchengdeng@gmail.com
 * Describle: 后台管理
 */
public class ManageActivity extends BaseActivity {

    @BindView(R.id.viewpagertab)
    SmartTabLayout viewpagertab;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        ButterKnife.bind(this);
        setTitle("会员管理");
        getChildList();
    }

    private void getChildList() {
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("page", String.valueOf(page));
//        data.put("user_type",String.valueOf(Utils.getUserInfo().user_type));
        OkGo.<LotteryResponse<ManageMemberInfo>>post(Constants.Net.LEADER_GETCHILDLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<ManageMemberInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<ManageMemberInfo>> response) {
                        dismissLoadingBar();
                        ManageMemberInfo memberInfo = response.body().body;
                        if (memberInfo != null && memberInfo.tabs != null && memberInfo.tabs.size() > 0) {
                            initFragments(memberInfo.tabs);
                            if (memberInfo.tabs.size()==1){
                                viewpagertab.setVisibility(View.GONE);
                            }
                            showContentView();
                        } else {
                            showError(" " + response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        dismissLoadingBar();
                        showError(Utils.toastInfo(response));

                    }
                });
    }

    private void initFragments(List<MemberTabs> tabs) {
        ArrayList<Pair<String, Fragment>> items = new ArrayList<>();

        for (MemberTabs item : tabs) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.PASS_OBJECT, item);
            ManageFragment consultTagsFragment = new ManageFragment();
            consultTagsFragment.setArguments(bundle);
            items.add(new Pair<String, Fragment>(item.title, consultTagsFragment));
        }
        viewpager.setAdapter(new FrgmentBaseAdapter(getSupportFragmentManager(), items));
        viewpager.setOffscreenPageLimit(tabs.size());
        viewpagertab.setViewPager(viewpager);
    }
}
