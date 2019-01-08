package com.top.lottery.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.AwardRecodList;

import java.util.List;

//中奖记录
public class AwardRecordAdapter extends BaseMultiItemQuickAdapter<AwardRecodList, BaseViewHolder> {


    public AwardRecordAdapter(Context context, List data) {
        super(data);
        addItemType(AwardRecodList.TEXT, R.layout.adapter_award_record);
        addItemType(AwardRecodList.RECYCLE, R.layout.adapter_award_record);
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardRecodList item) {

        if (helper.getItemViewType() == AwardRecodList.TEXT) {
            if (!TextUtils.isEmpty(item.create_date)) {
                ((TextView) helper.getView(R.id.tv_times)).setText(item.create_date);
                helper.getView(R.id.tv_times).setVisibility(View.VISIBLE);
            }
        } else {
            if (item.recordItems != null && item.recordItems.size() > 0) {
                helper.getView(R.id.tv_times).setVisibility(View.GONE);
                ((RecyclerView) helper.getView(R.id.recycle_item)).setLayoutManager(new LinearLayoutManager(mContext));
//                ((RecyclerView) helper.getView(R.id.recycle_item)).addItemDecoration(RecycleViewUtils.getItemDecoration(mContext));
                ((RecyclerView) helper.getView(R.id.recycle_item)).setAdapter(new AwardRecodeItemAdapter(R.layout.adapter_award_item_record, item.recordItems));
            }
        }
    }
}
