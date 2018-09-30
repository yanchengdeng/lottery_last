package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.top.lottery.adapters.MemberStaticAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.beans.MemberStaticItem;
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
 * Author: 邓言诚  Create at : 2018/9/13  23:39
 * Email: yanchengdeng@gmail.com
 * Describle: 会员统计
 */
public class MemberStaticActivity extends BaseActivity {

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
    RecyclerView recycle;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.tv_error_tips_new)
    TextView tvErrorTipsNew;
    private ManageMemberItem manageMemberItem;
    private MemberStaticAdapter memberStaticAdapter;

    TimePickerView start, end;
    private String start_date, end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_static);
        ButterKnife.bind(this);
        setTitle("积分统计");

        manageMemberItem = (ManageMemberItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);

        if (!TextUtils.isEmpty(manageMemberItem.uid)) {
            etInput.setText(manageMemberItem.uid);
        }

        View viewHeader = LayoutInflater.from(mContext).inflate(R.layout.header_static_member, null, false);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        memberStaticAdapter = new MemberStaticAdapter(R.layout.adapter_static_member, new ArrayList<MemberStaticItem>());
//        memberStaticAdapter.addHeaderView(viewHeader);
        recycle.setAdapter(memberStaticAdapter);

        memberStaticAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                memberStaticAdapter.getData().get(position).start_time = start_date;
                memberStaticAdapter.getData().get(position).end_time = end_date;
                bundle.putSerializable(Constants.PASS_OBJECT,memberStaticAdapter.getData().get(position));
                ActivityUtils.startActivity(bundle,MemberStaticListActivity.class);
            }
        });

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

        getStatics();
    }

    private void getStatics() {

        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        if (!TextUtils.isEmpty(start_date)) {
            data.put("start_date", start_date);
        }
        if (!TextUtils.isEmpty(etInput.getEditableText().toString())) {
            data.put("member_uid", etInput.getEditableText().toString());
        }
        if (!TextUtils.isEmpty(end_date)) {
            data.put("end_date", end_date);
        }
        OkGo.<LotteryResponse<List<MemberStaticItem>>>post(Constants.Net.STATISTICS_GETSCORESTATISTICSBYUID)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<MemberStaticItem>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<MemberStaticItem>>> response) {
                        dismissLoadingBar();
                        List<MemberStaticItem> memberStaticItems = response.body().body;
                        if (memberStaticItems != null && memberStaticItems.size() > 0) {
                            tvErrorTipsNew.setVisibility(View.GONE);
                            memberStaticAdapter.setNewData(memberStaticItems);
                        } else {
                            memberStaticAdapter.setNewData(new ArrayList<MemberStaticItem>());
                            tvErrorTipsNew.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        ToastUtils.showShort(Utils.toastInfo(response));
                        memberStaticAdapter.setNewData(new ArrayList<MemberStaticItem>());
                        tvErrorTipsNew.setVisibility(View.VISIBLE);
                    }
                });

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
                if (!TextUtils.isEmpty(etInput.getEditableText().toString())) {
                    if (etInput.getEditableText().toString().length() < 8) {
                        ToastUtils.showShort("请输入正确UID");
                        return;
                    }
                }
                getStatics();
                break;
        }
    }
}
