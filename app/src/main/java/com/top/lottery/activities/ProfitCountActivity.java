package com.top.lottery.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.GridPeridAdapter;
import com.top.lottery.adapters.StaticContributeAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.beans.MemberDetail;
import com.top.lottery.beans.StaticsDetailInfo;
import com.top.lottery.beans.StaticsDetailListInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/9/27  22:47
 * Email: yanchengdeng@gmail.com
 * Describle: 利益汇总
 */
public class ProfitCountActivity extends BaseActivity {

    @BindView(R.id.tv_all_lottery)
    TextView tvAllLottery;
    @BindView(R.id.tv_period)
    TextView tvPeriod;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.ll_start_time)
    LinearLayout llStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_end_time)
    LinearLayout llEndTime;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_uid)
    TextView tvUid;
    @BindView(R.id.tv_host_type)
    TextView tvHostType;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_accout_intergray)
    TextView tvAccoutIntergray;
    @BindView(R.id.tv_credit_count)
    TextView tvCreditCount;
    @BindView(R.id.tv_fanli_count)
    TextView tvFanliCount;
    @BindView(R.id.tv_fenhong_count)
    TextView tvFenhongCount;
    @BindView(R.id.tv_count_buy_lottery)
    TextView tvCountBuyLottery;
    @BindView(R.id.tv_count_got_lottery)
    TextView tvCountGotLottery;
    @BindView(R.id.tv_count_fanli)
    TextView tvCountFanli;
    @BindView(R.id.tv_count_yingli)
    TextView tvCountYingli;
    @BindView(R.id.tv_count_recharge)
    TextView tvCountRecharge;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.ll_static_info)
    LinearLayout llStaticInfo;
    @BindView(R.id.tv_aciton_recharge)
    TextView tvAcitonRecharge;
    @BindView(R.id.tv_aciton_withdraw)
    TextView tvAcitonWithdraw;
    @BindView(R.id.tv_aciton_static)
    TextView tvAcitonStatic;
    @BindView(R.id.tv_aciton_setting)
    TextView tvAcitonSetting;
    @BindView(R.id.tv_aciton_contribute)
    TextView tvAcitonContribute;
    @BindView(R.id.tv_aciton_harvest)
    TextView tvAcitonHarvest;
    @BindView(R.id.tv_below_nums)
    TextView tvBelowNums;
    @BindView(R.id.tv_profxy_count_info)
    TextView tvProfxyCountInfo;
    @BindView(R.id.ll_below_member_info)
    LinearLayout llBelowMemberInfo;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.ll_actions_ui)
    LinearLayout llActionsUi;
    /**
     * 1：贡献列表
     * 2：业绩列表
     */
    private String profitType;
    private int page = 1;
    private String search_date = "week", lid = "0", start_date, end_date;
    TimePickerView start, end;
    private PopupWindow PopallLottery, Popperidod;
    private GridView gridAllLottery, gridAllPeriod;
    private GridPeridAdapter gridLotteryTypeAdapter, gridPeriodTypeAdpter;
    private String member_uid;
    private StaticContributeAdapter adapterStatic;
    private View viewHeader;
    private List<LotteryType>  peridsData = new ArrayList<>();

    private TextView tvUidHeader, tvTwo, tvThree, tvFour, tvFive, tvSix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_count);
        ButterKnife.bind(this);


        profitType = getIntent().getStringExtra(Constants.PASS_STRING);
        member_uid = getIntent().getStringExtra(Constants.PASS_CHILD_UID);

        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constants.PASS_START_TIME))) {
            start_date = getIntent().getStringExtra(Constants.PASS_START_TIME);
            tvStartTime.setText(start_date);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constants.PASS_END_TIME))) {
            end_date = getIntent().getStringExtra(Constants.PASS_END_TIME);
            tvEndTime.setText(end_date);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constants.PASS_LOOTERY_TYPE))) {
            lid = getIntent().getStringExtra(Constants.PASS_LOOTERY_TYPE);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra(Constants.PASS_WEEK_TYPE))){
            search_date = getIntent().getStringExtra(Constants.PASS_WEEK_TYPE);
        }

        if (!TextUtils.isEmpty(member_uid)) {
            etInput.setText(member_uid);
        }



        peridsData = Utils.gePeriData();
        if (profitType.equals("1")) {
            setTitle("详细贡献");
        } else {
            setTitle("详细业绩");
        }

        for (LotteryType item:peridsData){
            if (item.lottery_type.equals(search_date)){
                item.isSelect = true;
                tvPeriod.setText(item.title);
            }else{
                item.isSelect = false;
            }
        }

        initHeaderView();

        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        adapterStatic = new StaticContributeAdapter(R.layout.adapter_static_member_contribute, profitType, new ArrayList<StaticsDetailListInfo>());
        adapterStatic.addHeaderView(viewHeader);
        recycle.setAdapter(adapterStatic);

        adapterStatic.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_STRING,profitType);
                bundle.putString(Constants.PASS_CHILD_UID,adapterStatic.getData().get(position).uid);
                ActivityUtils.startActivity(bundle,ProfitCountActivity.class);
            }
        });

        getAllLottery();
        initPopALLPeriod();
        //时间选择器
        start = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                start_date = TimeUtils.date2String(date, new SimpleDateFormat("yyyy-MM-dd"));
                tvStartTime.setText("" + start_date);
            }
        }).build();


        end = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                end_date = TimeUtils.date2String(date, new SimpleDateFormat("yyyy-MM-dd"));
                tvEndTime.setText("" + end_date);
            }
        }).build();

        getProfitlist();
    }


    private void initHeaderView() {
        viewHeader = LayoutInflater.from(mContext).inflate(R.layout.header_static_member_contribute, null, false);
        tvUidHeader = viewHeader.findViewById(R.id.tv_below_class_tittle);
        tvTwo = viewHeader.findViewById(R.id.tv_two);
        tvThree = viewHeader.findViewById(R.id.tv_three);
        tvFour = viewHeader.findViewById(R.id.tv_four);
        tvFive = viewHeader.findViewById(R.id.tv_five);
        tvSix = viewHeader.findViewById(R.id.tv_six);

        if (profitType.equals("1")) {
            tvTwo.setText("贡献返利");
            tvThree.setText("贡献分红");
            tvFour.setText("信用额度");
            tvFive.setText("下级");
            tvSix.setVisibility(View.GONE);
        } else {
            tvSix.setVisibility(View.VISIBLE);
            tvTwo.setText("总购彩");
            tvThree.setText("总中奖");
            tvFour.setText("总返利");
            tvFive.setText("总充值");
            tvSix.setText("下级");
        }

    }


    private void getProfitlist() {
        if (page == 1) {
            showLoadingBar();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("search_date", search_date);//时间段选择：today：今天 week：一周 month：一个月 three_month：3个月
        if (!TextUtils.isEmpty(start_date)) {
            data.put("start_date", start_date);
        }

        if (!TextUtils.isEmpty(member_uid)) {
            data.put("member_uid", member_uid);
        }

        if (!TextUtils.isEmpty(lid)) {
            data.put("lid", lid);
        }
        if (!TextUtils.isEmpty(end_date)) {
            data.put("end_date", end_date);
        }

        if (!TextUtils.isEmpty(etInput.getEditableText().toString())) {
            data.put("search_key", etInput.getEditableText().toString());
        }

        data.put("list_type", profitType);

        data.put("page", String.valueOf(page));
        data.put("page_size",String.valueOf(Integer.MAX_VALUE));


        OkGo.<LotteryResponse<StaticsDetailInfo>>post(Constants.Net.STATISTICS_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<StaticsDetailInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<StaticsDetailInfo>> response) {
                        StaticsDetailInfo staticsDetailInfo = response.body().body;
                        dismissLoadingBar();
                        if (staticsDetailInfo != null) {
                            initData(staticsDetailInfo);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        recycle.setVisibility(View.GONE);
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    private void initData(StaticsDetailInfo staticsDetailInfo) {
        if (staticsDetailInfo.member != null) {
            llStaticInfo.setVisibility(View.VISIBLE);
            tvUid.setText(staticsDetailInfo.member.uid);
            member_uid = staticsDetailInfo.member.uid;
            tvHostType.setText(staticsDetailInfo.member.user_type_title);
            tvNickname.setText("昵称：" + staticsDetailInfo.member.nickname);
            tvPhone.setText("手机：" + staticsDetailInfo.member.mobile);
            tvAccoutIntergray.setText("账户积分：" + staticsDetailInfo.member.score);
            tvCreditCount.setText("信用额度：" + staticsDetailInfo.member.credit_line_score);
            tvFanliCount.setText("贡献返利：" + staticsDetailInfo.member.contribute_daili_score);
            tvFenhongCount.setText("贡献分红：" + staticsDetailInfo.member.contribute_bonus_score);
            tvCountBuyLottery.setText("" + staticsDetailInfo.member.child_cost_score);
            tvCountGotLottery.setText("" + staticsDetailInfo.member.child_reward_score);
            tvCountFanli.setText("" + staticsDetailInfo.member.member_daili_score);
            tvCountYingli.setText("" + staticsDetailInfo.member.member_bonus_score);
            tvCountRecharge.setText("" + staticsDetailInfo.member.member_recharge_score);
            tvRemark.setText("" + staticsDetailInfo.member.remark);
        }

        if (staticsDetailInfo.sum != null) {
            llBelowMemberInfo.setVisibility(View.VISIBLE);
            tvBelowNums.setText("" + staticsDetailInfo.sum.child_total);
            StringBuilder stringBuilder = new StringBuilder();
            tvUidHeader.setText(""+staticsDetailInfo.sum.child_user_type_title);
            if (!TextUtils.isEmpty(staticsDetailInfo.sum.child_district_total)) {
                stringBuilder.append("区域代理：" + staticsDetailInfo.sum.child_district_total).append("人 ").append(" | ");
            }

            if (!TextUtils.isEmpty(staticsDetailInfo.sum.child_leader_total)) {
                stringBuilder.append("  店主：" + staticsDetailInfo.sum.child_leader_total).append("人 ").append(" | ");
            }

            if (!TextUtils.isEmpty(staticsDetailInfo.sum.child_member_total)) {
                stringBuilder.append("   普通会员：" + staticsDetailInfo.sum.child_member_total).append("人 ");
            }
            tvProfxyCountInfo.setText(stringBuilder.toString());
        }

        if (!TextUtils.isEmpty(member_uid)) {
            llActionsUi.setVisibility(View.VISIBLE);
        }

        if (staticsDetailInfo.list != null && staticsDetailInfo.list.size() > 0) {
            adapterStatic.setNewData(staticsDetailInfo.list);
        }else{
            recycle.setVisibility(View.GONE);
        }


    }

    @OnClick({R.id.tv_all_lottery, R.id.tv_period, R.id.ll_start_time, R.id.ll_end_time, R.id.tv_search, R.id.tv_aciton_recharge, R.id.tv_aciton_withdraw, R.id.tv_aciton_static, R.id.tv_aciton_setting, R.id.tv_aciton_contribute, R.id.tv_aciton_harvest})
    public void onViewClicked(View view) {
        String actionType;
        Bundle bundle = new Bundle();
        switch (view.getId()) {
            case R.id.tv_all_lottery:
                if (PopallLottery != null) {
                    PopallLottery.showAtLocation(viewRoot, Gravity.BOTTOM , 0, 0);;
                }
                break;
            case R.id.tv_period:
                if (Popperidod != null) {
                    Popperidod.showAtLocation(viewRoot, Gravity.BOTTOM , 0, 0);;
                }
                break;
            case R.id.ll_start_time:
                start.show();
                break;
            case R.id.ll_end_time:
                end.show();
                break;
            case R.id.tv_search:
                page = 1;
                getProfitlist();
                break;
            case R.id.tv_aciton_recharge:
                actionType = "deposit";
                getMemberDetail(actionType, "充值");
                break;
            case R.id.tv_aciton_withdraw:
                actionType = "withdraw";
                getMemberDetail(actionType, "提现");
                break;
            case R.id.tv_aciton_static:
                actionType = "statistics";
                getMemberDetail(actionType, "统计");
                break;
            case R.id.tv_aciton_setting:
                actionType = "setAuth";
                getMemberDetail(actionType, "设置");
                break;
            case R.id.tv_aciton_contribute:


                if (!TextUtils.isEmpty(member_uid)) {
                    bundle.putString(Constants.PASS_CHILD_UID,member_uid);
                }
                bundle.putString(Constants.PASS_STRING,"1");
                ActivityUtils.startActivity(bundle,ProfitCountActivity.class);
                break;
            case R.id.tv_aciton_harvest:

                if (!TextUtils.isEmpty(member_uid)) {
                    bundle.putString(Constants.PASS_CHILD_UID,member_uid);
                }
                bundle.putString(Constants.PASS_STRING,"2");
                ActivityUtils.startActivity(bundle,ProfitCountActivity.class);
                break;
        }
    }


    private void getMemberDetail(final String actionType, final String tittle) {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        if (!TextUtils.isEmpty(member_uid)) {
            data.put("member_uid", member_uid);
        }
        OkGo.<LotteryResponse<MemberDetail>>post(Constants.Net.LEADER_GETUSERINFO)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<MemberDetail>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<MemberDetail>> response) {
                        MemberDetail memberDetail = response.body().body;
                        if (memberDetail != null) {
                            ManageMemberItem manageMemberItem = new ManageMemberItem();
                            manageMemberItem.uid = member_uid;
                            manageMemberItem.type = actionType;
                            manageMemberItem.title = tittle;
                            manageMemberItem.score = memberDetail.score;

                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.PASS_OBJECT, manageMemberItem);

                            if (actionType.equals("deposit")) {
                                //充值
                                ActivityUtils.startActivity(bundle, RechargeWithdrawActivity.class);
                            } else if (actionType.equals("withdraw")) {
                                //提现
                                ActivityUtils.startActivity(bundle, RechargeWithdrawActivity.class);
                            } else if (actionType.equals("statistics")) {
                                //统计
                                ActivityUtils.startActivity(bundle, MemberStaticActivity.class);
                            } else if (actionType.equals("setAuth")) {
                                //设置权限
                                ActivityUtils.startActivity(bundle, MemberSettingActivity.class);
                            }

                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    //获取所有彩种
    private void getAllLottery() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<List<LotteryType>>>post(Constants.Net.LOTTERY_GETLIDS)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotteryType>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotteryType>>> response) {
                        List<LotteryType> lotteryTypes = new ArrayList<>();
                        LotteryType allType = new LotteryType();
                        allType.isSelect = true;
                        allType.title="所有彩种";
                        allType.lid = "0";
                        lotteryTypes.add(allType);
                        List<LotteryType> lotteryResponse = response.body().body;
                        if (lotteryResponse != null && lotteryResponse.size() > 0) {
                            lotteryTypes.addAll(lotteryResponse);

                            for (LotteryType item:lotteryTypes){
                                if (item.lid.equals(lid)){
                                    item.isSelect = true;
                                    tvAllLottery.setText(item.title);
                                }else{
                                    item.isSelect = false;
                                }
                            }

                            initPopAllLottery(lotteryTypes);
                        }

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                    }
                });
    }

    private void initPopAllLottery(final List<LotteryType> lotteryTypes) {
        View popGrid = LayoutInflater.from(mContext).inflate(R.layout.pop_period_view, null, false);
        gridAllLottery = popGrid.findViewById(R.id.grid);
        gridLotteryTypeAdapter = new GridPeridAdapter(mContext, lotteryTypes);
        gridAllLottery.setAdapter(gridLotteryTypeAdapter);
        PopallLottery = new PopupWindow(tvAllLottery, ScreenUtils.getScreenWidth(),  Utils.getPeriodPopHeight(mContext));
        PopallLottery.setContentView(popGrid);
        PopallLottery.setFocusable(true);
        PopallLottery.setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(0x55000000);//
        PopallLottery.setBackgroundDrawable(dw);
        gridAllLottery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lid = lotteryTypes.get(position).lid;
                PopallLottery.dismiss();
                tvAllLottery.setText(lotteryTypes.get(position).title);
                for (LotteryType item:lotteryTypes){
                    item.isSelect = false;
                }
                lotteryTypes.get(position).isSelect = true;
                gridLotteryTypeAdapter.notifyDataSetChanged();
                page = 1;
                getProfitlist();
            }
        });


    }


    //弹出日期区间
    private void initPopALLPeriod() {
        View popGrid = LayoutInflater.from(mContext).inflate(R.layout.pop_period_view, null, false);
        gridAllPeriod = popGrid.findViewById(R.id.grid);
        gridPeriodTypeAdpter = new GridPeridAdapter(mContext, peridsData);
        gridAllPeriod.setAdapter(gridPeriodTypeAdpter);

        Popperidod = new PopupWindow(tvPeriod, ScreenUtils.getScreenWidth(),  Utils.getPeriodPopHeight(mContext));
        Popperidod.setContentView(popGrid);
        Popperidod.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x55000000);//
        Popperidod.setBackgroundDrawable(dw);
        gridAllPeriod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search_date = peridsData.get(position).lottery_type;
                tvPeriod.setText(peridsData.get(position).title);
                for (LotteryType item:peridsData){
                    item.isSelect = false;
                }
                peridsData.get(position).isSelect = true;
                gridPeriodTypeAdpter.notifyDataSetChanged();
                page = 1;
                Popperidod.dismiss();
                getProfitlist();
            }
        });
    }

}
