package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.MainWinCode;

import java.util.List;

//首页中奖号码
public class AwardCodeWinAdapter extends BaseQuickAdapter<MainWinCode, BaseViewHolder> {

    public AwardCodeWinAdapter(int layoutResId, @Nullable List<MainWinCode> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MainWinCode item) {
        ((TextView) helper.getView(R.id.tv_ball)).setText("" + item.code);
        helper.getView(R.id.tv_ball).setBackground(mContext.getResources().getDrawable(item.codeBg));

    }

}
