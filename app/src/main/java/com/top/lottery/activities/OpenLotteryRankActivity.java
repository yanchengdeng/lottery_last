package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.top.lottery.R;
import com.top.lottery.adapters.OpenLotteryCodeAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.LotteryResponse;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.beans.OpenLotteryCode;
import com.top.lottery.liseners.PerfectClickListener;
import com.top.lottery.utils.NewsCallback;
import com.top.lottery.utils.RecycleViewUtils;
import com.top.lottery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: 邓言诚  Create at : 2018/8/18  23:20
 * Email: yanchengdeng@gmail.com
 * Describle:开奖榜单
 */
public class OpenLotteryRankActivity extends BaseActivity {

    private int page = 1;

    private RecyclerView recyclerView;
    private OpenLotteryCodeAdapter openLotteryCodeAdapter;
    private OpenLotteryCode openLotteryCode ;
    private Button btnLottery;
    private TextView tvopenTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lottery_rank);

        setTitle("最新开奖");

        openLotteryCodeAdapter = new OpenLotteryCodeAdapter(R.layout.adapter_open_lottery_centre, new ArrayList<OpenLotteryCode>());
        recyclerView = getView(R.id.recycle);
        tvopenTitle = getView(R.id.tv_open_title);
        btnLottery = getView(R.id.bnt_do_lottery);
        openLotteryCode = (OpenLotteryCode) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        recyclerView.setAdapter(openLotteryCodeAdapter);

        if (openLotteryCode!=null){
            btnLottery.setText("投注"+openLotteryCode.lid_title);
        }
        if (openLotteryCode.lottery_type.equals("2")){
            tvopenTitle.setText("84期/天，每日每期10分钟开奖");
        }

        btnLottery.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                LotteryType lotteryType = new LotteryType();
                lotteryType.lottery_type = openLotteryCode.lottery_type;
                lotteryType.lid = openLotteryCode.lid;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_LOOTERY_TYPE,lotteryType);
                if (openLotteryCode.lottery_type.equals("1")){
                    //11选5
                    ActivityUtils.startActivity(bundle,LotteryFunnyActivity.class);

                }else if (openLotteryCode.lottery_type.equals("2")){
                    //快三
                    ActivityUtils.startActivity(bundle,LotteryFunnyThreeActivity.class);
                }

            }
        });


        openLotteryCodeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getLastestLotteryRank();
            }
        }, recyclerView);

        getLastestLotteryRank();
    }

    private void getLastestLotteryRank() {
        HashMap<String, String> data = new HashMap<>();
        data.put("uid", Utils.getUserInfo().uid);
        if (openLotteryCode!=null) {
            data.put("lottery_type",openLotteryCode.lottery_type);
        }
        data.put("page", String.valueOf(page));
        OkGo.<LotteryResponse<List<OpenLotteryCode>>>post(Constants.Net.AWARD_GETLIST)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(Utils.getParams(data))
                .execute(new NewsCallback<LotteryResponse<List<OpenLotteryCode>>>() {
                    @Override
                    public void onSuccess(Response<LotteryResponse<List<OpenLotteryCode>>> response) {
                        showContentView();
                        List<OpenLotteryCode> openLotteryCodes = response.body().body;
                        if (openLotteryCodes != null && openLotteryCodes.size() > 0) {
                            page++;
                            openLotteryCodeAdapter.addData(openLotteryCodes);
                            openLotteryCodeAdapter.loadMoreComplete();

                        } else {
                            if (openLotteryCodeAdapter.getData() != null && openLotteryCodeAdapter.getData().size() > 0) {
                                openLotteryCodeAdapter.loadMoreComplete();
                                openLotteryCodeAdapter.loadMoreEnd();
                            } else {
                                openLotteryCodeAdapter.setNewData(null);
                                openLotteryCodeAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                            }
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        if (openLotteryCodeAdapter.getData() != null && openLotteryCodeAdapter.getData().size() > 0) {
                            openLotteryCodeAdapter.loadMoreComplete();
                            openLotteryCodeAdapter.loadMoreEnd();
                        } else {
                            openLotteryCodeAdapter.setNewData(null);
                            openLotteryCodeAdapter.setEmptyView(RecycleViewUtils.getEmptyView(mContext, recyclerView));
                        }
                    }
                });
    }
}
