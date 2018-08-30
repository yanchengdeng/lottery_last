package com.top.lottery.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.activities.LotteryRecordDetailActivity;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.AwardRecordItem;

import java.util.List;

//中奖纪录 子项
public class AwardRecodeItemAdapter extends BaseQuickAdapter<AwardRecordItem, BaseViewHolder> {

    public AwardRecodeItemAdapter(int layoutResId, @Nullable List<AwardRecordItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final AwardRecordItem item) {
        if (!TextUtils.isEmpty(item.lid_title)) {
            ((TextView) helper.getView(R.id.tv_lottery_tittle)).setText(item.lid_title);
        }

        ((TextView) helper.getView(R.id.tv_open_times)).setText("第" + item.award_id + "期");


        if (!TextUtils.isEmpty(item.order_title)) {
            ((TextView) helper.getView(R.id.tv_lottery_type)).setText(item.order_title);
        }


        ((TextView) helper.getView(R.id.tv_intergray)).setText(  item.cost_score+" 积分");



        ((TextView)  helper.getView(R.id.tv_intergray)).setText(""+item.reward_title);


        helper.getView(R.id.rl_lottery_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.PASS_OBJECT,item);
                ActivityUtils.startActivity(bundle, LotteryRecordDetailActivity.class);
            }
        });
    }
}
