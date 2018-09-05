package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.AwardOrderScore;

import java.util.List;

//订单详情里的  开奖号码 横向
public class LotteryOpenCodeHorAdapter extends BaseQuickAdapter<AwardOrderScore, BaseViewHolder> {

    public LotteryOpenCodeHorAdapter(int layoutResId, @Nullable List<AwardOrderScore> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final AwardOrderScore item) {
        ((TextView) helper.getView(R.id.tv_code)).setText("" + item.lottery_title);

        ((TextView) helper.getView(R.id.tv_name)).setText(""+item.number);


    }

}
