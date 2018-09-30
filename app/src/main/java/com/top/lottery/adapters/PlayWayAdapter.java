package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.LotteryPlayWay;

import java.util.List;

//玩法
public class PlayWayAdapter extends BaseQuickAdapter<LotteryPlayWay, BaseViewHolder> {
    private LinearLayout.LayoutParams layoutParams;
    public PlayWayAdapter(int layoutResId, @Nullable List<LotteryPlayWay> data) {
        super(layoutResId, data);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWidth() /6);

    }

    @Override
    protected void convert(BaseViewHolder helper, final LotteryPlayWay item) {
        ((TextView) helper.getView(R.id.tv_actions)).setText("" + item.title);
        helper.getView(R.id.tv_actions).setLayoutParams(layoutParams);


    }

}
