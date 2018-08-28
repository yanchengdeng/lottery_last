package com.top.lottery.activities;

import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MainPrizeCodeInfo;
import com.top.lottery.beans.TrendSettingInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;

/**
*
* Author: 邓言诚  Create at : 2018/8/23  01:26
* Email: yanchengdeng@gmail.com
* Describle: 走势图
*/
public class TrendChartActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_chart);
        setTitle("走势图");

        getTrendData();

        getTrendSetting();
    }

    //获取走势图设置信息
    private void getTrendSetting() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("setting", "1");//
        OkGo.<LotteryResponse<TrendSettingInfo>>post(Constants.Net.USER_GETTRENDCHARTCONFIG)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<TrendSettingInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<TrendSettingInfo>> response) {
                        TrendSettingInfo trendSettingInfo = response.body().body;


                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));

                    }
                });
    }

    //走势图数据
    private void getTrendData() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");// 彩种
        OkGo.<LotteryResponse<MainPrizeCodeInfo>>post(Constants.Net.AWARD_TRENDCHART)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MainPrizeCodeInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MainPrizeCodeInfo>> response) {
                        MainPrizeCodeInfo mainPrizeCodeInfo = response.body().body;

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));

                    }
                });



    }
}
