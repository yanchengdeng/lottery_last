package com.top.lottery.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.activities.MemberSettingActivity;
import com.top.lottery.activities.MemberStaticActivity;
import com.top.lottery.activities.RechargeWithdrawActivity;
import com.top.lottery.base.Constants;
import com.top.lottery.beans.ManageMemberItem;
import com.top.lottery.utils.GridSpacingItemDecoration;

import java.util.List;

//人员管理
public class MemberManageAdapter extends BaseQuickAdapter<ManageMemberItem, BaseViewHolder> {

    public MemberManageAdapter(int layoutResId, @Nullable List<ManageMemberItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final ManageMemberItem item) {
        ((TextView) helper.getView(R.id.tv_uid)).setText("" + item.uid);

        ((TextView) helper.getView(R.id.tv_intergray)).setText("" + item.score);


        RecyclerView recyclerView  = helper.getView(R.id.recycle_actions);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,4));
        if (recyclerView.getItemDecorationCount()==0) {
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 5, false));
        }
        recyclerView.setAdapter(new MemberActionsAdapter(R.layout.adapter_manage_actions_view,item.buttons));

        ((MemberActionsAdapter) recyclerView.getAdapter()).setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String type = ((MemberActionsAdapter)adapter).getData().get(position).type;
                String title =  ((MemberActionsAdapter)adapter).getData().get(position).title;
                Bundle bundle = new Bundle();
                if (!TextUtils.isEmpty(type)) {
                    if (type.equals("deposit")) {
                        //充值
                        item.type = type;
                        item.title = title;
                        bundle.putSerializable(Constants.PASS_OBJECT,item);
                        ActivityUtils.startActivity(bundle, RechargeWithdrawActivity.class);
                    } else if (type.equals("withdraw")) {
                        //提现
                        item.type = type;
                        item.title = title;
                        bundle.putSerializable(Constants.PASS_OBJECT,item);
                        ActivityUtils.startActivity(bundle, RechargeWithdrawActivity.class);
                    } else if (type.equals("statistics")) {
                        //统计
                        bundle.putSerializable(Constants.PASS_OBJECT,item);
                        ActivityUtils.startActivity(bundle, MemberStaticActivity.class);
                    } else if (type.equals("setAuth")) {
                        //设置权限
                        bundle.putSerializable(Constants.PASS_OBJECT,item);
                        ActivityUtils.startActivity(bundle, MemberSettingActivity.class);
                    }
                }
            }
        });

    }

}
