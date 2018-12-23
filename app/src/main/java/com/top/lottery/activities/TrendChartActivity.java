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
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.AcountChartAwardBallsAdapter;
import com.top.lottery.adapters.AwardDialogKindsAdapter;
import com.top.lottery.adapters.TrendListItemAdapter;
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
 * Describle: 走势图
 */
public class TrendChartActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_count_down_tips)
    TextView tvCountDownTips;
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
    @BindView(R.id.tv_three_ball_name)
    TextView tvThreeBallName;
    @BindView(R.id.recycle_three_ball)
    RecyclerView recycleThreeBall;
    @BindView(R.id.ll_three_balls)
    LinearLayout llThreeBalls;
    @BindView(R.id.tv_lottery_name)
    TextView tvLotteryName;
    @BindView(R.id.tv_award_vluese)
    TextView tvAwardVluese;
    @BindView(R.id.tv_touzhu_action)
    TextView tvTouzhuAction;
    @BindView(R.id.ll_ui_touzhu)
    LinearLayout llUiTouzhu;
    //走势图配置信息
    private TrendSettingInfo trendSettingInfo;
    private Dialog dialogSetting, dialogAward;
    private String day, sort, showHide, dismiss;
    private String currentLotterTerm;//最新一期投注号2018081430
    private CountDownTimer countDownTimer;
    private LasterLotteryAwardInfo lasterLotteryAwardInfo;
    private TrendListItemAdapter trendListAdapter;
    private List<LotteryPlayWay> lotteryPlayWays;
    private LotteryPlayWay lotteryPlayWaySelect;//当前选择的彩种
    private AcountChartAwardBallsAdapter acountChartAwardBallsAdapterOne, acountChartAwardBallsAdapterTwo, acountChartAwardBallsAdapterThree;
    private AwardDialogKindsAdapter awardDialogKindsAdapter;//弹出彩种对话框
    private LotteryInfo lotteryInfoPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_chart);
        ButterKnife.bind(this);
        setTitle("走势图");
        ivRightFunction.setVisibility(View.VISIBLE);
        ivRightFunction.setImageResource(R.mipmap.trend_setting);
        lotteryInfoPass = (LotteryInfo) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        trendListAdapter = new TrendListItemAdapter(R.layout.adapter_trends_item_num, new ArrayList<TerdNormalBall>());
        recycle.setLayoutManager(new GridLayoutManager(this, 12));
        recycle.addItemDecoration(new GridSpacingItemDecoration(12, 1, false));
        recycle.setAdapter(trendListAdapter);
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


        acountChartAwardBallsAdapterOne = new AcountChartAwardBallsAdapter(R.layout.adapter_trends_awardball, Utils.get11Code());
        recycleOneBall.setLayoutManager(new GridLayoutManager(mContext, 11));
        recycleOneBall.setAdapter(acountChartAwardBallsAdapterOne);

        //第一个球   胆码
        acountChartAwardBallsAdapterOne.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (lotteryInfo.type == 1 || lotteryInfo.type == 3) {
                    acountChartAwardBallsAdapterOne.getData().get(position).isSelected = !acountChartAwardBallsAdapterOne.getData().get(position).isSelected;
                    acountChartAwardBallsAdapterOne.notifyItemChanged(position);
                    countIntergaryNormal();
                } else if (lotteryInfo.type == 2) {
                    //直选
                    acountChartAwardBallsAdapterOne.getData().get(position).isSelected = !acountChartAwardBallsAdapterOne.getData().get(position).isSelected;
                    acountChartAwardBallsAdapterOne.notifyItemChanged(position);
                    countIntergaryDirect();
                } else if (lotteryInfo.type == 4 || lotteryInfo.type == 6) {
                    //胆拖
                    if (getSelectBallsDan().size() >= lotteryInfo.num - 1) {
                        ToastUtils.showShort("最多只能选择" + getSelectBallsDan().size() + "个胆码");
                        return;
                    }
                    acountChartAwardBallsAdapterOne.getData().get(position).isSelected = !acountChartAwardBallsAdapterOne.getData().get(position).isSelected;
                    if (acountChartAwardBallsAdapterOne.getData().get(position).isSelected) {
                        acountChartAwardBallsAdapterTwo.getData().get(position).isSelected = false;
                        acountChartAwardBallsAdapterTwo.notifyItemChanged(position);
                    }
                    acountChartAwardBallsAdapterOne.notifyItemChanged(position);
                    countIntergaryDanTuo();
                }
            }
        });


        acountChartAwardBallsAdapterTwo = new AcountChartAwardBallsAdapter(R.layout.adapter_trends_awardball, Utils.get11Code());
        recycleTwoBall.setLayoutManager(new GridLayoutManager(mContext, 11));
        recycleTwoBall.setAdapter(acountChartAwardBallsAdapterTwo);

        //第二个球
        acountChartAwardBallsAdapterTwo.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (lotteryInfo.type == 2) {
                    //直选
                    acountChartAwardBallsAdapterTwo.getData().get(position).isSelected = !acountChartAwardBallsAdapterTwo.getData().get(position).isSelected;
                    acountChartAwardBallsAdapterTwo.notifyItemChanged(position);
                    countIntergaryDirect();
                } else if (lotteryInfo.type == 4 || lotteryInfo.type == 6) {
                    //胆拖
                    acountChartAwardBallsAdapterTwo.getData().get(position).isSelected = !acountChartAwardBallsAdapterTwo.getData().get(position).isSelected;
                    if (acountChartAwardBallsAdapterTwo.getData().get(position).isSelected) {
                        acountChartAwardBallsAdapterOne.getData().get(position).isSelected = false;
                        acountChartAwardBallsAdapterOne.notifyItemChanged(position);
                    }
                    acountChartAwardBallsAdapterTwo.notifyItemChanged(position);
                    countIntergaryDanTuo();
                }

            }
        });

        acountChartAwardBallsAdapterThree = new AcountChartAwardBallsAdapter(R.layout.adapter_trends_awardball, Utils.get11Code());
        recycleThreeBall.setLayoutManager(new GridLayoutManager(mContext, 11));
        recycleThreeBall.setAdapter(acountChartAwardBallsAdapterThree);
        //第三个
        acountChartAwardBallsAdapterThree.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (lotteryInfo.type == 2) {
                    //直选
                    acountChartAwardBallsAdapterThree.getData().get(position).isSelected = !acountChartAwardBallsAdapterThree.getData().get(position).isSelected;
                    acountChartAwardBallsAdapterThree.notifyItemChanged(position);
                    countIntergaryDirect();
                }
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

    //做投注操作
    private void doTouZhuAction() {
        if (lotteryInfo != null) {
            if (lotteryInfo.type == 1 || lotteryInfo.type == 3) {
                checkSelectOne();
            } else if (lotteryInfo.type == 2) {
                checkSelectDirect();
            } else if (lotteryInfo.type == 4 || lotteryInfo.type == 6) {
                checkSelectDanTuo();
            }
        } else {
            ToastUtils.showShort("请选择彩种");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoticeToDoNewTermCodeEvent event) {
        if (event!=null){
            String className = event.className;
            String methodName = event.methodName;
            if (className.equals(TrendChartActivity.class.getName())){
                doTouZhuAction();
            }
        }
    }

    //检查胆拖
    private void checkSelectDanTuo() {
        if (getSelectBallsDanTuo().size() > lotteryInfo.num) {
            showLoadingBar();
            checkAwardId();
        } else {
            ToastUtils.showShort("胆码+拖码至少选择大于" + lotteryInfo.num);
        }
    }


    //已选择胆拖所有的球数
    private List<AwardBallInfo> getSelectBallsDanTuo() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (acountChartAwardBallsAdapterOne != null && acountChartAwardBallsAdapterOne.getData().size() > 0) {
            for (AwardBallInfo item : acountChartAwardBallsAdapterOne.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }


        if (acountChartAwardBallsAdapterTwo != null && acountChartAwardBallsAdapterTwo.getData().size() > 0) {
            for (AwardBallInfo item : acountChartAwardBallsAdapterTwo.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }

    //计算胆拖积分  4   6  类型
    private void countIntergaryDanTuo() {
        if (getSelectBallsTuo().size() >= lotteryInfo.num) {
            int integray = 2 * Utils.combination(getSelectBallsTuo().size(), lotteryInfo.num - getSelectBallsDan().size());
            int zhuCount = Utils.combination(getSelectBallsTuo().size(), lotteryInfo.num - getSelectBallsDan().size());
            tvAwardVluese.setText("积分" + integray + "  共" + zhuCount + "注");
            lotteryInfo.codes = getSelectBallsCode();
            lotteryInfo.TOUZHU_COUNT = Utils.combination(getSelectBallsTuo().size(), lotteryInfo.num - getSelectBallsDan().size());
            lotteryInfo.TOUZHU_INTEGRY = 2 * Utils.combination(getSelectBallsTuo().size(), lotteryInfo.num - getSelectBallsDan().size());
        } else {
            tvAwardVluese.setText("积分0  共0注");
        }
    }


    //已选择胆的球数
    private List<AwardBallInfo> getSelectBallsDan() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (acountChartAwardBallsAdapterOne != null && acountChartAwardBallsAdapterOne.getData().size() > 0) {
            for (AwardBallInfo item : acountChartAwardBallsAdapterOne.getData()) {
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


        if (acountChartAwardBallsAdapterTwo != null && acountChartAwardBallsAdapterTwo.getData().size() > 0) {
            for (AwardBallInfo item : acountChartAwardBallsAdapterTwo.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }


    //计算直选积分
    private void countIntergaryDirect() {
        if (getSelectBalls().size() >= lotteryInfo.num) {
            int touzhuCount = 0;
            if (lotteryInfo.num == 1) {
                touzhuCount = Utils.combination(getSelectBallsOne().size(), lotteryInfo.num);
                tvAwardVluese.setText("积分：" + 2 * touzhuCount + "注数：" + touzhuCount);
                lotteryInfo.codes = getSelectBallsCode();
                lotteryInfo.TOUZHU_COUNT = touzhuCount;
                lotteryInfo.TOUZHU_INTEGRY = 2 * touzhuCount;
            }
            if (lotteryInfo.num == 2) {
                touzhuCount = Utils.combination(getSelectBallsOne().size(), 1) * Utils.combination(getSelectBallsTwo().size(), 1) - getDoubleAppear();
                tvAwardVluese.setText("积分：" + 2 * touzhuCount + "注数：" + touzhuCount);
                lotteryInfo.codes = getSelectBallsCode();
                lotteryInfo.TOUZHU_COUNT = touzhuCount;
                lotteryInfo.TOUZHU_INTEGRY = 2 * touzhuCount;
            }

            if (lotteryInfo.num == 3) {
                //TODO  直选三种类型的算法
                touzhuCount = getDoubleAppear();
                tvAwardVluese.setText("积分：" + 2 * touzhuCount + "注数：" + touzhuCount);
                lotteryInfo.codes = getSelectBallsCode();
                lotteryInfo.TOUZHU_COUNT = touzhuCount;
                lotteryInfo.TOUZHU_INTEGRY = 2 * touzhuCount;
            }

        } else {
            tvAwardVluese.setText("积分0  共0注");
        }
    }


    //去掉n 组 两两组合中有重复的 个数
    private int getDoubleAppear() {

        int i = 0;
        if (lotteryInfo.num == 2) {
            if (getSelectBallsOne().size() == 0) {
                i = 0;
            }
            if (getSelectBallsTwo().size() == 0) {
                i = 0;
            }
            for (AwardBallInfo itemOne : getSelectBallsOne()) {

                for (AwardBallInfo itemTwo : getSelectBallsTwo()) {
                    if (itemOne.value.equals(itemTwo.value)) {
                        i++;
                    }
                }

            }

        }


        if (lotteryInfo.num == 3) {
            if (getSelectBallsOne().size() == 0) {
                i = 0;
            }
            if (getSelectBallsTwo().size() == 0) {
                i = 0;
            }

            if (getSelectBallsThree().size() == 0) {
                i = 0;
            }
            for (AwardBallInfo itemOne : getSelectBallsOne()) {

                for (AwardBallInfo itemTwo : getSelectBallsTwo()) {

                    for (AwardBallInfo itemThree : getSelectBallsThree()) {
                        if (itemOne.value.equals(itemTwo.value) || itemOne.value.equals(itemThree.value) || itemTwo.value.equals(itemThree.value)) {
                            continue;
                        } else {
                            i++;
                        }
                    }
                }
            }
        }


        LogUtils.w("dyc", "去重个数：：：" + i);
        return i;
    }


    //已选择的球数  所有球  第一 ~第三行
    private List<AwardBallInfo> getSelectBalls() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        awardBallInfos.addAll(getSelectBallsOne());
        awardBallInfos.addAll(getSelectBallsTwo());
        awardBallInfos.addAll(getSelectBallsThree());
        return awardBallInfos;
    }


    //彩票 任选    组选积分计算    1  3   类型
    private void countIntergaryNormal() {
        if (getSelectBallsOne().size() >= lotteryInfo.num) {
            int integray = 2 * Utils.combination(getSelectBallsOne().size(), lotteryInfo.num);
            int zhuCount = Utils.combination(getSelectBallsOne().size(), lotteryInfo.num);
            tvAwardVluese.setText("积分" + integray + "  共" + zhuCount + "注");
            lotteryInfo.codes = getSelectBallsCodeOne();
            lotteryInfo.TOUZHU_COUNT = Utils.combination(getSelectBallsOne().size(), lotteryInfo.num);
            lotteryInfo.TOUZHU_INTEGRY = 2 * Utils.combination(getSelectBallsOne().size(), lotteryInfo.num);
        } else {
            tvAwardVluese.setText("积分0  共0注");
        }
    }


    //获取所选球号  一个球的选号
    private String[][] getSelectBallsCodeOne() {
        List<AwardBallInfo> awardBallInfos = getSelectBallsOne();
        String[] codes = new String[awardBallInfos.size()];
        for (int i = 0; i < awardBallInfos.size(); i++) {
            codes[i] = awardBallInfos.get(i).value;
        }

        String[][] arrays = new String[1][1];
        arrays[0] = codes;
        return arrays;
    }


    //获取所选球号
    private String[][] getSelectBallsCodeTuoDan() {
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


    //已选择的球数 第一个
    private List<AwardBallInfo> getSelectBallsOne() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (acountChartAwardBallsAdapterOne != null && acountChartAwardBallsAdapterOne.getData().size() > 0) {
            for (AwardBallInfo item : acountChartAwardBallsAdapterOne.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }

    //已选择的球数 第二个
    private List<AwardBallInfo> getSelectBallsTwo() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (acountChartAwardBallsAdapterTwo != null && acountChartAwardBallsAdapterTwo.getData().size() > 0) {
            for (AwardBallInfo item : acountChartAwardBallsAdapterTwo.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }

    //已选择的球数 第三个
    private List<AwardBallInfo> getSelectBallsThree() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (acountChartAwardBallsAdapterThree != null && acountChartAwardBallsAdapterThree.getData().size() > 0) {
            for (AwardBallInfo item : acountChartAwardBallsAdapterThree.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }

    //获取选彩种类
    private void getAwardKinds() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lid", lotteryInfoPass.lid);
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
                            if (info.code == -3 || info.code==-2) {
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
        for (AwardBallInfo item : acountChartAwardBallsAdapterOne.getData()) {
            item.isSelected = false;
        }
        for (AwardBallInfo item : acountChartAwardBallsAdapterTwo.getData()) {
            item.isSelected = false;
        }
        for (AwardBallInfo item : acountChartAwardBallsAdapterThree.getData()) {
            item.isSelected = false;
        }
        acountChartAwardBallsAdapterOne.notifyDataSetChanged();
        acountChartAwardBallsAdapterTwo.notifyDataSetChanged();
        acountChartAwardBallsAdapterThree.notifyDataSetChanged();
    }

    /**
     * /**
     * 更具彩球切换球号模板
     * type://1-任选，2-前一、前二、前三直选等，3-前二、前三组选，4-胆拖任选，6-胆拖组选
     * num://3 球的个数
     */

    private LotteryInfo lotteryInfo;

    private void getLotteryById(String lottery_id) {

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
                        changeContent();
                    }


                    @Override
                    public void onError(Response response) {
                        showError(Utils.toastInfo(response));
                        dismissLoadingBar();
                    }
                });
    }


    /**
     * /**
     * 更具彩球切换球号模板
     * type://1-任选，2-前一、前二、前三直选等，3-前二、前三组选，4-胆拖任选，6-胆拖组选
     * num://3 球的个数
     */
    //改变选球球样式
    private void changeContent() {
        if (lotteryInfo.type == 1 || lotteryInfo.type == 3) {
            tvFisrtBallName.setText("选号");
            llFirstBalls.setVisibility(View.VISIBLE);
            llSecondBalls.setVisibility(View.GONE);
            llThreeBalls.setVisibility(View.GONE);

        } else if (lotteryInfo.type == 2) {
            tvFisrtBallName.setText("第一");
            tvSencondBallName.setText("第二");
            tvThreeBallName.setText("第三");
            if (lotteryInfo.num == 1) {
                llFirstBalls.setVisibility(View.VISIBLE);
                llSecondBalls.setVisibility(View.GONE);
                llThreeBalls.setVisibility(View.GONE);
            } else if (lotteryInfo.num == 2) {
                llFirstBalls.setVisibility(View.VISIBLE);
                llSecondBalls.setVisibility(View.VISIBLE);
                llThreeBalls.setVisibility(View.GONE);
            } else if (lotteryInfo.num == 3) {
                llFirstBalls.setVisibility(View.VISIBLE);
                llSecondBalls.setVisibility(View.VISIBLE);
                llThreeBalls.setVisibility(View.VISIBLE);
            }
        } else if (lotteryInfo.type == 4 || lotteryInfo.type == 6) {
            tvFisrtBallName.setText("胆码");
            tvSencondBallName.setText("拖码");
            tvSencondBallName.setVisibility(View.VISIBLE);
            llSecondBalls.setVisibility(View.VISIBLE);
            llThreeBalls.setVisibility(View.GONE);
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
                                Constants.LASTEST_AWARD_ID = lasterLotteryAwardInfo.award_id;
                                Constants.LASTER_AWARD_END_TIME = lasterLotteryAwardInfo.current_time;
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
                    llUiTouzhu.setVisibility(View.VISIBLE);
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
        }else{
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
            trendNormalInfo.lists = Utils.get11CodeForTrend(trendCodeInfo, dismiss);
            trendNormalInfos.add(trendNormalInfo);
        }

        TrendNormalInfo numbers = new TrendNormalInfo();
        numbers.lists = Utils.get11CodeForTrendCount("出现次数", trendChartInfo.number);


        TrendNormalInfo laverage_missing = new TrendNormalInfo();
        laverage_missing.lists = Utils.get11CodeForTrendCount("平均遗漏", trendChartInfo.average_missing);

        TrendNormalInfo max_miss = new TrendNormalInfo();
        max_miss.lists = Utils.get11CodeForTrendCount("最大遗漏", trendChartInfo.max_missing);

        TrendNormalInfo max_kits = new TrendNormalInfo();
        max_kits.lists = Utils.get11CodeForTrendCount("最大连击", trendChartInfo.max_double_hits);


        trendNormalInfos.add(numbers);
        trendNormalInfos.add(laverage_missing);
        trendNormalInfos.add(max_miss);
        trendNormalInfos.add(max_kits);


        List<TerdNormalBall> terdNormalBalls = new ArrayList<>();

        for (TrendNormalInfo item : trendNormalInfos) {
            terdNormalBalls.addAll(item.lists);
        }


        trendListAdapter.setNewData(terdNormalBalls);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
             recycle.scrollToPosition(trendListAdapter.getData().size()-1);
            }
        },500);
    }


    //检查选择一个球的流程
    private void checkSelectOne() {
        if (getSelectBallsOne().size() >= lotteryInfo.num) {
            showLoadingBar();
            checkAwardId();
        } else {
            ToastUtils.showShort("只是选择" + lotteryInfo.num + "球");
        }
    }


    //选择直选
    private void checkSelectDirect() {
        if (lotteryInfo.num == 1) {
            if (getSelectBallsOne().size() == 0) {
                ToastUtils.showShort("请至少选择一个号码");
                return;
            }
        }

        if (lotteryInfo.num == 2) {
            if (getSelectBallsOne().size() == 0) {
                ToastUtils.showShort("请选择第一位");
                return;
            }

            if (getSelectBallsTwo().size() == 0) {
                ToastUtils.showShort("请选择第二位");
                return;
            }
        }

        if (lotteryInfo.num == 3) {
            if (getSelectBallsOne().size() == 0) {
                ToastUtils.showShort("请选择第一位");
                return;
            }

            if (getSelectBallsTwo().size() == 0) {
                ToastUtils.showShort("请选择第二位");
                return;
            }
            if (getSelectBallsThree().size() == 0) {
                ToastUtils.showShort("请选择第三位");
                return;
            }
        }


        if (getSelectBalls().size() >= lotteryInfo.num) {
            showLoadingBar();
            checkAwardId();
        }
    }


    //校验期号
    private void checkAwardId() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", "1");// 彩种
        data.put("award_id", Constants.LASTEST_AWARD_ID);// 最新彩种期数id
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
        data.put("codes", new Gson().toJson(getSelectBallsCode()));
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

    //TODO 获取选择的球
    private String[][] getSelectBallsCode() {
        if (lotteryInfo.type == 1 || lotteryInfo.type == 3) {
            return getSelectBallsCodeOne();
        } else if (lotteryInfo.type == 2) {
            return getSelectBallsCodeDirect();
        } else if (lotteryInfo.type == 4 || lotteryInfo.type == 6) {
            return getSelectBallsCodeTuoDan();
        }


        return new String[0][];
    }


    //获取所选球号 直选
    private String[][] getSelectBallsCodeDirect() {
        String[][] arrays = new String[lotteryInfo.num][1];
        String[] codesone = new String[getSelectBallsOne().size()];
        for (int i = 0; i < getSelectBallsOne().size(); i++) {
            codesone[i] = getSelectBallsOne().get(i).value;
        }
        arrays[0] = codesone;
        if (lotteryInfo.num == 2) {
            String[] codetwo = new String[getSelectBallsTwo().size()];
            for (int i = 0; i < getSelectBallsTwo().size(); i++) {
                codetwo[i] = getSelectBallsTwo().get(i).value;
            }
            arrays[1] = codetwo;
        } else if (lotteryInfo.num == 3) {
            String[] codetwo = new String[getSelectBallsTwo().size()];
            for (int i = 0; i < getSelectBallsTwo().size(); i++) {
                codetwo[i] = getSelectBallsTwo().get(i).value;
            }
            arrays[1] = codetwo;

            String[] codethree = new String[getSelectBallsThree().size()];
            for (int i = 0; i < getSelectBallsThree().size(); i++) {
                codethree[i] = getSelectBallsThree().get(i).value;
                arrays[2] = codethree;
            }

        }
        return arrays;
    }


    //加入购物车
    private void addCar() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("award_id", Constants.LASTEST_AWARD_ID);
        data.put("lottery_id", lotteryInfo.lottery_id);// 最新彩种期数id
        data.put("codes", new Gson().toJson(getSelectBallsCode()));
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
        bundle.putBoolean(Constants.PASS_BOLLEAN,true);//
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
