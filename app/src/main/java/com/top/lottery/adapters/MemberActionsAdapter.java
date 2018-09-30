package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.MemberTabsItem;

import java.util.List;

//账单明细
public class MemberActionsAdapter extends BaseQuickAdapter<MemberTabsItem, BaseViewHolder> {

    public MemberActionsAdapter(int layoutResId, @Nullable List<MemberTabsItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MemberTabsItem item) {
        ((TextView) helper.getView(R.id.tv_actions)).setText("" + item.title);

        if (!TextUtils.isEmpty(item.color)) {
            if (item.color.equals("green")) {

                helper.getView(R.id.tv_actions).setBackgroundColor(mContext.getResources().getColor(R.color.green));
            } else if (item.color.equals("orange")) {
                helper.getView(R.id.tv_actions).setBackgroundColor(mContext.getResources().getColor(R.color.orange));

            } else if (item.color.equals("blue")) {
                helper.getView(R.id.tv_actions).setBackgroundColor(mContext.getResources().getColor(R.color.color_blue));

            } else if (item.color.equals("orange")) {
                helper.getView(R.id.tv_actions).setBackgroundColor(mContext.getResources().getColor(R.color.orange));

            }
        }
        helper.addOnClickListener(R.id.tv_actions);
    }
}
