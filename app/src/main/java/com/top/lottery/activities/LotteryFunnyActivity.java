package com.top.lottery.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.GridPlayWayAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LasterLotteryAwardInfo;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryPlayWay;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.fragments.LotteryFunnyAnySelectFragment;
import com.top.lottery.fragments.LotteryFunnyPreDirectSelectFragment;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/8/19  15:05
 * Email: yanchengdeng@gmail.com
 * Describle: 彩票娱乐
 */
public class LotteryFunnyActivity extends BaseActivity {


    @BindView(R.id.tv_count_down_tips)
    TextView tvCountDownTips;
    @BindView(R.id.tv_trend_chart)
    TextView tvTrendChart;
    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.grid)
    GridView grid;
    private PopupWindow popupWindow;
    private List<LotteryPlayWay> lotteryPlayWays;
    private String currentLotterTerm;//最新一期投注号2018081430
    private CountDownTimer countDownTimer;
    private LasterLotteryAwardInfo lasterLotteryAwardInfo;
    private  LotteryInfo lotteryInfo;
    public boolean isShowMissValue = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_funny);
        ButterKnife.bind(this);
        setTitle("投注-");
        ivRightFunction.setVisibility(View.VISIBLE);
        ivRightFunction.setImageResource(R.mipmap.more_menu);
        tvTittle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.icon_down_fill), null);
        getLotteryList();
        initPopWindow();


        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setTitle("投注-" + lotteryPlayWays.get(i).title);
                grid.setVisibility(View.GONE);
                getLotteryById(lotteryPlayWays.get(i).lottery_id);
            }
        });

        tvTittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (grid.getVisibility() == View.VISIBLE) {
                    grid.setVisibility(View.GONE);
                } else {
                    grid.setVisibility(View.VISIBLE);
                }
            }
        });


        ivRightFunction.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (popupWindow != null) {
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    } else {
                        popupWindow.showAsDropDown(ivRightFunction, 0, 20);
                    }
                }
            }
        });

        //走势图
        tvTrendChart.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                ActivityUtils.startActivity(TrendChartActivity.class);
            }
        });

    }

    private void getLotteryById(String lottery_id) {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_id", lottery_id);// 彩种
        OkGo.<LotteryResponse<LotteryInfo>>post(Constants.Net.LOTTERY_GETLOTTERYBYID)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<LotteryInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<LotteryInfo>> response) {

                         lotteryInfo = response.body().body;
                        String[] options = lotteryInfo.options.split("[,:\\r\\n]");
                        lotteryInfo.type = Integer.parseInt(options[1]);
                        lotteryInfo.num = Integer.parseInt(options[4]);
                        changeContent();
                    }


                    @Override
                    public void onError(Response response) {
                        showError(Utils.toastInfo(response));
                    }
                });
    }

    /**
     * 更具彩球切换球号模板
     * type://1-任选，2-前一、前二、前三直选等，3-前二、前三组选，4-胆拖任选，6-胆拖组选
     * num://3 球的个数
     */
    private void changeContent() {
        Bundle bundle = new Bundle();
        if (lotteryInfo.type==1){
            bundle.putSerializable(Constants.PASS_OBJECT,lotteryInfo);
            LotteryFunnyAnySelectFragment lotteryFunnyAnySelectFragment = new LotteryFunnyAnySelectFragment();
            lotteryFunnyAnySelectFragment.setArguments(bundle);
            FragmentUtils.replace(getSupportFragmentManager(),lotteryFunnyAnySelectFragment,R.id.fl_content);
        }else if (lotteryInfo.type==2){
            bundle.putSerializable(Constants.PASS_OBJECT,lotteryInfo);
            LotteryFunnyPreDirectSelectFragment lotteryFunnyPreDirectSelectFragment = new LotteryFunnyPreDirectSelectFragment();
            lotteryFunnyPreDirectSelectFragment.setArguments(bundle);
            FragmentUtils.replace(getSupportFragmentManager(),lotteryFunnyPreDirectSelectFragment,R.id.fl_content);
        }else if (lotteryInfo.type==3){

        }

    }


    private void initPopWindow() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_window_more, null);
        popupWindow = new PopupWindow(view, ConvertUtils.dp2px(100), LinearLayout.LayoutParams.WRAP_CONTENT);
//        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setAnimationStyle(0);

        view.findViewById(R.id.tv_show_hide_value).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)view.findViewById(R.id.tv_show_hide_value)).setText(isShowMissValue?"显示遗漏球":"隐藏遗漏球");
                isShowMissValue = ! isShowMissValue;
                doShowHideValue();
                popupWindow.dismiss();
            }
        });

        view.findViewById(R.id.tv_lastest_lottery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                ActivityUtils.startActivity(OpenLotteryRankActivity.class);
            }
        });


        view.findViewById(R.id.tv_play_introduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_NAME,"玩法说明");
                bundle.putString(Constants.PASS_STRING,Constants.Net.WEB_PLAY_WAY);
                ActivityUtils.startActivity(bundle,OpenWebViewActivity.class);
            }
        });
    }

    private void doShowHideValue() {
       Fragment fragment =  FragmentUtils.getTop(getSupportFragmentManager());
       if (fragment instanceof LotteryFunnyAnySelectFragment){
           ((LotteryFunnyAnySelectFragment)fragment).setShowLotteryMiss();
       }
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


    private void getLotteryList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", "1");
        OkGo.<LotteryResponse<List<LotteryPlayWay>>>post(Constants.Net.LOTTERY_GETLOTTERYS)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotteryPlayWay>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotteryPlayWay>>> response) {
                        lotteryPlayWays = response.body().body;
                        if (lotteryPlayWays != null && lotteryPlayWays.size() > 0) {
                            setTitle("投注-" + lotteryPlayWays.get(0).title);
                            grid.setAdapter(new GridPlayWayAdapter(mContext, lotteryPlayWays));
                            grid.setVisibility(View.VISIBLE);
                            getLotteryById(lotteryPlayWays.get(0).lottery_id);
                        }
                    }


                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==200){
            if (resultCode==RESULT_OK){
                grid.setVisibility(View.VISIBLE);
            }else if (resultCode==Constants.BACK_TO_MAIN){
                finish();
            }
        }
    }
}
