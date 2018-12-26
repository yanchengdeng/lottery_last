package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.AwardBallInfo;

import java.util.List;

//彩球信息  快三 走势图
public class AwardThreeBallChartAdapter extends BaseQuickAdapter<AwardBallInfo, BaseViewHolder> {
    public AwardThreeBallChartAdapter(int layoutResId, @Nullable List<AwardBallInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardBallInfo item) {

        if (!TextUtils.isEmpty(item.value)) {
            ((TextView) helper.getView(R.id.tv_values)).setText(item.value);
        }
        if (item.isSelected) {
            helper.getView(R.id.ll_bg).setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red));
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            helper.getView(R.id.ll_bg).setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.red));
        }
    }
}
