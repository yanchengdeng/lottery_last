package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.AcountBillIAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AcounBillIInfo;
import com.top.lottery.beans.AcountBillItem;
import com.top.lottery.beans.LotteryResponse;
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
 * Author: 邓言诚  Create at : 2018/9/1  22:53
 * Email: yanchengdeng@gmail.com
 * Describle:  分类
 */
public class AccountBillsTypeActivity extends BaseActivity {

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
    @BindView(R.id.tv_accout_tips)
    TextView tvAccoutTips;
    @BindView(R.id.recycle)
    RecyclerView recyclerView;

    private int page = 1;
    private AcountBillIAdapter acountBillIAdapter;
    TimePickerView start, end;
    private String start_date, end_date, score_type;
    private String title;
    private String uidChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_bills_type);
        ButterKnife.bind(this);
        title = getIntent().getStringExtra(Constants.PASS_NAME);
        score_type = getIntent().getStringExtra(Constants.PASS_STRING);
        uidChild = getIntent().getStringExtra(Constants.PASS_CHILD_UID);
        setTitle("" + title);
        tvAccoutTips.setText("" + title);


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


        recyclerView = getView(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        acountBillIAdapter = new AcountBillIAdapter(R.layout.adapter_bill_item_record, new ArrayList<AcountBillItem>());
        recyclerView.setAdapter(acountBillIAdapter);

        acountBillIAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getAcountData();
            }
        }, recyclerView);

        getAcountData();

    }

    /**
     * score_type/**
     * 不同的值可以获取不同的列表
     * 1：充值积分列表
     * 2：中间积分列表
     * 3：提现积分列表
     * 4：购彩支出列表
     * 6：累计代理返点
     */
    private void getAcountData() {

        final HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("page", String.valueOf(page));
        if (!TextUtils.isEmpty(start_date)) {
            data.put("start_date", start_date);
        }
        if (!TextUtils.isEmpty(score_type)) {
            data.put("score_type", score_type);
        }

        if (!TextUtils.isEmpty(uidChild)) {
            data.put("child_uid", uidChild);//下级用户的uid,当scrore_type为6时有效，用于下级人数列表中点击用户ID是跳出来的列表信息
        }

        if (!TextUtils.isEmpty(end_date)) {
            data.put("end_date", end_date);
        }
        OkGo.<LotteryResponse<AcounBillIInfo>>post(Constants.Net.SCORELOG_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<AcounBillIInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<AcounBillIInfo>> response) {
                        if (page == 1) {
                            acountBillIAdapter.getData().clear();
                        }
                        AcounBillIInfo awradRecordInterface = response.body().body;
                        List<AcountBillItem> awardRecordItemList = awradRecordInterface.list;
                        if (awardRecordItemList != null && awardRecordItemList.size() > 0) {
                            boolean isRefresh = page == 1;
                            setData(isRefresh, awardRecordItemList);
                        } else {
                            if (acountBillIAdapter.getData().size() > 0) {
                                acountBillIAdapter.loadMoreComplete();
                                acountBillIAdapter.loadMoreEnd();
                            } else {
                                acountBillIAdapter.setNewData(null);
                                acountBillIAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        if (acountBillIAdapter.getData().size() > 0) {
                            acountBillIAdapter.loadMoreComplete();
                            acountBillIAdapter.loadMoreEnd();
                        } else {
                            acountBillIAdapter.setNewData(null);
                            acountBillIAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                        }
                    }
                });
    }


    private void setData(boolean isRefresh, List data) {
        page++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            acountBillIAdapter.setNewData(data);
        } else {
            if (size > 0) {
                acountBillIAdapter.addData(data);
            } else {
                acountBillIAdapter.loadMoreComplete();
                acountBillIAdapter.loadMoreEnd();
            }
        }
        if (size < Constants.PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            acountBillIAdapter.loadMoreEnd(isRefresh);
        } else {
            acountBillIAdapter.loadMoreComplete();
        }
    }

    @OnClick({R.id.ll_start_time, R.id.ll_end_time, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_start_time:
                start.show();
                break;
            case R.id.ll_end_time:
                end.show();
                break;
            case R.id.tv_search:
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
                getAcountData();
                break;
        }
    }
}
