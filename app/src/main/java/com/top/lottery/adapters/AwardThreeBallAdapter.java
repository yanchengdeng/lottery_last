package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.AwardBallInfo;
import com.top.lottery.beans.MissValueInfo;
import com.top.lottery.utils.Utils;

import java.util.List;

//彩球信息  快三
public class AwardThreeBallAdapter extends BaseQuickAdapter<AwardBallInfo, BaseViewHolder> {
    public AwardThreeBallAdapter(int layoutResId, @Nullable List<AwardBallInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AwardBallInfo item) {

        if (!TextUtils.isEmpty(item.value)) {
            ((TextView) helper.getView(R.id.tv_values)).setText(item.value);
        }
        if (!TextUtils.isEmpty(item.price)) {
            ((TextView) helper.getView(R.id.tv_money)).setText("奖金" + item.price + "积分");
            helper.getView(R.id.tv_money).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.tv_money).setVisibility(View.GONE);
        }


        if (item.isSelected) {
            helper.getView(R.id.ll_bg).setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_red));
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.white));
            ((TextView) helper.getView(R.id.tv_money)).setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            helper.getView(R.id.ll_bg).setBackground(mContext.getResources().getDrawable(R.drawable.normal_submit_btn_gray_trans));
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.red));
            ((TextView) helper.getView(R.id.tv_money)).setTextColor(mContext.getResources().getColor(R.color.red));

        }
//        ((TextView) helper.getView(R.id.tv_values_miss)).setText(item.missValue);

        if (!item.isShowMissValue) {
            helper.getView(R.id.tv_values_miss).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.tv_values_miss).setVisibility(View.VISIBLE);
            try {
                if (!TextUtils.isEmpty( Utils.getMissValues().get(helper.getAdapterPosition()).value)){
                    List<MissValueInfo>  XXX = Utils.getMissValues();
                    String ss = item.value;

                    String values = XXX.get(helper.getAdapterPosition()).value;

                    LogUtils.w("dyc",XXX+ss+values);

                    ((TextView)helper.getView(R.id.tv_values_miss)).setText(Utils.getMissValuesByKey(item.value));
                }
            }catch (Exception e){

            }
        }
    }
}
