package com.top.lottery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.top.lottery.R;
import com.top.lottery.adapters.AwardCodeWinAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LasterLotteryAwardInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MainPrizeCodeInfo;
import com.top.lottery.beans.MainWinCode;
import com.top.lottery.utils.AppManager;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.ScrollLinearLayoutManager;
import com.top.lottery.utils.Utils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: dyc  Create at : 2018/8/12  21:54
 * Email: yanchengdeng@gmail.com
 * Describle: 首页
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_lottery_tittle)
    TextView tvLotteryTittle;
    @BindView(R.id.iv_awards)
    ImageView ivAwards;
    @BindView(R.id.iv_account)
    ImageView ivAccount;
    @BindView(R.id.iv_lottery_funny)
    ImageView ivLotteryFunny;
    @BindView(R.id.iv_trend)
    ImageView ivTrend;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.iv_bottom)
    ImageView ivBottom;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.tv_until_time_tips)
    TextView tvUntilTimeTips;
    boolean isReqeustAwardTime, isRequestNuminfo;
    @BindView(R.id.iv_time_hour_one)
    ImageView ivTimeHourOne;
    @BindView(R.id.iv_time_hour_two)
    ImageView ivTimeHourTwo;
    @BindView(R.id.iv_time_hour_quatation_code)
    ImageView ivTimeHourQuatationCode;
    @BindView(R.id.iv_time_min_one)
    ImageView ivTimeMinOne;
    @BindView(R.id.iv_time_min_two)
    ImageView ivTimeMinTwo;
    @BindView(R.id.iv_time_min_quatation_code)
    ImageView ivTimeMinQuatationCode;
    @BindView(R.id.iv_time_second_one)
    ImageView ivTimeSecondOne;
    @BindView(R.id.iv_time_second_two)
    ImageView ivTimeSecondTwo;
    @BindView(R.id.recycle_one)
    RecyclerView recycleOne;
    @BindView(R.id.recycle_two)
    RecyclerView recycleTwo;
    @BindView(R.id.recycle_thre)
    RecyclerView recycleThre;
    @BindView(R.id.recycle_four)
    RecyclerView recycleFour;
    @BindView(R.id.recycle_five)
    RecyclerView recycleFive;
    private CountDownTimer countDownTimer;
    private MainPrizeCodeInfo mainPrizeCodeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        StatusBarUtil.setLightMode(this);
        mSwipeBackHelper.setSwipeBackEnable(false);
        hideTittle();
        showContentView();


        recycleOne.setLayoutManager(new ScrollLinearLayoutManager(this));

        recycleTwo.setLayoutManager(new ScrollLinearLayoutManager(this));

        recycleThre.setLayoutManager(new ScrollLinearLayoutManager(this));

        recycleFour.setLayoutManager(new ScrollLinearLayoutManager(this));

        recycleFive.setLayoutManager(new ScrollLinearLayoutManager(this));

        initRecycleViewData();

        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {

                initRecycleViewData();
                doMainRequest();
            }
        });


        refresh.autoRefresh();
//        doMainRequest();

    }

    private void initRecycleViewData() {
        recycleOne.setAdapter(new AwardCodeWinAdapter(R.layout.adapter_award_win_item, Utils.get12Code(R.mipmap.ball_01)));
        recycleTwo.setAdapter(new AwardCodeWinAdapter(R.layout.adapter_award_win_item, Utils.get12Code(R.mipmap.ball_02)));
        recycleThre.setAdapter(new AwardCodeWinAdapter(R.layout.adapter_award_win_item, Utils.get12Code(R.mipmap.ball_03)));
        recycleFour.setAdapter(new AwardCodeWinAdapter(R.layout.adapter_award_win_item, Utils.get12Code(R.mipmap.ball_04)));
        recycleFive.setAdapter(new AwardCodeWinAdapter(R.layout.adapter_award_win_item, Utils.get12Code(R.mipmap.ball_05)));
        recycleOne.smoothScrollToPosition(0);
        recycleTwo.smoothScrollToPosition(0);
        recycleThre.smoothScrollToPosition(0);
        recycleFour.smoothScrollToPosition(0);
        recycleFive.smoothScrollToPosition(0);
    }


    private void doMainRequest() {
        getLastestLotteryForMain();
        getlastestLotteryPrizeAward();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        doMainRequest();
    }

    @OnClick({R.id.iv_awards, R.id.iv_account, R.id.iv_lottery_funny, R.id.iv_trend, R.id.iv_record, R.id.iv_bottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_awards:
                ActivityUtils.startActivity(OpenLotteryRankActivity.class);
                break;
            case R.id.iv_account:
                ActivityUtils.startActivityForResult(this, PercentInfoActivity.class, 200);
                break;
            case R.id.iv_lottery_funny:
                ActivityUtils.startActivity(LotteryFunnyActivity.class);
                break;
            case R.id.iv_trend:
                ActivityUtils.startActivity(TrendChartActivity.class);
                break;
            case R.id.iv_record:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_STRING, "1");
                Utils.openActivity(mContext, BuyLotteryRecordActivity.class, bundle);
                break;
            case R.id.iv_bottom:
                break;
        }
    }


    //获取最新可以投注的期数信息
    private void getLastestLotteryForMain() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");// 彩种
        data.put("lottery_page", "3");//类型，1- 投注页面 ；2-走势图页面；3-首页;4-购物车页面
        OkGo.<LotteryResponse<LasterLotteryAwardInfo>>post(Constants.Net.LOTTERY_GETNEWESTAWARDINFO)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<LasterLotteryAwardInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<LasterLotteryAwardInfo>> response) {
                        isReqeustAwardTime = true;
                        LasterLotteryAwardInfo lasterLotteryAwardInfo = response.body().body;
                        if (lasterLotteryAwardInfo != null) {
                            Constants.LASTER_AWARD_END_TIME = lasterLotteryAwardInfo.current_time;
                            Constants.LASTEST_AWARD_ID = lasterLotteryAwardInfo.award_id;
                            isCanTouzhu = lasterLotteryAwardInfo.status == 1;
                            if (lasterLotteryAwardInfo.status == 1) {
                                tvUntilTimeTips.setText(getString(R.string.can_do_until_stop_for_main));
                            } else {
                                tvUntilTimeTips.setText(getString(R.string.can_do_until_start_for_main));
                            }

                            //todo  测试
//                            lasterLotteryAwardInfo.server_time = "2018-09-09 08:11:21";
//                            lasterLotteryAwardInfo.current_time = "2018-09-09 08:16:00";
                            curretDifServer = getCurrentDifServer(lasterLotteryAwardInfo.server_time, lasterLotteryAwardInfo.current_time);

                            initStartCountDown();
                        }
                        finishRequest();

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        isReqeustAwardTime = true;
                        refresh.finishRefresh();
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
                    setMainTime(Utils.millis2FitTimeSpanForMain(l, 4));
                } else {
                    setMainTime(Utils.millis2FitTimeSpanForMain(l, 4));
                }
            }

            @Override
            public void onFinish() {
                doMainRequest();
            }
        };

        countDownTimer.start();
    }

    private void setMainTime(int[] times) {
        if (times.length == 4) {
            ivTimeMinOne.setImageResource(getTimeResourseByNum(times[0]));
            ivTimeMinTwo.setImageResource(getTimeResourseByNum(times[1]));
            ivTimeSecondOne.setImageResource(getTimeResourseByNum(times[2]));
            ivTimeSecondTwo.setImageResource(getTimeResourseByNum(times[3]));
            ivTimeMinOne.setVisibility(View.VISIBLE);
            ivTimeMinTwo.setVisibility(View.VISIBLE);
            ivTimeSecondOne.setVisibility(View.VISIBLE);
            ivTimeSecondTwo.setVisibility(View.VISIBLE);
            ivTimeMinQuatationCode.setVisibility(View.VISIBLE);
            ivTimeHourOne.setVisibility(View.GONE);
            ivTimeHourTwo.setVisibility(View.GONE);
            ivTimeHourQuatationCode.setVisibility(View.GONE);
        } else if (times.length == 6) {
            ivTimeHourOne.setImageResource(getTimeResourseByNum(times[0]));
            ivTimeHourTwo.setImageResource(getTimeResourseByNum(times[1]));
            ivTimeMinOne.setImageResource(getTimeResourseByNum(times[2]));
            ivTimeMinTwo.setImageResource(getTimeResourseByNum(times[3]));
            ivTimeSecondOne.setImageResource(getTimeResourseByNum(times[4]));
            ivTimeSecondTwo.setImageResource(getTimeResourseByNum(times[5]));
            ivTimeMinOne.setVisibility(View.VISIBLE);
            ivTimeMinTwo.setVisibility(View.VISIBLE);
            ivTimeSecondOne.setVisibility(View.VISIBLE);
            ivTimeSecondTwo.setVisibility(View.VISIBLE);
            ivTimeHourOne.setVisibility(View.VISIBLE);
            ivTimeHourTwo.setVisibility(View.VISIBLE);
            ivTimeHourQuatationCode.setVisibility(View.VISIBLE);
            ivTimeMinQuatationCode.setVisibility(View.VISIBLE);
        } else {
            ivTimeMinOne.setVisibility(View.GONE);
            ivTimeMinTwo.setVisibility(View.GONE);
            ivTimeSecondOne.setVisibility(View.GONE);
            ivTimeSecondTwo.setVisibility(View.GONE);
            ivTimeHourOne.setVisibility(View.GONE);
            ivTimeHourTwo.setVisibility(View.GONE);
            ivTimeHourQuatationCode.setVisibility(View.GONE);
            ivTimeMinQuatationCode.setVisibility(View.VISIBLE);
        }

    }

    private int getTimeResourseByNum(int time) {
        switch (time) {
            case 0:
                return R.mipmap.time_zero;
            case 1:
                return R.mipmap.time_one;
            case 2:
                return R.mipmap.time_two;
            case 3:
                return R.mipmap.time_three;
            case 4:
                return R.mipmap.time_four;
            case 5:
                return R.mipmap.time_five;
            case 6:
                return R.mipmap.time_six;
            case 7:
                return R.mipmap.time_seven;
            case 8:
                return R.mipmap.time_eight;
            case 9:
                return R.mipmap.time_nine;
            default:
                return R.mipmap.time_zero;
        }

    }


    private void finishRequest() {
        if (isReqeustAwardTime && isRequestNuminfo) {
            refresh.finishRefresh();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doLoopCodes();
                }
            }, 1200);
        }
    }


    //循环球操作
    private void doLoopCodes() {
        if (mainPrizeCodeInfo!=null && !TextUtils.isEmpty(mainPrizeCodeInfo.prize_code)) {
            final String codes[] = mainPrizeCodeInfo.prize_code.split(" ");
            if (codes != null && codes.length == 5) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleOne.smoothScrollToPosition(getRecyclePositon(codes[0]));
                    }
                }, (long) ScrollLinearLayoutManager.MILLISECONDS_PER_INCH);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleTwo.smoothScrollToPosition(getRecyclePositon(codes[1]));
                    }
                }, (long) (ScrollLinearLayoutManager.MILLISECONDS_PER_INCH * 2));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleThre.smoothScrollToPosition(getRecyclePositon(codes[2]));
                    }
                }, (long) (ScrollLinearLayoutManager.MILLISECONDS_PER_INCH * 3));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleFour.smoothScrollToPosition(getRecyclePositon(codes[3]));
                    }
                }, (long) (ScrollLinearLayoutManager.MILLISECONDS_PER_INCH * 4));


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recycleFive.smoothScrollToPosition(getRecyclePositon(codes[4]));
                    }
                }, (long) (ScrollLinearLayoutManager.MILLISECONDS_PER_INCH * 5));
            }
        }
    }


    //获取最新的开奖期数的信息，放在首页
    private void getlastestLotteryPrizeAward() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");// 彩种
        OkGo.<LotteryResponse<MainPrizeCodeInfo>>post(Constants.Net.LOTTERY_GETNEWESTPRIZEAWARD)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MainPrizeCodeInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MainPrizeCodeInfo>> response) {
                        mainPrizeCodeInfo = response.body().body;
                        if (mainPrizeCodeInfo != null) {
                            if (!TextUtils.isEmpty(mainPrizeCodeInfo.id)) {
                                if (!TextUtils.isEmpty(mainPrizeCodeInfo.id)) {
                                    tvLotteryTittle.setText(String.format(getString(R.string.main_open_award), mainPrizeCodeInfo.id
                                    ));
                                }
                            }
                        }
                        isRequestNuminfo = true;
                        finishRequest();

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        isRequestNuminfo = true;
                        finishRequest();
                    }
                });
    }

    private int getRecyclePositon(String code) {
        int posotin = 0;
        List<MainWinCode> mainWinCodes = Utils.get12Code(0);
        for (int i = 0; i < mainWinCodes.size(); i++) {
            if (mainWinCodes.get(i).code.equals(code)) {
                posotin = i;
            }
        }
        return posotin;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == -1) {
                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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
            AppManager.getAppManager().AppExit(this);
            finish();
        }
        return false;
    }
}
