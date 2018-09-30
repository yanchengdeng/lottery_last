package com.top.lottery.adapters;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.StaticListItem;

import java.util.List;

//会员统计
public class MemberStaticListAdapter extends BaseQuickAdapter<StaticListItem, BaseViewHolder> {

    public MemberStaticListAdapter(int layoutResId, @Nullable List<StaticListItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final StaticListItem item) {
        ((TextView) helper.getView(R.id.tv_uid)).setText("" + item.uid);

        ((TextView) helper.getView(R.id.tv_score_title)).setText("(" + item.score_title + ")");

        if (!TextUtils.isEmpty(item.score_type_color)) {
            ((TextView) helper.getView(R.id.tv_score_title)).setTextColor(Color.parseColor(item.score_type_color));
        }

        ((TextView) helper.getView(R.id.tv_score)).setText("" + item.score);

        ((TextView) helper.getView(R.id.tv_create_time)).setText("" + item.create_time);

        ((TextView) helper.getView(R.id.tv_accout_score)).setText("" + item.account_score);
    }

}
