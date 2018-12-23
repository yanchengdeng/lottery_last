package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.LotteryType;
import com.top.lottery.utils.Utils;

import java.util.List;

//首页彩种
public class MainLotteryKindsAdapter extends BaseQuickAdapter<LotteryType, BaseViewHolder> {

    public MainLotteryKindsAdapter(int layoutResId, @Nullable List<LotteryType> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final LotteryType item) {
        if (!TextUtils.isEmpty(item.title)) {
            ((TextView) helper.getView(R.id.tv_name)).setText(item.title);
        }

        if (!TextUtils.isEmpty(item.short_title)) {
            ((TextView) helper.getView(R.id.tv_short_name)).setText(item.short_title);
        }

        if (!TextUtils.isEmpty(item.icon)) {
            Glide.with(Utils.context).load(item.icon).into((ImageView) helper.getView(R.id.iv_icon));
        }

    }

}
