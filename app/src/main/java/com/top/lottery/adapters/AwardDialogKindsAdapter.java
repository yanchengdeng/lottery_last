package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.LotteryPlayWay;

import java.util.List;

//曲线图   弹出种类
public class AwardDialogKindsAdapter extends BaseQuickAdapter<LotteryPlayWay, BaseViewHolder> {

    public AwardDialogKindsAdapter(int layoutResId, @Nullable List<LotteryPlayWay> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final LotteryPlayWay item) {
        ((TextView) helper.getView(R.id.tv_values)).setText("" + item.title);
        if (item.isSelect) {
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.white));
            ((TextView) helper.getView(R.id.tv_values)).setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red));
        } else {
            ((TextView) helper.getView(R.id.tv_values)).setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.color_tittle));
        }
    }

}
