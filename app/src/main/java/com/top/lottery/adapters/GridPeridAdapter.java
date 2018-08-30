package com.top.lottery.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.top.lottery.R;
import com.top.lottery.beans.LotteryType;

import java.util.List;

//查询记录的 筛选条件
public class GridPeridAdapter extends BaseAdapter {
    private Activity mContext;
  private   List<LotteryType> lotteryTypes;
    public GridPeridAdapter(Activity mContext, List<LotteryType> lotteryTypes) {
     this.mContext = mContext;
     this.lotteryTypes = lotteryTypes;
    }

    @Override
    public int getCount() {
        return lotteryTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return lotteryTypes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.adapter_record_period,null,false);
            viewHold = new ViewHold();
            viewHold.tvTitle = convertView.findViewById(R.id.tv_actions);
            viewHold.ivSelect = convertView.findViewById(R.id.iv_select);
            convertView.setTag(viewHold);
        }else{
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.tvTitle.setText(""+lotteryTypes.get(position).title);
        if (lotteryTypes.get(position).isSelect){
            viewHold.ivSelect.setVisibility(View.VISIBLE);
        }else{
            viewHold.ivSelect.setVisibility(View.GONE);
        }

        return convertView;
    }


    public class ViewHold {
        public TextView tvTitle;
        public ImageView ivSelect;
    }

}
