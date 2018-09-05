package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.TicketOutAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardOrderDetail;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.TicketOutInfo;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.top.lottery.utils.Utils.getUserInfo;

/**
*
* Author: 邓言诚  Create at : 2018/9/5  14:00
* Email: yanchengdeng@gmail.com
* Describle:出票列表
*/
public class TicketOutListActivity extends BaseActivity {


    private RecyclerView recyclerView;

    private AwardOrderDetail awardOrderDetail;

    private TicketOutAdapter ticketOutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_out_list);
        setTitle("出票详情");

        recyclerView = getView(R.id.recycle);
        awardOrderDetail = (AwardOrderDetail) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        ticketOutAdapter = new TicketOutAdapter(R.layout.adpter_ticket_out,new ArrayList<TicketOutInfo>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        recyclerView.setAdapter(ticketOutAdapter);

        getTicketList();

        ticketOutAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_OBJECT,ticketOutAdapter.getData().get(position));
                ActivityUtils.startActivity(bundle,TicketDetailActivity.class);
            }
        });

    }

    private void getTicketList() {
        showLoadingBar();
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", getUserInfo().uid);
        data.put("order_id", awardOrderDetail.order_id);
        OkGo.<LotteryResponse<List<TicketOutInfo>>>post(Constants.Net.RECORD_CHASELIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<TicketOutInfo>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<TicketOutInfo>>> response) {
                       showContentView();
                       dismissLoadingBar();
                        List<TicketOutInfo>  ticketOutInfos = response.body().body;
                        if (ticketOutInfos!=null && ticketOutInfos.size()>0){
                            ticketOutAdapter.setNewData(ticketOutInfos);
                        }else{
                            ticketOutAdapter.setNewData(null);
                            ticketOutAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext,recyclerView));
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        ToastUtils.showShort(Utils.toastInfo(response));
                        showError();
                        dismissLoadingBar();
                        ticketOutAdapter.setNewData(null);
                        ticketOutAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext,recyclerView));
                    }
                });
    }
}
