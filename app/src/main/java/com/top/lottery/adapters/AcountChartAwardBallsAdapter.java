package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.AwardBallInfo;

import java.util.List;

//走势图 球
public class AcountChartAwardBallsAdapter extends BaseQuickAdapter<AwardBallInfo, BaseViewHolder> {

    public AcountChartAwardBallsAdapter(int layoutResId, @Nullable List<AwardBallInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final AwardBallInfo item) {
        ((TextView) helper.getView(R.id.tv_values)).setText("" + item.value);

        if (item.isSelected) {
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.white));
            ((ImageView) helper.getView(R.id.iv_ball_bg)).setImageResource(R.mipmap.ball_red);
        }else{
            ((ImageView) helper.getView(R.id.iv_ball_bg)).setImageResource(R.mipmap.ball_gray);
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.color_tittle));
        }
    }

}
