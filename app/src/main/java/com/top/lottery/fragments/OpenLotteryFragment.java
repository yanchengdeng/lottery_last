package com.top.lottery.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.top.lottery.R;
import com.top.lottery.activities.OpenLotteryRankActivity;
import com.top.lottery.adapters.OpenLotteryCodeAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotterRecord;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.OpenLotteryCode;
import com.top.lottery.utils.AutoVerticalScrollTextViewUtil;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;
import com.top.lottery.views.AutoVerticalScrollTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: 邓言诚  Create at : 2018/12/15  22:35
 * Email: yanchengdeng@gmail.com
 * Describle: v2.0  开奖页
 */
public class OpenLotteryFragment extends Fragment {

    private AutoVerticalScrollTextView autoVerticalScrollTextView;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private OpenLotteryCodeAdapter openLotteryCodeAdapter;
    private int page = 1;
    private AutoVerticalScrollTextViewUtil autoVerticalScrollTextViewUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_lottery_rank, container, false);
        autoVerticalScrollTextView = view.findViewById(R.id.autoVerticalScrollTextView);
        recyclerView = view.findViewById(R.id.recycle);
        smartRefreshLayout = view.findViewById(R.id.refresh);
        openLotteryCodeAdapter = new OpenLotteryCodeAdapter(R.layout.adapter_open_lottery_centre, new ArrayList<OpenLotteryCode>(), true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Utils.context));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(Utils.context));
        recyclerView.setAdapter(openLotteryCodeAdapter);


        openLotteryCodeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getLastestLotteryRank();
            }
        }, recyclerView);


        openLotteryCodeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_OBJECT, openLotteryCodeAdapter.getData().get(position));
                ActivityUtils.startActivity(bundle, OpenLotteryRankActivity.class);
            }
        });


        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                page = 1;
                getData();
            }
        });

        smartRefreshLayout.autoRefresh();
        return view;
    }

    private ArrayList<CharSequence> getStringData(List<LotterRecord> body) {
        ArrayList<CharSequence> strins = new ArrayList<>();


        if (body != null && body.size() > 0) {
            for (int i = 0; i < body.size(); i++) {
                String item = "恭喜：<b><font color=\"#ffefbf\">" + "【" + body.get(i).uid + "】" + "</font></b>投" + body.get(i).lid_title + "中" + "<b><font  color=\"#ffefbf\">" + body.get(i).reward_score + "</font></b>分";
                strins.add(item);
            }
        }
        return strins;
    }

    private void getData() {
        getRewardList();
        getLastestLotteryRank();

    }

    //获取喜报
    private void getRewardList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        OkGo.<LotteryResponse<List<LotterRecord>>>post(Constants.Net.RECORD_GETREWARDLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotterRecord>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotterRecord>>> response) {
                        LogUtils.w("dyc", response);

                        autoVerticalScrollTextViewUtil = new AutoVerticalScrollTextViewUtil(autoVerticalScrollTextView, getStringData(response.body().body));
                        autoVerticalScrollTextViewUtil.start();
                    }


                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });

    }

    private void getLastestLotteryRank() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("page", String.valueOf(page));
        OkGo.<LotteryResponse<List<OpenLotteryCode>>>post(Constants.Net.AWARD_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<OpenLotteryCode>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<OpenLotteryCode>>> response) {
                        List<OpenLotteryCode> openLotteryCodes = response.body().body;
                        smartRefreshLayout.finishRefresh();
                        if (page == 1) {
                            if (openLotteryCodeAdapter.getData() != null && openLotteryCodeAdapter.getData().size() > 0) {
                                openLotteryCodeAdapter.getData().clear();
                            }
                        }
                        if (openLotteryCodes != null && openLotteryCodes.size() > 0) {
                            page++;
                            openLotteryCodeAdapter.addData(openLotteryCodes);
                            openLotteryCodeAdapter.loadMoreComplete();

                        } else {
                            if (openLotteryCodeAdapter.getData() != null && openLotteryCodeAdapter.getData().size() > 0) {
                                openLotteryCodeAdapter.loadMoreComplete();
                                openLotteryCodeAdapter.loadMoreEnd();
                            } else {
                                openLotteryCodeAdapter.setNewData(null);
                                openLotteryCodeAdapter.setEmptyView(RecycleViewUtils.getEmptyView(Utils.context, recyclerView));
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        smartRefreshLayout.finishRefresh();
                        if (openLotteryCodeAdapter.getData() != null && openLotteryCodeAdapter.getData().size() > 0) {
                            openLotteryCodeAdapter.loadMoreComplete();
                            openLotteryCodeAdapter.loadMoreEnd();
                        } else {
                            openLotteryCodeAdapter.setNewData(null);
                            openLotteryCodeAdapter.setEmptyView(RecycleViewUtils.getEmptyView(Utils.context, recyclerView));
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoVerticalScrollTextViewUtil.stop();
    }
}
