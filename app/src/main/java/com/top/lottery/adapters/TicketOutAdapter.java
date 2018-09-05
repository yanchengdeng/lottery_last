package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.TicketOutInfo;

import java.util.List;

//订单  出票列表
public class TicketOutAdapter extends BaseQuickAdapter<TicketOutInfo, BaseViewHolder> {
    public TicketOutAdapter(int layoutResId, @Nullable List<TicketOutInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TicketOutInfo item) {


        ((TextView) helper.getView(R.id.tv_terms)).setText("第" + item.award_id + "期 (" + item.record_times + "倍, " + item.reward_score + "积分）");
        ((TextView) helper.getView(R.id.tv_create_time)).setText("" + item.reward_title);

    }
}