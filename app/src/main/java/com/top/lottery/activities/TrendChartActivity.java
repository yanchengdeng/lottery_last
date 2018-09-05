package com.top.lottery.activities;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.TrendListItemAdapter;
import com.top.lottery.adapters.TrendSettingAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LasterLotteryAwardInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.TerdNormalBall;
import com.top.lottery.beans.TrendChartInfo;
import com.top.lottery.beans.TrendCodeInfo;
import com.top.lottery.beans.TrendNormalInfo;
import com.top.lottery.beans.TrendSettingInfo;
import com.top.lottery.beans.TrendSettingValues;
import com.top.lottery.utils.GridSpacingItemDecoration;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/8/23  01:26
 * Email: yanchengdeng@gmail.com
 * Describle: 走势图
 */
public class TrendChartActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_count_down_tips)
    TextView tvCountDownTips;
    //走势图配置信息
    private TrendSettingInfo trendSettingInfo;
    private Dialog dialog;
    private String day, sort, showHide, dismiss;
    private String currentLotterTerm;//最新一期投注号2018081430
    private CountDownTimer countDownTimer;
    private LasterLotteryAwardInfo lasterLotteryAwardInfo;
    private TrendListItemAdapter trendListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_chart);
        ButterKnife.bind(this);
        setTitle("走势图");
        ivRightFunction.setVisibility(View.VISIBLE);
        ivRightFunction.setImageResource(R.mipmap.trend_setting);
        trendListAdapter = new TrendListItemAdapter(R.layout.adapter_trends_item_num, new ArrayList<TerdNormalBall>());
        recycle.setLayoutManager(new GridLayoutManager(this,12));
        recycle.addItemDecoration(new GridSpacingItemDecoration(12,1,false));
        recycle.setAdapter(trendListAdapter);
        ivRightFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    } else {
                        dialog.show();
                    }
                }
            }
        });


        showLoadingBar();
        getTrendSetting();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getLastestLotteryForMain();
    }

    //获取最新可以投注的期数信息
    private void getLastestLotteryForMain() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");// 彩种
        data.put("lottery_page", "1");//类型，1- 投注页面 ；2-走势图页面；3-首页;4-购物车页面
        OkGo.<LotteryResponse<LasterLotteryAwardInfo>>post(Constants.Net.LOTTERY_GETNEWESTAWARDINFO)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<LasterLotteryAwardInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<LasterLotteryAwardInfo>> response) {
                        lasterLotteryAwardInfo = response.body().body;
                        if (lasterLotteryAwardInfo != null) {
                            if (!TextUtils.isEmpty(lasterLotteryAwardInfo.award_id)) {
                                currentLotterTerm = lasterLotteryAwardInfo.award_id;
                            }
                            if (lasterLotteryAwardInfo.status == 1) {
                                isCanTouzhu = true;
                            } else {
                                isCanTouzhu = false;
                            }

                            curretDifServer = getCurrentDifServer(lasterLotteryAwardInfo.server_time, lasterLotteryAwardInfo.current_time);
                        }

                        initStartCountDown();

                    }


                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    private void initStartCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(getCountDownMillions(), 1000) {
            @Override
            public void onTick(long l) {

                if (isCanTouzhu) {
                    tvCountDownTips.setText(String.format(getString(R.string.can_do_until_stop_for_funny), new Object[]{currentLotterTerm, Utils.millis2FitTimeSpan(l, 4)}));
                } else {
                    tvCountDownTips.setText(String.format(getString(R.string.can_do_until_start_for_funny), new Object[]{currentLotterTerm, Utils.millis2FitTimeSpan(l, 4)}));
                }
            }

            @Override
            public void onFinish() {
                getLastestLotteryForMain();
            }
        };

        countDownTimer.start();
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
                        trendSettingInfo = response.body().body;
                        initSetting(trendSettingInfo);
                        getTrendData();
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    //初始化配置
    private void initSetting(TrendSettingInfo trendSettingInfo) {
        day = trendSettingInfo.day;
        sort = trendSettingInfo.sort;
        showHide = trendSettingInfo.show_column;
        dismiss = trendSettingInfo.show_missing;
        if (trendSettingInfo.setting != null) {
            if (trendSettingInfo.setting.day != null) {
                for (TrendSettingValues item : trendSettingInfo.setting.day.list) {
                    if (item.value.equals(trendSettingInfo.day)) {
                        item.isSelected = true;
                    }
                }
            }


            if (trendSettingInfo.setting.sort != null) {
                for (TrendSettingValues item : trendSettingInfo.setting.sort.list) {
                    if (item.value.equals(trendSettingInfo.sort)) {
                        item.isSelected = true;
                    }
                }
            }

            if (trendSettingInfo.show_column != null) {
                for (TrendSettingValues item : trendSettingInfo.setting.show_column.list) {
                    if (item.value.equals(trendSettingInfo.show_column)) {
                        item.isSelected = true;
                    }
                }
            }

            if (trendSettingInfo.show_missing != null) {
                for (TrendSettingValues item : trendSettingInfo.setting.show_missing.list) {
                    if (item.value.equals(trendSettingInfo.show_missing)) {
                        item.isSelected = true;
                    }
                }
            }

            initSettingDialog(trendSettingInfo);

        }


    }

    private void initSettingDialog(TrendSettingInfo trendSettingInfo) {
        dialog = new Dialog(mContext);
        View viewLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_trenda_setting, null, false);
        RecyclerView recyclerViewPerid = viewLayout.findViewById(R.id.recycle_perid);
        recyclerViewPerid.setLayoutManager(new GridLayoutManager(mContext, 4));
        recyclerViewPerid.addItemDecoration(new GridSpacingItemDecoration(4, 16, true));
        final TrendSettingAdapter adapterPerid = new TrendSettingAdapter(R.layout.adapter_trends_setting, trendSettingInfo.setting.day.list);
        recyclerViewPerid.setAdapter(adapterPerid);
        RecyclerView recyclerViewSort = viewLayout.findViewById(R.id.recycle_sort);
        recyclerViewSort.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerViewSort.addItemDecoration(new GridSpacingItemDecoration(2, 30, true));
        final TrendSettingAdapter adapterSort = new TrendSettingAdapter(R.layout.adapter_trends_setting, trendSettingInfo.setting.sort.list);
        recyclerViewSort.setAdapter(adapterSort);
        RecyclerView recyclerViewShowHide = viewLayout.findViewById(R.id.recycle_show_hide);
        recyclerViewShowHide.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerViewShowHide.addItemDecoration(new GridSpacingItemDecoration(2, 30, true));
        final TrendSettingAdapter adaperShowHide = new TrendSettingAdapter(R.layout.adapter_trends_setting, trendSettingInfo.setting.show_column.list);
        recyclerViewShowHide.setAdapter(adaperShowHide);
        RecyclerView recyclerViewMiss = viewLayout.findViewById(R.id.recycle_miss);
        recyclerViewMiss.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerViewMiss.addItemDecoration(new GridSpacingItemDecoration(2, 30, true));
        final TrendSettingAdapter adapermiss = new TrendSettingAdapter(R.layout.adapter_trends_setting, trendSettingInfo.setting.show_missing.list);
        recyclerViewMiss.setAdapter(adapermiss);
        dialog.setContentView(viewLayout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        dialog.getWindow().setLayout(ScreenUtils.getScreenWidth() - 50, WindowManager.LayoutParams.WRAP_CONTENT);


        adapterPerid.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (TrendSettingValues item : adapterPerid.getData()) {
                    item.isSelected = false;
                }
                adapterPerid.getData().get(position).isSelected = true;
                day = adapterPerid.getData().get(position).value;
                adapterPerid.notifyDataSetChanged();
            }
        });


        adapterSort.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (TrendSettingValues item : adapterSort.getData()) {
                    item.isSelected = false;
                }
                adapterSort.getData().get(position).isSelected = true;
                sort = adapterSort.getData().get(position).value;
                adapterSort.notifyDataSetChanged();
            }
        });


        adaperShowHide.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (TrendSettingValues item : adaperShowHide.getData()) {
                    item.isSelected = false;
                }
                adaperShowHide.getData().get(position).isSelected = true;
                showHide = adaperShowHide.getData().get(position).value;
                adaperShowHide.notifyDataSetChanged();
            }
        });


        adapermiss.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (TrendSettingValues item : adapermiss.getData()) {
                    item.isSelected = false;
                }
                adapermiss.getData().get(position).isSelected = true;
                dismiss = adapermiss.getData().get(position).value;
                adapermiss.notifyDataSetChanged();
            }
        });

        viewLayout.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        viewLayout.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setSetting();

            }
        });
    }

    //设置功能
    private void setSetting() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");// 彩种
        data.put("day", day);
        data.put("sort", sort);
        data.put("show_column", showHide);
        data.put("show_missing", dismiss);
        OkGo.<LotteryResponse<String>>post(Constants.Net.USER_SETTRENDCHARTCONFIG)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<String>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<String>> response) {
                        dialog.dismiss();
                        if (response.body().code == 1) {
                            showLoadingBar();
                            getTrendData();
                        } else {
                            ToastUtils.showShort(response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dialog.dismiss();
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    //走势图数据
    private void getTrendData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");// 彩种
        OkGo.<LotteryResponse<TrendChartInfo>>post(Constants.Net.AWARD_TRENDCHART)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<TrendChartInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<TrendChartInfo>> response) {
                        TrendChartInfo trendChartInfo = response.body().body;
                        if (trendChartInfo != null) {
                            parseChartDataToNormal(trendChartInfo);
                        }
                        dismissLoadingBar();

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        dismissLoadingBar();

                    }
                });
    }

    // 转化成可用数据
    private void parseChartDataToNormal(TrendChartInfo trendChartInfo) {


        List<TrendNormalInfo> trendNormalInfos = new ArrayList<>();
        for (TrendCodeInfo trendCodeInfo : trendChartInfo.list) {
            TrendNormalInfo trendNormalInfo = new TrendNormalInfo();
            trendNormalInfo.lists = Utils.get11CodeForTrend(trendCodeInfo,dismiss);
            trendNormalInfos.add(trendNormalInfo);
        }

        TrendNormalInfo numbers = new TrendNormalInfo();
        numbers.lists = Utils.get11CodeForTrendCount("出现次数",trendChartInfo.number);


        TrendNormalInfo laverage_missing = new TrendNormalInfo();
        laverage_missing.lists = Utils.get11CodeForTrendCount("平均遗漏",trendChartInfo.average_missing);

        TrendNormalInfo max_miss = new TrendNormalInfo();
        max_miss.lists = Utils.get11CodeForTrendCount("最大遗漏",trendChartInfo.max_missing);

        TrendNormalInfo max_kits = new TrendNormalInfo();
        max_kits.lists = Utils.get11CodeForTrendCount("最大连击",trendChartInfo.max_double_hits);


        trendNormalInfos.add(numbers);
        trendNormalInfos.add(laverage_missing);
        trendNormalInfos.add(max_miss);
        trendNormalInfos.add(max_kits);


        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();

        for (TrendNormalInfo item:trendNormalInfos){
            terdNormalBalls.addAll(item.lists);
        }


        trendListAdapter.setNewData(terdNormalBalls);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
