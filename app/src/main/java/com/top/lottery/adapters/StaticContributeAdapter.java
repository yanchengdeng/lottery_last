package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.StaticsDetailListInfo;

import java.util.List;

//账单明细
public class StaticContributeAdapter extends BaseQuickAdapter<StaticsDetailListInfo, BaseViewHolder> {
    private String staticType;

    public StaticContributeAdapter(int layoutResId, String staticType, @Nullable List<StaticsDetailListInfo> data) {
        super(layoutResId, data);
        this.staticType = staticType;
    }

    /**
     * contribute_daili_score
     * 贡献返利
     * contribute_bonus_score
     * 贡献分红
     * child_total
     * 下级数量
     * credit_line_score
     * 下级信用额度
     */
    @Override
    protected void convert(BaseViewHolder helper, final StaticsDetailListInfo item) {
        ((TextView) helper.getView(R.id.tv_one)).setText(item.uid);
        if (staticType.equals("1")) {
            //贡献
            ((TextView) helper.getView(R.id.tv_two)).setText(item.contribute_daili_score);
            ((TextView) helper.getView(R.id.tv_three)).setText(item.contribute_bonus_score);
            ((TextView) helper.getView(R.id.tv_four)).setText(item.child_total);
            ((TextView) helper.getView(R.id.tv_five)).setText(item.credit_line_score);
            helper.getView(R.id.tv_six).setVisibility(View.GONE);

        } else {
            //业绩
/**
 * child_cost_score
 child_reward_score
 member_daili_score
 member_recharge_score

 */
            ((TextView) helper.getView(R.id.tv_two)).setText(item.child_cost_score);
            ((TextView) helper.getView(R.id.tv_three)).setText(item.child_reward_score);
            ((TextView) helper.getView(R.id.tv_four)).setText(item.member_daili_score);
            ((TextView) helper.getView(R.id.tv_five)).setText(item.member_recharge_score);
            ((TextView) helper.getView(R.id.tv_six)).setText(item.credit_line_score);
            helper.getView(R.id.tv_six).setVisibility(View.VISIBLE);
        }

    }

}
