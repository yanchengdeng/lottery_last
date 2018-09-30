package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.StaticLogAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.StaticListItem;
import com.top.lottery.beans.StaticLogInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.top.lottery.utils.Utils.getUserInfo;
/**
*
* Author: 邓言诚  Create at : 2018/9/27  21:09
* Email: yanchengdeng@gmail.com
* Describle: 用户积分详情
*/
public class StaticScoreLogActivity extends BaseActivity {


    private StaticListItem staticListItem;

    private StaticLogAdapter staticLogAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_score_log);
        recyclerView = getView(R.id.recycle);

        staticListItem = (StaticListItem) getIntent().getSerializableExtra(Constants.PASS_OBJECT);

        staticLogAdapter = new StaticLogAdapter(R.layout.adapter_static_log_record,new ArrayList<StaticLogInfo>());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        recyclerView.setAdapter(staticLogAdapter);

        setTitle("会员积分详情");


        getStaticLog();
    }

    private void getStaticLog() {
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);

        data.put("member_uid", staticListItem.uid);
        data.put("log_id", staticListItem.id);
        OkGo.<LotteryResponse<List<StaticLogInfo>>>post(Constants.Net.STATISTICS_DETAILSCORELOG)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<StaticLogInfo>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<StaticLogInfo>>> response) {
                        dismissLoadingBar();
                        List<StaticLogInfo> staticListItems = response.body().body;
                        if (staticListItems != null && staticListItems.size() > 0) {
                            staticLogAdapter.setNewData(staticListItems);
                        } else {
                            staticLogAdapter.setNewData(null);
                            staticLogAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        dismissLoadingBar();
                        ToastUtils.showShort(Utils.toastInfo(response));
                        staticLogAdapter.setNewData(null);
                        staticLogAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                    }
                });

    }
}
