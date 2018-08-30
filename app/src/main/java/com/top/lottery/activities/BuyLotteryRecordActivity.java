package com.top.lottery.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
    private int page = 1;
    private String[] dates = new String[]{"today", "week", "month", "three_month"};
    private String[] dates_info = new String[]{"今日", "一周", "1个月", "3个月"};
    private String search_date, lid, start_date, end_date;
    private List<AwardRecodList> awardRecodLists = new ArrayList<>();
    private AwardRecordAdapter awardRecordAdapter;
    private String type;
    TimePickerView start, end;
    private PopupWindow PopallLottery, Popperidod;
    private GridView gridAllLottery, gridAllPeriod;
    private GridPeridAdapter gridPeridAdapterLottery, gridPeridAdapterPeriod;
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
        // 1：购彩记录2：中奖记录3
        if ("1".equals(type)) {
            setTitle("购彩记录");
        } else {
            setTitle("中奖记录");
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


        getAwardRecord();
        getAllLottery();

        initPopALLPeriod();
        initLisener();
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
                page = 1;
                getAwardRecord();
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
                        List<LotteryType> lotteryTypes = response.body().body;
                        if (lotteryTypes != null && lotteryTypes.size() > 0) {
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
        gridPeridAdapterLottery = new GridPeridAdapter(mContext, lotteryTypes);
        gridAllLottery.setAdapter(gridPeridAdapterLottery);
        PopallLottery = new PopupWindow(tvAllLottery, ScreenUtils.getScreenWidth(), (int) (ScreenUtils.getScreenHeight() * 0.8));
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
                page = 1;
                getAwardRecord();
            }
        });


    }


    private void initPopALLPeriod() {
        View popGrid = LayoutInflater.from(mContext).inflate(R.layout.pop_period_view, null, false);
        gridAllPeriod = popGrid.findViewById(R.id.grid);
        gridPeridAdapterPeriod = new GridPeridAdapter(mContext, getSearchTime());
        gridAllPeriod.setAdapter(gridPeridAdapterPeriod);

        Popperidod = new PopupWindow(tvPeriod, ScreenUtils.getScreenWidth(), (int) (ScreenUtils.getScreenHeight() * 0.8));
        Popperidod.setContentView(popGrid);
        Popperidod.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x55000000);//
        Popperidod.setBackgroundDrawable(dw);
        gridAllPeriod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search_date = dates[position];
                tvPeriod.setText(dates_info[position]);
                Popperidod.dismiss();
                page = 1;
                getAwardRecord();
            }
        });
    }


    /**
     *
     */
    private void getAwardRecord() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("record_type", type);//1：购彩记录2：中奖记录3：追号记录
        data.put("page", String.valueOf(page));
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
        OkGo.<LotteryResponse<AwradRecordInterface>>post(Constants.Net.RECORD_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<AwradRecordInterface>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<AwradRecordInterface>> response) {
                        AwradRecordInterface awradRecordInterface = response.body().body;
                        List<AwardRecordItem> awardRecordItemList = awradRecordInterface.list;
                        if (awardRecordItemList != null && awardRecordItemList.size() > 0) {
                            boolean isRefresh = page == 1;
                            setData(isRefresh, parseData(awardRecordItemList), awardRecordItemList);
                        } else {
                            if (awardRecordAdapter.getData().size() > 0) {
                                awardRecordAdapter.loadMoreComplete();
                                awardRecordAdapter.loadMoreEnd();
                            } else {
                                awardRecordAdapter.setNewData(null);
                                awardRecordAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recycle));
                            }
                        }

                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        if (awardRecordAdapter.getData().size() > 0) {
                            awardRecordAdapter.loadMoreComplete();
                            awardRecordAdapter.loadMoreEnd();
                        } else {
                            awardRecordAdapter.setNewData(null);
                            awardRecordAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recycle));
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
                maps.put(item.create_date, new ArrayList<AwardRecordItem>());
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
                    PopallLottery.showAsDropDown(tvAllLottery, 0, 0);
                }
                break;
            case R.id.tv_period:
                if (Popperidod != null) {
                    Popperidod.showAsDropDown(tvPeriod, 0, 0);
                }
                break;
        }
    }

    //获取时间段
    private List<LotteryType> getSearchTime() {
        List<LotteryType> lotterySearchTimes = new ArrayList<>();
        for (String item : dates_info) {
            LotteryType lotterySearchTime = new LotteryType();
            lotterySearchTime.title = item;
            lotterySearchTimes.add(lotterySearchTime);
        }
        return lotterySearchTimes;
    }
}
