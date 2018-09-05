package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.TrendSettingValues;

import java.util.List;

//走势图设置
public class TrendSettingAdapter extends BaseQuickAdapter<TrendSettingValues, BaseViewHolder> {

    public TrendSettingAdapter(int layoutResId, @Nullable List<TrendSettingValues> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final TrendSettingValues item) {

        ((TextView) helper.getView(R.id.tv_trend_setting)).setText("" + item.title);
        if (item.isSelected) {
            helper.getView(R.id.tv_trend_setting).setBackground(mContext.getResources().getDrawable(R.color.orange));
            ((TextView) helper.getView(R.id.tv_trend_setting)).setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            helper.getView(R.id.tv_trend_setting).setBackground(mContext.getResources().getDrawable(R.color.list_divider_color));
            ((TextView) helper.getView(R.id.tv_trend_setting)).setTextColor(mContext.getResources().getColor(R.color.color_tittle));
        }


    }

}
