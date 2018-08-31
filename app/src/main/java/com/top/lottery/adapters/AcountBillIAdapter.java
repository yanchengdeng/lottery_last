package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.AcountBillItem;

import java.util.List;

//账单明细
public class AcountBillIAdapter extends BaseQuickAdapter<AcountBillItem, BaseViewHolder> {

    public AcountBillIAdapter(int layoutResId, @Nullable List<AcountBillItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final AcountBillItem item) {
        ((TextView) helper.getView(R.id.tv_bill_tittle)).setText("" + item.remark);

        ((TextView) helper.getView(R.id.tv_date)).setText("" + item.create_time);

        ((TextView) helper.getView(R.id.tv_status)).setText("" + item.score);

        if (!TextUtils.isEmpty(item.color)) {
            if (item.color.equals("green")) {
                ((TextView) helper.getView(R.id.tv_status)).setTextColor(mContext.getResources().getColor(R.color.color_blue));
            } else {
                ((TextView) helper.getView(R.id.tv_status)).setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }
    }
}
