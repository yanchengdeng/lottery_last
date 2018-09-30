package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.MemberStaticListAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.MemberStaticItem;
import com.top.lottery.beans.StaticListItem;
import com.top.lottery.beans.StaticType;
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
 * Author: 邓言诚  Create at : 2018/9/27  16:52
 * Email: yanchengdeng@gmail.com
 * Describle: 积分统计
 */
public class MemberStaticListActivity extends BaseActivity {

    @BindView(R.id.et_input_uid)
    EditText etInputUid;
    @BindView(R.id.et_input_order_id)
    EditText etInputOrderId;
    @BindView(R.id.tv_order_type)
    TextView tvOrderType;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.recycle)
    RecyclerView recycle;
    private MemberStaticItem memberStaticItem;
    private String score_type;

    TimePickerView start, end;
    private String start_date, end_date;
    private int page = 1;
    private MemberStaticListAdapter memberStaticListAdapter;
    private OptionsPickerView<StaticType> optionsPickerView;
    private  List<StaticType> staticTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_static_list);
        ButterKnife.bind(this);
        setTitle("积分统计");
        memberStaticItem = (MemberStaticItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);


        if (!TextUtils.isEmpty(memberStaticItem.start_time)){
            start_date= memberStaticItem.start_time;
            tvStartTime.setText(start_date);
        }
        if (!TextUtils.isEmpty(memberStaticItem.end_time)){
            end_date = memberStaticItem.end_time;
            tvEndTime.setText(end_date);
        }
        score_type = memberStaticItem.score_type;
        tvOrderType.setText(""+memberStaticItem.score_title);
        etInputUid.setText(memberStaticItem.member_uid);
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


        memberStaticListAdapter = new MemberStaticListAdapter(R.layout.adapter_static_list_member,new ArrayList<StaticListItem>());
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        recycle.setAdapter(memberStaticListAdapter);

        memberStaticListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_OBJECT,memberStaticListAdapter.getData().get(position));
                ActivityUtils.startActivity(bundle,StaticScoreLogActivity.class);
            }
        });

        memberStaticListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                doSearchList();
            }
        },recycle);

        doSearchList();
        getStaticType();

    }

    @OnClick({R.id.tv_order_type, R.id.tv_start_time, R.id.tv_end_time, R.id.tv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_order_type:
                if (optionsPickerView!=null){
                    if (optionsPickerView.isShowing()){
                        optionsPickerView.dismiss();
                    }else{
                        optionsPickerView.show();
                    }
                }else{
                    ToastUtils.showShort("暂无统计类型");
                }
                break;
            case R.id.tv_start_time:
                start.show();
                break;
            case R.id.tv_end_time:
                end.show();
                break;
            case R.id.tv_search:
                page  = 1;
                doSearchList();
                break;
        }
    }

    private void doSearchList() {
        if (page==1) {
            showLoadingBar();
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        if (!TextUtils.isEmpty(start_date)) {
            data.put("start_date", start_date);
        }
        if (!TextUtils.isEmpty(etInputUid.getEditableText().toString())) {
            data.put("member_uid", etInputUid.getEditableText().toString());
        }
        if (!TextUtils.isEmpty(end_date)) {
            data.put("end_date", end_date);
        }

        if (!TextUtils.isEmpty(etInputOrderId.getEditableText().toString())) {
            data.put("order_id",etInputOrderId.getEditableText().toString());
        }
        data.put("page",String.valueOf(page));
        data.put("score_type",score_type);
        OkGo.<LotteryResponse<List<StaticListItem>>>post(Constants.Net.STATISTICS_GETSCOREDETAILLISTBYUID)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<StaticListItem>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<StaticListItem>>> response) {
                        dismissLoadingBar();
                        List<StaticListItem> staticListItems = response.body().body;
                        if (page==1){
                            memberStaticListAdapter.getData().clear();
                        }
                        if (staticListItems != null && staticListItems.size() > 0) {
//                            memberStaticListAdapter.setNewData(staticListItems);
                            boolean isRefresh = page == 1;
                            setData(isRefresh, staticListItems);

                        } else {
                            memberStaticListAdapter.setNewData(null);
                            memberStaticListAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext,recycle));
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        memberStaticListAdapter.setNewData(null);
                        memberStaticListAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext,recycle));
                    }
                });

    }

    private void setData( boolean isRefresh, List data) {
        page++;
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            this.memberStaticListAdapter.setNewData(data);
        } else {
            if (size > 0) {
                this.memberStaticListAdapter.addData(data);
            } else {
                memberStaticListAdapter.loadMoreComplete();
                memberStaticListAdapter.loadMoreEnd();
            }
        }
        if (size < Constants.PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            memberStaticListAdapter.loadMoreEnd(isRefresh);
        } else {
            memberStaticListAdapter.loadMoreComplete();
        }
    }


    //统计类型
    private void getStaticType(){
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);

        OkGo.<LotteryResponse<List<StaticType>>>post(Constants.Net.STATISTICS_GETSCORETYPELIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<StaticType>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<StaticType>>> response) {
                        staticTypes = response.body().body;
                        if (staticTypes!=null && staticTypes.size()>0){
                            initPickTypes();
                        }

                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        memberStaticListAdapter.setNewData(null);
                        memberStaticListAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext,recycle));
                    }
                });
    }

    private void initPickTypes() {
        optionsPickerView = new OptionsPickerBuilder(mContext, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
               tvOrderType.setText(staticTypes.get(options1).title);
               score_type = staticTypes.get(options1).score_type;
               page=1;
               doSearchList();

            }
        }).build();

        optionsPickerView.setPicker(staticTypes);
    }
}
