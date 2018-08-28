package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.utils.Utils;

import java.util.List;

//彩球信息
public class AwardBallAdapter extends BaseQuickAdapter<AwardBallInfo, BaseViewHolder> {
    public AwardBallAdapter(int layoutResId, @Nullable List<AwardBallInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardBallInfo item) {

        ((TextView) helper.getView(R.id.tv_values)).setText(item.value);


        if (item.isSelected){
            helper.getView(R.id.tv_values).setBackground(mContext.getResources().getDrawable(R.drawable.shap_circle_num_red));
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.white));
        }else{
            helper.getView(R.id.tv_values).setBackground(mContext.getResources().getDrawable(R.drawable.shap_circle_num_gray));
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.red));

        }
        ((TextView) helper.getView(R.id.tv_values_miss)).setText(item.missValue);

        if (!item.isShowMissValue) {
            helper.getView(R.id.tv_values_miss).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.tv_values_miss).setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty( Utils.getMissValues().get(item.value))){
                ((TextView)helper.getView(R.id.tv_values_miss)).setText(Utils.getMissValues().get(item.value));
            }
        }
    }
}
