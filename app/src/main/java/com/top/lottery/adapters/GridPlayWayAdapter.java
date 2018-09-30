package com.top.lottery.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.top.lottery.R;
import com.top.lottery.beans.LotteryPlayWay;

import java.util.List;

/**
 * Author: 邓言诚  Create at : 2018/8/19  16:18
 * Email: yanchengdeng@gmail.com
 * Describle: 玩法
 */
public class GridPlayWayAdapter extends BaseAdapter {

    private Context mContext;
    private List<LotteryPlayWay> lotteryPlayWays;
    private LinearLayout.LayoutParams layoutParams;

    public GridPlayWayAdapter(Activity mContext, List<LotteryPlayWay> lotteryPlayWays) {
        this.mContext = mContext;
        this.lotteryPlayWays = lotteryPlayWays;
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWidth() /6);
    }

    @Override
    public int getCount() {
        return lotteryPlayWays.size();
    }

    @Override
    public Object getItem(int i) {
        return lotteryPlayWays.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextViewHold viewHold;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_play_way_centre, null, false);
            viewHold = new TextViewHold();
            viewHold.textView = view.findViewById(R.id.tv_actions);
            view.setTag(viewHold);

        } else {
            viewHold = (TextViewHold) view.getTag();
        }

        viewHold.textView.setText(lotteryPlayWays.get(i).title);
        viewHold.textView.setLayoutParams(layoutParams);
        return view;
    }


    class TextViewHold {

        public TextView textView;
    }
}
