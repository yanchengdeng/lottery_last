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
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.BelowClassAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.BelowClassInfo;
import com.top.lottery.beans.BelowClassInfoItem;
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
 * Author: 邓言诚  Create at : 2018/9/1  23:12
 * Email: yanchengdeng@gmail.com
 * Describle: 下级人数
 */
public class BelowClassCountActivity extends BaseActivity {

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
    @BindView(R.id.recycle)
    RecyclerView recyclerView;

    private int page = 1;
    private BelowClassAdapter belowClassAdapter;
    TimePickerView start, end;
    private String start_date, end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_below_class_count);
        ButterKnife.bind(this);
        setTitle("下级用户列表");



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




        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        belowClassAdapter = new BelowClassAdapter(R.layout.adapter_below_class_item_record, new ArrayList<BelowClassInfoItem>());
        recyclerView.setAdapter(belowClassAdapter);

        belowClassAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getAcountData();
            }
        },recyclerView);


        belowClassAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!TextUtils.isEmpty(belowClassAdapter.getData().get(position).uid) && TextUtils.isDigitsOnly(belowClassAdapter.getData().get(position).uid)){
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PASS_NAME,belowClassAdapter.getData().get(position).uid+"账户返利明细");
                    bundle.putString(Constants.PASS_STRING,"6");
                    bundle.putString(Constants.PASS_CHILD_UID,belowClassAdapter.getData().get(position).uid);
                    ActivityUtils.startActivity(bundle,AccountBillsTypeActivity.class);
                }
            }
        });

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
//        data.put("child_uid", "");//下级用户的uid,当scrore_type为6时有效，用于下级人数列表中点击用户ID是跳出来的列表信息
        if (!TextUtils.isEmpty(end_date)) {
            data.put("end_date", end_date);
        }
        OkGo.<LotteryResponse<BelowClassInfo>>post(Constants.Net.SCORELOG_GETCHILDUSERLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<BelowClassInfo>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<BelowClassInfo>> response) {
                        BelowClassInfo belowClassInfo = response.body().body;
                        if (page==1){
                            belowClassAdapter.getData().clear();
                            List<BelowClassInfoItem> defaluts = new ArrayList<>();
                            BelowClassInfoItem belowTitle = new BelowClassInfoItem();
                            belowTitle.uid = "用户id";
                            belowTitle.reward_score = "中奖积分";
                            belowTitle.cost_score = "购彩支出";
                            belowTitle.daili_score = "历史贡献总返利";

                            BelowClassInfoItem belowCount = new BelowClassInfoItem();
                            belowCount.uid = "总计";
                            belowCount.reward_score = ""+belowClassInfo.sum.reward_score;
                            belowCount.cost_score = ""+belowClassInfo.sum.cost_score;
                            belowCount.daili_score = ""+belowClassInfo.sum.fanli_score;

                            defaluts.add(belowTitle);
                            defaluts.add(belowCount);
                            belowClassAdapter.setNewData(defaluts);

                        }
                        List<BelowClassInfoItem> awardRecordItemList = belowClassInfo.list;
                        if (awardRecordItemList != null && awardRecordItemList.size() > 0) {
                            boolean isRefresh = page == 1;
                            setData(isRefresh, awardRecordItemList);
                        } else {
                            if (belowClassAdapter.getData().size() > 0) {
                                belowClassAdapter.loadMoreComplete();
                                belowClassAdapter.loadMoreEnd();
                            } else {
                                belowClassAdapter.setNewData(null);
                                belowClassAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        if (belowClassAdapter.getData().size() > 0) {
                            belowClassAdapter.loadMoreComplete();
                            belowClassAdapter.loadMoreEnd();
                        } else {
                            belowClassAdapter.setNewData(null);
                            belowClassAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                        }
                    }
                });
    }


    private void setData(boolean isRefresh, List data) {
        page++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            belowClassAdapter.addData(data);
        } else {
            if (size > 0) {
                belowClassAdapter.addData(data);
            } else {
                belowClassAdapter.loadMoreComplete();
                belowClassAdapter.loadMoreEnd();
            }
        }
        if (size < Constants.PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            belowClassAdapter.loadMoreEnd(isRefresh);
        } else {
            belowClassAdapter.loadMoreComplete();
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
