package com.top.lottery.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.activities.BaseActivity;
import com.top.lottery.activities.ConfirmCodesActivity;
import com.top.lottery.activities.LotteryFunnyThreeActivity;
import com.top.lottery.activities.TrendChartThreeActivity;
import com.top.lottery.adapters.AwardThreeBallAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.beans.CheckSelectCodeInfo;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MechineChoosInfo;
import com.top.lottery.beans.MissLotteryCode;
import com.top.lottery.events.NoticeToDoNewTermCodeEvent;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.GridSpacingItemDecoration;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author: 邓言诚  Create at : 2018/8/23  01:18
 * Email: yanchengdeng@gmail.com
 * Describle:  快三  胆拖
 */
public class LotteryFunnyThreeDanTuoFragment extends Fragment {

    @BindView(R.id.tv_choose_change)
    TextView tvChooseChange;
    @BindView(R.id.tv_intergry)
    TextView tvIntergry;
    @BindView(R.id.tv_note_numbers)
    TextView tvNoteNumbers;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    Unbinder unbinder;
    @BindView(R.id.tv_select_tips)
    TextView tvSelectTips;
    @BindView(R.id.recycle_dan)
    RecyclerView recycleDan;
    @BindView(R.id.recycle_tuo)
    RecyclerView recycleTuo;
    @BindView(R.id.tv_trend_chart)
    TextView tvTrendChart;
    @BindView(R.id.tv_tuo_tips)
    TextView tvTuoTips;
    private LotteryInfo lotteryInfo;
    private AwardThreeBallAdapter awardDanAdapter, awawrdTuoAdapter;
    private boolean isMechineChoose;//是否可以允许机选  允许则可以机选  清除 切换  否则是可以清除操作


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lottery_dantuo_type, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        lotteryInfo = (LotteryInfo) getArguments().getSerializable(Constants.PASS_OBJECT);


        //胆
        awardDanAdapter = new AwardThreeBallAdapter(R.layout.adapter_lottery_three_select_num, new ArrayList<AwardBallInfo>());
        recycleDan.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recycleDan.addItemDecoration(new GridSpacingItemDecoration(3,16,true));
        recycleDan.setAdapter(awardDanAdapter);
        if (lotteryInfo.type==5){
            awardDanAdapter.setNewData(Utils.parseThreeCodesDoule( lotteryInfo.score));
        }else {
            awardDanAdapter.setNewData(Utils.parseThreeCodes(lotteryInfo, lotteryInfo.score));
        }

        awardDanAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

               for (AwardBallInfo item:awardDanAdapter.getData()){
                   item.isSelected = false;
               }
                awardDanAdapter.getData().get(position).isSelected = true;
//                checkTouHasSame(awardDanAdapter.getData().get(position).value);
                awawrdTuoAdapter.getData().get(position).isSelected = false;
                awawrdTuoAdapter.notifyDataSetChanged();
                awardDanAdapter.notifyDataSetChanged();

                countIntergary();
            }
        });


        //走势图
        tvTrendChart.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_OBJECT,lotteryInfo);
                ActivityUtils.startActivity( bundle,TrendChartThreeActivity.class);
            }
        });

        //拖
        awawrdTuoAdapter = new AwardThreeBallAdapter(R.layout.adapter_lottery_three_select_num, new ArrayList<AwardBallInfo>());
        recycleTuo.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recycleTuo.addItemDecoration(new GridSpacingItemDecoration(3,16,true));
        recycleTuo.setAdapter(awawrdTuoAdapter);
        awawrdTuoAdapter.setNewData(Utils.parseThreeCodes(lotteryInfo, lotteryInfo.score));

        awawrdTuoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                awawrdTuoAdapter.getData().get(position).isSelected = !awawrdTuoAdapter.getData().get(position).isSelected;
                if (awawrdTuoAdapter.getData().get(position).isSelected) {
                    awardDanAdapter.getData().get(position).isSelected = false;
                    awardDanAdapter.notifyItemChanged(position);
                }
//                checkDanHasSame(awardDanAdapter.getData().get(position).value);
                awawrdTuoAdapter.notifyItemChanged(position);
                countIntergary();
            }
        });
        initAwardNum();
        initChangeButton();


        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NoticeToDoNewTermCodeEvent event) {
        if (event != null && isVisible()) {
            String className = event.className;
            String methodName = event.methodName;
            if (className.equals(LotteryFunnyThreeActivity.class.getName())) {
                checkSelect();
            }
        }
    }


    //创建任意选球
    private void initAwardNum() {
        if (lotteryInfo.type == 1) {
            tvSelectTips.setText("猜开奖号码相加的和");
        } else if (lotteryInfo.type == 2) {
            tvSelectTips.setText("猜豹子号(3个号相同)");
        } else if (lotteryInfo.type == 3) {
            tvSelectTips.setText("猜出一个豹子开出即中奖");
        } else if (lotteryInfo.type == 5) {
            //两个
            tvSelectTips.setText("猜对子号(有2个号相同)\n复选：猜开奖中两个指定的相同号码\n\n同号");
            tvTuoTips.setText("不同号");
        } else if (lotteryInfo.type == 4) {
            tvSelectTips.setText("猜对子号(有2个号相同)\n单选：选择同号和不同号的组合\n\n同号");
        } else if (lotteryInfo.type == 6) {
            tvSelectTips.setText("选3个不同号码\n与开奖相同即中");
        } else if (lotteryInfo.type == 7) {
            tvSelectTips.setText("选2个不同号码\n猜中开奖任意2位即中");
        } else if (lotteryInfo.type == 8) {
            tvSelectTips.setText("123/234/345/456任一开出即中");
        } else if (lotteryInfo.type == 9) {
            //两个
            tvSelectTips.setText("选3个不同号码\n与开奖相同即中\n\n请选择胆码");
            tvTuoTips.setText("请选择拖码");
        } else if (lotteryInfo.type == 10) {
            //两个
            tvSelectTips.setText("选2个不同号码\n与开奖任意两位相同即中\n\n请选择胆码");
            tvTuoTips.setText("请选择拖码");
        }
    }

    //计算积分 一注  等于 2个积分
    private void countIntergary() {
//        if (getSelectBallsDanTuo().size() >= lotteryInfo.num) {
//        if (getSelectBallsTuo().size() >= lotteryInfo.num) {


            lotteryInfo.codes = getSelectBallsCode();
            lotteryInfo.TOUZHU_COUNT = getSelectBallsTuo().size()*  getSelectBallsDan().size();
            lotteryInfo.TOUZHU_INTEGRY = 2 * Utils.combination(getSelectBallsTuo().size(),  getSelectBallsDan().size());

        tvIntergry.setText("积分：" + 2 *  lotteryInfo.TOUZHU_COUNT);
        tvNoteNumbers.setText("注数：" +  lotteryInfo.TOUZHU_COUNT);

//        } else {
//            tvIntergry.setText("积分：0");
//            tvNoteNumbers.setText("注数：0");
//        }
    }


    //已选择胆拖所有的球数
    private List<AwardBallInfo> getSelectBallsDanTuo() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardDanAdapter != null && awardDanAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awardDanAdapter.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }


        if (awawrdTuoAdapter != null && awawrdTuoAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awawrdTuoAdapter.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }


    //已选择胆的球数
    private List<AwardBallInfo> getSelectBallsDan() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardDanAdapter != null && awardDanAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awardDanAdapter.getData()) {
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


        if (awawrdTuoAdapter != null && awawrdTuoAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awawrdTuoAdapter.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }


    //获取所选球号
    private String[][] getSelectBallsCode() {
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


    @OnClick({R.id.tv_choose_change, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_choose_change:
                if (lotteryInfo.mechine == 1) {
                    //允许机选
                    if (isMechineChoose) {
                        doMechineAction();
                    } else {
                        clearSelectBalls();
                    }
                    isMechineChoose = !isMechineChoose;
                    initChangeButton();
                } else {
                    clearSelectBalls();
                }
                break;
            case R.id.tv_confirm:
                checkSelect();
                break;
        }
    }

    private void checkSelect() {
        if (getSelectBallsDanTuo().size() > lotteryInfo.num) {
            if (getActivity() != null) {
                ((BaseActivity) getActivity()).showLoadingBar();
            }
            checkAwardId();
        } else {
            ToastUtils.showShort("胆码+拖码至少选择大于" + lotteryInfo.num);
        }
    }


    //校验期号
    private void checkAwardId() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", lotteryInfo.lottery_type);// 彩种
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
                        if (getActivity() != null) {
                            ((BaseActivity) getActivity()).dismissLoadingBar();
                        }

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
                        if (getActivity() != null) {
                            ((BaseActivity) getActivity()).dismissLoadingBar();
                        }
                    }
                });
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
                        clearSelectBalls();
                        isMechineChoose = lotteryInfo.mechine == 1 ? true : false;
                        initChangeButton();
                        checkCodeAndAward();
                        if (getActivity() != null) {
                            ((BaseActivity) getActivity()).dismissLoadingBar();
                        }
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                        if (getActivity() != null) {
                            ((BaseActivity) getActivity()).dismissLoadingBar();
                        }
                    }
                });
    }


    //校验球号 和期号
    private void checkCodeAndAward() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PASS_OBJECT, lotteryInfo);
        ActivityUtils.startActivity(bundle, ConfirmCodesActivity.class);
    }

    //随机按钮显示
    private void initChangeButton() {
        if (isMechineChoose) {
            tvChooseChange.setText("机选");
            tvChooseChange.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.random), null, null, null);
        } else {
            tvChooseChange.setText("清除");
            tvChooseChange.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.delete_white), null, null, null);
        }
    }

    //清除所选球
    private void clearSelectBalls() {
        for (AwardBallInfo item : awardDanAdapter.getData()) {
            item.isSelected = false;
        }

        for (AwardBallInfo item : awawrdTuoAdapter.getData()) {
            item.isSelected = false;
        }
        awardDanAdapter.notifyDataSetChanged();
        awawrdTuoAdapter.notifyDataSetChanged();
    }

    //机选
    private void doMechineAction() {

        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("add", "0");
        data.put("lid", lotteryInfo.lid);
        data.put("lottery_id", lotteryInfo.lottery_id);// 最新彩种期数id
        OkGo.<LotteryResponse<MechineChoosInfo>>post(Constants.Net.CART_MECHINE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MechineChoosInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MechineChoosInfo>> response) {
                        MechineChoosInfo mechineChoosInfo = response.body().body;
//                        if (mechineChoosInfo != null && mechineChoosInfo.code != null && mechineChoosInfo.code.length > 0) {
//                            showMechineChoose(mechineChoosInfo.code);
//                        }
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }

                    }
                });
    }


    //获取遗漏值
    private void getMissValue() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("award_id", Constants.LASTEST_AWARD_ID);// 最新彩种期数id
        data.put("lottery_type", lotteryInfo.lottery_type);
        data.put("lottery_id", lotteryInfo.lottery_id);
        OkGo.<LotteryResponse<MissLotteryCode>>post(Constants.Net.AWARD_MISSINGVALUE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MissLotteryCode>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MissLotteryCode>> response) {
                        MissLotteryCode missLotteryCode = response.body().body;
                        if (missLotteryCode != null && missLotteryCode.missing_value != null && missLotteryCode.missing_value.size() > 0) {
                            Utils.parseMissValue(missLotteryCode.missing_value);
                            for (AwardBallInfo item : awardDanAdapter.getData()) {
                                item.isShowMissValue = true;
                            }
                            awardDanAdapter.notifyDataSetChanged();


                            for (AwardBallInfo item : awawrdTuoAdapter.getData()) {
                                item.isShowMissValue = true;
                            }
                            awawrdTuoAdapter.notifyDataSetChanged();

                        }
                    }


                    @Override
                    public void onError(Response response) {
                        if (!Utils.toastInfo(response).equals(Constants.ERROR_CODE_AWARD_EXPERID)) {
                            ToastUtils.showShort(Utils.toastInfo(response));
                        }
                    }
                });


    }


    public void setShowLotteryMiss() {
        if (getActivity() != null) {
            if (((LotteryFunnyThreeActivity) getActivity()).isShowMissValue) {
                if (Utils.getMissValues() != null && Utils.getMissValues().size() > 0) {
                    for (AwardBallInfo item : awardDanAdapter.getData()) {
                        item.isShowMissValue = true;
                    }
                    awardDanAdapter.notifyDataSetChanged();
                    for (AwardBallInfo item : awawrdTuoAdapter.getData()) {
                        item.isShowMissValue = true;
                    }
                    awawrdTuoAdapter.notifyDataSetChanged();
                } else {
                    getMissValue();
                }
            } else {
                for (AwardBallInfo item : awardDanAdapter.getData()) {
                    item.isShowMissValue = false;
                }

                for (AwardBallInfo item : awawrdTuoAdapter.getData()) {
                    item.isShowMissValue = false;
                }
                awardDanAdapter.notifyDataSetChanged();
                awawrdTuoAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (Utils.getMissValues() != null) {
            Utils.getMissValues().clear();
        }
        unbinder.unbind();
        super.onDestroy();
    }

}
