package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.OpenLotteryCode;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;

//最新开奖号
public class OpenLotteryCodeAdapter extends BaseQuickAdapter<OpenLotteryCode, BaseViewHolder> {

    public OpenLotteryCodeAdapter(int layoutResId, @Nullable List<OpenLotteryCode> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OpenLotteryCode item) {
        if (!TextUtils.isEmpty(item.id)) {
            ((TextView) helper.getView(R.id.tv_open_times)).setText("第"+item.id+"期");
        }

        if (!TextUtils.isEmpty(item.lottery_time)) {
            ((TextView) helper.getView(R.id.tv_open_date)).setText(item.lottery_time);
        }

        if (!TextUtils.isEmpty(item.prize_code)) {
//            item.prize_code = "06 05 03 10 09";
            String[] codes = item.prize_code.split(" ");

            ((TagGroup) helper.getView(R.id.tag_code)).setTags(codes);
        }

    }
}
