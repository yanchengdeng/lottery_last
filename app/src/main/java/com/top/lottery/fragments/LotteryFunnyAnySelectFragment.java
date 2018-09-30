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
import com.top.lottery.activities.LotteryFunnyActivity;
import com.top.lottery.activities.TrendChartActivity;
import com.top.lottery.adapters.AwardBallAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.beans.CheckSelectCodeInfo;
import com.top.lottery.beans.LotteryInfo;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MechineChoosInfo;
import com.top.lottery.beans.MissLotteryCode;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

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
 * Describle: 任意选
 */
public class LotteryFunnyAnySelectFragment extends Fragment {

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
    private LotteryInfo lotteryInfo;
    private AwardBallAdapter awardBallAdapter;
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


        lotteryInfo = (LotteryInfo) getArguments().getSerializable(Constants.PASS_OBJECT);
        isMechineChoose = lotteryInfo.mechine == 1 ? true : false;
        initChangeButton();
        awardBallAdapter = new AwardBallAdapter(R.layout.adapter_lottery_select_num, new ArrayList<AwardBallInfo>());
        recycle.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        recycle.setAdapter(awardBallAdapter);
        awardBallAdapter.setNewData(Utils.get11Code());

        awardBallAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                awardBallAdapter.getData().get(position).isSelected = !awardBallAdapter.getData().get(position).isSelected;
                awardBallAdapter.notifyItemChanged(position);
                countIntergary();
            }
        });

        //走势图
        tvTrendChart.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                ActivityUtils.startActivity(TrendChartActivity.class);
            }
        });

        initAwardNum();

        return view;
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
        if (getSelectBalls().size() >= lotteryInfo.num) {
            tvIntergry.setText("积分：" + 2 * Utils.combination(getSelectBalls().size(), lotteryInfo.num));
            tvNoteNumbers.setText("注数：" + Utils.combination(getSelectBalls().size(), lotteryInfo.num));
            lotteryInfo.codes = getSelectBallsCode();
            lotteryInfo.TOUZHU_COUNT = Utils.combination(getSelectBalls().size(), lotteryInfo.num);
            lotteryInfo.TOUZHU_INTEGRY = 2 * Utils.combination(getSelectBalls().size(), lotteryInfo.num);
        } else {
            tvIntergry.setText("积分：0");
            tvNoteNumbers.setText("注数：0");
        }
    }

    //创建任意选球
    private void initAwardNum() {
        tvSelectTips.setText(String.format(getString(R.string.tips_for_anyselect), String.valueOf(lotteryInfo.num)));

    }

    //获取遗漏值
    private void getMissValue() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        data.put("award_id", Constants.LASTEST_AWARD_ID);// 最新彩种期数id
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
        data.put("lid", "1");
        data.put("lottery_id", lotteryInfo.lottery_id);// 最新彩种期数id
        OkGo.<LotteryResponse<MechineChoosInfo>>post(Constants.Net.CART_MECHINE)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MechineChoosInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MechineChoosInfo>> response) {
                        MechineChoosInfo mechineChoosInfo = response.body().body;
                        if (mechineChoosInfo != null && mechineChoosInfo.code != null && mechineChoosInfo.code.length > 0) {
                            showMechineChoose(mechineChoosInfo.code);
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
            if (((LotteryFunnyActivity) getActivity()).isShowMissValue) {
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
