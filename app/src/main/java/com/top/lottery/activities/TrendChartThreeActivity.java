package com.top.lottery.activities;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.AwardDialogKindsAdapter;
import com.top.lottery.adapters.AwardThreeBallChartAdapter;
import com.top.lottery.adapters.TrendListItem3Adapter;
import com.top.lottery.adapters.TrendSettingAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.beans.CheckSelectCodeInfo;
import com.top.lottery.beans.LasterLotteryAwardInfo;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryPlayWay;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.TerdNormalBall;
import com.top.lottery.beans.TrendChartInfo;
import com.top.lottery.beans.TrendCodeInfo;
import com.top.lottery.beans.TrendNormalInfo;
import com.top.lottery.beans.TrendSettingInfo;
import com.top.lottery.beans.TrendSettingValues;
import com.top.lottery.events.NoticeToDoNewTermCodeEvent;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.GridSpacingItemDecoration;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/8/23  01:26
 * Email: yanchengdeng@gmail.com
 * Describle: 走势图 快三
 */
public class TrendChartThreeActivity extends BaseActivity {


    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_count_down_tips)
    TextView tvCountDownTips;
    @BindView(R.id.tv_lottery_name)
    TextView tvLotteryName;
    @BindView(R.id.tv_award_vluese)
    TextView tvAwardVluese;
    @BindView(R.id.tv_touzhu_action)
    TextView tvTouzhuAction;
    @BindView(R.id.ll_ui_touzhu)
    LinearLayout llUiTouzhu;
    @BindView(R.id.tv_fisrt_ball_name)
    TextView tvFisrtBallName;
    @BindView(R.id.recycle_one_ball)
    RecyclerView recycleOneBall;
    @BindView(R.id.ll_first_balls)
    LinearLayout llFirstBalls;
    @BindView(R.id.tv_sencond_ball_name)
    TextView tvSencondBallName;
    @BindView(R.id.recycle_two_ball)
    RecyclerView recycleTwoBall;
    @BindView(R.id.ll_second_balls)
    LinearLayout llSecondBalls;
    //走势图配置信息
    private TrendSettingInfo trendSettingInfo;
    private Dialog dialogSetting, dialogAward;
    private String day, sort, showHide, dismiss;
    private String currentLotterTerm;//最新一期投注号2018081430
    private CountDownTimer countDownTimer;
    private LasterLotteryAwardInfo lasterLotteryAwardInfo;
    private TrendListItem3Adapter trendListAdapter,trendListBottomAdapter;
    private List<LotteryPlayWay> lotteryPlayWays;
    private LotteryPlayWay lotteryPlayWaySelect;//当前选择的彩种
    private AwardDialogKindsAdapter awardDialogKindsAdapter;//弹出彩种对话框
    private LotteryInfo lotteryInfopASS;
    private AwardThreeBallChartAdapter awardThreeBallOneAdapter, awardThreeBallTwoAdapter;
    private View chatBottomView;
    private RecyclerView recycleBottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_3_chart);
        ButterKnife.bind(this);
        setTitle("快三走势图");
        ivRightFunction.setVisibility(View.VISIBLE);
        ivRightFunction.setImageResource(R.mipmap.trend_setting);
        chatBottomView = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_3_bottom,null,false);
        trendListAdapter = new TrendListItem3Adapter(R.layout.adapter_trends_3_item_num, new ArrayList<TerdNormalBall>());
        trendListBottomAdapter = new TrendListItem3Adapter(R.layout.adapter_trends_3_item_num_bottom,new ArrayList<TerdNormalBall>());

        recycleBottom = chatBottomView.findViewById(R.id.recycle);
        recycleBottom.setLayoutManager(new GridLayoutManager(this,6));
        recycleBottom.addItemDecoration(new GridSpacingItemDecoration(6, 1, false));
        recycleBottom.setAdapter(trendListBottomAdapter);
        chatBottomView.setVisibility(View.INVISIBLE);

        trendListAdapter.addFooterView(chatBottomView);

        recycle.setLayoutManager(new GridLayoutManager(this, 12));
        tvLotteryName.setText("和值");
        recycle.addItemDecoration(new GridSpacingItemDecoration(12, 1, false));
        recycle.setAdapter(trendListAdapter);
        lotteryInfopASS = (LotteryInfo) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        awardThreeBallOneAdapter = new AwardThreeBallChartAdapter(R.layout.adapter_lottery_three_chart_select_num, new ArrayList<AwardBallInfo>());
        recycleOneBall.setAdapter(awardThreeBallOneAdapter);

        awardThreeBallTwoAdapter = new AwardThreeBallChartAdapter(R.layout.adapter_lottery_three_chart_select_num, new ArrayList<AwardBallInfo>());
        recycleTwoBall.setAdapter(awardThreeBallTwoAdapter);

        ivRightFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSetting != null) {
                    if (dialogSetting.isShowing()) {
                        dialogSetting.dismiss();
                    } else {
                        dialogSetting.show();
                    }
                }
            }
        });


        tvTouzhuAction.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {

                doTouZhuAction();
            }
        });

        awardThreeBallOneAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (lotteryInfo != null) {
                    if (lotteryInfo.type == 5 || lotteryInfo.type == 9 || lotteryInfo.type == 10) {
                        if (lotteryInfo.type == 5) {

                            boolean isSelected = awardThreeBallOneAdapter.getData().get(position).isSelected;
                            awardThreeBallOneAdapter.getData().get(position).isSelected = !isSelected;
                            awardThreeBallTwoAdapter.getData().get(position).isSelected = isSelected;
                            awardThreeBallOneAdapter.notifyDataSetChanged();
                            awardThreeBallTwoAdapter.notifyDataSetChanged();
                        } else {
                            if (getSelectBallsDan().size() > lotteryInfo.num - 1) {
                                ToastUtils.showShort("最多只能选择" + getSelectBallsDan().size() + "个胆码");
                                return;
                            }
                            awardThreeBallOneAdapter.getData().get(position).isSelected = !awardThreeBallOneAdapter.getData().get(position).isSelected;
                            if (awardThreeBallOneAdapter.getData().get(position).isSelected) {
                                awardThreeBallTwoAdapter.getData().get(position).isSelected = false;
                                awardThreeBallTwoAdapter.notifyItemChanged(position);
                            }
                            awardThreeBallOneAdapter.notifyItemChanged(position);
                        }
                        countIntergaryDanTuo();

                    } else {
                        awardThreeBallOneAdapter.getData().get(position).isSelected = !awardThreeBallOneAdapter.getData().get(position).isSelected;
                        awardThreeBallOneAdapter.notifyItemChanged(position);
                        countIntergary();
                    }

                }
            }
        });


        //胆拖   不同号
        awardThreeBallTwoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (lotteryInfo != null) {
                    if (lotteryInfo.type == 5 || lotteryInfo.type == 9 || lotteryInfo.type == 10) {
                        if (lotteryInfo.type == 5) {
                            boolean isSelected = awardThreeBallTwoAdapter.getData().get(position).isSelected;
                            awardThreeBallTwoAdapter.getData().get(position).isSelected = !isSelected;
                            awardThreeBallOneAdapter.getData().get(position).isSelected = isSelected;
                            awardThreeBallOneAdapter.notifyDataSetChanged();
                            awardThreeBallTwoAdapter.notifyDataSetChanged();
                        } else {
                            awardThreeBallTwoAdapter.getData().get(position).isSelected = !awardThreeBallTwoAdapter.getData().get(position).isSelected;
                            if (awardThreeBallTwoAdapter.getData().get(position).isSelected) {
                                awardThreeBallOneAdapter.getData().get(position).isSelected = false;
                                awardThreeBallOneAdapter.notifyItemChanged(position);
                            }
                            awardThreeBallTwoAdapter.notifyItemChanged(position);
                        }
                    }
                }
                countIntergaryDanTuo();
            }
        });


        //弹出彩种
        tvLotteryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogAward != null && lotteryPlayWays != null && lotteryPlayWays.size() > 0) {
                    dialogAward.show();
                } else {
                    getAwardKinds();
                }
            }
        });
        showLoadingBar();
        getTrendSetting();
        getAwardKinds();
    }


    //已选择胆的球数
    private List<AwardBallInfo> getSelectBallsDan() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardThreeBallOneAdapter != null && awardThreeBallOneAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awardThreeBallOneAdapter.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }


        return awardBallInfos;
    }

    //已选择拖的球数
    private List<AwardBallInfo> getSelectBallsTuo() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardThreeBallTwoAdapter != null && awardThreeBallTwoAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awardThreeBallTwoAdapter.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }


    //计算积分 一注  等于 2个积分
    private void countIntergary() {
        if (lotteryInfo == null) {
            return;
        }
        lotteryInfo.codes = getSelectBallsCode();
        lotteryInfo.TOUZHU_COUNT = Utils.combination(getSelectBallsSingle().size(), lotteryInfo.num);
        lotteryInfo.TOUZHU_INTEGRY = 2 * Utils.combination(getSelectBallsSingle().size(), lotteryInfo.num);
        tvAwardVluese.setText("积分" + lotteryInfo.TOUZHU_COUNT + "  共" + lotteryInfo.TOUZHU_INTEGRY + "注");
    }


    //计算胆拖
    private void countIntergaryDanTuo() {
        if (lotteryInfo == null) {
            return;
        }
        lotteryInfo.codes = getSelectBallsCode();
        lotteryInfo.TOUZHU_COUNT = getSelectBallsTuo().size() * getSelectBallsDan().size();
        lotteryInfo.TOUZHU_INTEGRY = 2 * lotteryInfo.TOUZHU_COUNT;

        tvAwardVluese.setText("积分" + lotteryInfo.TOUZHU_COUNT + "  共" + lotteryInfo.TOUZHU_INTEGRY + "注");

    }


    //获取所选球号
    private String[][] getSelectBallsCode() {
        List<AwardBallInfo> awardBallInfos = getSelectBallsSingle();
        String[] codes = new String[awardBallInfos.size()];
        for (int i = 0; i < awardBallInfos.size(); i++) {
            codes[i] = awardBallInfos.get(i).value;
        }

        String[][] arrays = new String[1][1];
        arrays[0] = codes;
        return arrays;
    }



    //已选择的球数
    private List<AwardBallInfo> getSelectBallsSingle() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardThreeBallOneAdapter != null && awardThreeBallOneAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awardThreeBallOneAdapter.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }


    public  boolean   isSingleCode(){
        if (lotteryInfo.type==5 ||lotteryInfo.type==9 || lotteryInfo.type==10){
            return  false;
        }else{
            return  true;
        }

    }



    //获取所选球号
    private String[][] getSelectBallsCodeDanTuo() {
        List<AwardBallInfo> awardBallDans = getSelectBallsDan();
        String[] codesDan = new String[awardBallDans.size()];
        for (int i = 0; i < awardBallDans.size(); i++) {
            codesDan[i] = awardBallDans.get(i).value;
        }


        List<AwardBallInfo> awardBallTuos = getSelectBallsTuo();
        String[] codesTuo = new String[awardBallTuos.size()];
        for (int i = 0; i < awardBallTuos.size(); i++) {
            codesTuo[i] = awardBallTuos.get(i).value;
        }

        String[][] arrays = new String[2][1];
        arrays[0] = codesDan;
        arrays[1] = codesTuo;

        return arrays;
    }

    //做投注操作
    private void doTouZhuAction() {
        if (lotteryInfo != null) {
            if (lotteryInfo.type == 5 || lotteryInfo.type == 9 || lotteryInfo.type == 10) {
                checkSelectDanTuo();
            } else {
                checkSelectOne();
            }
        } else {
            ToastUtils.showShort("请选择彩种");
        }
    }

    private void checkSelectDanTuo() {
        if (getSelectBallsDan().size() == 0) {
            if (lotteryInfo.type == 5) {
                ToastUtils.showShort("至少选择一个同号数值");
            } else {
                ToastUtils.showShort("至少选择一个胆码");
            }

        } else if (getSelectBallsTuo().size() == 0) {
            if (lotteryInfo.type == 5) {
                ToastUtils.showShort("至少选择一个不同号数值");
            } else {
                ToastUtils.showShort("至少选择一个拖码");
            }
        } else {
            showLoadingBar();
            checkAwardId();
        }
    }


    //检查选择一个球的流程
    private void checkSelectOne() {
        if (getSelectBallsDan().size() >= lotteryInfo.num) {
            showLoadingBar();
            checkAwardId();
        } else {
            ToastUtils.showShort("至少选择" + lotteryInfo.num + "球");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoticeToDoNewTermCodeEvent event) {
        if (event != null) {
            String className = event.className;
            String methodName = event.methodName;
            if (className.equals(TrendChartThreeActivity.class.getName())) {
                doTouZhuAction();
            }
        }
    }


    //获取选彩种类
    private void getAwardKinds() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryInfopASS.lid);
        OkGo.<LotteryResponse<List<LotteryPlayWay>>>post(Constants.Net.LOTTERY_GETLOTTERYS)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotteryPlayWay>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotteryPlayWay>>> response) {
                        lotteryPlayWays = response.body().body;
                        if (lotteryPlayWays != null && lotteryPlayWays.size() > 0) {
                            tvLotteryName.setText(lotteryPlayWays.get(0).title);
                            initShowLotterDialog();

                        }
                    }


                    @Override
                    public void onError(Response response) {
                        String toast = response.getException().getMessage();
                        LotteryResponse info = new Gson().fromJson(toast, LotteryResponse.class);
                        if (info != null) {
                            if (info.code == -3 || info.code == -2) {
                                llUiTouzhu.setVisibility(View.GONE);
                            } else {
//                                ToastUtils.showShort(Utils.toastInfo(response));
                            }
                        }
                    }
                });
    }

    //初始化彩票数据
    private void initShowLotterDialog() {
        dialogAward = new Dialog(mContext);

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_lottery_kinds, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycle_lottery);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 20, false));
        lotteryPlayWays.get(0).isSelect = true;
        recyclerView.setAdapter(awardDialogKindsAdapter = new AwardDialogKindsAdapter(R.layout.adapter_trends_dialog_lottery_kinds, lotteryPlayWays));

        dialogAward.setContentView(view);
        dialogAward.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialogAward.getWindow().getAttributes();
        params.width = ScreenUtils.getScreenWidth() - 50;
//        params.height = ScreenUtils.getScreenHeight() - 200;
        dialogAward.getWindow().setAttributes(params);
        lotteryPlayWaySelect = lotteryPlayWays.get(0);
        getLotteryById(lotteryPlayWaySelect.lottery_id);
        awardDialogKindsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (LotteryPlayWay item : lotteryPlayWays) {
                    item.isSelect = false;
                }
                initAllAwardsBall();
                lotteryPlayWays.get(position).isSelect = true;
                tvLotteryName.setText("" + lotteryPlayWays.get(position).title);
                lotteryPlayWaySelect = lotteryPlayWays.get(position);
                getLotteryById(lotteryPlayWaySelect.lottery_id);
                awardDialogKindsAdapter.notifyDataSetChanged();
                dialogAward.dismiss();
            }
        });
    }

    //初始化所有已选ball
    private void initAllAwardsBall() {
        if (awardThreeBallOneAdapter.getData() != null) {
            for (AwardBallInfo item : awardThreeBallOneAdapter.getData()) {
                item.isSelected = false;
            }
            awardThreeBallOneAdapter.notifyDataSetChanged();
        }
        if (awardThreeBallTwoAdapter.getData() != null) {
            for (AwardBallInfo item : awardThreeBallTwoAdapter.getData()) {
                item.isSelected = false;
            }
            awardThreeBallTwoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * /**
     * 更具彩球切换球号模板
     * type://1-任选，2-前一、前二、前三直选等，3-前二、前三组选，4-胆拖任选，6-胆拖组选
     * num://3 球的个数
     */

    private LotteryInfo lotteryInfo;

    private void getLotteryById(final String lottery_id) {

        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_id", lottery_id);// 彩种
        OkGo.<LotteryResponse<LotteryInfo>>post(Constants.Net.LOTTERY_GETLOTTERYBYID)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<LotteryInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<LotteryInfo>> response) {
                        dismissLoadingBar();
                        lotteryInfo = response.body().body;
                        String[] options = lotteryInfo.options.split("[,:\\r\\n]");
                        lotteryInfo.type = Integer.parseInt(options[1]);
                        lotteryInfo.num = Integer.parseInt(options[4]);
                        lotteryInfo.lid = lotteryInfopASS.lid;


                        int spancout = Utils.getSpanCountForChart(lotteryInfo.type);
                        recycleOneBall.setLayoutManager(new GridLayoutManager(mContext, spancout));
                        recycleTwoBall.setLayoutManager(new GridLayoutManager(mContext, spancout));
//                        recycleOneBall.addItemDecoration(new GridSpacingItemDecoration(spancout,16,true));


                        if (lotteryInfo.type == 5 || lotteryInfo.type == 9 || lotteryInfo.type == 10) {
                            if (lotteryInfo.type == 5) {
                                awardThreeBallOneAdapter.setNewData(Utils.parseThreeCodesDoule(lotteryInfo.score));
                                tvFisrtBallName.setText("同号");
                                tvSencondBallName.setText("不同");
                            } else {
                                awardThreeBallOneAdapter.setNewData(Utils.parseThreeCodes(lotteryInfo, lotteryInfo.score));
                                tvFisrtBallName.setText("胆码");
                                tvSencondBallName.setText("拖码");
                            }
                            awardThreeBallTwoAdapter.setNewData(Utils.parseThreeCodes(lotteryInfo, lotteryInfo.score));
                            llSecondBalls.setVisibility(View.VISIBLE);
                        } else {
                            awardThreeBallOneAdapter.setNewData(Utils.parseThreeCodes(lotteryInfo, lotteryInfo.score));
                            llSecondBalls.setVisibility(View.GONE);
                            tvFisrtBallName.setText("选号");
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        showError(Utils.toastInfo(response));
                        dismissLoadingBar();
                    }
                });
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
        data.put("lottery_type", lotteryInfopASS.lottery_type);// 彩种
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
                        tvCountDownTips.setVisibility(View.VISIBLE);

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

        if (showHide.equals("hide")) {
            llUiTouzhu.setVisibility(View.GONE);
        } else {
            llUiTouzhu.setVisibility(View.VISIBLE);
        }

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
                if (trendSettingInfo.setting.show_column != null && trendSettingInfo.setting.show_column.list != null) {
                    for (TrendSettingValues item : trendSettingInfo.setting.show_column.list) {
                        if (item.value.equals(trendSettingInfo.show_column)) {
                            item.isSelected = true;
                        }
                    }
//                    llUiTouzhu.setVisibility(View.VISIBLE);
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

//            if (lotteryInfo == null) {
//                llUiTouzhu.setVisibility(View.GONE);
//            }

        }


    }

    private void initSettingDialog(TrendSettingInfo trendSettingInfo) {
        dialogSetting = new Dialog(mContext);
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
        if (trendSettingInfo.setting.show_column != null && trendSettingInfo.setting.show_column.list != null) {
            final TrendSettingAdapter adaperShowHide = new TrendSettingAdapter(R.layout.adapter_trends_setting, trendSettingInfo.setting.show_column.list);
            recyclerViewShowHide.setAdapter(adaperShowHide);
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
        } else {
            viewLayout.findViewById(R.id.tv_choose_code_clum).setVisibility(View.GONE);
        }
        RecyclerView recyclerViewMiss = viewLayout.findViewById(R.id.recycle_miss);
        recyclerViewMiss.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerViewMiss.addItemDecoration(new GridSpacingItemDecoration(2, 30, true));
        final TrendSettingAdapter adapermiss = new TrendSettingAdapter(R.layout.adapter_trends_setting, trendSettingInfo.setting.show_missing.list);
        recyclerViewMiss.setAdapter(adapermiss);
        dialogSetting.setContentView(viewLayout);
        dialogSetting.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        dialogSetting.getWindow().setLayout(ScreenUtils.getScreenWidth() - 50, WindowManager.LayoutParams.WRAP_CONTENT);


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
                dialogSetting.dismiss();
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
        data.put("lottery_type", lotteryInfopASS.lottery_type);// 彩种
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
                        dialogSetting.dismiss();
                        if (response.body().code == 1) {
                            showLoadingBar();

                            if (showHide.equals("hide")) {
                                llUiTouzhu.setVisibility(View.GONE);
                            } else {
                                llUiTouzhu.setVisibility(View.VISIBLE);
                            }

                            getTrendData();
                        } else {
                            ToastUtils.showShort(response.body().msg);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dialogSetting.dismiss();
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    //走势图数据
    private void getTrendData() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", lotteryInfopASS.lottery_type);// 彩种
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
            trendNormalInfo.lists = Utils.get3CodeForTrend(trendCodeInfo, dismiss);
            trendNormalInfos.add(trendNormalInfo);
        }

        TrendNormalInfo numbers = new TrendNormalInfo();
//        numbers.lists = Utils.get3CodeForTrendCount("出现次数", trendChartInfo.number);
        numbers.lists = Utils.get3CodeForTrendCount(trendChartInfo.number);


        TrendNormalInfo laverage_missing = new TrendNormalInfo();
//        laverage_missing.lists = Utils.get3CodeForTrendCount("平均遗漏", trendChartInfo.average_missing);
        laverage_missing.lists = Utils.get3CodeForTrendCount( trendChartInfo.average_missing);

        TrendNormalInfo max_miss = new TrendNormalInfo();
//        max_miss.lists = Utils.get3CodeForTrendCount("最大遗漏", trendChartInfo.max_missing);
        max_miss.lists = Utils.get3CodeForTrendCount( trendChartInfo.max_missing);

        TrendNormalInfo max_kits = new TrendNormalInfo();
//        max_kits.lists = Utils.get3CodeForTrendCount("最大连击", trendChartInfo.max_double_hits);
        max_kits.lists = Utils.get3CodeForTrendCount( trendChartInfo.max_double_hits);


        List<TrendNormalInfo> trendNormalInfosCount = new ArrayList<>();
        trendNormalInfosCount.add(numbers);
        trendNormalInfosCount.add(laverage_missing);
        trendNormalInfosCount.add(max_miss);
        trendNormalInfosCount.add(max_kits);


        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();
        List<TerdNormalBall> terdNormalBallsCount = new ArrayList<>();

        for (TrendNormalInfo item : trendNormalInfosCount) {
            terdNormalBallsCount.addAll(item.lists);
        }


        if (trendNormalInfos.size()>0) {
            for (TrendNormalInfo item:trendNormalInfos){
                terdNormalBalls.addAll(item.lists);
            }
            trendListAdapter.setNewData(terdNormalBalls);
        }
        trendListBottomAdapter.setNewData(terdNormalBallsCount);
        chatBottomView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recycle.scrollToPosition(trendListAdapter.getData().size());
            }
        }, 500);
    }


    //校验期号
    private void checkAwardId() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", lotteryInfopASS.lottery_type);// 彩种
        data.put("award_id", Constants.LASTEST_AWARD_ID_THREE);// 最新彩种期数id
        OkGo.<LotteryResponse<CheckSelectCodeInfo>>post(Constants.Net.CART_CHECKAWARD)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<CheckSelectCodeInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<CheckSelectCodeInfo>> response) {
                        checkCodes();
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                        dismissLoadingBar();

                    }
                });
    }

    //校验球号
    private void checkCodes() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_id", lotteryInfo.lottery_id);// 最新彩种期数id
        data.put("codes", new Gson().toJson(isSingleCode()?getSelectBallsCode():getSelectBallsCodeDanTuo()));
        OkGo.<LotteryResponse<CheckSelectCodeInfo>>post(Constants.Net.CART_CHECKCODES)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<CheckSelectCodeInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<CheckSelectCodeInfo>> response) {
                        addCar();
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                        dismissLoadingBar();
                    }
                });
    }


    //加入购物车
    private void addCar() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("award_id", Constants.LASTEST_AWARD_ID_THREE);
        data.put("lottery_id", lotteryInfo.lottery_id);// 最新彩种期数id
        data.put("codes", new Gson().toJson(isSingleCode()?getSelectBallsCode():getSelectBallsCodeDanTuo()));
        OkGo.<LotteryResponse<CheckSelectCodeInfo>>post(Constants.Net.CART_ADDCART)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<CheckSelectCodeInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<CheckSelectCodeInfo>> response) {
                        initAllAwardsBall();
                        checkCodeAndAward();
                        dismissLoadingBar();
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                        dismissLoadingBar();
                    }
                });
    }


    //校验球号 和期号
    private void checkCodeAndAward() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.PASS_BOLLEAN, true);//
        bundle.putSerializable(Constants.PASS_OBJECT, lotteryInfo);
        ActivityUtils.startActivity(bundle, ConfirmCodesActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
