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
import android.widget.Button;
import android.widget.LinearLayout;
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
 * Describle:  快三  任意选
 */
public class LotteryFunnyThreeSelectFragment extends Fragment {

    @BindView(R.id.recycle)
    RecyclerView recycle;
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
    @BindView(R.id.tv_trend_chart)
    TextView tvTrendChart;
    @BindView(R.id.btn_big)
    Button btnBig;
    @BindView(R.id.btn_small)
    Button btnSmall;
    @BindView(R.id.btn_single)
    Button btnSingle;
    @BindView(R.id.btn_double)
    Button btnDouble;
    @BindView(R.id.ll_and_quick_ui)
    LinearLayout llAndQuickUi;
    private LotteryInfo lotteryInfo;
    private AwardThreeBallAdapter awardBallAdapter;
    private boolean isSelectSmall, isSelectBig, isSelectDouble, isSelectSigle;
    private boolean isMechineChoose;//是否可以允许机选  允许则可以机选  清除 切换  否则是可以清除操作


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lottery_funny_type, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);


        lotteryInfo = (LotteryInfo) getArguments().getSerializable(Constants.PASS_OBJECT);
        isMechineChoose = lotteryInfo.mechine == 1 ? true : false;
        initChangeButton();
        awardBallAdapter = new AwardThreeBallAdapter(R.layout.adapter_lottery_three_select_num, new ArrayList<AwardBallInfo>());


        initAwardNum();
        int spancout = Utils.getSpanCount(lotteryInfo.type);
        recycle.setLayoutManager(new GridLayoutManager(getActivity(), spancout));
        recycle.addItemDecoration(new GridSpacingItemDecoration(spancout,16,true));
        recycle.setAdapter(awardBallAdapter);
        awardBallAdapter.setNewData(Utils.parseThreeCodes(lotteryInfo,lotteryInfo.score));
        awardBallAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                btnBig.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnBig.setTextColor(getResources().getColor(R.color.red));
                btnSmall.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnSmall.setTextColor(getResources().getColor(R.color.red));
                btnDouble.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnDouble.setTextColor(getResources().getColor(R.color.red));
                btnSingle.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnSingle.setTextColor(getResources().getColor(R.color.red));
                isSelectBig = false;
                isSelectSmall = false;
                isSelectDouble = false;
                isSelectSigle = false;

                awardBallAdapter.getData().get(position).isSelected = !awardBallAdapter.getData().get(position).isSelected;
                awardBallAdapter.notifyItemChanged(position);
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

    //计算积分 一注  等于 2个积分
    private void countIntergary() {
//        if (getSelectBalls().size() >= lotteryInfo.num) {
        tvIntergry.setText("积分：" + 2 * Utils.combination(getSelectBalls().size(), lotteryInfo.num));
        tvNoteNumbers.setText("注数：" + Utils.combination(getSelectBalls().size(), lotteryInfo.num));
        lotteryInfo.codes = getSelectBallsCode();
        lotteryInfo.TOUZHU_COUNT = Utils.combination(getSelectBalls().size(), lotteryInfo.num);
        lotteryInfo.TOUZHU_INTEGRY = 2 * Utils.combination(getSelectBalls().size(), lotteryInfo.num);
//        } else {
//            tvIntergry.setText("积分：0");
//            tvNoteNumbers.setText("注数：0");
//        }
    }

    //创建任意选球
    private void initAwardNum() {
        if (lotteryInfo.type == 1) {
            tvSelectTips.setText("猜开奖号码相加的和");
            llAndQuickUi.setVisibility(View.VISIBLE);
        }else if (lotteryInfo.type==2){
            tvSelectTips.setText("猜豹子号(3个号相同)");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==3){
            tvSelectTips.setText("猜出一个豹子开出即中奖");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==5){
            tvSelectTips.setText("猜对子号(有两个号相同)\n复选：猜开奖中两个指定的相同号码");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==4){
            //todo
            tvSelectTips.setText("猜对子号(有两个号相同)\n单选：选择同号和不同号的组合");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==6){
            tvSelectTips.setText("选3个不同号码\n与开奖相同即中");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==7){
            tvSelectTips.setText("选2个不同号码\n猜中开奖任意2位即中");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==8){
            tvSelectTips.setText("123/234/345/456任一开出即中");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==9){
            //todo
            tvSelectTips.setText("选3个不同号码\n与开奖相同即中");
            llAndQuickUi.setVisibility(View.GONE);
        }else if (lotteryInfo.type==10){
            //todo
            tvSelectTips.setText("选2个不同号码\n与开奖任意两位相同即中");
            llAndQuickUi.setVisibility(View.GONE);
        }

    }

    //获取遗漏值
    private void getMissValue() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("award_id", Constants.LASTEST_AWARD_ID_THREE);// 最新彩种期数id
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
//                            JSONObject jsonObject = missLotteryCode.missing_value;
                            Utils.parseMissValue(missLotteryCode.missing_value);
                            for (AwardBallInfo item : awardBallAdapter.getData()) {
                                item.isShowMissValue = true;
                            }
                            awardBallAdapter.notifyDataSetChanged();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_choose_change, R.id.tv_confirm, R.id.btn_big, R.id.btn_small, R.id.btn_single, R.id.btn_double})
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
            case R.id.btn_big:
                isSelectBig = !isSelectBig;
                isSelectSmall = false;
                doSelectBig(true);
                if (isSelectBig){
                    btnBig.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                    btnBig.setTextColor(getResources().getColor(R.color.white));
                }else {
                    btnBig.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                    btnBig.setTextColor(getResources().getColor(R.color.red));
                }

                btnSmall.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnSmall.setTextColor(getResources().getColor(R.color.red));
                break;
            case R.id.btn_small:
                isSelectSmall = !isSelectSmall;
                isSelectBig = false;
                doSelectBig(false);
                if (isSelectSmall){
                    btnSmall.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                    btnSmall.setTextColor(getResources().getColor(R.color.white));
                }else{
                    btnSmall.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                    btnSmall.setTextColor(getResources().getColor(R.color.red));
                }

                btnBig.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnBig.setTextColor(getResources().getColor(R.color.red));
                break;
            case R.id.btn_single:
                isSelectSigle = !isSelectSigle;
                isSelectDouble = false;
                doSelectSingle(true);
                if (isSelectSigle){
                    btnSingle.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                    btnSingle.setTextColor(getResources().getColor(R.color.white));
                }else{
                    btnSingle.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                    btnSingle.setTextColor(getResources().getColor(R.color.red));
                }

                btnDouble.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnDouble.setTextColor(getResources().getColor(R.color.red));
                break;
            case R.id.btn_double:
                isSelectDouble = !isSelectDouble;
                isSelectSigle =false;
                doSelectSingle(false);
                if (isSelectDouble){
                    btnDouble.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_red));
                    btnDouble.setTextColor(getResources().getColor(R.color.white));
                }else{
                    btnDouble.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                    btnDouble.setTextColor(getResources().getColor(R.color.red));
                }
                btnSingle.setBackground(getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
                btnSingle.setTextColor(getResources().getColor(R.color.red));
                break;

        }
    }

    //选择大 小
    private void doSelectBig(boolean isBig) {
        for (AwardBallInfo item : awardBallAdapter.getData()) {
            item.isSelected = false;
        }
        if (!isSelectDouble && !isSelectSigle) {
            if (isBig) {
                for (AwardBallInfo item : awardBallAdapter.getData()) {
                    if (Integer.parseInt(item.value) > 10) {
                        item.isSelected = true;
                    }
                }
            } else {
                for (AwardBallInfo item : awardBallAdapter.getData()) {
                    if (Integer.parseInt(item.value) < 11) {
                        item.isSelected = true;
                    }
                }
            }
        } else {
            if (isBig) {
                if (isSelectSigle) {
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) > 10 && (Integer.parseInt(item.value) % 2 != 0)) {
                            item.isSelected = true;
                        }
                    }
                } else if (isSelectDouble) {
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) > 10 && (Integer.parseInt(item.value) % 2 == 0)) {
                            item.isSelected = true;
                        }
                    }
                }
            } else {
                if (isSelectSigle) {
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) < 11 && (Integer.parseInt(item.value) % 2 != 0)) {
                            item.isSelected = true;
                        }
                    }
                } else if (isSelectDouble) {
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) < 11 && (Integer.parseInt(item.value) % 2 == 0)) {
                            item.isSelected = true;
                        }
                    }
                }
            }

        }

        awardBallAdapter.notifyDataSetChanged();
        countIntergary();
    }

    //选择单双
    private void doSelectSingle(boolean isSingle) {
        for (AwardBallInfo item : awardBallAdapter.getData()) {
            item.isSelected = false;
        }
        if (!isSelectBig && !isSelectSmall) {
            if (isSelectSigle){
                for (AwardBallInfo item : awardBallAdapter.getData()) {
                    if ( Integer.parseInt(item.value) % 2 != 0) {
                        item.isSelected = true;
                    }
                }
            }else if (isSelectDouble){
                for (AwardBallInfo item : awardBallAdapter.getData()) {
                    if ( Integer.parseInt(item.value) % 2 == 0) {
                        item.isSelected = true;
                    }
                }
            }

        }else {
            if (isSingle) {
                if (isSelectBig) {
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) >10 && Integer.parseInt(item.value) % 2 != 0) {
                            item.isSelected = true;
                        }
                    }
                }else if (isSelectSmall){
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) <11 && Integer.parseInt(item.value) % 2 != 0) {
                            item.isSelected = true;
                        }
                    }
                }
            } else {
                if (isSelectBig) {
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) % 2 == 0 && Integer.parseInt(item.value)>10) {
                            item.isSelected = true;
                        }
                    }
                }else if (isSelectSmall){
                    for (AwardBallInfo item : awardBallAdapter.getData()) {
                        if (Integer.parseInt(item.value) % 2 == 0 && Integer.parseInt(item.value)<11) {
                            item.isSelected = true;
                        }
                    }
                }
            }
        }
        awardBallAdapter.notifyDataSetChanged();
        countIntergary();
    }

    //清除所选球
    private void clearSelectBalls() {
        for (AwardBallInfo item : awardBallAdapter.getData()) {
            item.isSelected = false;
        }
        awardBallAdapter.notifyDataSetChanged();
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
                        if (mechineChoosInfo != null && mechineChoosInfo.records != null && mechineChoosInfo.records.code != null && mechineChoosInfo.records.code.length > 0) {
                            showMechineChoose(mechineChoosInfo.records.code);
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

    //展示机选信息
    private void showMechineChoose(String[] codes) {
        for (AwardBallInfo awardBallInfo : awardBallAdapter.getData()) {
            awardBallInfo.isSelected = false;
        }

        for (AwardBallInfo awardBallInfo : awardBallAdapter.getData()) {
            for (String code : codes) {
                if (awardBallInfo.value.equals(code)) {
                    awardBallInfo.isSelected = true;
                }
            }
        }
        awardBallAdapter.notifyDataSetChanged();
        countIntergary();
    }

    private void checkSelect() {
        if (getSelectBalls().size() >= lotteryInfo.num) {
            if (getActivity() != null) {
                ((BaseActivity) getActivity()).showLoadingBar();
            }
            checkAwardId();
        } else {
            ToastUtils.showShort(tvSelectTips.getText().toString());
        }
    }


    //校验期号
    private void checkAwardId() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("lottery_type", lotteryInfo.lottery_type);// 彩种
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
        data.put("award_id", Constants.LASTEST_AWARD_ID_THREE);
        data.put("lottery_id", lotteryInfo.lottery_id);// 最新彩种期数id
        data.put("codes", new Gson().toJson(getSelectBallsCode()));
        OkGo.<LotteryResponse<CheckSelectCodeInfo>>post(Constants.Net.CART_ADDCART)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<CheckSelectCodeInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<CheckSelectCodeInfo>> response) {
                        clearSelectBalls();
                        isMechineChoose = lotteryInfo.mechine == 1;
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


    //已选择的球数
    private List<AwardBallInfo> getSelectBalls() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardBallAdapter != null && awardBallAdapter.getData().size() > 0) {
            for (AwardBallInfo item : awardBallAdapter.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }


    //获取所选球号
    private String[][] getSelectBallsCode() {
        List<AwardBallInfo> awardBallInfos = getSelectBalls();
        String[] codes = new String[awardBallInfos.size()];
        for (int i = 0; i < awardBallInfos.size(); i++) {
            codes[i] = awardBallInfos.get(i).value;
        }

        String[][] arrays = new String[1][1];
        arrays[0] = codes;
        return arrays;
    }

    //设置 遗漏值 是否显示
    public void setShowLotteryMiss() {
        if (getActivity() != null) {
            if (((LotteryFunnyThreeActivity) getActivity()).isShowMissValue) {
//                if (Utils.getMissValues()!=null && Utils.getMissValues().size()>0){
//                    for (AwardBallInfo item:awardBallAdapter.getData()){
//                        item.isShowMissValue = true;
//                    }
//                    awardBallAdapter.notifyDataSetChanged();
//                }else {
                getMissValue();
//                }
            } else {
                for (AwardBallInfo item : awardBallAdapter.getData()) {
                    item.isShowMissValue = false;
                }
                awardBallAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (Utils.getMissValues() != null) {
            Utils.getMissValues().clear();
        }
        super.onDestroy();
    }


}
