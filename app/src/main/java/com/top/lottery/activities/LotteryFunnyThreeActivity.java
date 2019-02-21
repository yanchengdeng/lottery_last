package com.top.lottery.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.PlayWayAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LasterLotteryAwardInfo;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryPlayWay;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.fragments.LotteryFunnyThreeDanTuoFragment;
import com.top.lottery.fragments.LotteryFunnyThreeSelectFragment;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;
import com.top.lottery.views.BubblePopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/12/16  01:54
 * Email: yanchengdeng@gmail.com
 * Describle: 快三投注
 */
public class LotteryFunnyThreeActivity extends BaseActivity {


    @BindView(R.id.tv_count_down_tips)
    TextView tvCountDownTips;
    @BindView(R.id.fl_content)
    FrameLayout flContent;
    private PopupWindow popupWindow;
    private List<LotteryPlayWay> lotteryPlayWays;
    private String currentLotterTerm;//最新一期投注号2018081430
    private CountDownTimer countDownTimer;
    private LasterLotteryAwardInfo lasterLotteryAwardInfo;
    private LotteryInfo lotteryInfo;
    public boolean isShowMissValue = false;
    private PlayWayAdapter playWayAdapter;
    private LotteryType lotteryType;
    private String lottery_id_old = "0";
    private BubblePopup quickPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_funny);
        setTitle("投注-");
        ButterKnife.bind(this);
        lotteryType = (LotteryType) getIntent().getSerializableExtra(Constants.PASS_LOOTERY_TYPE);
        if (lotteryType == null) {
            return;
        }
        ivRightFunction.setVisibility(View.VISIBLE);
        ivRightFunction.setImageResource(R.mipmap.more_menu);
        playWayAdapter = new PlayWayAdapter(R.layout.adapter_play_way_centre, new ArrayList<LotteryPlayWay>());
        tvTittle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.arrow_up_down_white), null);
        getLotteryList();
        initPopWindow();


        playWayAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                setTitle("投注-" + lotteryPlayWays.get(position).title);
                showLotterPlus();
                if (!lotteryPlayWays.get(position).lottery_id.equals(lottery_id_old)) {
                    getLotteryById(lotteryPlayWays.get(position).lottery_id);
                    lottery_id_old = lotteryPlayWays.get(position).lottery_id;
                }
            }
        });


        tvTittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLotterPlus();
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


        getLastestLotteryForMain();
    }

    private void showLotterPlus() {

        if (quickPopup==null) {
            quickPopup = new BubblePopup(mContext);
            RecyclerView recyclerView = quickPopup.getContentView();
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
            recyclerView.addItemDecoration(RecycleViewUtils.getItemDecorationHorizontal(this));
            recyclerView.setAdapter(playWayAdapter);
        }

        if (quickPopup.isShowing()) {
            quickPopup.dismiss();
        } else {
            quickPopup.showPopupWindow(tittleUi);
        }
    }

    private void getLotteryById(final String lottery_id) {

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
                        lotteryInfo.lid = lotteryType.lid;
                        Constants.CURRENT_LID = lotteryType.lid;
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
        if (lotteryType.lottery_type.equals("2")) {
            if (lotteryInfo.type == 5 || lotteryInfo.type == 9 || lotteryInfo.type == 10) {
                bundle.putSerializable(Constants.PASS_OBJECT, lotteryInfo);
                LotteryFunnyThreeDanTuoFragment lotteryFunnyDanTuoSelectFragment = new LotteryFunnyThreeDanTuoFragment();
                lotteryFunnyDanTuoSelectFragment.setArguments(bundle);
                FragmentUtils.replace(getSupportFragmentManager(), lotteryFunnyDanTuoSelectFragment, R.id.fl_content);
            } else {
                bundle.putSerializable(Constants.PASS_OBJECT, lotteryInfo);
                LotteryFunnyThreeSelectFragment lotteryFunnyThreeSelectFragment = new LotteryFunnyThreeSelectFragment();
                lotteryFunnyThreeSelectFragment.setArguments(bundle);
                FragmentUtils.replace(getSupportFragmentManager(), lotteryFunnyThreeSelectFragment, R.id.fl_content);
            }
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
                ((TextView) view.findViewById(R.id.tv_show_hide_value)).setText(isShowMissValue ? "显示遗漏球" : "隐藏遗漏球");
                isShowMissValue = !isShowMissValue;
                doShowHideValue();
                popupWindow.dismiss();
            }
        });

        view.findViewById(R.id.tv_lastest_lottery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_STRING, lotteryType.lottery_type);
                ActivityUtils.startActivity(bundle, OpenLotteryRankActivity.class);
            }
        });


        view.findViewById(R.id.tv_play_introduce).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_NAME, "玩法说明");
                bundle.putString(Constants.PASS_STRING, Constants.Net.WEB_PLAY_WAY_THREE);
                ActivityUtils.startActivity(bundle, OpenWebViewActivity.class);
            }
        });
    }

    private void doShowHideValue() {
        Fragment fragment = FragmentUtils.getTop(getSupportFragmentManager());
        if (fragment instanceof LotteryFunnyThreeDanTuoFragment) {
            ((LotteryFunnyThreeDanTuoFragment) fragment).setShowLotteryMiss();
        } else if (fragment instanceof LotteryFunnyThreeSelectFragment) {
            ((LotteryFunnyThreeSelectFragment) fragment).setShowLotteryMiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        getLastestLotteryForMain();
    }

    //获取最新可以投注的期数信息
    private void getLastestLotteryForMain() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", lotteryType.lottery_type);// 彩种
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
                                Constants.LASTEST_AWARD_ID_THREE = lasterLotteryAwardInfo.award_id;
                                Constants.LASTER_AWARD_END_TIME_THREE = lasterLotteryAwardInfo.current_time;
                                Constants.TIME_CAN_NOT_TOUZHU = lasterLotteryAwardInfo.count_down*1000;
                                Constants.TIME_BUY_TIME = lasterLotteryAwardInfo.buy_time*1000;
                            }
                            if (lasterLotteryAwardInfo.status == 1) {
                                currentLotterTerm = lasterLotteryAwardInfo.award_id;
                                isCanTouzhu = true;
                            } else {
                                isCanTouzhu = false;
                                currentLotterTerm = lasterLotteryAwardInfo.next_award_id;
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
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryType.lid);
        OkGo.<LotteryResponse<List<LotteryPlayWay>>>post(Constants.Net.LOTTERY_GETLOTTERYS)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotteryPlayWay>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotteryPlayWay>>> response) {
                        lotteryPlayWays = response.body().body;
                        dismissLoadingBar();
                        if (lotteryPlayWays != null && lotteryPlayWays.size() > 0) {
                            setTitle("投注-" + lotteryPlayWays.get(0).title);
                            playWayAdapter.setNewData(lotteryPlayWays);
                            showLotterPlus();
                            getLotteryById(lotteryPlayWays.get(0).lottery_id);
                        }
                    }


                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
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
    public void onBackPressed() {
            if (quickPopup!=null) {
                if (quickPopup.isShowing()){
                    quickPopup.dismiss();
                }else{
                    super.onBackPressed();
                }
            }else {
                super.onBackPressed();
            }
    }
}
