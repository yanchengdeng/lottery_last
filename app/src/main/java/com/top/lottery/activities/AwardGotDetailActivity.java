package com.top.lottery.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.top.lottery.R;
import com.top.lottery.adapters.LotteryOpenCodeHorAdapter;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardOrderDetail;
import com.top.lottery.beans.AwardOrderScore;
import com.top.lottery.utils.RecycleViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: 邓言诚  Create at : 2018/9/2  21:42
 * Email: yanchengdeng@gmail.com
 * Describle: 中奖详情
 */
public class AwardGotDetailActivity extends BaseActivity {

    @BindView(R.id.recycle)
    RecyclerView recycle;
    @BindView(R.id.tv_count)
    TextView tvCount;
    private LotteryOpenCodeHorAdapter adapter;
    private AwardOrderDetail awardOrderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award_got_detail);
        ButterKnife.bind(this);
        setTitle("中奖详情");
        awardOrderDetail = (AwardOrderDetail) getIntent().getSerializableExtra(Constants.PASS_OBJECT);
        adapter = new LotteryOpenCodeHorAdapter(R.layout.adapter_award_open_item_hor, awardOrderDetail.records);
        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.setNestedScrollingEnabled(false);
        recycle.addItemDecoration(RecycleViewUtils.getItemDecoration(this));
        recycle.setAdapter(adapter);
        tvCount.setText("合计：" + getCount());

    }

    private String getCount() {
        int count = 0;
        for (AwardOrderScore item : awardOrderDetail.records) {
            count = count + Integer.parseInt(item.number);
        }
        return String.valueOf(count);
    }
}
