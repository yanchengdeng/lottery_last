package com.top.lottery.adapters;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.MemberStaticItem;

import java.util.List;

//会员统计
public class MemberStaticAdapter extends BaseQuickAdapter<MemberStaticItem, BaseViewHolder> {

    public MemberStaticAdapter(int layoutResId, @Nullable List<MemberStaticItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MemberStaticItem item) {
        ((TextView) helper.getView(R.id.tv_type)).setText("" + item.score_title);

        ((TextView) helper.getView(R.id.tv_total)).setText("" + item.score_total);

        if (!TextUtils.isEmpty(item.score_type_color)) {
            ((TextView) helper.getView(R.id.tv_type)).setTextColor(Color.parseColor(item.score_type_color));
        }
    }

}
