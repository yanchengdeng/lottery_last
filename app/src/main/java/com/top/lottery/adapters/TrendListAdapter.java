package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.TrendNormalInfo;
import com.top.lottery.utils.RecycleViewUtils;

import java.util.List;

//走势图 列表适配器
public class TrendListAdapter extends BaseQuickAdapter<TrendNormalInfo, BaseViewHolder> {

    public TrendListAdapter(int layoutResId, @Nullable List<TrendNormalInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TrendNormalInfo item) {

        if (helper.getLayoutPosition() / 2 == 0) {
            helper.getView(R.id.recycle_trend_data).setBackgroundColor(mContext.getResources().getColor(R.color.list_divider_color));
        } else {
            helper.getView(R.id.recycle_trend_data).setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        ((RecyclerView) helper.getView(R.id.recycle_trend_data)).setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));
        ((RecyclerView) helper.getView(R.id.recycle_trend_data)).addItemDecoration(RecycleViewUtils.getItemDecoration(mContext));
        ((RecyclerView) helper.getView(R.id.recycle_trend_data)).setAdapter(new TrendListItemAdapter(R.layout.adapter_trends_item_num, item.lists));


    }
}
