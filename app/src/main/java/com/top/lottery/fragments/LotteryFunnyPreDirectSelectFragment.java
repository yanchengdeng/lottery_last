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
import com.blankj.utilcode.util.LogUtils;
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
 * Describle: 前 一  二   三 直选
 */
public class LotteryFunnyPreDirectSelectFragment extends Fragment {


    @BindView(R.id.tv_choose_change)
    TextView tvChooseChange;
    @BindView(R.id.tv_intergry)
    TextView tvIntergry;
    @BindView(R.id.tv_note_numbers)
    TextView tvNoteNumbers;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    Unbinder unbinder;
    @BindView(R.id.tv_select_tips_one)
    TextView tvSelectTipsOne;
    @BindView(R.id.recycle_one)
    RecyclerView recycleOne;
    @BindView(R.id.tv_select_tips_two)
    TextView tvSelectTipsTwo;
    @BindView(R.id.recycle_two)
    RecyclerView recycleTwo;
    @BindView(R.id.tv_select_tips_three)
    TextView tvSelectTipsThree;
    @BindView(R.id.recycle_three)
    RecyclerView recycleThree;
    @BindView(R.id.tv_trend_chart)
    TextView tvTrendChart;

    private LotteryInfo lotteryInfo;
    private AwardBallAdapter awardBallAdapterOne, awardBallAdapterTwo, awardBallAdapterThree;
    private boolean isMechineChoose;//是否可以允许机选  允许则可以机选  清除 切换  否则是可以清除操作

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lottery_predirect_type, container, false);
        unbinder = ButterKnife.bind(this, view);
        lotteryInfo = (LotteryInfo) getArguments().getSerializable(Constants.PASS_OBJECT);
        isMechineChoose = lotteryInfo.mechine == 1 ? true : false;
        isMechineChoose = false;


        awardBallAdapterOne = new AwardBallAdapter(R.layout.adapter_lottery_select_num, new ArrayList<AwardBallInfo>());
        recycleOne.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        recycleOne.setAdapter(awardBallAdapterOne);
        awardBallAdapterOne.setNewData(Utils.get11Code());

        awardBallAdapterOne.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                awardBallAdapterOne.getData().get(position).isSelected = !awardBallAdapterOne.getData().get(position).isSelected;
                awardBallAdapterOne.notifyItemChanged(position);
                countIntergary();
            }
        });


        awardBallAdapterTwo = new AwardBallAdapter(R.layout.adapter_lottery_select_num, new ArrayList<AwardBallInfo>());
        recycleTwo.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        recycleTwo.setAdapter(awardBallAdapterTwo);
        awardBallAdapterTwo.setNewData(Utils.get11Code());

        awardBallAdapterTwo.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                awardBallAdapterTwo.getData().get(position).isSelected = !awardBallAdapterTwo.getData().get(position).isSelected;
                awardBallAdapterTwo.notifyItemChanged(position);
                countIntergary();
            }
        });


        awardBallAdapterThree = new AwardBallAdapter(R.layout.adapter_lottery_select_num, new ArrayList<AwardBallInfo>());
        recycleThree.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        recycleThree.setAdapter(awardBallAdapterThree);
        awardBallAdapterThree.setNewData(Utils.get11Code());

        awardBallAdapterThree.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                awardBallAdapterThree.getData().get(position).isSelected = !awardBallAdapterThree.getData().get(position).isSelected;
                awardBallAdapterThree.notifyItemChanged(position);
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



        initChangeButton();
        initAwardNum();
//        getMissValue();
        return view;
    }


    //计算积分 一注  等于 2个积分
    private void countIntergary() {
        if (getSelectBalls().size() >= lotteryInfo.num) {
            int touzhuCount = 0;
            if (lotteryInfo.num == 1) {
                touzhuCount = Utils.combination(getSelectBalls().size(), lotteryInfo.num);
                tvIntergry.setText("积分：" + 2 * touzhuCount);
                tvNoteNumbers.setText("注数：" + touzhuCount);
                lotteryInfo.codes = getSelectBallsCode();
                lotteryInfo.TOUZHU_COUNT = touzhuCount;
                lotteryInfo.TOUZHU_INTEGRY = 2 * touzhuCount;
            }
            if (lotteryInfo.num == 2) {
                touzhuCount = Utils.combination(getAdpaterBallOne().size(), 1) * Utils.combination(getAdpaterBallTwo().size(), 1) - getDoubleAppear();
                tvIntergry.setText("积分：" + 2 * touzhuCount);
                tvNoteNumbers.setText("注数：" + touzhuCount);
                lotteryInfo.codes = getSelectBallsCode();
                lotteryInfo.TOUZHU_COUNT = touzhuCount;
                lotteryInfo.TOUZHU_INTEGRY = 2 * touzhuCount;
            }

            if (lotteryInfo.num == 3) {
                //TODO  直选三种类型的算法
                touzhuCount = getDoubleAppear();
                tvIntergry.setText("积分：" + 2 * touzhuCount);
                tvNoteNumbers.setText("注数：" + touzhuCount);
                lotteryInfo.codes = getSelectBallsCode();
                lotteryInfo.TOUZHU_COUNT = touzhuCount;
                lotteryInfo.TOUZHU_INTEGRY = 2 * touzhuCount;
            }

        } else {
            tvIntergry.setText("积分：0");
            tvNoteNumbers.setText("注数：0");
        }
    }


    //去掉n 组 两两组合中有重复的 个数
    private int getDoubleAppear() {

        int i = 0;
        if (lotteryInfo.num == 2) {
            if (getAdpaterBallOne().size() == 0) {
                i = 0;
            }
            if (getAdpaterBallTwo().size() == 0) {
                i = 0;
            }
            for (AwardBallInfo itemOne : getAdpaterBallOne()) {

                for (AwardBallInfo itemTwo : getAdpaterBallTwo()) {
                    if (itemOne.value.equals(itemTwo.value)) {
                        i++;
                    }
                }

            }

        }


        if (lotteryInfo.num == 3) {
            if (getAdpaterBallOne().size() == 0) {
                i = 0;
            }
            if (getAdpaterBallTwo().size() == 0) {
                i = 0;
            }

            if (getAdpaterBallThree().size() == 0) {
                i = 0;
            }
            for (AwardBallInfo itemOne : getAdpaterBallOne()) {

                for (AwardBallInfo itemTwo : getAdpaterBallTwo()) {

                    for (AwardBallInfo itemThree : getAdpaterBallThree()) {
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


    //已选择的球数
    private List<AwardBallInfo> getSelectBalls() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardBallAdapterOne != null && awardBallAdapterOne.getData().size() > 0) {
            for (AwardBallInfo item : awardBallAdapterOne.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }

        if (awardBallAdapterTwo != null && awardBallAdapterTwo.getData().size() > 0) {
            for (AwardBallInfo item : awardBallAdapterTwo.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }

        if (awardBallAdapterThree != null && awardBallAdapterThree.getData().size() > 0) {
            for (AwardBallInfo item : awardBallAdapterThree.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }

//        HashSet<String> codesSet = new HashSet<>();
//        for (AwardBallInfo item:awardBallInfos){
//            codesSet.add(item.value);
//        }
        return awardBallInfos;
    }


    //第一个所选球
    private List<AwardBallInfo> getAdpaterBallOne() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardBallAdapterOne != null && awardBallAdapterOne.getData().size() > 0) {
            for (AwardBallInfo item : awardBallAdapterOne.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }

    //第二个所选球
    private List<AwardBallInfo> getAdpaterBallTwo() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardBallAdapterTwo != null && awardBallAdapterTwo.getData().size() > 0) {
            for (AwardBallInfo item : awardBallAdapterTwo.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }

    //第三个所选球
    private List<AwardBallInfo> getAdpaterBallThree() {
        List<AwardBallInfo> awardBallInfos = new ArrayList<>();
        if (awardBallAdapterThree != null && awardBallAdapterThree.getData().size() > 0) {
            for (AwardBallInfo item : awardBallAdapterThree.getData()) {
                if (item.isSelected) {
                    awardBallInfos.add(item);
                }
            }
        }
        return awardBallInfos;
    }


    //获取所选球号
    private String[][] getSelectBallsCode() {
        String[][] arrays = new String[lotteryInfo.num][1];
        String[] codesone = new String[getAdpaterBallOne().size()];
        for (int i = 0; i < getAdpaterBallOne().size(); i++) {
            codesone[i] = getAdpaterBallOne().get(i).value;
        }
        arrays[0] = codesone;
        if (lotteryInfo.num == 2) {
            String[] codetwo = new String[getAdpaterBallTwo().size()];
            for (int i = 0; i < getAdpaterBallTwo().size(); i++) {
                codetwo[i] = getAdpaterBallTwo().get(i).value;
            }
            arrays[1] = codetwo;
        } else if (lotteryInfo.num == 3) {
            String[] codetwo = new String[getAdpaterBallTwo().size()];
            for (int i = 0; i < getAdpaterBallTwo().size(); i++) {
                codetwo[i] = getAdpaterBallTwo().get(i).value;
            }
            arrays[1] = codetwo;

            String[] codethree = new String[getAdpaterBallThree().size()];
            for (int i = 0; i < getAdpaterBallThree().size(); i++) {
                codethree[i] = getAdpaterBallThree().get(i).value;
                arrays[2] = codethree;
            }

        }
        return arrays;
    }


    //创建任意选球
    private void initAwardNum() {
        if (lotteryInfo.num == 1) {
            tvSelectTipsOne.setText("请至少选择一个号码");
            tvSelectTipsTwo.setVisibility(View.GONE);
            tvSelectTipsThree.setVisibility(View.GONE);
            recycleTwo.setVisibility(View.GONE);
            recycleThree.setVisibility(View.GONE);
        } else if (lotteryInfo.num == 2) {
            tvSelectTipsOne.setText("第一位");
            tvSelectTipsTwo.setVisibility(View.VISIBLE);
            tvSelectTipsThree.setVisibility(View.GONE);
            recycleTwo.setVisibility(View.VISIBLE);
            recycleThree.setVisibility(View.GONE);

        } else if (lotteryInfo.num == 3) {
            tvSelectTipsOne.setText("第一位");
            tvSelectTipsTwo.setVisibility(View.VISIBLE);
            tvSelectTipsThree.setVisibility(View.VISIBLE);
            recycleTwo.setVisibility(View.VISIBLE);
            recycleThree.setVisibility(View.VISIBLE);

        }

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
        if (lotteryInfo.num == 1) {
            if (getAdpaterBallOne().size() == 0) {
                ToastUtils.showShort("请至少选择一个号码");
                return;
            }
        }

        if (lotteryInfo.num == 2) {
            if (getAdpaterBallOne().size() == 0) {
                ToastUtils.showShort("请选择第一位");
                return;
            }

            if (getAdpaterBallTwo().size() == 0) {
                ToastUtils.showShort("请选择第二位");
                return;
            }
        }

        if (lotteryInfo.num == 3) {
            if (getAdpaterBallOne().size() == 0) {
                ToastUtils.showShort("请选择第一位");
                return;
            }

            if (getAdpaterBallTwo().size() == 0) {
                ToastUtils.showShort("请选择第二位");
                return;
            }
            if (getAdpaterBallThree().size() == 0) {
                ToastUtils.showShort("请选择第三位");
                return;
            }
        }


        if (getSelectBalls().size() >= lotteryInfo.num) {
            if (getActivity() != null) {
                ((BaseActivity) getActivity()).showLoadingBar();
            }
            checkAwardId();
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
        for (AwardBallInfo item : awardBallAdapterOne.getData()) {
            item.isSelected = false;
        }
        awardBallAdapterOne.notifyDataSetChanged();

        for (AwardBallInfo item : awardBallAdapterTwo.getData()) {
            item.isSelected = false;
        }
        awardBallAdapterTwo.notifyDataSetChanged();

        for (AwardBallInfo item : awardBallAdapterThree.getData()) {
            item.isSelected = false;
        }
        awardBallAdapterThree.notifyDataSetChanged();
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
//                            showMechineChoose(mechineChoosInfo.code);
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

    //设置 遗漏值 是否显示
    public void setShowLotteryMiss() {
        if (getActivity() != null) {
            if (((LotteryFunnyActivity) getActivity()).isShowMissValue) {
//                if (Utils.getMissValues()!=null && Utils.getMissValues().size()>0){
//                    for (AwardBallInfo item:awardBallAdapterOne.getData()){
//                        item.isShowMissValue = true;
//                    }
//                    awardBallAdapterOne.notifyDataSetChanged();
//                }else {
                getMissValue();
//                }
            } else {
                for (AwardBallInfo item : awardBallAdapterOne.getData()) {
                    item.isShowMissValue = false;
                }
                awardBallAdapterOne.notifyDataSetChanged();

                for (AwardBallInfo item : awardBallAdapterTwo.getData()) {
                    item.isShowMissValue = false;
                }
                awardBallAdapterTwo.notifyDataSetChanged();

                for (AwardBallInfo item : awardBallAdapterThree.getData()) {
                    item.isShowMissValue = false;
                }
                awardBallAdapterThree.notifyDataSetChanged();
            }
        }
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
                            Utils.parseMissValue(missLotteryCode.missing_value);
                            for (AwardBallInfo item : awardBallAdapterOne.getData()) {
                                item.isShowMissValue = true;
                            }
                            for (AwardBallInfo item : awardBallAdapterTwo.getData()) {
                                item.isShowMissValue = true;
                            }
                            for (AwardBallInfo item : awardBallAdapterThree.getData()) {
                                item.isShowMissValue = true;
                            }
                            awardBallAdapterOne.notifyDataSetChanged();
                            awardBallAdapterTwo.notifyDataSetChanged();
                            awardBallAdapterThree.notifyDataSetChanged();

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
}
