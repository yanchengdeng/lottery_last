package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.MenuCenterItem;
import com.top.lottery.utils.Utils;

import java.util.List;

//账单明细
public class PercentCenterMenuAdapter extends BaseQuickAdapter<MenuCenterItem, BaseViewHolder> {

    public PercentCenterMenuAdapter(int layoutResId, @Nullable List<MenuCenterItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MenuCenterItem item) {
        ((TextView) helper.getView(R.id.tv_actions)).setText("" + item.title);

        Glide.with(Utils.context).load(item.icon).into((ImageView) helper.getView(R.id.iv_icon));

        if (item.isShowRedPoint){
            helper.getView(R.id.iv_read_status).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.iv_read_status).setVisibility(View.GONE);
        }

    }

}
