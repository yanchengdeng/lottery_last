package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.BelowClassInfoItem;

import java.util.List;

//下级人数
public class BelowClassAdapter extends BaseQuickAdapter<BelowClassInfoItem, BaseViewHolder> {

    public BelowClassAdapter(int layoutResId, @Nullable List<BelowClassInfoItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final BelowClassInfoItem item) {
        ((TextView) helper.getView(R.id.tv_user_id)).setText("" + item.uid);
        ((TextView) helper.getView(R.id.tv_user_award)).setText("" + item.reward_score);
        ((TextView) helper.getView(R.id.tv_bill_pay)).setText("" + item.cost_score);
        ((TextView) helper.getView(R.id.tv_bill_daili)).setText("" + item.daili_score);


    }

}
