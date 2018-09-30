package com.top.lottery.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.GridPeridAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.beans.ProfitStatices;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/9/26  00:02
 * Email: yanchengdeng@gmail.com
 * Describle:利润收益
 */
public class ProfitActivity extends BaseActivity {

    @BindView(R.id.tv_all_lottery)
    TextView tvAllLottery;
    @BindView(R.id.tv_period)
    TextView tvPeriod;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.ll_start_time)
    LinearLayout llStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.ll_end_time)
    LinearLayout llEndTime;
    @BindView(R.id.et_search_key)
    EditText etSearchKey;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_detal_contribution)
    TextView tvDetalContribution;
    @BindView(R.id.tv_profit_and_loss)
    TextView tvProfitAndLoss;
    @BindView(R.id.tv_total_daili_score)
    TextView tvTotalDailiScore;
    @BindView(R.id.tv_total_bonus_score)
    TextView tvTotalBonusScore;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.tv_credit_balance_score)
    TextView tvCreditBalanceScore;
    @BindView(R.id.tv_credit_line_score)
    TextView tvCreditLineScore;
    @BindView(R.id.tv_detal_achievments)
    TextView tvDetalAchievments;
    @BindView(R.id.tv_child_total)
    TextView tvChildTotal;
    @BindView(R.id.tv_child_cost_score)
    TextView tvChildCostScore;
    @BindView(R.id.tv_child_reward_score)
    TextView tvChildRewardScore;
    @BindView(R.id.tv_child_daili_score)
    TextView tvChildDailiScore;
    @BindView(R.id.tv_child_bonus_score)
    TextView tvChildBonusScore;
    @BindView(R.id.tv_child_recharge_score)
    TextView tvChildRechargeScore;
    @BindView(R.id.tv_child_score)
    TextView tvChildScore;
    @BindView(R.id.ll_profit_count)
    LinearLayout llProfitCount;
    @BindView(R.id.ll_profit_below_class)
    LinearLayout llProfitBelowClass;

    private String type;
    private String search_date="week", lid = "0", start_date, end_date;
    private List<LotteryType>  peridsData = new ArrayList<>();
    TimePickerView start, end;
    private PopupWindow PopallLottery, Popperidod;
    private GridView gridAllLottery, gridAllPeriod;
    private GridPeridAdapter gridLotteryTypeAdapter, gridPeriodTypeAdpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
        ButterKnife.bind(this);
        peridsData = Utils.gePeriData();
        setTitle("利润收益");
        tvRightFunction.setVisibility(View.VISIBLE);
        tvRightFunction.setText("收益参数 >");

        getStatic();

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


        tvAllLottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PopallLottery != null) {
                    PopallLottery.showAtLocation(viewRoot, Gravity.BOTTOM , 0, 0);;
                }
            }
        });


        tvPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Popperidod != null) {
                    Popperidod.showAtLocation(viewRoot, Gravity.BOTTOM , 0, 0);;
                }
            }
        });

        llStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.show();
            }
        });

        llEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end.show();
            }
        });

        tvRightFunction.setTextColor(getResources().getColor(R.color.white));
        tvRightFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(ProfitParamsActivity.class);

            }
        });


        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (TextUtils.isEmpty(start_date)) {
//                    ToastUtils.showShort("起始时间不能为空");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(end_date)) {
//                    ToastUtils.showShort("结束时间不能为空");
//                    return;
//                }
//
//                if (start_date.compareTo(end_date) > 0) {
//                    ToastUtils.showShort("结束时间小于起始时间");
//                    return;
//
//                }
                getStatic();
            }
        });

        /**
         * 1：贡献列表
         2：业绩列表
         */
        llProfitBelowClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_STRING,"2");
                ActivityUtils.startActivity(bundle,ProfitCountActivity.class);

            }
        });

        llProfitCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PASS_STRING,"1");
                ActivityUtils.startActivity(bundle,ProfitCountActivity.class);
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
                getStatic();
            }
        });


    }


    //弹出日期区间
    private void initPopALLPeriod() {
        View popGrid = LayoutInflater.from(mContext).inflate(R.layout.pop_period_view, null, false);
        gridAllPeriod = popGrid.findViewById(R.id.grid);
        gridPeriodTypeAdpter = new GridPeridAdapter(mContext, peridsData);
        gridAllPeriod.setAdapter(gridPeriodTypeAdpter);

        Popperidod = new PopupWindow(tvPeriod, ScreenUtils.getScreenWidth(), Utils.getPeriodPopHeight(mContext));
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
                Popperidod.dismiss();
                getStatic();
            }
        });
    }


    private void getStatic() {
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("search_date", search_date);//时间段选择：today：今天 week：一周 month：一个月 three_month：3个月
        if (!TextUtils.isEmpty(start_date)) {
            data.put("start_date", start_date);
        }

        if (!TextUtils.isEmpty(lid)) {
            data.put("lid", lid);
        }
        if (!TextUtils.isEmpty(end_date)) {
            data.put("end_date", end_date);
        }

        if (!TextUtils.isEmpty(etSearchKey.getEditableText().toString())) {
            data.put("search_key", etSearchKey.getEditableText().toString());
        }

        OkGo.<LotteryResponse<ProfitStatices>>post(Constants.Net.STATISTICS_GETSTATISTICSBYUID)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<ProfitStatices>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<ProfitStatices>> response) {
                        ProfitStatices profitStatices = response.body().body;
                        dismissLoadingBar();
                        if (profitStatices != null) {
                            initData(profitStatices);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        showError(Utils.toastInfo(response));
                    }
                });
    }

    private void initData(ProfitStatices profitStatices) {

        if (!TextUtils.isEmpty(profitStatices.child_bonus_score)) {
            tvChildBonusScore.setText(profitStatices.child_bonus_score);
        }

        if (!TextUtils.isEmpty(profitStatices.child_cost_score)) {
            tvChildCostScore.setText(profitStatices.child_cost_score);
        }

        if (!TextUtils.isEmpty(profitStatices.child_daili_score)) {
            tvChildDailiScore.setText(profitStatices.child_daili_score);
        }

        if (!TextUtils.isEmpty(profitStatices.child_recharge_score)) {
            tvChildRechargeScore.setText(profitStatices.child_recharge_score);
        }

        if (!TextUtils.isEmpty(profitStatices.child_reward_score)) {
            tvChildRewardScore.setText(profitStatices.child_reward_score);
        }

        if (!TextUtils.isEmpty(profitStatices.child_score)) {
            tvChildScore.setText(profitStatices.child_score);
        }

        if (!TextUtils.isEmpty(profitStatices.child_total)) {
            tvChildTotal.setText(profitStatices.child_total);
        }

        if (!TextUtils.isEmpty(profitStatices.credit_balance_score)) {
            tvCreditBalanceScore.setText(profitStatices.credit_balance_score);
        }

        if (!TextUtils.isEmpty(profitStatices.credit_line_score)) {
            tvCreditLineScore.setText(profitStatices.credit_line_score);
        }

        if (!TextUtils.isEmpty(profitStatices.profit_and_loss)) {
            tvProfitAndLoss.setText(profitStatices.profit_and_loss);
        }

        if (!TextUtils.isEmpty(profitStatices.score)) {
            tvScore.setText(profitStatices.score);
        }

        if (!TextUtils.isEmpty(profitStatices.total_bonus_score)) {
            tvTotalBonusScore.setText(profitStatices.total_bonus_score);
        }

        if (!TextUtils.isEmpty(profitStatices.total_daili_score)) {
            tvTotalDailiScore.setText(profitStatices.total_daili_score);
        }
    }

}
