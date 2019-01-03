package com.top.lottery.adapters;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.top.lottery.R;
import com.top.lottery.beans.OpenLotteryCode;

import java.util.List;

import me.gujun.android.taggroup.TagGroup;

//最新开奖号
public class OpenLotteryCodeAdapter extends BaseQuickAdapter<OpenLotteryCode, BaseViewHolder> {

    private boolean isNew;
    public OpenLotteryCodeAdapter(int layoutResId, @Nullable List<OpenLotteryCode> data) {
        super(layoutResId, data);
    }

    public OpenLotteryCodeAdapter(int layoutResId, @Nullable List<OpenLotteryCode> data,boolean isNew) {
        super(layoutResId, data);
        this.isNew = isNew;
    }

    @Override
    protected void convert(BaseViewHolder helper, OpenLotteryCode item) {
        if (!TextUtils.isEmpty(item.id)) {
            ((TextView) helper.getView(R.id.tv_open_times)).setText("第"+item.id+"期");
        }



        if (!TextUtils.isEmpty(item.lottery_time)) {
            ((TextView) helper.getView(R.id.tv_open_date)).setText(item.lottery_time);
        }

        if (!TextUtils.isEmpty(item.prize_code)) {
//            item.prize_code = "06 05 03 10 09";
            String[] codes = item.prize_code.split(" ");



            if (codes.length>3) {
                ((TagGroup) helper.getView(R.id.tag_code)).setTags(new String[]{codes[0], codes[1], codes[2]});
                ((TagGroup) helper.getView(R.id.tag_code_red)).setTags(new String[]{codes[3], codes[4]});
                helper.getView(R.id.tag_code_red).setVisibility(View.VISIBLE);
                helper.getView(R.id.tag_code).setVisibility(View.VISIBLE);
            }else{
                ((TagGroup) helper.getView(R.id.tag_code_red)).setTags(new String[]{appendZero(codes[0]), appendZero(codes[1]), appendZero(codes[2])});
                helper.getView(R.id.tag_code_red).setVisibility(View.VISIBLE);
                helper.getView(R.id.tag_code).setVisibility(View.GONE);

            }
        }

        if (!TextUtils.isEmpty(item.code_sum)) {
            ((TextView)helper.getView(R.id.tv_lottery_sum)).setText("和值："+item.code_sum);
            helper.getView(R.id.tv_lottery_sum).setVisibility(View.VISIBLE);
        }else{
            helper.getView(R.id.tv_lottery_sum).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.lid_title)) {
            ((TextView)helper.getView(R.id.tv_lottery_name)).setText(item.lid_title);
        }

//        helper.getView(R.id.tv_lottery_name).setVisibility(isNew?View.VISIBLE:View.GONE);
        helper.getView(R.id.iv_arrow).setVisibility(isNew?View.VISIBLE:View.GONE);

    }

    private String appendZero(String code) {
       return code.startsWith("0")?code:"0"+code;
    }
}
