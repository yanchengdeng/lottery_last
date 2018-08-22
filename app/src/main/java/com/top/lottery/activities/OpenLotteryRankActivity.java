package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.OpenLotteryCodeAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.OpenLotteryCode;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: 邓言诚  Create at : 2018/8/18  23:20
 * Email: yanchengdeng@gmail.com
 * Describle:开奖榜单
 */
public class OpenLotteryRankActivity extends BaseActivity {

    private int page = 1;

    private RecyclerView recyclerView;
    private OpenLotteryCodeAdapter openLotteryCodeAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lottery_rank);

        setTitle("最新开奖");

        openLotteryCodeAdapter = new OpenLotteryCodeAdapter(R.layout.adapter_open_lottery_centre,new ArrayList<OpenLotteryCode>());
        recyclerView = getView(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        recyclerView.setAdapter(openLotteryCodeAdapter);


        getLastestLotteryRank();
    }

    private void getLastestLotteryRank() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");
        data.put("page_current", String.valueOf(page));
        OkGo.<LotteryResponse<List<OpenLotteryCode>>>post(Constants.Net.AWARD_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<OpenLotteryCode>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<OpenLotteryCode>>> response) {
                        openLotteryCodeAdapter.setNewData(response.body().body);
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }
}
