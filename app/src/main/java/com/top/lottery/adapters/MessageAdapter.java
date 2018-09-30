package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.MessageItem;

import java.util.List;

//彩球信息
public class MessageAdapter extends BaseQuickAdapter<MessageItem, BaseViewHolder> {
    public MessageAdapter(int layoutResId, @Nullable List<MessageItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageItem item) {


        ((TextView) helper.getView(R.id.tv_title)).setText("" + item.title);
        ((TextView) helper.getView(R.id.tv_date)).setText("" + item.create_time);
        if (item.is_read == 1) {
            ((TextView) helper.getView(R.id.tv_unread_tips)).setText(" ");
        } else {
            ((TextView) helper.getView(R.id.tv_unread_tips)).setText("【未读】");
        }
    }
}
