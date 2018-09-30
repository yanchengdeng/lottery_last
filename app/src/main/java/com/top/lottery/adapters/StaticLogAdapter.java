package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.StaticLogInfo;

import java.util.List;

//用户积分详情
public class StaticLogAdapter extends BaseQuickAdapter<StaticLogInfo, BaseViewHolder> {

    public StaticLogAdapter(int layoutResId, @Nullable List<StaticLogInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final StaticLogInfo item) {
        ((TextView) helper.getView(R.id.tv_tittle)).setText("" + item.title);

        ((TextView) helper.getView(R.id.tv_info)).setText("" + item.info);


    }

}
