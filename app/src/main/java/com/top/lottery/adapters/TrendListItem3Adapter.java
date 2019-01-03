package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.TerdNormalBall;

import java.util.List;

//趋势图设置  横向列表数据展示  快三
public class TrendListItem3Adapter extends BaseQuickAdapter<TerdNormalBall, BaseViewHolder> {
    private LinearLayout.LayoutParams params;

    public TrendListItem3Adapter(int layoutResId, @Nullable List<TerdNormalBall> data) {
        super(layoutResId, data);
        params = new LinearLayout.LayoutParams(ScreenUtils.getScreenWidth() / 12, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
    }

    @Override
    protected void convert(BaseViewHolder helper, TerdNormalBall item) {


        if (!TextUtils.isEmpty(item.awardId)) {
            ((TextView) helper.getView(R.id.tv_values)).setText(item.awardId);
            ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.color_info));
            helper.getView(R.id.tv_values).setBackground(null);
        } else {
            if (item.isAwardCode && item.isNeedRedCircle) {
                ((TextView) helper.getView(R.id.tv_values)).setText(item.value);
                ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.white));
                helper.getView(R.id.tv_values).setBackground(mContext.getResources().getDrawable(R.drawable.shap_circle_num_red_small));
            }else if (item.isAwardCode){
                ((TextView) helper.getView(R.id.tv_values)).setText(item.value);
                ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.blue_ball));
                helper.getView(R.id.tv_values).setBackground(null);
            }else if (item.isExtraValue){
                helper.getView(R.id.tv_values).setBackground(null);
                ((TextView) helper.getView(R.id.tv_values)).setText(item.value);
                ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.color_info));
            }else if (item.isCount) {
                helper.getView(R.id.tv_values).setBackground(null);
                if (TextUtils.isEmpty(item.missVlaue)) {
                    item.missVlaue = " ";
                }
                ((TextView) helper.getView(R.id.tv_values)).setText(item.missVlaue);
                ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.color_info));
            } else if (item.isCountDismiss){
                helper.getView(R.id.tv_values).setBackground(null);
                ((TextView) helper.getView(R.id.tv_values)).setText(" - ");
                ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.red));
            }else {
                helper.getView(R.id.tv_values).setBackground(null);
                ((TextView) helper.getView(R.id.tv_values)).setTextColor(mContext.getResources().getColor(R.color.color_date));
                if (item.isShowMiss) {
                    ((TextView) helper.getView(R.id.tv_values)).setText("" + item.missVlaue);
                } else {
                    ((TextView) helper.getView(R.id.tv_values)).setText("");
                }
            }
        }

//        helper.getView(R.id.ll_trend_ui).setLayoutParams(params);
    }
}
