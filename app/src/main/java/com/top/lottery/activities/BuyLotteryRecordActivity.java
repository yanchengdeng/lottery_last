package com.top.lottery.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.AwardRecordAdapter;
import com.top.lottery.adapters.GridPeridAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardRecodList;
import com.top.lottery.beans.AwardRecordItem;
import com.top.lottery.beans.AwradRecordInterface;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
 * Author: 邓言诚  Create at : 2018/8/21  22:02
 * Email: yanchengdeng@gmail.com
 * Describle: 购彩记录
 */
public class BuyLotteryRecordActivity extends BaseActivity {


    @BindView(R.id.tv_all_lottery)
    TextView tvAllLottery;
    @BindView(R.id.tv_period)
    TextView tvPeriod;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.ll_error_refresh_else)
    LinearLayout llErrorRefresh;
    @BindView(R.id.line)
    TextView line;
    private int page = 1;


    private List<LotteryType> peridsData = new ArrayList<>();
    private List<LotteryType> statusData = new ArrayList<>();
    //如果是追号记录  则

    private String search_date, lid = "0", start_date, end_date, lottery_status;
    private List<AwardRecodList> awardRecodLists = new ArrayList<>();
    private AwardRecordAdapter awardRecordAdapter;
    private String type;
    TimePickerView start, end;
    private PopupWindow PopallLottery, Popperidod, PopulaStatus;
    private GridView gridAllLottery, gridAllPeriod, gridAllStatus;
    private GridPeridAdapter gridLotteryTypeAdapter, gridPeriodTypeAdpter, gridStatusAdapter;
    LinearLayout llStartTime;
    LinearLayout llEndTime;
    TextView tvStartTime;
    TextView tvEndTime;
    private View viewHead;
    TextView tvSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_lottery_record);
        ButterKnife.bind(this);
        type = getIntent().getStringExtra(Constants.PASS_STRING);
        peridsData = Utils.gePeriData();
        statusData = Utils.geStatusData();
        // 1：购彩记录2：中奖记录3
        if ("1".equals(type)) {
            setTitle("购彩记录");
            initPopALLPeriod();
            tvPeriod.setText(peridsData.get(1).title);
            search_date = peridsData.get(1).lottery_type;
        } else if ("2".equals(type)) {
            initPopALLPeriod();

            setTitle("中奖记录");
            tvPeriod.setText(peridsData.get(0).title);
            search_date = peridsData.get(0).lottery_type;
        } else {
            setTitle("追号记录");
            initPopALLStatus();
            tvPeriod.setText(statusData.get(0).title);
            lottery_status = statusData.get(0).lottery_type;
        }

        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        awardRecordAdapter = new AwardRecordAdapter(mContext, awardRecodLists);
        viewHead = LayoutInflater.from(mContext).inflate(R.layout.header_lottery_record, null, false);
        llStartTime = viewHead.findViewById(R.id.ll_start_time);
        llEndTime = viewHead.findViewById(R.id.ll_end_time);
        tvStartTime = viewHead.findViewById(R.id.tv_start_time);
        tvEndTime = viewHead.findViewById(R.id.tv_end_time);
        tvSearch = viewHead.findViewById(R.id.tv_search);
        awardRecordAdapter.addHeaderView(viewHead);
        recycle.setNestedScrollingEnabled(false);
        recycle.setAdapter(awardRecordAdapter);
        llErrorRefresh.setVisibility(View.GONE);


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


        awardRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getAwardRecord();
            }
        }, recycle);
        getAllLottery();


        initLisener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                getAwardRecord();
            }
        },300);

    }

    private void initLisener() {
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


        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(start_date)) {
                    ToastUtils.showShort("起始时间不能为空");
                    return;
                }

                if (TextUtils.isEmpty(end_date)) {
                    ToastUtils.showShort("结束时间不能为空");
                    return;
                }

                if (start_date.compareTo(end_date) > 0) {
                    ToastUtils.showShort("结束时间小于起始时间");
                    return;

                }

            }
        });
    }


    //获取所有彩种
    private void getAllLottery() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        OkGo.<LotteryResponse<List<LotteryType>>>post(Constants.Net.RECORD_GETLIDS)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<LotteryType>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<LotteryType>>> response) {
                        List<LotteryType> lotteryTypes = new ArrayList<>();
                        LotteryType allType = new LotteryType();
                        allType.isSelect = true;
                        allType.title = "所有彩种";
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
        PopallLottery = new PopupWindow(tvAllLottery, ScreenUtils.getScreenWidth(), Utils.getPeriodPopHeight(mContext));
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
                for (LotteryType item : lotteryTypes) {
                    item.isSelect = false;
                }
                lotteryTypes.get(position).isSelect = true;
                gridLotteryTypeAdapter.notifyDataSetChanged();
                page = 1;
                getAwardRecord();
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
                for (LotteryType item : peridsData) {
                    item.isSelect = false;
                }
                peridsData.get(position).isSelect = true;
                gridPeriodTypeAdpter.notifyDataSetChanged();
                Popperidod.dismiss();
                page = 1;
                getAwardRecord();
            }
        });
    }


    //弹出追号状态
    private void initPopALLStatus() {
        View popGrid = LayoutInflater.from(mContext).inflate(R.layout.pop_period_view, null, false);
        gridAllStatus = popGrid.findViewById(R.id.grid);
        gridStatusAdapter = new GridPeridAdapter(mContext, statusData);
        gridAllStatus.setAdapter(gridStatusAdapter);

        PopulaStatus = new PopupWindow(tvPeriod, ScreenUtils.getScreenWidth(), Utils.getPeriodPopHeight(mContext));
        PopulaStatus.setContentView(popGrid);
        PopulaStatus.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x55000000);//
        PopulaStatus.setBackgroundDrawable(dw);
        gridAllStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lottery_status = statusData.get(position).lottery_type;
                tvPeriod.setText(statusData.get(position).title);
                for (LotteryType item : statusData) {
                    item.isSelect = false;
                }
                statusData.get(position).isSelect = true;
                gridStatusAdapter.notifyDataSetChanged();
                PopulaStatus.dismiss();
                page = 1;
                getAwardRecord();
            }
        });
    }

    /**
     *
     */
    private void getAwardRecord() {
        if (page == 1) {
            showLoadingBar();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("record_type", type);//1：购彩记录2：中奖记录3：追号记录
        data.put("page", String.valueOf(page));
        data.put("search_date", search_date);//时间段选择：today：今天 week：一周 month：一个月 three_month：3个月
        if (!TextUtils.isEmpty(start_date)) {
            data.put("start_date", start_date);
        }
        if (!TextUtils.isEmpty(lottery_status)) {
            data.put("lottery_status", lottery_status);
        }
        if (!TextUtils.isEmpty(lid)) {
            data.put("lid", lid);
        }
        if (!TextUtils.isEmpty(end_date)) {
            data.put("end_date", end_date);
        }
        OkGo.<LotteryResponse<AwradRecordInterface>>post(Constants.Net.RECORD_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<AwradRecordInterface>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<AwradRecordInterface>> response) {
                        dismissLoadingBar();
                        if (page == 1) {
                            awardRecordAdapter.getData().clear();
                        }
                        AwradRecordInterface awradRecordInterface = response.body().body;
                        List<AwardRecordItem> awardRecordItemList = awradRecordInterface.list;
                        if (awardRecordItemList != null && awardRecordItemList.size() > 0) {
                            llErrorRefresh.setVisibility(View.GONE);
                            boolean isRefresh = page == 1;
                            setData(isRefresh, parseData(awardRecordItemList), awardRecordItemList);
                        } else {
                            if (awardRecordAdapter.getData().size() > 0) {
                                awardRecordAdapter.loadMoreComplete();
                                awardRecordAdapter.loadMoreEnd();
                                llErrorRefresh.setVisibility(View.GONE);
                            } else {
                                awardRecordAdapter.setNewData(new ArrayList<AwardRecodList>());
                                llErrorRefresh.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        ToastUtils.showShort(Utils.toastInfo(response));
                        if (awardRecordAdapter.getData().size() > 0) {
                            awardRecordAdapter.loadMoreComplete();
                            awardRecordAdapter.loadMoreEnd();
                            llErrorRefresh.setVisibility(View.GONE);
                        } else {
                            awardRecordAdapter.setNewData(new ArrayList<AwardRecodList>());
                            llErrorRefresh.setVisibility(View.VISIBLE);
//                            awardRecordAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recycle));
                        }
                    }
                });
    }


    private void setData(boolean isRefresh, List data, List<AwardRecordItem> awardRecordItemList) {
        page++;
        final int size = data == null ? 0 : awardRecordItemList.size();
        if (isRefresh) {
            awardRecordAdapter.setNewData(data);
        } else {
            if (size > 0) {
                awardRecordAdapter.addData(data);
            } else {
                awardRecordAdapter.loadMoreComplete();
                awardRecordAdapter.loadMoreEnd();
            }
        }
        if (size < Constants.PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            awardRecordAdapter.loadMoreEnd(isRefresh);
        } else {
            awardRecordAdapter.loadMoreComplete();
        }
    }


    //转化成可执行的数据
    private List<AwardRecodList> parseData(List<AwardRecordItem> awardRecordItemList) {
        List<AwardRecodList> awardRecodLists = new ArrayList<>();
        LinkedHashMap<String, List<AwardRecordItem>> maps = new LinkedHashMap<>();
        for (AwardRecordItem item : awardRecordItemList) {
            if (maps.containsKey(item.create_date)) {
                maps.get(item.create_date).add(item);
            } else {
                List<AwardRecordItem> newRecords = new ArrayList<AwardRecordItem>();
                newRecords.add(item);
                maps.put(item.create_date, newRecords);
            }
        }

        for (String entrySet : maps.keySet()) {

            if (maps.get(entrySet).size() > 0) {
                AwardRecodList awardRecodList_time = new AwardRecodList();
                awardRecodList_time.create_date = entrySet;
                awardRecodList_time.setItemType(AwardRecodList.TEXT);
                AwardRecodList awardRecodList_record = new AwardRecodList();
                awardRecodList_record.recordItems = maps.get(entrySet);
                awardRecodList_record.setItemType(AwardRecodList.RECYCLE);
                awardRecodLists.add(awardRecodList_time);
                awardRecodLists.add(awardRecodList_record);
            }
        }

        return awardRecodLists;
    }

    @OnClick({R.id.tv_all_lottery, R.id.tv_period})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_all_lottery:
                if (PopallLottery != null) {
                    PopallLottery.showAtLocation(viewRoot, Gravity.BOTTOM , 0, 0);
                }
                break;
            case R.id.tv_period:
                if ("1".equals(type) || "2".equals(type)) {
                    if (Popperidod != null) {
                        Popperidod.showAtLocation(viewRoot, Gravity.BOTTOM , 0, 0);
                    }
                } else if ("3".equals(type)) {
                    if (PopulaStatus != null) {
                        PopulaStatus.showAtLocation(viewRoot, Gravity.BOTTOM , 0, 0);
                    }
                }
                break;
        }
    }


}
