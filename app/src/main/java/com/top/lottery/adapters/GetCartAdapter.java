package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.GetCart;

import java.util.List;

//购物车列表
public class GetCartAdapter extends BaseQuickAdapter<GetCart.CartItem, BaseViewHolder> {
    public GetCartAdapter(int layoutResId, @Nullable List<GetCart.CartItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GetCart.CartItem item) {

        if (!TextUtils.isEmpty(item.record_code_string)) {
            ((TextView) helper.getView(R.id.tv_values)).setText(item.record_code_string);
        }

        if (!TextUtils.isEmpty(item.lottery_title)) {
            ((TextView) helper.getView(R.id.tv_values_tips)).setText(item.lottery_title);
        }

        helper.addOnClickListener(R.id.iv_dele_item);
    }
}
