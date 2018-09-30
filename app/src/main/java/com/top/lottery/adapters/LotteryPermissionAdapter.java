package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.MemberPermissionItemOptions;

import java.util.List;

//会员权限设置  彩种设置
public class LotteryPermissionAdapter extends BaseQuickAdapter<MemberPermissionItemOptions, BaseViewHolder> {

    public LotteryPermissionAdapter(int layoutResId, @Nullable List<MemberPermissionItemOptions> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final MemberPermissionItemOptions item) {

        CheckBox checkBox = helper.getView(R.id
                .check);
        checkBox.setText("" + item.title);

        checkBox.setChecked(item.isSelected);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    item.isSelected = true;
                }else{
                    item.isSelected = false;
                }
            }
        });

    }

}
