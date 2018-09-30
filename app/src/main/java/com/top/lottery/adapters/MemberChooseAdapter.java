package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.NewAddMemberTypeOptions;

import java.util.List;

public class MemberChooseAdapter extends BaseQuickAdapter<NewAddMemberTypeOptions, BaseViewHolder> {
    public MemberChooseAdapter(int layoutResId, @Nullable List<NewAddMemberTypeOptions> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewAddMemberTypeOptions item) {

        if (!TextUtils.isEmpty(item.title)){
            ((RadioButton) helper.getView(R.id.tv_actions)).setText(item.title);
        }

//        ((RadioButton) helper.getView(R.id.tv_actions)).setEnabled(false);
        ((RadioButton) helper.getView(R.id.tv_actions)).setChecked(item.isSelected);

    }
}
